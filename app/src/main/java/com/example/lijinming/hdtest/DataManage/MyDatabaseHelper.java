package com.example.lijinming.hdtest.DataManage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/3/23.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "MyDatabaseHelper";
    SimpleDateFormat    formatter    =   new SimpleDateFormat("yyyy年MM月dd日    HH:mm:ss     ");
    Date curDate    =   new    Date(System.currentTimeMillis());//获取当前时间
    String    str   =    formatter.format(curDate);

    public   String CREATE_HealthyData = "create table "+str+"("+"id integer primary key autoincrement, "
                                            + "Ecg_tata real)";
    private Context mContext;
    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_HealthyData);
        Log.e(TAG,"表格创建成功");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exist HeathyData");
        onCreate(db);

    }
}
