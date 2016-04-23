package com.example.lijinming.hdtest.HeartMessage;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.ToggleButton;

import com.example.lijinming.hdtest.DataManage.MyInternalStorage;
import com.example.lijinming.hdtest.WaveShow.DataPlayBack.WaveView;
import com.example.lijinming.hdtest.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ThirdFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ThirdFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThirdFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    private MyInternalStorage mMyInternalStorage;
    private Spinner mSpinner;
    private ToggleButton mButton;
    String str;
    Boolean flag;



    private  WaveView mWaveView;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ThirdFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ThirdFragment newInstance(String param1, String param2) {
        ThirdFragment fragment = new ThirdFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ThirdFragment() {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_third, container, false);
        mWaveView = (WaveView)view.findViewById(R.id.surfaceView1);
        mMyInternalStorage = new MyInternalStorage(getContext());
        mButton = (ToggleButton)view.findViewById(R.id.start);
        mButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    flag = true;
                    mWaveView.ClearDraw();
                    GetDataThread mGetDataThread = new GetDataThread(str);
                    Thread mThread = new Thread(mGetDataThread);
                    mThread.start();
                }else {
                    flag = false;
                }
            }
        });

        mSpinner = (Spinner)view.findViewById(R.id.dataPlayBack);
        ArrayAdapter<String> fileArray = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1,mMyInternalStorage.queryAllFile());
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
    public class GetDataThread implements Runnable{
        String path;
        public GetDataThread(String pathname){
            path = pathname;

        }

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
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            try {
                while ((line = reader.readLine()) != null&flag) {
                    Message msg = WaveView.chartHandler.obtainMessage();
                    float fy = Float.parseFloat(line);
                    int iY = (int) (fy * 1000000);
                    String sY = String.valueOf(iY);
                    String cut = sY.substring(2);

                    Log.e("CUT", cut);

                    int iy = Integer.parseInt(cut);
                    int Y = iy * -1;
                    msg.what = Y;
                    WaveView.chartHandler.sendMessage(msg);
                    Log.e("TAG", String.valueOf(msg.what));
                    try {
                        Thread.sleep(20);
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

        }
    }


    @Override
    public void onStop() {
        super.onStop();

        mButton.setChecked(false);
        mWaveView.ClearDraw();

    }

    @Override
    public void onPause() {
        super.onPause();

        /*mButton.setChecked(false);
        mWaveView.ClearDraw();*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*mButton.setChecked(false);
        mWaveView.ClearDraw();*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        /*mButton.setChecked(false);
        mWaveView.ClearDraw();*/
    }
}
