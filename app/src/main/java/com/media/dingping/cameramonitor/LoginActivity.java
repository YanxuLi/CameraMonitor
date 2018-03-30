package com.media.dingping.cameramonitor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.media.dingping.cameramonitor.bean.UserInfo;
import com.media.dingping.cameramonitor.listener.MyTexWatch;
import com.media.dingping.cameramonitor.main.MainActivity2;
import com.media.dingping.cameramonitor.presenter.LoginPresenter;
import com.media.dingping.cameramonitor.utils.Patterns;
import com.media.dingping.cameramonitor.view.ILoginView;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 登录
 */

public class LoginActivity extends AppCompatActivity implements ILoginView {
    @BindView(R2.id.et_username)
    EditText etUsername;
    @BindView(R2.id.username)
    TextInputLayout username;
    @BindView(R2.id.et_password)
    EditText etPassword;
    @BindView(R2.id.password)
    TextInputLayout password;
    @BindView(R2.id.login)
    Button login;
    @BindView(R2.id.server_ip)
    EditText serverIp;
    @BindView(R2.id.server_port)
    EditText serverPort;
    @BindView(R2.id.login_drawer)
    DrawerLayout loginDrawer;
    @BindView(R2.id.push_server_ip)
    EditText pushServerIp;
    @BindView(R2.id.push_server_port)
    EditText pushServerPort;


    private LoginPresenter mLoginPresenter;
    private ProgressDialog mDialog;
    private Pattern mPhone = Pattern.compile(Patterns.phone);
    private MyTexWatch mTextWatch = new MyTexWatch() {
        @Override
        public void afterTextChanged(Editable editable) {
            if (isEmpty()) {
                login.setTextColor(getResources().getColor(R.color.login_text_enable));
                login.setEnabled(true);
            } else {
                login.setTextColor(getResources().getColor(R.color.login_text_noenable));
                login.setEnabled(false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int widthPixels = getResources().getDisplayMetrics().widthPixels;
        int heightPixels = getResources().getDisplayMetrics().heightPixels;
        Log.i("LoginActivity", widthPixels + "");
        Log.i("LoginActivity", heightPixels + "");
//        onForceUpdate();
        //13202038062
        mLoginPresenter = new LoginPresenter(this);
//        boolean loginState = mLoginPresenter.getLoginState();
//        if (loginState) {
//            //说明在登陆状态
//            Intent intent = new Intent(this, MainActivity2.class);
//            startActivity(intent);
//            finish();
//        }
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mLoginPresenter.selectUserInfo();
        mDialog = new ProgressDialog(this);
//        mDialog.setIcon(R.mipmap.sport2);
        mDialog.setTitle(R.string.logining);
        etUsername.addTextChangedListener(mTextWatch);
        etPassword.addTextChangedListener(mTextWatch);
        loginDrawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                showServerInfo();
            }
        });
        if (!GCApplication.isOpenDraw)
            loginDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private void showServerInfo() {
        serverIp.setText(GCApplication.getServerIp());
        serverPort.setText(GCApplication.getServerPort() + "");
        pushServerIp.setText(GCApplication.getPushServerIp());
        pushServerPort.setText(GCApplication.getPushServerPort()+"");
    }

    /**
     * 登陆成功
     */
    @Override
    public void loginSuccess() {
        //成功跳转显示终端列表
        saveLastUserInfo();
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);
        finish();
    }

    /**
     * 登陆错误
     */
    @Override
    public void loginError() {
        //错误提示一下
        Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show();
    }

    /**
     * 保存最近登录的账号
     */
    @Override
    public void saveLastUserInfo() {
        mLoginPresenter.saveUserInfo(new UserInfo(getUserName(), getPassWord()));
        startService(new Intent(getApplicationContext(), PushServer.class));  //开启推送服务
        PushServer.setUser(getUserName(),getPassWord());
    }

    /**
     * 设置最近登录过的账号
     *
     * @param info
     */
    @Override
    public void showLastRecordUser(UserInfo info) {
        if (!"".equals(info.getPassWord()) && !"".equals(info.getUserName())) {
            etUsername.setText(info.getUserName());
            etPassword.setText(info.getPassWord());
            login.setEnabled(true);
            login.setTextColor(getResources().getColor(R.color.login_text_enable));
        }
    }

    /**
     * 获取用户输入的用户名
     *
     * @return
     */
    @Override
    public String getUserName() {
        return etUsername.getText().toString();
    }

    /**
     * 获取用户输入的密码
     *
     * @return
     */
    @Override
    public String getPassWord() {
        return etPassword.getText().toString();
    }

    @Override
    public void showLoading() {
        mDialog.show();
    }

    @Override
    public void hideLoading() {
        mDialog.dismiss();
    }


    /**
     * 验证
     *
     * @return
     */
    private boolean isEmpty() {
        return !getUserName().equals("") && !getPassWord().equals("");
    }


    @OnClick(R2.id.login)
    public void onViewClicked() {
        if (mPhone.matcher(getUserName()).matches()) {
            mLoginPresenter.login(getUserName(), getPassWord());
        } else {
            Toast.makeText(LoginActivity.this, "Not a valid phone number!!!", Toast.LENGTH_SHORT).show();
        }
    }
    @OnClick(R2.id.register)
    public void onViewClicked1() {
        //跳转至注册界面
        Intent register = new Intent(this, RegisterActivity.class);
        startActivity(register);
    }
    @OnClick(R2.id.save)
    public void onViewClicked2() {
        GCApplication.saveServerInfo(serverIp.getText().toString(), Integer.parseInt(serverPort.getText().toString())
                , pushServerIp.getText().toString(),Integer.parseInt(pushServerPort.getText().toString()));
        loginDrawer.closeDrawer(Gravity.LEFT);
    }


//    /**
//     * 点击事件
//     *
//     * @param view
//     */
//    @OnClick({R2.id.login, R2.id.register, R2.id.save})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R2.id.login:
//                if (mPhone.matcher(getUserName()).matches()) {
//                    mLoginPresenter.login(getUserName(), getPassWord());
//                } else {
//                    Toast.makeText(LoginActivity.this, "Not a valid phone number!!!", Toast.LENGTH_SHORT).show();
//                }
//                break;
//            case R2.id.register:
//                //跳转至注册界面
//                Intent register = new Intent(this, RegisterActivity.class);
//                startActivity(register);
//                break;
//            case R2.id.save:
//                GCApplication.saveServerInfo(serverIp.getText().toString(), Integer.parseInt(serverPort.getText().toString())
//                        , pushServerIp.getText().toString(),Integer.parseInt(pushServerPort.getText().toString()));
//                loginDrawer.closeDrawer(Gravity.LEFT);
//                break;
//        }
//    }

}
