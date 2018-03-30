package com.media.dingping.cameramonitor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.media.dingping.cameramonitor.listener.MyTexWatch;
import com.media.dingping.cameramonitor.presenter.ModifyPresenter;
import com.media.dingping.cameramonitor.presenter.impl.IModifyPresenter;
import com.media.dingping.cameramonitor.view.IModifyView;
import com.videogo.widget.TitleBar;

/**
 * Created by Administrator on 2017/5/18 0018.
 * 修改密码
 */
public class ModifyActvity extends AppCompatActivity implements IModifyView {
    private IModifyPresenter mModifyPresenter;
    private TitleBar mTitleBar;
    private Button mModifyButton;
    private EditText et_userName, et_oldPassword, et_newPassword,et_newPassword2;
    private ProgressDialog mDialog;
    private MyTexWatch mTextWatch = new MyTexWatch() {
        @Override
        public void afterTextChanged(Editable editable) {
            if (isVerify()) {
                mModifyButton.setTextColor(getResources().getColor(R.color.login_text_enable));
                mModifyButton.setEnabled(true);
            } else {
                mModifyButton.setTextColor(getResources().getColor(R.color.login_text_noenable));
                mModifyButton.setEnabled(false);
            }
        }
    };

    private boolean isVerify() {
        return getPassWord()!=null&&!getPassWord().equals("")&&getNewPassword()!=null&&!getNewPassword().equals("")&&getNewPassword().equals(getNewPassword2());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_activity);
        mModifyPresenter = new ModifyPresenter(this);
        initView();
    }

    private void initView() {
        mModifyButton = (Button) this.findViewById(R.id.modify);
        et_userName = (EditText) this.findViewById(R.id.user_name);
        et_oldPassword = (EditText) this.findViewById(R.id.old_password);
        et_newPassword = (EditText) this.findViewById(R.id.new_password);
        et_newPassword2=(EditText)this.findViewById(R.id.new_password2);
        mTitleBar = (TitleBar) this.findViewById(R.id.modify_title);
        mTitleBar.setTitle("修改密码");
        mDialog = new ProgressDialog(this);
        mDialog.setTitle(R.string.modify);
        mTitleBar.addBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mModifyPresenter.modify(getUserName(), getPassWord(), getNewPassword());
            }
        });
        mModifyPresenter.selectUserInfo();
        et_oldPassword.addTextChangedListener(mTextWatch);
        et_newPassword.addTextChangedListener(mTextWatch);
        et_newPassword2.addTextChangedListener(mTextWatch);
    }

    @Override
    public void modifySuccess() {
        Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
        //跳转登录界面
        Intent stopPlay = new Intent("donot.pressed.update");
        sendBroadcast(stopPlay);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void modifyError() {
        Toast.makeText(this, "修改失败，原密码错误", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void currUserName(String userName) {
        et_userName.setText(userName);
    }

    @Override
    public String getNewPassword() {

        return et_newPassword.getText().toString();
    }

    @Override
    public String getNewPassword2() {
        return et_newPassword2.getText().toString();
    }

    @Override
    public String getUserName() {

        return et_userName.getText().toString();
    }

    @Override
    public String getPassWord() {

        return et_oldPassword.getText().toString();
    }

    @Override
    public void showLoading() {
        mDialog.show();
    }

    @Override
    public void hideLoading() {
        mDialog.dismiss();
    }
}
