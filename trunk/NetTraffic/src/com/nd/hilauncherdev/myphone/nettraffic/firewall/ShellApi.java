package com.nd.hilauncherdev.myphone.nettraffic.firewall;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import com.nd.hilauncherdev.datamodel.CommonGlobal;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public final class ShellApi {
	
	private static final String TAG = "ShellApi";
	
	private static final String SCRIPT_FILE = "firewall.sh";
	private static final String FIREWALL_PRE = "FIREWALLSAFE";
	
	/**配置文件*/
	public static final String PREFS_NAME 			= "SafeCenter";
	public static final String PREF_3G_UIDS			= "ForbidUids3G";
	public static final String PREF_WIFI_UIDS		= "ForbidUidsWifi";
	
	/**是否Root*/
	private static boolean hasroot = false;

	
	/**
	 * alert 显示提示框
	 * @param ctx
	 * @param msg    
	 * @return void   
	 */
	public static void alert(Context ctx, CharSequence msg) {
    	if (ctx != null) {
    		Log.e(TAG, msg+"");
    		/*
        	new AlertDialog.Builder(ctx)
        	.setNeutralButton(android.R.string.ok, null)
        	.setMessage(msg)
        	.show();
        	*/
    	}
    }
	
	/**
	 * 创建 iptables文件的环境变量初始化脚本
	 * @param ctx context
	 * @return script header
	 */
	private static String scriptHeader(Context ctx) {
		final String dir = ctx.getDir("bin",0).getAbsolutePath();
		final String myiptables = dir + "/iptables_armv5";
		return "" +
			"IPTABLES=iptables\n" +
			"BUSYBOX=busybox\n" +
			"GREP=grep\n" +
			"ECHO=echo\n" +
			"# Try to find busybox\n" +
			"if " + dir + "/busybox_g1 --help >/dev/null 2>/dev/null ; then\n" +
			"	BUSYBOX="+dir+"/busybox_g1\n" +
			"	GREP=\"$BUSYBOX grep\"\n" +
			"	ECHO=\"$BUSYBOX echo\"\n" +
			"elif busybox --help >/dev/null 2>/dev/null ; then\n" +
			"	BUSYBOX=busybox\n" +
			"elif /system/xbin/busybox --help >/dev/null 2>/dev/null ; then\n" +
			"	BUSYBOX=/system/xbin/busybox\n" +
			"elif /system/bin/busybox --help >/dev/null 2>/dev/null ; then\n" +
			"	BUSYBOX=/system/bin/busybox\n" +
			"fi\n" +
			"# Try to find grep\n" +
			"if ! $ECHO 1 | $GREP -q 1 >/dev/null 2>/dev/null ; then\n" +
			"	if $ECHO 1 | $BUSYBOX grep -q 1 >/dev/null 2>/dev/null ; then\n" +
			"		GREP=\"$BUSYBOX grep\"\n" +
			"	fi\n" +
			"	# Grep is absolutely required\n" +
			"	if ! $ECHO 1 | $GREP -q 1 >/dev/null 2>/dev/null ; then\n" +
			"		$ECHO The grep command is required. firewall will not work.\n" +
			"		exit 1\n" +
			"	fi\n" +
			"fi\n" +
			"# Try to find iptables\n" +
			"if " + myiptables + " --version >/dev/null 2>/dev/null ; then\n" +
			"	IPTABLES="+myiptables+"\n" +
			"fi\n" +
			"";
	}
	
	/**
	 * Copy raw资源文件到指定的路径
	 * @param ctx
	 * @param resid raw资源文件ID
	 * @param file 目标文件路径
	 * @param mode 文件权限 (E.g.: "755")
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	private static void copyRawFile(Context ctx, String resName, File file, String mode) throws IOException, InterruptedException
	{
		final String abspath = file.getAbsolutePath();
		// Write the iptables binary
		final FileOutputStream out = new FileOutputStream(file);
		final InputStream is = ctx.getAssets().open(resName);
		byte buf[] = new byte[1024];
		int len;
		while ((len = is.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		out.close();
		is.close();
		// Change the permissions
		Runtime.getRuntime().exec("chmod "+mode+" "+abspath).waitFor();
	}
	
	/**
	 * 清空iptable已有规则,并重建所有规则
	 * @param ctx
	 * @param uidsWifi 所选择的应用UIDs
	 * @param uids3g	 所选择的应用UIDs
	 * @param showErrors 是否显示错误
	 * @return boolean
	 */
	private static boolean applyIptablesRulesImpl(Context ctx, List<Integer> uidsWifi, List<Integer> uids3g, boolean showErrors) {
		if (ctx == null) {
			return false;
		}
		assertBinaries(ctx, showErrors);
		final String ITFS_WIFI[] = {"tiwlan+", "wlan+", "eth+", "ra+"};
		final String ITFS_3G[] = {"rmnet+","pdp+","ppp+","uwbr+","wimax+","vsnet+","ccmni+","usb+"};

    	final StringBuilder script = new StringBuilder();
		try {
			int code;
			script.append(scriptHeader(ctx));
			script.append("" +
				"$IPTABLES --version || exit 1\n" +
				"# Clear all rules\n" +	
				"$IPTABLES -F || exit 100\n" +
				"# Create the "+FIREWALL_PRE+" chains if necessary\n" +
				"$IPTABLES -L "+FIREWALL_PRE+" >/dev/null 2>/dev/null || $IPTABLES --new "+FIREWALL_PRE+" || exit 2\n" +
				"$IPTABLES -L "+FIREWALL_PRE+"-3G >/dev/null 2>/dev/null || $IPTABLES --new "+FIREWALL_PRE+"-3G || exit 3\n" +
				"$IPTABLES -L "+FIREWALL_PRE+"-WIFI >/dev/null 2>/dev/null || $IPTABLES --new "+FIREWALL_PRE+"-WIFI || exit 4\n" +
				"$IPTABLES -L "+FIREWALL_PRE+"-REJECT >/dev/null 2>/dev/null || $IPTABLES --new "+FIREWALL_PRE+"-REJECT || exit 5\n" +
				"# Add "+FIREWALL_PRE+" chain to OUTPUT chain if necessary\n" +
				"$IPTABLES -L OUTPUT | $GREP -q "+FIREWALL_PRE+" || $IPTABLES -A OUTPUT -j "+FIREWALL_PRE+" || exit 6\n" +
				"# Flush existing rules\n" +	
				"$IPTABLES -F "+FIREWALL_PRE+" || exit 7\n" +
				"$IPTABLES -F "+FIREWALL_PRE+"-3G || exit 8\n" +
				"$IPTABLES -F "+FIREWALL_PRE+"-WIFI || exit 9\n" +
				"$IPTABLES -F "+FIREWALL_PRE+"-REJECT || exit 10\n" +
			"");
			
			//网络拦截规则
			script.append("" +
					"# Create the reject rule (log disabled)\n" +
					"$IPTABLES -A "+FIREWALL_PRE+"-REJECT -j REJECT || exit 11\n" +
				"");
			
			script.append("# Main rules (per interface)\n");
			for (final String itf : ITFS_3G) {
				//匹配-o 以包离开本地所使用的网络接口来匹配包
				script.append("$IPTABLES -A "+FIREWALL_PRE+" -o ").append(itf).append(" -j "+FIREWALL_PRE+"-3G || exit\n");
			}
			for (final String itf : ITFS_WIFI) {
				script.append("$IPTABLES -A "+FIREWALL_PRE+" -o ").append(itf).append(" -j "+FIREWALL_PRE+"-WIFI || exit\n");
			}
			
			script.append("# Filtering rules\n");
			//黑名单模式
			final String targetRule = ""+FIREWALL_PRE+"-REJECT";
			
			for (final Integer uid : uids3g) {
				if (uid >= 0) script.append("$IPTABLES -A "+FIREWALL_PRE+"-3G -m owner --uid-owner ").append(uid).append(" -j ").append(targetRule).append(" || exit\n");
			}
			
			for (final Integer uid : uidsWifi) {
				if (uid >= 0) script.append("$IPTABLES -A "+FIREWALL_PRE+"-WIFI -m owner --uid-owner ").append(uid).append(" -j ").append(targetRule).append(" || exit\n");
			}
			
	    	final StringBuilder res = new StringBuilder();
			code = runScriptAsRoot(ctx, script.toString(), res);
			if (showErrors && code != 0) {
				String msg = res.toString();
				Log.e(TAG, msg);
				alert(ctx, "Exit code: " + code + "\n\n" + msg.trim());
			} else {
				return true;
			}
		} catch (Exception e) {
			if (showErrors) alert(ctx, "error refreshing iptables: " + e);
		}
		return false;
    }
	
    /**
     * 规则变更后经常调用的接口: 从配置文件读取规则并应用
     * applySavedIptablesRules
     * @param ctx
     * @param showErrors
     * @return boolean
     */
	private static boolean applySavedIptablesRules(Context ctx, boolean showErrors) {
		if (ctx == null) {
			return false;
		}
		final SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, 0);
		final String savedUids_wifi = prefs.getString(PREF_WIFI_UIDS, "");
		final String savedUids_3g = prefs.getString(PREF_3G_UIDS, "");
		final List<Integer> uids_wifi = new LinkedList<Integer>();
		if (savedUids_wifi.length() > 0) {
			// Check which applications are allowed on wifi
			final StringTokenizer tok = new StringTokenizer(savedUids_wifi, "|");
			while (tok.hasMoreTokens()) {
				final String uid = tok.nextToken();
				if (!uid.equals("")) {
					try {
						uids_wifi.add(Integer.parseInt(uid));
					} catch (Exception ex) {
					}
				}
			}
		}
		final List<Integer> uids_3g = new LinkedList<Integer>();
		if (savedUids_3g.length() > 0) {
			// Check which applications are allowed on 2G/3G
			final StringTokenizer tok = new StringTokenizer(savedUids_3g, "|");
			while (tok.hasMoreTokens()) {
				final String uid = tok.nextToken();
				if (!uid.equals("")) {
					try {
						uids_3g.add(Integer.parseInt(uid));
					} catch (Exception ex) {
					}
				}
			}
		}
		return applyIptablesRulesImpl(ctx, uids_wifi, uids_3g, showErrors);
	}
	
    
	/**
	 * 外部修改规则后调用本方法进行规则保存及应用
	 * @param ctx
	 * @param showErrors
	 * @return boolean
	 */
	public static boolean applyIptablesRules(Context ctx, List<FireWallAppItem> appCacheList, boolean showErrors) {
		if (ctx == null) {
			return false;
		}
		saveRules(ctx, appCacheList);
		return applySavedIptablesRules(ctx, showErrors);
    }
	
	/**
	 * 将规则保存到配置文件中
	 * @param ctx void
	 */
	public static void saveRules(Context ctx, List<FireWallAppItem> appCacheList) {
		final SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, 0);
		// Builds a pipe-separated list of names
		final StringBuilder newuids_wifi = new StringBuilder();
		final StringBuilder newuids_3g = new StringBuilder();
		
		FireWallAppItem fireWallAppItem = null;
		for (int i=0; i<appCacheList.size(); i++) {
			
			fireWallAppItem = appCacheList.get(i);
			
			if (fireWallAppItem.bWifiSelect) {
				if (newuids_wifi.length() != 0) newuids_wifi.append('|');
				newuids_wifi.append(fireWallAppItem.uid);
			}
			if (fireWallAppItem.b3GSelect) {
				if (newuids_3g.length() != 0) newuids_3g.append('|');
				newuids_3g.append(fireWallAppItem.uid);
			}
		}
		// save the new list of UIDs
		final Editor edit = prefs.edit();
		edit.putString(PREF_WIFI_UIDS, newuids_wifi.toString());
		edit.putString(PREF_3G_UIDS, newuids_3g.toString());
		edit.commit();
    }
    
	/**
	 * 清空iptables的所有规则
	 * @param ctx
	 * @param showErrors
	 * @return boolean
	 */
	public static boolean purgeIptables(Context ctx, boolean showErrors) {
    	final StringBuilder res = new StringBuilder();
		try {
			assertBinaries(ctx, showErrors);
			// Custom "shutdown" script
	    	final StringBuilder script = new StringBuilder();
	    	script.append(scriptHeader(ctx));
	    	script.append("" +
					"$IPTABLES -F "+FIREWALL_PRE+"\n" +
					"$IPTABLES -F "+FIREWALL_PRE+"-REJECT\n" +
					"$IPTABLES -F "+FIREWALL_PRE+"-3G\n" +
					"$IPTABLES -F "+FIREWALL_PRE+"-WIFI\n" +
	    			"");
			int code = runScriptAsRoot(ctx, script.toString(), res);
			if (code == -1) {
				if (showErrors) alert(ctx, "Error purging iptables. exit code: " + code + "\n" + res);
				return false;
			}
			return true;
		} catch (Exception e) {
			if (showErrors) alert(ctx, "Error purging iptables: " + e);
			return false;
		}
    }
	
	/**
	 * 显示 iptables规则并输出，调试使用
	 * @param ctx Context
	 */
	public static void showIptablesRules(Context ctx) {
		try {
    		final StringBuilder res = new StringBuilder();
			runScriptAsRoot(ctx, scriptHeader(ctx) +
								 "$ECHO $IPTABLES\n" +
								 "$IPTABLES -L -v -n\n", res);
			alert(ctx, res);
		} catch (Exception e) {
			alert(ctx, "error: " + e);
		}
	}

   
	/**
	 * 检测是否有root访问权限
	 * @param ctx
	 * @param showErrors
	 * @return boolean
	 */
	public static boolean hasRootAccess(final Context ctx, boolean showErrors) {
		if (hasroot) return true;
		final StringBuilder res = new StringBuilder();
		try {
			// Run an empty script just to check root access
			if (runScriptAsRoot(ctx, "exit 0", res) == 0) {
				hasroot = true;
				return true;
			}
		} catch (Exception e) {
		}
		if (showErrors) {
			alert(ctx, "Error message: " + res.toString());
		}
		return false;
	}
	
	
    /**
     * Runs a script, wither as root or as a regular user (multiple commands separated by "\n").
	 * @param ctx mandatory context
     * @param script the script to be executed
     * @param res the script output response (stdout + stderr)
     * @param timeout timeout in milliseconds (-1 for none)
     * @return the script exit code
     */
	public static int runScript(Context ctx, String script, StringBuilder res, long timeout, boolean asroot) {
		final File file = new File(ctx.getDir("bin",0), SCRIPT_FILE);
		final ScriptRunner runner = new ScriptRunner(file, script, res, asroot);
		runner.start();
		try {
			if (timeout > 0) {
				runner.join(timeout);
			} else {
				runner.join();
			}
			if (runner.isAlive()) {
				// Timed-out
				runner.interrupt();
				runner.join(150);
				runner.destroy();
				runner.join(50);
			}
		} catch (InterruptedException ex) {}
		return runner.exitcode;
	}
    /**
     * Runs a script as root (multiple commands separated by "\n").
	 * @param ctx mandatory context
     * @param script the script to be executed
     * @param res the script output response (stdout + stderr)
     * @param timeout timeout in milliseconds (-1 for none)
     * @return the script exit code
     */
	public static int runScriptAsRoot(Context ctx, String script, StringBuilder res, long timeout) {
		return runScript(ctx, script, res, timeout, true);
    }
    /**
     * Runs a script as root (multiple commands separated by "\n") with a default timeout of 20 seconds.
	 * @param ctx mandatory context
     * @param script the script to be executed
     * @param res the script output response (stdout + stderr)
     * @param timeout timeout in milliseconds (-1 for none)
     * @return the script exit code
     * @throws IOException on any error executing the script, or writing it to disk
     */
	public static int runScriptAsRoot(Context ctx, String script, StringBuilder res) throws IOException {
		return runScriptAsRoot(ctx, script, res, 40000);
	}
    /**
     * Runs a script as a regular user (multiple commands separated by "\n") with a default timeout of 20 seconds.
	 * @param ctx mandatory context
     * @param script the script to be executed
     * @param res the script output response (stdout + stderr)
     * @param timeout timeout in milliseconds (-1 for none)
     * @return the script exit code
     * @throws IOException on any error executing the script, or writing it to disk
     */
	public static int runScript(Context ctx, String script, StringBuilder res) throws IOException {
		return runScript(ctx, script, res, 40000, false);
	}
	
	/**
	 * Asserts that the binary files are installed in the cache directory.
	 * @param ctx context
     * @param showErrors indicates if errors should be alerted
	 * @return false if the binary files could not be installed
	 */
	public static boolean assertBinaries(Context ctx, boolean showErrors) {
		boolean changed = false;
		try {
			// Check iptables_armv5
			File file = new File(ctx.getDir("bin",0), "iptables_armv5");
			if (!file.exists() || file.length()!=198652) {
				copyRawFile(ctx, "iptables_armv5", file, "755");
				changed = true;
			}
			// Check busybox
			file = new File(ctx.getDir("bin",0), "busybox_g1");
			if (!file.exists()) {
				copyRawFile(ctx, "busybox_g1", file, "755");
				changed = true;
			}
		} catch (Exception e) {
			if (showErrors) alert(ctx, "Error installing binary files: " + e);
			return false;
		}
		return true;
	}


	/**
	 * Internal thread used to execute scripts (as root or not).
	 */
	private static final class ScriptRunner extends Thread {
		private final File file;
		private final String script;
		private final StringBuilder res;
		private final boolean asroot;
		public int exitcode = -1;
		private Process exec;
		private DataOutputStream dataOutputStream = null;
		
		/**
		 * Creates a new script runner.
		 * @param file temporary script file
		 * @param script script to run
		 * @param res response output
		 * @param asroot if true, executes the script as root
		 */
		public ScriptRunner(File file, String script, StringBuilder res, boolean asroot) {
			this.file = file;
			this.script = script;
			this.res = res;
			this.asroot = asroot;
		}
		@Override
		public void run() {
			try {
				file.createNewFile();
				final String abspath = file.getAbsolutePath();
				// make sure we have execution permission on the script file
				Runtime.getRuntime().exec("chmod 777 "+abspath).waitFor();
				// Write the script to be executed
				final OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file));
				if (new File("/system/bin/sh").exists()) {
					out.write("#!/system/bin/sh\n");  
				}
				out.write(script);
				if (!script.endsWith("\n")) out.write("\n");
				out.write("exit\n");
				out.flush();
				out.close();
				if (this.asroot) {
					// Create the "su" request to run the script
					exec = Runtime.getRuntime().exec(new String[] { "/system/bin/" + CommonGlobal.SUPER_SHELL_FILE_NAME, CommonGlobal.SUPER_SHELL_PERMISSION });
					dataOutputStream = new DataOutputStream(exec.getOutputStream());
					dataOutputStream.writeBytes("sh "+abspath + "\n");
					dataOutputStream.writeBytes("exit\n");
					dataOutputStream.flush();
				} else {
					// Create the "sh" request to run the script
					exec = Runtime.getRuntime().exec("sh "+abspath);
				}
				final InputStream stdout = exec.getInputStream();
				final InputStream stderr = exec.getErrorStream();
				final byte buf[] = new byte[8192];
				int read = 0;
				while (true) {
					final Process localexec = exec;
					if (localexec == null) break;
					try {
						// get the process exit code - will raise IllegalThreadStateException if still running
						this.exitcode = localexec.exitValue();
					} catch (IllegalThreadStateException ex) {
						// The process is still running
					}
					// Read stdout
					if (stdout.available() > 0) {
						read = stdout.read(buf);
						if (res != null) res.append(new String(buf, 0, read));
					}
					// Read stderr
					if (stderr.available() > 0) {
						read = stderr.read(buf);
						if (res != null) res.append(new String(buf, 0, read));
					}
					if (this.exitcode != -1) {
						// finished
						break;
					}
					// Sleep for the next round
					Thread.sleep(50);
				}
			} catch (InterruptedException ex) {
				if (res != null) res.append("\nOperation timed-out");
			} catch (Exception ex) {
				if (res != null) res.append("\n" + ex);
			} finally {
				destroy();
			}
		}
		/**
		 * Destroy this script runner
		 */
		public synchronized void destroy() {
			
			try {
				if (dataOutputStream != null) {
					dataOutputStream.close();
					dataOutputStream = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (exec != null) exec.destroy();
			exec = null;
		}
	}


	//=========与通知栏检测模块交互接口==============
	
	/**
	 * 获取禁止Wifi模式联网的应用ID列表
	 * @param ctx
	 * @return HashSet<String>
	 */
	public static HashSet<String> getSavedUidsWifi(Context ctx){
		
		HashSet<String> wifiUidSet = new HashSet<String>();
		final SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, 0);
		final String savedUids_wifi = prefs.getString(PREF_WIFI_UIDS, "");
		
		if (savedUids_wifi.length() > 0) {
			
			final StringTokenizer tok = new StringTokenizer(savedUids_wifi, "|");
			int count = tok.countTokens();
			for (int i=0; i<count; i++) {
				final String uid = tok.nextToken();
				if ( uid!=null && !uid.equals("")) {
					wifiUidSet.add(uid);
				}
			}
		}
		
		return wifiUidSet;
	}
	
	/**
	 * 获取禁止3G模式联网的应用ID列表
	 * @param ctx
	 * @return HashSet<String>
	 */
	public static HashSet<String> getSavedUids3G(Context ctx){
		
		HashSet<String> g3UidSet = new HashSet<String>();
		final SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, 0);
		final String savedUids_3G = prefs.getString(PREF_3G_UIDS, "");
		
		if (savedUids_3G.length() > 0) {
			
			final StringTokenizer tok = new StringTokenizer(savedUids_3G, "|");
			int count = tok.countTokens();
			for (int i=0; i<count; i++) {
				final String uid = tok.nextToken();
				if ( uid!=null && !uid.equals("")) {
					g3UidSet.add(uid);
				}
			}
		}
		
		return g3UidSet;
	}
	
	/**
	 * 获取搜有禁止联网的应用ID列表
	 * @param ctx 
	 * @return HashSet<String>
	 */
	public static HashSet<String> getSavedUidsWifiAnd3G(Context ctx){
		
		HashSet<String> allUidSet = getSavedUidsWifi(ctx);
		
		HashSet<String> g3UidSet = getSavedUids3G(ctx);
		
		allUidSet.addAll(g3UidSet);
		
		return allUidSet;
	}
	
	/**
	 * 单独禁止一个应用的联网权限时,更新防火墙规则配置,并刷新iptable.
	 * @param ctx
	 * @param uid void 
	 */
	public static boolean applicationAdd(Context ctx, int uid){
		final SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, 0);
		final Editor editor = prefs.edit();
		// allowed application names separated by pipe '|' (persisted)
		final String savedUids_wifi = prefs.getString(PREF_WIFI_UIDS, "");
		final String savedUids_3g = prefs.getString(PREF_3G_UIDS, "");
		final String uid_str = uid + "";

		// look for the removed application in the "wi-fi" list
		
		HashSet<String> wifiUidSet = getSavedUidsWifi(ctx);
		HashSet<String> g3UidSet = getSavedUids3G(ctx);
		
		if (!wifiUidSet.contains(uid_str)){
			
			editor.putString(PREF_WIFI_UIDS, savedUids_wifi+"|"+uid_str);
		}
		
		if (!g3UidSet.contains(uid_str)){
			
			editor.putString(PREF_3G_UIDS, savedUids_3g+"|"+uid_str);
		}
		
		editor.commit();
		return applySavedIptablesRules(ctx, false);
	}
	
	/**
	 * 当有应用卸载时后者单独开启一个应用的联网权限时,更新防火墙规则配置,并刷新iptable.
	 * @param ctx
	 * @param uid void
	 */
	public static boolean applicationRemoved(Context ctx, int uid) {
		final SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, 0);
		final Editor editor = prefs.edit();
		// allowed application names separated by pipe '|' (persisted)
		final String savedUids_wifi = prefs.getString(PREF_WIFI_UIDS, "");
		final String savedUids_3g = prefs.getString(PREF_3G_UIDS, "");
		final String uid_str = uid + "";
		boolean changed = false;
		// look for the removed application in the "wi-fi" list
		if (savedUids_wifi.length() > 0) {
			final StringBuilder newuids = new StringBuilder();
			final StringTokenizer tok = new StringTokenizer(savedUids_wifi, "|");
			while (tok.hasMoreTokens()) {
				final String token = tok.nextToken();
				if (uid_str.equals(token)) {
					Log.d(TAG, "Removing UID " + token + " from the wi-fi list (package removed)!");
					changed = true;
				} else {
					if (newuids.length() > 0) newuids.append('|');
					newuids.append(token);
				}
			}
			if (changed) {
				editor.putString(PREF_WIFI_UIDS, newuids.toString());
			}
		}
		// look for the removed application in the "3g" list
		if (savedUids_3g.length() > 0) {
			final StringBuilder newuids = new StringBuilder();
			final StringTokenizer tok = new StringTokenizer(savedUids_3g, "|");
			while (tok.hasMoreTokens()) {
				final String token = tok.nextToken();
				if (uid_str.equals(token)) {
					Log.d(TAG, "Removing UID " + token + " from the 3G list (package removed)!");
					changed = true;
				} else {
					if (newuids.length() > 0) newuids.append('|');
					newuids.append(token);
				}
			}
			if (changed) {
				editor.putString(PREF_3G_UIDS, newuids.toString());
			}
		}
		// if anything has changed, save the new prefs...
		if (changed) {
			editor.commit();
			return applySavedIptablesRules(ctx, false);
		}
		
		return false;
	}  
	
}
