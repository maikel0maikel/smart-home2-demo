package com.felix.demo.activity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.TrafficStats;

/**
 * 流量排行数据存储
 * @author cfb
 *
 */
public class NetTrafficRankingAccessor{

    private static final String TAG = "NetTrafficRankingAccessor";
	private static final String DATABASE_NAME = "data.db";	
	private static final String PREFS_NAME = "NetTrafficPrefs2";
	private static final String bootCompletedRankingKey = "isBootCompletedRanking"; //流量排行  是否重启如果是重启则需要增加1  
	
	private static final String T_NETTRAFFIC_RANKING = "NetTrafficRanking";
	private static final String T_NETTRAFFIC_BYTES = "NetTrafficBytes";
	
	private static int RANKING_DATE_ID = -1; //流量排行数据标示  
	
	private static HashSet<String> ignorePkgSet;
	
	//创建流量排行表
	private static final String SQL_CREATE_TABLE_NETTRAFFIC_RANKING = ""
			+ "CREATE TABLE IF NOT EXISTS NetTrafficRanking "
			+ "  ( "
			+ "     id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "     pkg     TEXT NOT NULL, "
			+ "     rx      DOUBLE, "
			+ "     tx      DOUBLE, "
			+ "     date    TEXT NOT NULL, "
			+ "     data_id INTEGER DEFAULT 0, "
			+ "     uid     INTEGER, "
			+ "     names   TEXT "
			+ "  )";
	
	private Context ctx;	

	private NetTrafficRankingAccessor(Context ctx){
		this.ctx = ctx;
		SQLiteDatabase db = openDB();
		db.execSQL(SQL_CREATE_TABLE_NETTRAFFIC_RANKING);		
		db.close();
	}	

	static private NetTrafficRankingAccessor accessor; 
	
	public static NetTrafficRankingAccessor getInstance(Context context){

		if(accessor == null){
			accessor = new NetTrafficRankingAccessor(context);
		}
		return accessor;
	}
	
	private SQLiteDatabase openDB(){		
		return ctx.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
	}
	
	private NetTrafficRankingItem buildNetTrafficRankingItem(Cursor c) {
		
		NetTrafficRankingItem ret = new NetTrafficRankingItem();  
		ret.id = c.getInt(0);
		ret.pkg = c.getString(1); 
		ret.rx = c.getFloat(2);
        ret.tx = c.getFloat(3);
        ret.date = c.getString(4);
        ret.data_id = c.getInt(5);
        ret.uid = c.getInt(6);
        ret.names = c.getString(7);
		return ret; 
	}
	
	//流量排行统计组装
	private NetTrafficRankingItem buildNetTrafficRankingItemForSum(Cursor c) {
		
		NetTrafficRankingItem ret = new NetTrafficRankingItem();  
		ret.pkg = c.getString(0); 
		ret.names = c.getString(1);
		ret.rx = c.getFloat(2);
        ret.tx = c.getFloat(3);
        ret.tal = c.getFloat(4);
        
		return ret; 
	}
	
	//查询单个软件的最后一次流量排行
	public NetTrafficRankingItem getNetTrafficRankingItem(String pkgName, int data_id) throws Exception{
		NetTrafficRankingItem ret = null;
		SQLiteDatabase db = openDB();
        Cursor c = db.rawQuery("select * from "+T_NETTRAFFIC_RANKING+" where pkg=? and data_id=?", new String[] {pkgName, data_id+""});        
        if (c.moveToFirst()) {            
            ret = buildNetTrafficRankingItem(c);
        }
        c.close();
        db.close();
        return ret;	
	}	
	
	//增加或修改某个软件的排行流量
	public boolean updateNetTrafficRankingItem(NetTrafficRankingItem item) throws Exception{
		ContentValues values = new ContentValues();		
		//values.put("id", item.id); //系统自增
		values.put("pkg", item.pkg);
		values.put("rx", item.rx);
		values.put("tx", item.tx);
		values.put("date", item.date);
		values.put("data_id", item.data_id);
		values.put("uid", item.uid);
		values.put("names", item.names);
				
		SQLiteDatabase db = openDB();
		if(this.getNetTrafficRankingItem(item.pkg, item.data_id)== null){			
			db.insertOrThrow(T_NETTRAFFIC_RANKING, null, values);			
		}else{
		    db.update(T_NETTRAFFIC_RANKING, values, "pkg=? and data_id=?", new String[] {item.pkg, item.data_id+""});		
		}		
		db.close();
		return true;
	}
	
	//删除某个软件的流量排行
	public int deleteNetTrafficRankingItem(String pkg) throws Exception{
		SQLiteDatabase db = openDB();
		int count = db.delete(T_NETTRAFFIC_RANKING, "pkg=?", new String[]{pkg});      
		db.close();
		return count;
	}
	
	//删除某个软件的流量排行
	public int deleteNetTrafficRankingItemByUid(int uid) throws Exception{
		SQLiteDatabase db = openDB();
		int count = db.delete(T_NETTRAFFIC_RANKING, "uid=?", new String[]{uid+""});      
		db.close();
		return count;
	}
	
	//清空流量排行表
	public void clearNetTrafficRanking() {
		SQLiteDatabase db = openDB();		
		db.delete(T_NETTRAFFIC_RANKING, null, null);
		db.close();		
	}
	
	//删除流量排行表
	public void dropNetTrafficRanking() {
		SQLiteDatabase db = openDB();	
		db.execSQL("DROP TABLE IF EXISTS "+T_NETTRAFFIC_RANKING);
		db.close();		
	}
		
	//判断流量排行表是否为空
	public boolean isNetTrafficRankingEmpty(){
		SQLiteDatabase db = openDB();		
		Cursor c = db.query(T_NETTRAFFIC_RANKING, null, null, null, null, null, null);
		boolean ret = c.getCount() == 0 ? true : false ;
		c.close();
		db.close();
		return ret;
	}
	
	//获取最大的数据批次数据表示
	public int getMaxDataID(){
		SQLiteDatabase db = openDB();	
		int maxID = 0;
		Cursor c = db.rawQuery("select max(data_id) from "+T_NETTRAFFIC_RANKING, null);        
        if (c.moveToFirst()) {            
        	maxID= c.getInt(0);
        }
		c.close();
		db.close();
		return maxID;
	}
	
	//获取所有的流量行记录
	public ArrayList<NetTrafficRankingItem> getAllNetTrafficRanking() throws Exception {

		ArrayList<NetTrafficRankingItem> ret = new ArrayList<NetTrafficRankingItem>();

		SQLiteDatabase db = openDB();

		//rx、tx在数据库中已 KB单位存放
		String sql = ""
				+ "SELECT pkg,names, "
				+ "       Sum(rx) / 1024                      rx_tal, "
				+ "       Sum(tx) / 1024                      tx_tal, "
				+ "       ( Sum(rx) / 1024 + Sum(tx) / 1024 ) all_tal "
				+ "FROM   "+T_NETTRAFFIC_RANKING+" "
				+ "GROUP  BY pkg,names "
				+ "HAVING all_tal > 0.01 "
				+ "ORDER  BY all_tal DESC ";
		
		Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();

		while (!c.isAfterLast()) {
			NetTrafficRankingItem item = buildNetTrafficRankingItemForSum(c);
			ret.add(item);
			c.moveToNext();
		}

		c.close();
		db.close();
		return ret;
	}

	/**
	 * TODO 修改为批量插入
	 */
	public void insertALLAppNetTrafficToDB(){
				
		try {
			final PackageManager pkgmanager = ctx.getPackageManager();
			final List<ApplicationInfo> installed = pkgmanager.getInstalledApplications(0);
			String name = null;
			NetTrafficRankingItem app = null;
			
			for (final ApplicationInfo apinfo : installed) {
				
				if ((apinfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) { // 系统程序
					continue;
				}  
				//过滤只处理有Internet连接权限的app
				if (PackageManager.PERMISSION_GRANTED != 
						pkgmanager.checkPermission(Manifest.permission.INTERNET, apinfo.packageName)) {
					continue;
				}

				//过滤不统计流量的软件包名
				if ( isIgnoreProcess(apinfo.packageName) ) {
					continue;
				}
				
				//总流量为0的不统计，无流量时函数返回-1,所以两个相加为-2
				if ( TrafficStats.getUidTxBytes(apinfo.uid)+TrafficStats.getUidRxBytes(apinfo.uid)<10 ){
					continue;
				}
				
				//apinfo.packageName 查询库中的中文名;
				name =""; //获取软件的名称
				if (name.length() == 0) {					
					name = pkgmanager.getApplicationLabel(apinfo).toString();
				}
				
				app = new NetTrafficRankingItem();
				app.uid = apinfo.uid;				
				app.names = name;
				app.pkg = apinfo.packageName;
				app.rx = TrafficStats.getUidRxBytes(apinfo.uid)/1024f;
				app.tx = TrafficStats.getUidTxBytes(apinfo.uid)/1024f;
				app.date = getStringDate();
				app.data_id = getDataID();//数据流量批次标示
				
				updateNetTrafficRankingItem(app);
			}		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//是否过滤不统计流量的软件包名
	public boolean isIgnoreProcess(String pkgName) {
		
		if (ignorePkgSet==null) {
			
			ignorePkgSet = new HashSet<String>();
		
			try {
				InputStream is = ctx.getAssets().open("traffic.nd");
				InputStreamReader isr = new InputStreamReader(is);			
				BufferedReader bufferedReader = new BufferedReader(isr);
				String read = null;
				while ((read = bufferedReader.readLine()) != null) {
					read = read.trim();
					ignorePkgSet.add(read);
				}
				bufferedReader.close();
				isr.close();
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return ignorePkgSet.contains(pkgName);
		}
		
		return ignorePkgSet.contains(pkgName);
	}
	
    public static String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String dateString = formatter.format(currentTime);
        return dateString;
    }
    
    public int getDataID(){
    	
    	//RANKING_DATE_ID 初始化后，只有在重启手机或者重启应用是才需要重新初始化
    	if (RANKING_DATE_ID==-1){
    		
    		if ( isNetTrafficRankingEmpty() ){
    			RANKING_DATE_ID = 0;
    		}else{
    			
    			int maxID = getMaxDataID();
    			System.out.println( "NetTrafficRankingAccessor getDataID="+maxID );
    			
    			RANKING_DATE_ID = getBootCompletedRanking()?maxID+1:maxID;
    			
    			if ( getBootCompletedRanking() ){ 
    				setBootCompletedRanking(false);
    			}
    		}
    	}
    	
    	return RANKING_DATE_ID;
    }
    
    public boolean getBootCompletedRanking(){
    	
    	final SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, 0);
    	return prefs.getBoolean(bootCompletedRankingKey, false);
    }
    
    public void setBootCompletedRanking(boolean bootComplete){
    	
    	final SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, 0);
    	Editor editor = prefs.edit();
    	editor.putBoolean(bootCompletedRankingKey, bootComplete);
    	editor.commit();
    }
    
    public void applicationRemoved(String pkgName,int uid){
    	try {
    		//deleteNetTrafficRankingItemByUid(uid);
    		deleteNetTrafficRankingItem(pkgName);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
