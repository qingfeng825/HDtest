package com.example.lijinming.hdtest.HeartMessage;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.lijinming.hdtest.DataManage.MyInternalStorage;
import com.example.lijinming.hdtest.R;
import com.example.lijinming.hdtest.WaveShow.DataPlayBack.WaveViewSound;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SecondFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SecondFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SecondFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;private String mParam2;
    private OnFragmentInteractionListener mListener;
    private MyInternalStorage mMyInternalStorage;
    private Spinner mSpinner;private ToggleButton mButton;
    String str;Boolean flag;private WaveViewSound mWaveViewSound;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ThirdFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SecondFragment newInstance(String param1, String param2) {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public SecondFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    /**
     * 主要功能是选择现有数据文件，然后开启线程读取数据*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        mWaveViewSound = (WaveViewSound) view.findViewById(R.id.surfaceViewSound);
        mMyInternalStorage = new MyInternalStorage(getContext());
        mButton = (ToggleButton)view.findViewById(R.id.soundStart);
        mButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    flag = true;
                    mWaveViewSound.ClearDraw();
                    GetDataThread mGetDataThread = new GetDataThread(str);
                    Thread mThread = new Thread(mGetDataThread);
                    mThread.start();
                }else {
                    flag = false;
                }
            }
        });
        mSpinner = (Spinner)view.findViewById(R.id.soundPlayBack);
        ArrayAdapter<String> fileArray = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1,mMyInternalStorage.querySoundFile());
        mSpinner.setAdapter(fileArray);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                str = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        return view;
    }
    public class GetDataThread implements Runnable{
        String path;
        public GetDataThread(String pathname){
            path = pathname;
        }
        /**
         * 将读取的数据进行处理然后发送给波形显示模块*/
        @Override
        public void run() {
            FileInputStream in = null;
            BufferedReader reader = null;
            File filename = new File(path);
            /*getExternalStorageBasePath() + "/" + "123.txt"*/
            try {
                in = new FileInputStream(filename);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Log.e("filename", String.valueOf(filename));
            if(in!=null){
                reader = new BufferedReader(new InputStreamReader(in));
                String line;
                try {
                    while ((line = reader.readLine()) != null&flag) {
                        Message msg = WaveViewSound.chartHandler.obtainMessage();
                        int Y = Integer.parseInt(line);
                        msg.what = Y;
                        WaveViewSound.chartHandler.sendMessage(msg);
                        Log.e("TAG", String.valueOf(msg.what));
                        try {
                            Thread.sleep(20);//设置频率，20毫秒读取一次
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }else {
                //如果没有相应的生理信号数据则提示用户：请先接收数据
                Looper.prepare();
                Toast.makeText(getContext(),"请先接收数据",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
    @Override
    public void onStop() {
        super.onStop();
        mButton.setChecked(false);
        mWaveViewSound.ClearDraw();
    }
    @Override
    public void onPause() {
        super.onPause();
        super.onStop();
        mButton.setChecked(false);
        mWaveViewSound.ClearDraw();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        super.onStop();
        mButton.setChecked(false);
        mWaveViewSound.ClearDraw();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        super.onStop();
        mButton.setChecked(false);
        mWaveViewSound.ClearDraw();
    }
}
