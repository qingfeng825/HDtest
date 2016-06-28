package com.example.lijinming.hdtest.DataManage;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

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

    SimpleDateFormat formatter    =   new SimpleDateFormat("yy-MM-dd HH:mm:ss");
    Date curDate    =   new    Date(System.currentTimeMillis());//获取当前时间
    String    Str   =    formatter.format(curDate);//获取系统时间
    //需要保存当前调用对象的Context
    private Context context;
    public MyInternalStorage(Context context) {
        this.context = context;
    }
    /**
     * 存储数据到Sd card
     * @param inputText 为要保存的数据
     * */
    public void saveToSdcard(String inputText)  {
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            Log.e("main", "本设备有存储卡！");
            String basePath = getExternalStorageBasePath();
            BufferedWriter writer = null;
            File file = new File(basePath+"/"+Str+".txt");
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                writer = new BufferedWriter(new OutputStreamWriter(out));
                writer.write(inputText);
                Log.e("TAG", "write successful");
                //   Log.e("filename", String.valueOf(file));
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
    /**从Sdcard中读取数据
     * @return 读取的数据以String格式返回
     * */
    public String get()  {
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        try {
            File filename = new File(getExternalStorageBasePath()+"/"+Str+".txt");
            in = new FileInputStream(filename);
            Log.e("filename", String.valueOf(filename));
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                Log.e("TAG",line);
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
     * 以追加的方式在心电文件末尾添加数据
     * @param content 追加的内容
     */
    public void appendECG(String content) throws IOException {
        File filename = new File(getExternalStorageBasePath()+"/"+Str+"ECG.txt");
        String file =filename.toString();
        FileOutputStream fos = new FileOutputStream(file, true);
        String ad = content+System.lineSeparator();//每次加入一个数据之后都要在末尾加上换行符
        fos.write(ad.getBytes());
        fos.close();
    }
    /**
     * 以追加的方式在脉搏文件末尾添加数据
     * @param content 追加的内容
     */
    public void appendPusle(String content) throws IOException {
        File filename = new File(getExternalStorageBasePath()+"/"+Str+"Pulse.txt");
        String file =filename.toString();
        FileOutputStream fos = new FileOutputStream(file, true);
        String ad = content+System.lineSeparator();//每次加入一个数据之后都要在末尾加上换行符
        fos.write(ad.getBytes());
        fos.close();
    }
    /**
     * 以追加的方式在心音文件末尾添加数据
     * @param content 追加的内容
     */
    public void appendSound(String content) throws IOException {
        File filename = new File(getExternalStorageBasePath()+"/"+Str+"Sound.txt");
        String file =filename.toString();
        FileOutputStream fos = new FileOutputStream(file, true);
        String ad = content+System.lineSeparator();//每次加入一个数据之后都要在末尾加上换行符
        fos.write(ad.getBytes());
        fos.close();
    }
    /**
     * 删除文件
     * @param filename 文件名
     * @return 是否成功
     */
    public boolean delete(String filename) {
        File file =new File(filename);
        file.delete();
        return true;
    }
    /**
     * 获取SD card指定存储路径下的所有文件名
     * @return 文件名数组
     */
    public List<String> queryAllFile() {
        File file = new File(Environment.getExternalStorageDirectory()+"/MyDATA/");
        File [] files  =  file.listFiles();
        List <String> pathname = new ArrayList<>();
        for (File f:files){
            String dirName =f.toString();
            String cut = dirName.substring(27);//将目录名称去掉
            pathname.add(cut);

            //            pathname.add(f.toString());
        }
        return pathname;
    }
    public List<String> queryPulseFile() {
        File file = new File(Environment.getExternalStorageDirectory()+"/MyDATA/");
        File [] files  =  file.listFiles();
        List <String> pathname = new ArrayList<>();
            for (File f:files){
                String dirName =f.toString();
                String cut = dirName.substring(27);//将目录名称去掉
                int size = cut.indexOf("Pulse");//将脉搏的数据文件搜出来
                if (size != -1){
                    pathname.add(cut);
                }
            }
        if(pathname.isEmpty()){
            String notify = "There is no Pulse Data !";//没有找到脉搏数据
            pathname.add(notify);
        }
        return pathname;
    }
    public List<String> queryECGFile() {
        File file = new File(Environment.getExternalStorageDirectory()+"/MyDATA/");
        File [] files  =  file.listFiles();
        List <String> pathname = new ArrayList<>();

        for (File f:files){
            String dirName =f.toString();
            String cut = dirName.substring(27);//将目录名称去掉
            int size = cut.indexOf("ECG");//将心电的数据文件搜出来
            if (size != -1){
                pathname.add(cut);
            }
        }
        if(pathname.isEmpty()){
            String notify = "There is no ECG Data !";//没有找到脉搏数据
            pathname.add(notify);
        }
        return pathname;
    }
    public List<String> querySoundFile() {
        File file = new File(Environment.getExternalStorageDirectory()+"/MyDATA/");
        File [] files  =  file.listFiles();
        List <String> pathname = new ArrayList<>();

        for (File f:files){
            String dirName =f.toString();
            String cut = dirName.substring(27);//将目录名称去掉
            int size = cut.indexOf("Sound");//将心音的数据文件搜出来
            if (size != -1){
                pathname.add(cut);
            }
        }
        if(pathname.isEmpty()){
            String notify = "There is no Sound Data !";//没有找到脉搏数据
            pathname.add(notify);
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
     * 获取存储文件的根路径
     * @return
     */
    private String getExternalStorageBasePath(){
        if(isExternalStorageWriteable()){
            File file = new File(Environment.getExternalStorageDirectory()+"/MyDATA/");
            file.mkdirs();
            Log.e("getAbsolutePath",file.getAbsolutePath());
            return file.getAbsolutePath();
        }else {
            Toast.makeText(context, "SD card不可读写", Toast.LENGTH_SHORT).show();
        }
        return null;
    }
}
