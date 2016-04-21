package com.example.lijinming.hdtest.HeartMessage;

import android.app.Activity;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lijinming.hdtest.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentManage.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentManage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentManage extends Fragment /*implements FirstFragment.OnFragmentInteractionListener ,
SecondFragment.OnFragmentInteractionListener,ThirdFragment.OnFragmentInteractionListener,
ForthFragment.OnFragmentInteractionListener*/{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private Resources resources;
    private ViewPager mPager;
    private ArrayList<Fragment> fragmentsList;
    private ImageView ivBottomLine;
    private TextView tvTabNew, tvTabHot, monthHot, dayHot;

    private int currIndex = 0;
    private int bottomLineWidth;
    private int offset = 0;
    private int position_one;
    private int position_two;
    private int position_three;
    Fragment home1;
    Fragment home2;
    Fragment home3;
    Fragment home4;

    private MyPagerAdapter pageAdapter;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment FragmentManage.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentManage newInstance(String param1/*, String param2*/) {
        FragmentManage fragment = new FragmentManage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        // args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentManage() {
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
        View view = inflater.inflate(R.layout.fragment_manage, container, false);
        ViewPager vp = (ViewPager) view.findViewById(R.id.viewPaper);
        resources = getResources();
        InitWidth(view);
        InitTextView(view);
        InitViewPager(view);
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

   /* @Override
    public void onFragmentInteraction(Uri uri) {

    }*/

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
    private void InitTextView(View parentView) {
        tvTabNew = (TextView) parentView.findViewById(R.id.tv_tab_1);
        tvTabHot = (TextView) parentView.findViewById(R.id.tv_tab_2);
        monthHot = (TextView) parentView.findViewById(R.id.tv_tab_3);
//        dayHot = (TextView) parentView.findViewById(R.id.tv_tab_4);
        tvTabNew.setOnClickListener(new MyOnClickListener(0));
        tvTabHot.setOnClickListener(new MyOnClickListener(1));
        monthHot.setOnClickListener(new MyOnClickListener(2));
//        dayHot.setOnClickListener(new MyOnClickListener(3));

    }

    private void InitViewPager(View parentView) {



        mPager = (ViewPager) parentView.findViewById(R.id.viewPaper);



        fragmentsList = new ArrayList<Fragment>();

        home1 = new FirstFragment();
        home2 = new SecondFragment();
        home3 = new ThirdFragment();
//        home4 = new ForthFragment();

        fragmentsList.add(home1);
        fragmentsList.add(home2);
        fragmentsList.add(home3);
//        fragmentsList.add(home4);


        mPager.setAdapter(new MyPagerAdapter(getChildFragmentManager(), fragmentsList));
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
        mPager.setCurrentItem(0);

    }

    private void InitWidth(View parentView) {
        ivBottomLine = (ImageView) parentView.findViewById(R.id.iv_bottom_line);
        bottomLineWidth = ivBottomLine.getLayoutParams().width;
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        offset = (int) ((screenW / 3.0 - bottomLineWidth) / 2);
        //int avg = (int) (screenW / num);
        position_one = (int) (screenW / 3.0);
        position_two = position_one * 2;
        position_three = position_one * 3;
    }
    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            mPager.setCurrentItem(index);
        }
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {


        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            switch (arg0) {
                case 0:
                    if (currIndex == 1) {
                        animation = new TranslateAnimation(position_one, 0, 0,0);
                        //tvTabHot.setTextColor(resources.getColor(R.color.lightwhite));
                    } else if (currIndex == 2) {
                        animation = new TranslateAnimation(position_two, 0, 0, 0);
                        // monthHot.setTextColor(resources.getColor(R.color.lightwhite));
                    }/* else if (currIndex == 3) {
                        animation = new TranslateAnimation(position_three, 0, 0, 0);
                        // dayHot.setTextColor(resources.getColor(R.color.lightwhite));
                    }*/
                    //tvTabNew.setTextColor(resources.getColor(R.color.white));
                    break;

                case 1:
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(0, position_one, 0, 0);
                        // tvTabNew.setTextColor(resources.getColor(R.color.lightwhite));
                    } else if (currIndex == 2) {
                        animation = new TranslateAnimation(position_two, position_one, 0, 0);
                        // monthHot.setTextColor(resources.getColor(R.color.lightwhite));
                    } /*else if (currIndex == 3) {
                        animation = new TranslateAnimation(position_three, position_one, 0, 0);
                        // dayHot.setTextColor(resources.getColor(R.color.lightwhite));
                    }*/
                    //tvTabHot.setTextColor(resources.getColor(R.color.white));
                    break;
                case 2:
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(0, position_two, 0, 0);
                        // tvTabNew.setTextColor(resources.getColor(R.color.lightwhite));
                    } else if (currIndex == 1) {
                        animation = new TranslateAnimation(position_one, position_two, 0, 0);
                        // tvTabHot.setTextColor(resources.getColor(R.color.lightwhite));
                    } /*else if (currIndex == 3) {
                        animation = new TranslateAnimation(position_three, position_two, 0, 0);
                        //                            dayHot.setTextColor(resources.getColor(R.color.lightwhite));
                    }*/
                    // monthHot.setTextColor(resources.getColor(R.color.white));
                    break;
              /*  case 3:
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(0, position_three, 0, 0);
                        //tvTabNew.setTextColor(resources.getColor(R.color.lightwhite));
                    } else if (currIndex == 1) {
                        animation = new TranslateAnimation(position_one, position_three, 0, 0);
                        //  tvTabHot.setTextColor(resources.getColor(R.color.lightwhite));
                    } else if (currIndex == 2) {
                        animation = new TranslateAnimation(position_two, position_three, 0, 0);
                        //                            monthHot.setTextColor(resources.getColor(R.color.lightwhite));
                    }
                    //dayHot.setTextColor(resources.getColor(R.color.white));
                    break;*/
            }

            currIndex = arg0;
            animation.setFillAfter(true);
            animation.setDuration(300);
            ivBottomLine.startAnimation(animation);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

}
