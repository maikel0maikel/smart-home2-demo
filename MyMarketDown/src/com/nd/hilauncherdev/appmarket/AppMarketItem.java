package com.nd.hilauncherdev.appmarket;

import java.io.File;
import java.io.Serializable;

import android.text.TextUtils;

import com.nd.hilauncherdev.framework.view.commonsliding.datamodel.ICommonDataItem;
import com.nd.hilauncherdev.webconnect.downloadmanage.util.DownloadState;

public class AppMarketItem implements ICommonDataItem,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4105688150370660275L;
	private long id;
	
	/**
	 * 下载记录的唯一标识
	 */
	private String key;
	
	private String title;
	private String packageName;
	/**
	 * 软件下载地址
	 */
	private String apkUrl;
	/**
	 * 图标地址
	 */
	private String iconUrl;
	/**
	 * 软件大小，格式如：xx.xxMB
	 */
	private String size;
	/**
	 * 软件版本名称
	 */
	private String versionName;
	/**
	 * 软件版本号码
	 */
	private String versionCode;
	/**
	 * 详情页的地址
	 */
	private String detailUrl;
	
	/**
	 * 软件简介
	 */
	private String desc;
	
	/**
	 * 图标缓存的文件路径
	 */
	private String iconFilePath;
	/**
	 * 软件保存的文件路径
	 */
	private String apkFilePath;
	/**
	 * 软件保存的文件名
	 */
	private String apkFileName;
	
	/**
	 * 下载次数
	 */
	private String downloadNumber;
	
	private int pos;
	
	/**
	 * 客户端类别：一键装机，一键玩机
	 */
	private int clientType;
	
	/**
	 * 下载成功后反馈地址
	 */
	private String feedbackUrl;
	
	/**
	 * 星级
	 */
	private int star;
	
	/**
	 * 下载状态
	 */
	private int downloadState=DownloadState.STATE_NONE;
	
	/**
	 * 下载进度
	 */
	private int downloadProccess;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	
	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getApkUrl() {
		return apkUrl;
	}

	public void setApkUrl(String apkUrl) {
		this.apkUrl = apkUrl;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	
	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public String getDetailUrl() {
		return detailUrl;
	}

	public void setDetailUrl(String detailUrl) {
		this.detailUrl = detailUrl;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public String getIconFilePath() {
		return iconFilePath;
	}

	public void setIconFilePath(String iconFilePath) {
		this.iconFilePath = iconFilePath;
	}

	public String getApkFilePath() {
		return apkFilePath;
	}

	public void setApkFilePath(String apkFilePath) {
		this.apkFilePath = apkFilePath;
	}
	
	

	public String getApkFileName() {
		return apkFileName;
	}

	public void setApkFileName(String apkFileName) {
		this.apkFileName = apkFileName;
	}
	
	



	public int getClientType() {
		return clientType;
	}

	public void setClientType(int clientType) {
		this.clientType = clientType;
	}
	
	

	public String getFeedbackUrl() {
		return feedbackUrl;
	}

	public void setFeedbackUrl(String feedbackUrl) {
		this.feedbackUrl = feedbackUrl;
	}
	
	

	public String getDownloadNumber() {
		return downloadNumber;
	}

	public void setDownloadNumber(String downloadNumber) {
		this.downloadNumber = downloadNumber;
	}
	
	

	public int getStar() {
		return star;
	}

	public void setStar(int star) {
		this.star = star;
	}

	
	public int getDownloadState() {
		return downloadState;
	}

	public void setDownloadState(int downloadState) {
		this.downloadState = downloadState;
	}

	public int getDownloadProccess() {
		return downloadProccess;
	}

	public void setDownloadProccess(int downloadProccess) {
		this.downloadProccess = downloadProccess;
	}

	
	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		if(!TextUtils.isEmpty(key))
			return key;
		
		key=packageName+versionCode;
		if(TextUtils.isEmpty(key))
			key=apkUrl;
		
		return key;
	}

	@Override
	public int getPosition() {
		return pos;
	}

	@Override
	public boolean isFolder() {
		return false;
	}

	@Override
	public void setPosition(int position) {
		pos=position;
	}

	@Override
	public boolean equals(Object o) {
		
		if(!(o instanceof AppMarketItem))
			return false;
		
		try {
			
			AppMarketItem target=(AppMarketItem)o;
			if(target.getKey()!=null && target.getKey().equals(this.getKey()))
				return true;
			else if(target.getApkFilePath()!=null && this.getApkFilePath()!=null){
				String targetFilePath=new File(target.getApkFilePath()).getAbsolutePath();
				String srcFilePath=new File(this.getApkFilePath()).getAbsolutePath();
				if(targetFilePath.equals(srcFilePath))
					return true;
				else
					return false;
			}else
				return false;
			
		} catch (Exception e) {
		}
		
		return super.equals(o);
	}

}
