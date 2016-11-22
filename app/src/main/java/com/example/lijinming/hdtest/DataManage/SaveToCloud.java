package com.example.lijinming.hdtest.dataManage;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.sina.cloudstorage.auth.AWSCredentials;
import com.sina.cloudstorage.auth.BasicAWSCredentials;
import com.sina.cloudstorage.services.scs.SCS;
import com.sina.cloudstorage.services.scs.SCSClient;

import java.io.File;

/**
 * Created by Administrator on 2016/3/27.
 */
public class SaveToCloud {
    //需要保存当前调用对象的Context
    private Context context;
    private static final String TAG = "SaveToCloud";

    public SaveToCloud(Context context) {
        this.context = context;
    }

    String accessKey = "13vziz5ijUx06F8DuDFC";//新浪云存储Key
    String accessSecret = "0cf0f43fc8567fd47e9e42fb331ee768d56b4730";//新浪云存储Secret

    AWSCredentials credentials = new BasicAWSCredentials(accessKey,accessSecret);
    SCS conn = new SCSClient(credentials);
    /**
     *  上传Object到新浪云存储端
     *  @param pathname 文件名
     *  @return 是否成功
     *  */
    public boolean UpLoad(String pathname){
        String localFilePath = pathname;
        if(localFilePath!=null){
            File localFile = new File(localFilePath);
            Log.e(TAG, "进入UpLoad");
            if(localFile.exists()){
                Log.e(TAG,"本地文件存在");
                conn.putObject("qingfeng",pathname ,localFile);
              //  Toast.makeText(context.this, "上传成功", Toast.LENGTH_SHORT).show();
                Log.e(TAG,"UpLoad success");
            }else{
                Log.e(TAG,"上传失败");
               // Toast.makeText(context,"本地文件不存在，请先点击下载文件！",Toast.LENGTH_SHORT).show();

            }
        }else{
            Toast.makeText(context,"请检查是否有可用的externalStorage！",Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
