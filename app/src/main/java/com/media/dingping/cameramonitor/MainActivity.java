package com.media.dingping.cameramonitor;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.media.dingping.cameramonitor.bean.Cameras;
import com.media.dingping.cameramonitor.bean.StreamInfo;
import com.media.dingping.cameramonitor.presenter.MainPresenter;
import com.media.dingping.cameramonitor.view.IMainAcvityView;
import com.media.dingping.cameramonitor.view.ListViewAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lu on 2017/5/19.
 * 展示摄像头影响及控制
 */

public class MainActivity extends AppCompatActivity implements IMainAcvityView, NavigationView.OnNavigationItemSelectedListener, View.OnTouchListener {

    @BindView(R2.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R2.id.header_container)
    CoordinatorLayout headerContainer;
    @BindView(R2.id.Left_layout)
    RelativeLayout leftlayout;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.tv_currentNumber)
    TextView currentNumber;
    @BindView(R2.id.iv_first_tip)
    ImageView ivFirstTip;

    @BindView(R2.id.container_main)
    RelativeLayout containerMain;
    @BindView(R2.id.iv_null_data)
    ImageView ivNullData;
    @BindView(R2.id.bt_up)
    Button btUp;
    @BindView(R2.id.bt_down)
    Button btDown;
    @BindView(R2.id.bt_left)
    Button btLeft;
    @BindView(R2.id.bt_right)
    Button btRight;

    @BindView(R2.id.user_name)
    TextView username;
    @BindView(R2.id.listview)
    ListView mListView;
    @BindView(R2.id.bt_exitLogin)
    Button btExitLogin;
    @BindView(R2.id.swiperefreshlayout)
    SwipeRefreshLayout swiperefreshlayout;

    private MainPresenter mainPresenter;
    List<Cameras.DatasBean> databeans = null; //获取数据列表返回的终端信息集合
    int currentTerminalNumber = 0;
    private ListViewAdapter adapter;
    TextView tv_total;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mainPresenter = new MainPresenter(this);
        mainPresenter.isFirstTime();
        mainPresenter.getCameraList(getLoginUser());
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        initView();
    }

    private void initView() {
        btUp.setOnTouchListener(this);
        btDown.setOnTouchListener(this);
        btLeft.setOnTouchListener(this);
        btRight.setOnTouchListener(this);
        username.setText(getLoginUser());
        adapter = new ListViewAdapter(this);

        //listview添加头部
        LayoutInflater lif = getLayoutInflater();
        View headerView = lif.inflate(R.layout.navigation_header, null);
        tv_total = (TextView) headerView.findViewById(R.id.totals);
        mListView.addHeaderView(headerView);
        mListView = (ListView) leftlayout.findViewById(R.id.listview);
        mListView.setAdapter(adapter);
        //侧边listviewitem的点击
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("mainActivity", "--------点击了第" + position + "屏");
                currentTerminalNumber = position;
                if (currentTerminalNumber >= databeans.size())
                    currentTerminalNumber = 0;
                mDrawerLayout.closeDrawer(GravityCompat.START);
                refreshview(currentTerminalNumber);
            }
        });
        swiperefreshlayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE);
        swiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mainPresenter.getCameraList(getLoginUser());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getTitle().toString()) {
            case "下一屏":
                currentTerminalNumber = currentTerminalNumber + 1;
                if (currentTerminalNumber >= databeans.size())
                    currentTerminalNumber = 0;
                refreshview(currentTerminalNumber);
                Toast.makeText(this, "下一屏", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R2.id.iv_toobar_user)
    public void onViewClicked() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }
    @OnClick(R2.id.iv_first_tip)
    public void onViewClicked1() {
        if (ivFirstTip.getVisibility() == View.VISIBLE) {
            ivFirstTip.setVisibility(View.INVISIBLE);
        }
    }
    @OnClick(R2.id.bt_exitLogin)
    public void onViewClicked2() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


//    /*
//      点击头像打开抽屉
//       */
//    @OnClick({R2.id.iv_toobar_user, R2.id.iv_first_tip, R2.id.bt_exitLogin})
//    void onclick(View view) {
//        switch (view.getId()) {
//            case R2.id.iv_toobar_user:
//                mDrawerLayout.openDrawer(GravityCompat.START);
//                break;
//            case R2.id.iv_first_tip:
//                if (ivFirstTip.getVisibility() == View.VISIBLE) {
//                    ivFirstTip.setVisibility(View.INVISIBLE);
//                }
//                break;
////            侧边点击退出登录
//            case R2.id.bt_exitLogin:
//                Intent intent = new Intent(this, LoginActivity.class);
//                startActivity(intent);
//                break;
//        }
//    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void isFirstLogin() {
        //第一次登录做处理
        ivFirstTip.setVisibility(View.VISIBLE);

    }

    @Override
    public void showDataListSuccessd(Cameras cameras) {
        Log.i("MainActivity", "--------获取数据成功！");
        swiperefreshlayout.setRefreshing(false);
        //成功拿到数据后做处理
        if (cameras != null) {
            if (cameras.getDatas().size() > 0) {
                ivNullData.setVisibility(View.INVISIBLE);
                mainPresenter.getCameraPlayUrl(getLoginUser(), cameras.getDatas().get(0).getCaremaID(), cameras.getDatas().get(0).getPlayVerifyCode());
                Log.i("MainActivity", "-----获取到的数据集合大小" + cameras.getDatas().size());
                databeans = cameras.getDatas();
                adapter.setmList(databeans);
                tv_total.setText("共" + databeans.size() + "块屏");
                currentNumber.setText(databeans.get(currentTerminalNumber).getTerminal_ID());
                //播放视频
            } else {
                //数据为空处理.显示暂无数据图片
//                mListView.setEmptyView();
                ivNullData.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void showDataListFaild() {
        swiperefreshlayout.setRefreshing(false);
        Log.i("MainActivity", "--------获取数据失败！");
    }

    /**
     * @param streamInfo 需要保存StreamInfo
     *                   包含：直播流端口，控制流端口，跟中间件ip
     */
    @Override
    public void showgPlayUrlSuccessed(StreamInfo streamInfo) {
        Log.i("MainActivity", "--------获取直播流地址成功！开始播放" + streamInfo.toString());
        currentNumber.setText(databeans.get(0).getTerminal_ID());
    }

    @Override
    public void showgPlayUrlFaild() {

    }

    @Override
    public void refreshview(int currentTerminalNumber) {
        Log.i("MainActivity", "-------------更新界面");
        if (databeans != null && databeans.size() > 0) {
            mainPresenter.getCameraPlayUrl(getLoginUser(), databeans.get(currentTerminalNumber).getCaremaID(), databeans.get(currentTerminalNumber).getPlayVerifyCode());
            currentNumber.setText(databeans.get(currentTerminalNumber).getTerminal_ID());
        }
    }

    @Override
    public String getLoginUser() {
        return mainPresenter.getLoginUser();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (v.getId() == R.id.bt_up) {
                    mainPresenter.controlCamera(0, 0);
                } else if (v.getId() == R.id.bt_down) {
                    mainPresenter.controlCamera(1, 0);
                } else if (v.getId() == R.id.bt_left) {
                    mainPresenter.controlCamera(2, 0);
                } else if (v.getId() == R.id.bt_right) {
                    mainPresenter.controlCamera(3, 0);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (v.getId() == R.id.bt_up) {
                    mainPresenter.controlCamera(0, 0);
                } else if (v.getId() == R.id.bt_down) {
                    mainPresenter.controlCamera(1, 0);
                } else if (v.getId() == R.id.bt_left) {
                    mainPresenter.controlCamera(2, 0);
                } else if (v.getId() == R.id.bt_right) {
                    mainPresenter.controlCamera(3, 0);
                }
                break;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainPresenter.close();
    }


}



