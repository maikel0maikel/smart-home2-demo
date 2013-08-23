package com.nd.hilauncherdev.appmarket;

import java.io.Serializable;
import java.util.List;

public class AppMarketDetailItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 资源ID
	 */
	private long resId;
	
	/**
	 * 名称
	 */
	private String title;
	
	/**
	 * 描述文字
	 */
	private String description;
	
	/**
	 * 包名
	 */
	private String packageName;
	/**
	 * 作者
	 */
	private String auther;
	/**
	 * 版本名称
	 */
	private String versionName;
	/**
	 * 语言
	 */
	private String language;
	/**
	 * 大小
	 */
	private String size;
	/**
	 * 星级数
	 */
	private int star;
	/**
	 * 图标地址
	 */
	private String iconUrl;
	/**
	 * 分享人
	 */
	private String sharer;
	/**
	 * 预览图下载地址集合
	 */
	private List<String> previewImageUrlList;
	/**
	 * 软件下载地址
	 */
	private String downloadUrl;
	/**
	 * 更新时间
	 */
	private String updateTime;
	/**
	 * 下载次数
	 */
	private String downloadNumber;
	/**
	 * 版本号码
	 */
	private String versionCode;

	/**
	 * 安全信息
	 */
	private transient AppMarketAppSafeInfo safeInfo;
	
	public long getResId() {
		return resId;
	}

	public void setResId(long resId) {
		this.resId = resId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getAuther() {
		return auther;
	}

	public void setAuther(String auther) {
		this.auther = auther;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public int getStar() {
		return star;
	}

	public void setStar(int star) {
		this.star = star;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getSharer() {
		return sharer;
	}

	public void setSharer(String sharer) {
		this.sharer = sharer;
	}

	public List<String> getPreviewImageUrlList() {
		return previewImageUrlList;
	}

	public void setPreviewImageUrlList(List<String> previewImageUrlList) {
		this.previewImageUrlList = previewImageUrlList;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getDownloadNumber() {
		return downloadNumber;
	}

	public void setDownloadNumber(String downloadNumber) {
		this.downloadNumber = downloadNumber;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public AppMarketAppSafeInfo getSafeInfo() {
		return safeInfo;
	}

	public void setSafeInfo(AppMarketAppSafeInfo safeInfo) {
		this.safeInfo = safeInfo;
	}
	
	
}
