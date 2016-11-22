package com.example.lijinming.hdtest.dataManage;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lijinming.hdtest.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DataManageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DataManageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DataManageFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private MyInternalStorage mMyInternalStorage;
    private SaveToCloud mSaveToCloud = new SaveToCloud(getActivity());
    private EditText writeText;
    private TextView readText;
    private ListView querydata;
    private Button writebutton,readbutton,appendbutton,querybutton;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DataManageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment DataManageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DataManageFragment newInstance(String param1) {
        DataManageFragment fragment = new DataManageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_data_manage, container, false);
       /* writebutton = (Button)view.findViewById(R.id.writebutton);
        writebutton.setOnClickListener(this);
        writeText = (EditText)view.findViewById(R.id.edittext);

        readbutton = (Button)view.findViewById(R.id.readbutton);
        readbutton.setOnClickListener(this);
        readText = (TextView)view.findViewById(R.id.readText);

        appendbutton = (Button)view.findViewById(R.id.append);
        appendbutton.setOnClickListener(this);*/

        querybutton = (Button)view.findViewById(R.id.query);
        querybutton.setOnClickListener(this);

        querydata = (ListView)view.findViewById(R.id.list);

        mMyInternalStorage =new MyInternalStorage(getContext());
        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.query:
                ArrayAdapter<String> fileArray = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_list_item_1,mMyInternalStorage.queryAllFile());
                querydata.setAdapter(fileArray);
                querydata.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String pathname =parent.getItemAtPosition(position).toString();//获取Spinner列表项的名称
                       String str = Environment.getExternalStorageDirectory()+"/MyDATA/"+pathname;//将文件目录名称补全
//                        String pathname = mMyInternalStorage.queryAllFile().get(position);
                        showDialog(getContext(),str);
//                        Toast.makeText(getContext(), pathname, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }
    /**
     * 给Dialog设置了三个按钮，取消按钮什么也不做
     * 删除按钮功能为删除点击的文件
     * 上传按钮功能为上传点击的文件到新浪云存储
     * */
    private void showDialog(Context context , final String pathname1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //        builder.setIcon(R.drawable.icon);
        builder.setTitle("数据管理");
        builder.setMessage("选择删除、上传或者取消返回");
        builder.setPositiveButton("取消",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(getContext(), "取消成功", Toast.LENGTH_SHORT).show();
                    }
                });
        builder.setNegativeButton("删除",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mMyInternalStorage.delete(pathname1);
                        Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
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


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
