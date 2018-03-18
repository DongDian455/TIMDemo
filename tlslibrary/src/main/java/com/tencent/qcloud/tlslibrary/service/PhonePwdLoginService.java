package com.tencent.qcloud.tlslibrary.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tencent.qcloud.tlslibrary.activity.ImgCodeActivity;
import com.tencent.qcloud.tlslibrary.helper.Util;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSPwdLoginListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * Created by dgy on 15/8/14.
 */
public class PhonePwdLoginService {

    private final static String TAG = "PhonePwdLoginService";

    private Context context;
    private EditText txt_countrycode;
    private EditText txt_phone;
    private EditText txt_pwd;

    private String countrycode;
    private String phone;
    private String password;

    private TLSService tlsService;
    public static PwdLoginListener pwdLoginListener;

    public PhonePwdLoginService(Context context,
                                EditText txt_countrycode,
                                EditText txt_phone,
                                EditText txt_pwd,
                                Button btn_login){
        this.context = context;
        this.txt_countrycode = txt_countrycode;
        this.txt_phone = txt_phone;
        this.txt_pwd = txt_pwd;

        tlsService = TLSService.getInstance();
        pwdLoginListener = new PwdLoginListener();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countrycode = PhonePwdLoginService.this.txt_countrycode.getText().toString();
                countrycode = countrycode.substring(countrycode.indexOf('+') + 1);  // 解析国家码
                phone = PhonePwdLoginService.this.txt_phone.getText().toString();
                password = PhonePwdLoginService.this.txt_pwd.getText().toString();

                // 验证手机号和密码的有效性
                if (phone.length() == 0 || password.length() == 0) {
                    Util.showToast(PhonePwdLoginService.this.context, "手机号密码不能为空");
                    return;
                }

                Log.e(TAG, Util.getWellFormatMobile(countrycode, phone));

                tlsService.TLSPwdLogin(Util.getWellFormatMobile(countrycode, phone), password, pwdLoginListener);
            }
        });
    }

    class PwdLoginListener implements TLSPwdLoginListener {
        @Override
        public void OnPwdLoginSuccess(TLSUserInfo userInfo) {
            Util.showToast(context, "密码登录成功！");
            TLSService.getInstance().setLastErrno(0);
            PhonePwdLoginService.this.jumpToSuccActivity();
        }

        @Override
        public void OnPwdLoginReaskImgcodeSuccess(byte[] picData) {
            ImgCodeActivity.fillImageview(picData);
        }

        @Override
        public void OnPwdLoginNeedImgcode(byte[] picData, TLSErrInfo errInfo) {
            Intent intent = new Intent(context, ImgCodeActivity.class);
            intent.putExtra(Constants.EXTRA_IMG_CHECKCODE, picData);
            intent.putExtra(Constants.EXTRA_LOGIN_WAY, Constants.PHONEPWD_LOGIN);
            context.startActivity(intent);
        }

        @Override
        public void OnPwdLoginFail(TLSErrInfo errInfo) {
            TLSService.getInstance().setLastErrno(-1);
            Util.notOK(context, errInfo);
        }

        @Override
        public void OnPwdLoginTimeout(TLSErrInfo errInfo) {
            TLSService.getInstance().setLastErrno(-1);
            Util.notOK(context, errInfo);
        }
    }

    void jumpToSuccActivity() {
        String thirdappPackageNameSucc = Constants.thirdappPackageNameSucc;
        String thirdappClassNameSucc = Constants.thirdappClassNameSucc;

        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_LOGIN_WAY, Constants.PHONEPWD_LOGIN);
        intent.putExtra(Constants.EXTRA_PHONEPWD_LOGIN, Constants.PHONEPWD_LOGIN_SUCCESS);
        if (thirdappPackageNameSucc != null && thirdappClassNameSucc != null) {
            intent.setClassName(thirdappPackageNameSucc, thirdappClassNameSucc);
            ((Activity) context).startActivity(intent);
        } else {
            ((Activity) context).setResult(Activity.RESULT_OK, intent);
        }
        ((Activity) context).finish();
    }

}
