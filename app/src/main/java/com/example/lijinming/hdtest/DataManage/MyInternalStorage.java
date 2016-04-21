package com.example.lijinming.hdtest.DataManage;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/3/24.
 */
public class MyInternalStorage {


    private MyDatabaseHelper dbHelper;
    SimpleDateFormat formatter    =   new SimpleDateFormat("yyyy��MM��dd��HH:mm:ss");
    Date curDate    =   new    Date(System.currentTimeMillis());//��ȡ��ǰʱ��
    String    Str   =    formatter.format(curDate);//��ȡϵͳʱ��

    private Context context;

    public MyInternalStorage(Context context) {
        this.context = context;
    }


    public void saveToSdcard(String inputText)  {
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            Log.e("main", "���豸�д洢����");
            String basePath = getExternalStorageBasePath();
            BufferedWriter writer = null;
            File file = new File(basePath+"/"+Str+".txt");
            /*Environment.getExternalStorageDirectory(),
                    Str+".txt"*/
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                writer = new BufferedWriter(new OutputStreamWriter(out));
                Log.e("TAG","write successful");
                writer.write(inputText);
                Log.e("TAG", "write successful123");
                Log.e("filename", String.valueOf(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public String get()  {
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        try {
            //            String basePath = getExternalStorageBasePath();
            //            String filename =getExternalStorageBasePath()+"/"+Str+".txt";
            File filename = new File(getExternalStorageBasePath()+"/"+Str+".txt");
            in = new FileInputStream(filename);
            Log.e("filename", String.valueOf(filename));

            //            in = openFileInput(filename);

            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content.toString();
    }
    /**
     * ��׷�ӵķ�ʽ���ļ���ĩβ�������
     *
     * @param content ׷�ӵ�����
     */
    public void append(String content) throws IOException {
        String file = getExternalStorageBasePath()+"/"+Str+".txt";
       // File file = new File(basePath+"/"+Str+".txt");
        FileOutputStream fos =new FileOutputStream(file,true);

       /* FileOutputStream fos = context.openFileOutput(Str,
                Context.MODE_APPEND);*/
        fos.write(content.getBytes());
        fos.close();
    }


    /**
     * ɾ���ļ�
     * @param filename �ļ���
     * @return �Ƿ�ɹ�
     */
    public boolean delete(String filename) {
        return context.deleteFile(filename);
    }
    /**
     * ��ȡSD cardָ���洢·���µ������ļ���
     * @return �ļ�������
     */
    public List<String> queryAllFile() {
        File file = new File(Environment.getExternalStorageDirectory()+"/MyDATA/");
        File [] files  =  file.listFiles();
        List <String> pathname = new ArrayList<>();
        for (File f:files){
            pathname.add(f.toString());
        }
        return pathname;
    }


    private boolean isExternalStorageWriteable(){
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            return true;
        }

        return false;
    }

    /**
     * ��ȡ�洢�ļ��ĸ�·��
     * @return
     */
    private String getExternalStorageBasePath(){
        if(isExternalStorageWriteable()){
            File file = new File(Environment.getExternalStorageDirectory()+"/MyDATA/");
            file.mkdirs();
            Log.e("getAbsolutePath", file.getAbsolutePath());
            return file.getAbsolutePath();

        }
        return null;
    }
}
