package com.tencent.qcloud.tlslibrary.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tencent.qcloud.tlslibrary.activity.PhonePwdLoginActivity;
import com.tencent.qcloud.tlslibrary.helper.Util;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSPwdRegListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * Created by dgy on 15/8/14.
 */
public class PhonePwdRegisterService {

    private final static String TAG = "PhonePwdRegisterService";

    private Context context;
    private EditText txt_countryCode;
    private EditText txt_phoneNumber;
    private EditText txt_checkCode;
    private Button btn_requireCheckCode;
    private Button btn_verify;

    private String countryCode;
    private String phoneNumber;
    private String checkCode;

    private PwdRegListener pwdRegListener;
    private TLSService tlsService;

    public PhonePwdRegisterService(Context context,
                                   EditText txt_countryCode,
                                   EditText txt_phoneNumber,
                                   EditText txt_checkCode,
                                   Button btn_requireCheckCode,
                                   Button btn_verify) {
        this.context = context;
        this.txt_countryCode = txt_countryCode;
        this.txt_phoneNumber = txt_phoneNumber;
        this.txt_checkCode = txt_checkCode;
        this.btn_requireCheckCode = btn_requireCheckCode;
        this.btn_verify = btn_verify;

        tlsService = TLSService.getInstance();
        pwdRegListener = new PwdRegListener();

        btn_requireCheckCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryCode = PhonePwdRegisterService.this.txt_countryCode.getText().toString();
                countryCode = countryCode.substring(countryCode.indexOf('+') + 1);  // 解析国家码
                phoneNumber = PhonePwdRegisterService.this.txt_phoneNumber.getText().toString();

                if (!Util.validPhoneNumber(countryCode, phoneNumber)) {
                    Util.showToast(PhonePwdRegisterService.this.context, "请输入有效的手机号");
                    return;
                }

                Log.e(TAG, Util.getWellFormatMobile(countryCode, phoneNumber));

                tlsService.TLSPwdRegAskCode(countryCode, phoneNumber, pwdRegListener);
            }
        });

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryCode = PhonePwdRegisterService.this.txt_countryCode.getText().toString();
                countryCode = countryCode.substring(countryCode.indexOf('+') + 1);  // 解析国家码
                phoneNumber = PhonePwdRegisterService.this.txt_phoneNumber.getText().toString();
                checkCode = PhonePwdRegisterService.this.txt_checkCode.getText().toString();

                if (!Util.validPhoneNumber(countryCode, phoneNumber)) {
                    Util.showToast(PhonePwdRegisterService.this.context, "请输入有效的手机号");
                    return;
                }

                if (checkCode.length() == 0) {
                    Util.showToast(PhonePwdRegisterService.this.context, "请输入验证码");
                    return;
                }

                Log.e(TAG, Util.getWellFormatMobile(countryCode, phoneNumber));

                tlsService.TLSPwdRegVerifyCode(checkCode, pwdRegListener);
            }
        });
    }

    public class PwdRegListener implements TLSPwdRegListener {
        @Override
        public void OnPwdRegAskCodeSuccess(int reaskDuration, int expireDuration) {
            Util.showToast(context, "请求下发短信成功,验证码" + expireDuration / 60 + "分钟内有效");

            // 在获取验证码按钮上显示重新获取验证码的时间间隔
            Util.startTimer(btn_requireCheckCode, "获取验证码", "重新获取", reaskDuration, 1);
        }

        @Override
        public void OnPwdRegReaskCodeSuccess(int reaskDuration, int expireDuration) {
            Util.showToast(context, "注册短信重新下发,验证码" + expireDuration / 60 + "分钟内有效");
            Util.startTimer(btn_requireCheckCode, "获取验证码", "重新获取", reaskDuration, 1);
        }

        @Override
        public void OnPwdRegVerifyCodeSuccess() {
            Util.showToast(context, "注册验证通过，准备获取号码");
            Intent intent = new Intent(context, PhonePwdLoginActivity.class);
            intent.putExtra(Constants.EXTRA_PHONEPWD_REG_RST, Constants.PHONEPWD_REGISTER);
            intent.putExtra(Constants.COUNTRY_CODE, txt_countryCode.getText().toString());
            intent.putExtra(Constants.PHONE_NUMBER, txt_phoneNumber.getText().toString());
            intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            context.startActivity(intent);
            ((Activity)context).finish();
        }

        @Override
        public void OnPwdRegCommitSuccess(TLSUserInfo userInfo) {}

        @Override
        public void OnPwdRegFail(TLSErrInfo errInfo) {
            Util.notOK(context, errInfo);
        }

        @Override
        public void OnPwdRegTimeout(TLSErrInfo errInfo) {
            Util.notOK(context, errInfo);
        }
    }
}
