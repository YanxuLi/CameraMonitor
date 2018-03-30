package com.media.dingping.cameramonitor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.media.dingping.cameramonitor.bean.UserInfo;
import com.media.dingping.cameramonitor.listener.MyTexWatch;
import com.media.dingping.cameramonitor.presenter.RegisterPresenter;
import com.media.dingping.cameramonitor.utils.Patterns;
import com.media.dingping.cameramonitor.view.IRegisterView;
import com.media.dingping.cameramonitor.main.MainActivity2;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zsc on 2017/5/18 0018.
 * 注册
 */
public class RegisterActivity extends AppCompatActivity implements IRegisterView {


    @BindView(R2.id.bt_register)
    Button btRegister;
    RegisterPresenter registerPresenter;
    @BindView(R2.id.edit_phone)
    EditText editPhone;
    @BindView(R2.id.edit_password)
    EditText editPassword;
    @BindView(R2.id.edit_confirmpassword)
    EditText editConfirmpassword;
    private Pattern mPhone = Pattern.compile(Patterns.phone);
    private ProgressDialog mDialog;
    MyTexWatch myTexWatch = new MyTexWatch() {
        @Override
        public void afterTextChanged(Editable s) {
            if (isMatches()) {
                btRegister.setEnabled(true);
                btRegister.setTextColor(Color.parseColor("#061304"));
            } else {
                btRegister.setEnabled(false);
                btRegister.setTextColor(Color.parseColor("#3b3f3b"));
            }
        }

    };

    private boolean isMatches() {
        return !getUserName().equals("")
                && !getPassWord().equals("")
                && !getConfirmPassword().equals("");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        mDialog = new ProgressDialog(this);
        mDialog.setTitle(R.string.registering);
        registerPresenter = new RegisterPresenter(this);
        editPhone.addTextChangedListener(myTexWatch);
        editPassword.addTextChangedListener(myTexWatch);
        editConfirmpassword.addTextChangedListener(myTexWatch);

    }


    @Override
    public void registerSuccess() {
        //注册成功，带参数返回到登陆页
        saveLastUserInfo();
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void registerError() {
        toast("注册失败");
    }

    @Override
    public void onExists() {
        toast("已存在此用户");
    }

    public void toast(String toastStr) {
        Toast.makeText(this, toastStr, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void saveLastUserInfo() {
        registerPresenter.saveUserInfo(new UserInfo(getUserName(), getPassWord()));
    }


    @Override
    public String getUserName() {
        return editPhone.getText().toString();
    }


    @Override
    public String getPassWord() {
        return editPassword.getText().toString();
    }

    @Override
    public void showLoading() {
        mDialog.show();
    }

    @Override
    public void hideLoading() {
        mDialog.dismiss();
    }

    @Override
    public String getConfirmPassword() {
        return editConfirmpassword.getText().toString();
    }

    @OnClick(R2.id.bt_register)
    public void onViewClicked() {
        if (getPassWord().equals(getConfirmPassword())) {
            if (mPhone.matcher(getUserName()).matches()) {
                registerPresenter.register(getUserName(), getPassWord());
            } else {
                toast("Not a valid phone number!!!");
            }
        } else {
            toast("两次密码输入不一致");
        }
    }@OnClick(R2.id.back)
    public void onViewClicked1() {
        finish();
    }
//    @OnClick({R.id.bt_register, R.id.back})
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.bt_register:
//                if (getPassWord().equals(getConfirmPassword())) {
//                    if (mPhone.matcher(getUserName()).matches()) {
//                        registerPresenter.register(getUserName(), getPassWord());
//                    } else {
//                        toast("Not a valid phone number!!!");
//                    }
//                } else {
//                    toast("两次密码输入不一致");
//                }
//                break;
//            case R.id.back:
//
//                finish();
//                break;
//        }
//    }
}
