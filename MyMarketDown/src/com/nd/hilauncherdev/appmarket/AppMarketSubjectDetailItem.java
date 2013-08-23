package com.nd.hilauncherdev.appmarket;

import java.io.Serializable;
import java.util.List;

/**
 * 专题详情数据项
 * @author zhuchenghua
 *
 */
public class AppMarketSubjectDetailItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7539870014086818549L;

	/**
	 * 专题名称
	 */
	private String subjectTitle;
	
	/**
	 * 主题的网页网址
	 */
	private String subjectWebUrl;
	
	/**
	 * 是否只有一个应用
	 */
	private boolean isSingleApp=false;
	
	/**
	 * 专题中包含的软件列表
	 */
	private List<AppMarketItem> appList;

	
	/**
	 * @return the subjectWebUrl
	 */
	public String getSubjectWebUrl() {
		return subjectWebUrl;
	}


	/**
	 * @param subjectWebUrl the subjectWebUrl to set
	 */
	public void setSubjectWebUrl(String subjectWebUrl) {
		this.subjectWebUrl = subjectWebUrl;
	}


	/**
	 * @return the isSingleApp
	 */
	public boolean isSingleApp() {
		return isSingleApp;
	}


	/**
	 * @param isSingleApp the isSingleApp to set
	 */
	public void setSingleApp(boolean isSingleApp) {
		this.isSingleApp = isSingleApp;
	}


	/**
	 * @return the itemList
	 */
	public List<AppMarketItem> getAppList() {
		return appList;
	}


	/**
	 * @param itemList the itemList to set
	 */
	public void setAppList(List<AppMarketItem> appList) {
		this.appList = appList;
	}



	/**
	 * @return the subjectTitle
	 */
	public String getSubjectTitle() {
		return subjectTitle;
	}


	/**
	 * @param subjectTitle the subjectTitle to set
	 */
	public void setSubjectTitle(String subjectTitle) {
		this.subjectTitle = subjectTitle;
	}


	@Override
	public boolean equals(Object o) {
		
		if(!(o instanceof AppMarketSubjectDetailItem))
			return false;
		
		if(o==this)
			return true;
		
		AppMarketSubjectDetailItem target=(AppMarketSubjectDetailItem)o;
		if(target.subjectWebUrl!=null && target.subjectWebUrl.equals(subjectWebUrl))
			return true;
		else
			return false;
	}
	
	
}
