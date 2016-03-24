package com.example.lijinming.hdtest.navigation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.lijinming.hdtest.HealthProject.HealthFragment;
import com.example.lijinming.hdtest.HeartMessage.FirstFragment;
import com.example.lijinming.hdtest.HeartMessage.ForthFragment;
import com.example.lijinming.hdtest.HeartMessage.FragmentManage;
import com.example.lijinming.hdtest.HeartMessage.SecondFragment;
import com.example.lijinming.hdtest.HeartMessage.ThirdFragment;
import com.example.lijinming.hdtest.R;
import com.example.lijinming.hdtest.bleManage.BLEActivity;

import br.liveo.interfaces.OnItemClickListener;
import br.liveo.model.HelpLiveo;
import br.liveo.model.Navigation;
import br.liveo.navigationliveo.NavigationLiveo;


public class NavigationActivity extends NavigationLiveo implements
        FragmentManage.OnFragmentInteractionListener,
        HealthFragment.OnFragmentInteractionListener,
        FirstFragment.OnFragmentInteractionListener ,
        SecondFragment.OnFragmentInteractionListener,
        ThirdFragment.OnFragmentInteractionListener,
        ForthFragment.OnFragmentInteractionListener,
        OnItemClickListener {
    private final static String TAG = "NavigationActivity";
    private HelpLiveo mHelpLiveo;
    private final static int REQUEST_ENABLE_BT = 2001;


    @Override
    public void onInt(Bundle savedInstanceState) {
        // User Information
        Intent mIntent = getIntent();
        String username = mIntent.getStringExtra("username");

        this.userName.setText("account:"+username);
        this.userEmail.setText("   ");
        this.userPhoto.setImageResource(R.drawable.background);
        this.userBackground.setImageResource(R.drawable.ic_user_background_first);

        // Creating items navigation
        mHelpLiveo = new HelpLiveo();
        mHelpLiveo.add(getString(R.string.inbox));
        // mHelpLiveo.addSubHeader(getString(R.string.categories)); //Item subHeader
        mHelpLiveo.add(getString(R.string.starred));
        mHelpLiveo.add(getString(R.string.sent_mail));
        // mHelpLiveo.add(getString(R.string.drafts), R.mipmap.ic_drafts_black_24dp);
        mHelpLiveo.addSeparator(); // Item separator
        // mHelpLiveo.add(getString(R.string.trash), R.mipmap.ic_delete_black_24dp);
        //mHelpLiveo.add(getString(R.string.spam), R.mipmap.ic_report_black_24dp, 120);

        with(this, Navigation.THEME_DARK);//. add theme dark
        //with(this, Navigation.THEME_LIGHT). add theme light
        with(this) // default theme is dark
                .startingPosition(2) //Starting position in the list
                .addAllHelpItem(mHelpLiveo.getHelp())
                .footerItem(R.string.settings, R.mipmap.ic_launcher)
//                .footerSecondItem(R.string.BLEmanage,R.drawable.bluetooth)
                .setOnClickUser(onClickPhoto)
                .setOnClickFooter(onClickFooter)
//                .setOnClickFooterSecond(onClickFooter)
                .build();
        int position = this.getCurrentPosition();
        this.setElevationToolBar(position != 2 ? 15 : 0);

    }

    private View.OnClickListener onClickPhoto = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            closeDrawer();
            Toast.makeText(getBaseContext(), "this is Photo", Toast.LENGTH_SHORT).show();
        }
    };
    private View.OnClickListener onClickFooter = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        if(v.getId() ==R.id.footerDrawer) {
            closeDrawer();

            Toast.makeText(getBaseContext(), "this is 1", Toast.LENGTH_SHORT).show();
          }else {
            closeDrawer();
              /*Intent intent = new Intent(getApplicationContext(), BLEActivity.class);
                startActivity(intent);*/
            Toast.makeText(getBaseContext(), "this is 2", Toast.LENGTH_SHORT).show();
        }
        }

    };

    @Override
    public void onItemClick(int position) {
        Fragment mFragment;
        FragmentManager mFragmentManager = getSupportFragmentManager();
        switch (position){
            case 0:
                mFragment = FragmentManage.newInstance(mHelpLiveo.get(position).getName());
                break;
            case 1:
                mFragment = HealthFragment.newInstance(mHelpLiveo.get(position).getName());
                break;

            default:
                mFragment = FragmentManage.newInstance(mHelpLiveo.get(position).getName());
                break;
        }
        if (mFragment != null){
            mFragmentManager.beginTransaction().replace(R.id.container, mFragment).commit();
        }
        setElevationToolBar(position != 2 ? 15 : 0);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Toast.makeText(getBaseContext(), R.string.setting, Toast.LENGTH_SHORT).show();
                break;
            case R.id.search:
                Intent intent = new Intent(getApplicationContext(), BLEActivity.class);
                startActivity(intent);
                Log.e(TAG, "点击蓝牙");
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
