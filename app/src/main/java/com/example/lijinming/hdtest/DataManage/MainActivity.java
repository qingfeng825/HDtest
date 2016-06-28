/*
package com.example.lijinming.hdtest.DataManage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lijinming.hdtest.R;

import java.io.IOException;

public class MainActivity extends Activity implements View.OnClickListener{
    private MyInternalStorage mMyInternalStorage;
    private SaveToCloud mSaveToCloud;

     private EditText writeText;
     private TextView readText;
     private ListView querydata;
     private Button writebutton,readbutton,appendbutton,querybutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_data_manage);
        init();
    }
    */
/**
     * 进行初始化*//*

    public void init(){
        writebutton = (Button)findViewById(R.id.writebutton);
        writebutton.setOnClickListener(this);
        writeText = (EditText)findViewById(R.id.edittext);

        readbutton = (Button)findViewById(R.id.readbutton);
        readbutton.setOnClickListener(this);
        readText = (TextView)findViewById(R.id.readText);

        appendbutton = (Button)findViewById(R.id.a

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){ppend);
        appendbutton.setOnClickListener(this);

        querybutton = (Button)findViewById(R.id.query);
        querybutton.setOnClickListener(this);

        querydata = (ListView)findViewById(R.id.list);

        mMyInternalStorage =new MyInternalStorage(this);

            case R.id.writebutton:
                String write = writeText.getText().toString();
                mMyInternalStorage.saveToSdcard(write);
                Toast.makeText(MainActivity.this,"write successful",Toast.LENGTH_SHORT).show();
                break;
            case R.id.readbutton:
                String read = null;
                read = mMyInternalStorage.get();
                if (!TextUtils.isEmpty(read)) {
                    readText.setText(read);
                    Toast.makeText(MainActivity.this,"read successful",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.append:
                String add = writeText.getText().toString();
                try {
                    mMyInternalStorage.append(add);
                    Toast.makeText(MainActivity.this,"append successful",Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.query:
                ArrayAdapter<String> fileArray = new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1,mMyInternalStorage.queryAllFile());
                querydata.setAdapter(fileArray);
                querydata.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String pathname = mMyInternalStorage.queryAllFile().get(position);
                        showDialog(MainActivity.this,pathname);
                        Toast.makeText(MainActivity.this, pathname, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }
    */
/**
     * 给Dialog设置了三个按钮，取消按钮什么也不做
     * 删除按钮功能为删除点击的文件
     * 上传按钮功能为上传点击的文件到新浪云存储
     * *//*

    private void showDialog(Context context , final String pathname1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //        builder.setIcon(R.drawable.icon);
        builder.setTitle("数据管理");
        builder.setMessage("选择删除、上传或者取消返回");
        builder.setPositiveButton("取消",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(MainActivity.this, "取消成功", Toast.LENGTH_SHORT).show();
                    }
                });
        builder.setNegativeButton("删除",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mMyInternalStorage.delete(pathname1);
                        Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    }
                });
        builder.setNeutralButton("上传",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Thread thread = new Thread(new Runnable() {//开辟一个线程进行上传操作
                            @Override
                            public void run() {
                                Looper.prepare();
                                mSaveToCloud.UpLoad(pathname1);
                                Looper.loop();
                            }
                        });
                        thread.start();
                    }
                });

        builder.show();
    }

}
*/
