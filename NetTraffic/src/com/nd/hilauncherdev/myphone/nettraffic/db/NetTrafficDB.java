package com.nd.hilauncherdev.myphone.nettraffic.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.nd.hilauncherdev.datamodel.AbstractDataBase;

public class NetTrafficDB extends AbstractDataBase{

	private static final int VERSION = 1;
	private static final String DB_NAME = "data.db";
	
	//创建WIFI、GPRS流量排行表
	private static final String SQL_CREATE_TABLE_NETTRAFFIC_RANKING = ""
			+ "CREATE TABLE IF NOT EXISTS NetTrafficRankingDetail "
			+ "  ( "
			+ "     id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "     dev  INTEGER NOT NULL, "
			+ "     pkg     TEXT NOT NULL, "
			+ "     rx      DOUBLE, "
			+ "     tx      DOUBLE, "
			+ "     date    TEXT NOT NULL, "
			+ "     data_id INTEGER DEFAULT 0, "
			+ "     uid     INTEGER, "
			+ "     names   TEXT "
			+ "  )";
	
	public NetTrafficDB(Context c) {
		super(c, DB_NAME, VERSION);
	}

	@Override
	public void onDataBaseCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_TABLE_NETTRAFFIC_RANKING);
	}

	@Override
	public void onDataBaseUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
