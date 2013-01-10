package com.nd.hilauncherdev.lib.theme.util;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.graphics.Bitmap;



/**
 * 文件解析类
 * @author Administrator
 *
 */
public class FileUtil {
	
	/**
     * 创建文件夹
     */
    public static void createDir(String dir) {
        File f = new File(dir);
        if (!f.exists()) {
            f.mkdirs();
        }

    }

    /**
     * 往创建文件，并往文件中写内容
     */
    public static void writeFile(String path, String content, boolean append) {
        try {
            File f = new File(path);
            if(!f.getParentFile().exists()){
            	f.getParentFile().mkdirs();
            }
            if (!f.exists()) {
                f.createNewFile();
                f = new File(path); // 重新实例化
            }
            FileWriter fw = new FileWriter(f, append);
            if ((content != null) && !"".equals(content)) {
                fw.write(content);
                fw.flush();
            }
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除某个文件
     * @param path 文件路径
     */
    public static void delFile(String path) {
        File f = new File(path);
        if (f.exists()) {
            f.delete();
        }
    }

    /**
     * 删除文件夹
     * @param folderPath
     */
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); // 删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File f = new File(filePath);
            f.delete(); // 删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除指定文件夹下所有文件
     * @param path
     * @return
     */
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);// 再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }
    
    /**
	 * 将文件或目录复制到特性目录下
	 * @param srcFile 源文件或源目录
	 * @param destDir 目标文件夹
	 * @param bsubdirs 此参数只对复制目录有效，复制文件无效；<br/>true 复制目录、子目录、目录中的所有文件， <br/>false 则只复制当前目录中的文件，不复制目录。
	 */
	public static void copy( String srcFile,String destDir, boolean bsubdirs ) {
		File f = new File( srcFile );
		File fDestDir = new File( destDir );
		if( !fDestDir.exists() ) fDestDir.mkdirs(); 
		copy( f, fDestDir, bsubdirs );		
	}
	
	
	/**
	 * 将文件或目录复制到特性目录下
	 * @param srcFile 源文件或源目录
	 * @param fDestDir
	 * @param bsubdirs 此参数只对复制目录有效，复制文件无效；<br/>true 复制目录、子目录、目录中的所有文件， <br/>false 则只复制当前目录中的文件，不复制目录。
	 */
	public static void copy( File srcFile, File fDestDir, boolean bsubdirs ) {
		if( srcFile.isFile() ) { //复制文件本身
			copyFile( srcFile, fDestDir );
		} else if( srcFile.isDirectory() ) { //复制目录
			if( bsubdirs ) { //复制目录所有内容
				fDestDir = createSubPath( srcFile.getName(), fDestDir.getAbsolutePath() );
				copyAllChildFilesIncludeChildrenDir( srcFile, fDestDir );
			} else { //复制文件
				copyAllChildFiles( srcFile, fDestDir );				
			}
		}
	}
	
	/**
	 * 在指定的目录下创建子目录
	 * @param subPath
	 * @param parentPath
	 */
	public static File createSubPath( String subPath, String parentPath ) {
		parentPath = checkFilePath( parentPath );
		String sFullPath = parentPath + subPath;
		File f = new File( sFullPath );
		if( !f.exists() ) f.mkdirs();
		return f;
	}
	
	/**
	 * 复制当前目录下所有文件至特定目录下
	 * @param sourceDir
	 * @param destDir
	 */
	private static void copyAllChildFiles( File sourceDir, File destDir ) {
		File[]  fs = sourceDir.listFiles( new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if( pathname.isFile() ) return true;
				return false;
			}					
		});
		for( File f : fs ) {
			copyFile( f, destDir );
		};
	}
	/**
	 * 复制文件
	 * @param file
	 * @param destDir
	 */
	private static void copyFile( File file, File destDir ) {
		FileInputStream is = null;
		FileOutputStream os = null;
		try{
			if( !destDir.exists() ) destDir.mkdirs();
			String destFile = checkFilePath( destDir.getAbsolutePath() ) + file.getName();
			is = new FileInputStream( file );
			os = new FileOutputStream( destFile );
			FileChannel fcIn = is.getChannel();
			FileChannel fcOut = os.getChannel();
			fcIn.transferTo( 0, fcIn.size(), fcOut );
			
			tryClose( fcOut );
			tryClose( fcIn );
		} catch( Exception e ) {
			e.printStackTrace();
		} finally {
			if( null != is ) {
				tryClose(is);
			}
			if( null != os ) {
				tryClose( os );
			}
		}
	}
	
	/**
     * 文件复制类
     * 
     * @param srcFile
     *            源文件
     * @param destFile
     *            目录文件
     * @return 是否复制成功
     */
    public static boolean copy(String srcFile, String destFile) {
        try {
            FileInputStream in = new FileInputStream(srcFile);
            FileOutputStream out = new FileOutputStream(destFile);
            byte[] bytes = new byte[1024];
            int c;
            while ((c = in.read(bytes)) != -1) {
                out.write(bytes, 0, c);
            }
            in.close();
            out.close();
            return true;
        } catch (Exception e) {
        	e.printStackTrace();
            return false;
        }
    }	
	
	/**
	 * 关闭通道
	 * @param fc
	 */
	private static void tryClose( FileChannel fc ) {
		try{
			fc.close();
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}
	/**
	 * 创建任意深度的文件所在文件夹,可以用来替代直接new File(path)。
	 * 
	 * @param path
	 * @return File对象
	 */
	public static File createFile(String path) {
		File file = new File(path);
		
		// 寻找父目录是否存在
		File parent = new File(file.getAbsolutePath().substring(0,
				file.getAbsolutePath().lastIndexOf(File.separator)));
		// 如果父目录不存在，则递归寻找更上一层目录
		if (!parent.exists()) {
			createFile(parent.getPath());
			// 创建父目录
			parent.mkdirs();
		}
		
		return file;
	}
	
	/**
	 * 关闭输入流
	 * @param is
	 */
	private static void tryClose( InputStream is ) {
		try{
			is.close();
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}
	

	/**
	 * 关闭输出流
	 * @param os
	 */
	private static void tryClose( OutputStream os ) {
		try{
			os.close();
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取正确的路径名
	 * @param path
	 * @return
	 */
	private static String checkFilePath( String path ) {
		if( path.lastIndexOf( "/" ) !=  ( path.length() - 1 ) )
			path = path + File.separator;
		return path;
	}
	

	/**
	 * 复制当前目录所有内容至特定目录下
	 * @param sourceDir
	 * @param destDir
	 */
	private static void copyAllChildFilesIncludeChildrenDir( File sourceDir, File destDir ) {
		File[] fs = sourceDir.listFiles();
		for( File f : fs ) {
			if( f.isFile() ) {
				copyFile( f, destDir );
			} else if( f.isDirectory() ) {
				File fDestChild = createSubPath( f.getName(), destDir.getAbsolutePath() );
				copyAllChildFilesIncludeChildrenDir( f, fDestChild );
			}
		}
	}
	
	/**
	 * <br>
	 * Description:根据输入流生成文件 <br>
	 * Author:caizp <br>
	 * Date:2011-5-3下午04:08:34
	 * @param inputStream
	 * @param desFileName
	 */
	public static void generateFile(InputStream inputStream, String desFileName) {
		try {
			File f = new File(desFileName);
			OutputStream out = new FileOutputStream(f);
			byte buf[] = new byte[1024];
			int len;
			while ((len = inputStream.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				if(inputStream!=null)
					inputStream.close();
			} catch (Exception e2) {
			}
		}
	}
	
	/**
	 * 获取指定目录下指定格式的所有文件的文件名
	 * Author:zhuchenghua
	 * @param dir     目录
	 * @param fileFilter 过滤器
	 * @param hasSuffix 是否包含后缀
	 * @return
	 */
	private static List<String> getExistsFileNames(String dir, FileFilter fileFilter, boolean hasSuffix) {
		String path = dir;
		File file = new File(path);
		File[] files=null;
		if(fileFilter!=null)
			files = file.listFiles(fileFilter);
		else
			files = file.listFiles();
		
		List<String> fileNameList = new ArrayList<String>();
		for (File tmpFile : files) {
			String tmppath = tmpFile.getAbsolutePath();
			String fileName = getFileNameFromPath(tmppath, hasSuffix);
			fileNameList.add(fileName);
		}
		return fileNameList;
	}
	
	/**
	 * 从路径中获取 文件名
	 * Author:zhuchenghua
	 * @param path
	 * @param hasSuffix
	 *            是否包括后缀
	 * @return
	 */
	public static String getFileNameFromPath(String path, boolean hasSuffix) {
		if(path==null)
			return null;
		if (!hasSuffix)
			return path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
		else
			return path.substring(path.lastIndexOf("/") + 1);
	}
	
	/**
	 * 指定目录下是否存在指定名称的文件
	 * Author:zhuchenghua
	 * @param path     目录
	 * @param fileName 文件名称
	 * @return
	 */
	public static boolean isFileExits(String dir,String fileName)
	{
		fileName=fileName==null?"":fileName;
		dir=dir==null?"":dir;
		int index=dir.lastIndexOf("/");
		String filePath;
		if(index==dir.length()-1)
			filePath=dir+fileName;
		else
			filePath=dir+"/"+fileName;
		File file=new File(filePath);
		return file.exists();
	}
	
	/**
	 * 指定路么下是否存在文件
	 * Author:zhuchenghua
	 * @param filePath 文件路径
	 * @return
	 */
	public static boolean isFileExits(String filePath)
	{
		try {
			File file=new File(filePath);
			if(file.exists())
				return true;
		} catch (Exception e) {
			
		}
		return false;
	}
	
	/**
	 * 保存图片文件
	 * Author:zhuchenghua
	 * @param path
	 * @param fileName
	 * @param bmp
	 * @return
	 */
	public static boolean saveImageFile(String path,String fileName,Bitmap bmp)
	{
		try {
			File dir=new File(path);
			
			//目录不存时创建目录
			if(!dir.exists())
			{
				boolean flag=dir.mkdirs();
				if(flag==false)
					return false;
			}
			
			//未指定文件名时取当前毫秒作为文件名
			if(fileName==null||fileName.trim().length()==0)
				fileName=System.currentTimeMillis()+".jpg";
			FileOutputStream fos=new FileOutputStream(path+"/"+fileName);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
		
	}
	
	/**
	 * 保存文件
	 * Author:zhuchenghua
	 * @param in
	 * @param filePath
	 * @return
	 */
	public static boolean saveFile(InputStream in,String filePath)
	{
		FileOutputStream fos=null;
		try {
			File file=new File(filePath);
			fos=new FileOutputStream(file);
			byte[] buffer=new byte[4096];
			int len=-1;
			while((len=in.read(buffer))!=-1)
			{
				fos.write(buffer, 0, len);
				fos.flush();
			}
			return true;
		} catch (Exception e) {
		}
		return false;
	}
	
	/**
     * <br>Description: 将输入流保存为文件
     * <br>Author:caizp
     * <br>Date:2011-7-19下午02:37:25
     * @param in
     * @param fileName 文件名称
     */
	public static void saveStream2File(InputStream in, String fileName) {
		int size;
		byte[] buffer = new byte[1000];
		BufferedOutputStream bufferedOutputStream = null;
		try {
			bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File(fileName)));
			while ((size = in.read(buffer)) > -1) {
				bufferedOutputStream.write(buffer, 0, size);
			}
			bufferedOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 从路径中获取 文件名
	 * 
	 * @param path
	 * @param hasSuffix
	 *            是否包括后缀
	 * @return
	 */
	public static String getFileName(String path, boolean hasSuffix) {
		if(null==path || -1 ==path.lastIndexOf("/") || -1 == path.lastIndexOf("."))return null;
		if (!hasSuffix)
			return path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
		else
			return path.substring(path.lastIndexOf("/") + 1);
	}
	
	/**  
     * 重命名文件或文件夹  
     *  
     * @param resFilePath 源文件路径  
     * @param newFileName 重命名  
     * @return 操作成功标识  
     */  
    public static boolean renameFile(String resFilePath, String newFilePath) {  
        File resFile = new File(resFilePath);  
        File newFile = new File(newFilePath);  
        return resFile.renameTo(newFile);  
    }
    
    /**
     * <br>Description: 文件生成时间倒序排序比较器
     * <br>Author:caizp
     * <br>Date:2012-1-11下午05:34:35
     */
	public static class CompratorByLastModified implements Comparator {
		public int compare(Object o1, Object o2) {
			File file1 = (File) o1;
			File file2 = (File) o2;
			long diff = file1.lastModified() - file2.lastModified();
			if (diff > 0)
				return -1;
			else if (diff == 0)
				return 0;
			else
				return 1;
		}
	}
	
}