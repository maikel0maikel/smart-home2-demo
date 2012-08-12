package com.hs.smarthome.db;

import java.util.ArrayList;

import com.hs.smarthome.R;

public class ControlPanel {
	
	/**开关面板*/
	public static final int PANEL1 = 1;
	
	/**空调面板*/
	public static final int PANEL2 = 2;
	
	/**电视机面板*/
	public static final int PANEL3 = 3;
	
	/**播放器面板*/
	public static final int PANEL4 = 4;
	
	/**多媒体控制面板*/
	public static final int PANEL5 = 5;
	
	/**空调多功能面板*/
	public static final int PANEL6 = 6;
	
	/**1路开关面板*/
	public static final int PANEL7 = 7;
	
	/**2路开关面板*/
	public static final int PANEL8 = 8;
	
	public static int getImgResByPanelID(int panelID){
		int returnRes = 0;
		switch(panelID){
		case	PANEL1:
			returnRes = R.drawable.menu_list_equipement_kg;
			break;
		case	PANEL2:
			returnRes = R.drawable.menu_list_equipement_kt;
			break;
		case	PANEL3:
			returnRes = R.drawable.menu_list_equipement_tv;
			break;
		case	PANEL4:
			returnRes = R.drawable.menu_list_equipement_dvd;
			break;
		case	PANEL5:
			returnRes = R.drawable.menu_list_equipement_media;
			break;
		case	PANEL6:
			returnRes = R.drawable.menu_list_equipement_kg;
			break;
		case	PANEL7:
			returnRes = R.drawable.menu_list_equipement_kg;
			break;
		case	PANEL8:
			returnRes = R.drawable.menu_list_equipement_kg;
			break;
		default:
			returnRes = R.drawable.menu_list_equipement_kg;
			break;
		}
		
		return returnRes;
	}
	

	public static String getPanelNameByPanelID(int panelID){
		String returnRes = "";
		switch(panelID){
		case	PANEL1:
			returnRes = "开关面板";
			break;
		case	PANEL2:
			returnRes = "空调面板";
			break;
		case	PANEL3:
			returnRes = "电视机面板";
			break;
		case	PANEL4:
			returnRes = "播放器面板";
			break;
		case	PANEL5:
			returnRes = "多媒体控制面板";
			break;
		case	PANEL6:
			returnRes = "空调多功能面板";
			break;
		case	PANEL7:
			returnRes = "1路开关面板";
			break;
		case	PANEL8:
			returnRes = "2路开关面板";
			break;
		default:
			returnRes = "开关面板";
			break;
		}
		
		return returnRes;
	}
	
	public static ArrayList<String> getAllPanelName(){
		
		return null;
	}
}
