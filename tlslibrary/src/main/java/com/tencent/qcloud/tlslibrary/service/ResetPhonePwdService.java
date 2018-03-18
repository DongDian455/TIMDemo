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
import tencent.tls.platform.TLSPwdResetListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * Created by dgy on 15/8/14.
 */
public class ResetPhonePwdService {
    private final static String TAG = "ResetPhonePwdService";

    private Context context;
    private EditText txt_countryCode;
    private EditText txt_phoneNumber;
    private EditText txt_checkCode;
    private Button btn_requireCheckCode;
    private Button btn_verify;

    private String countryCode;
    private String phoneNumber;
    private String checkCode;

    private PwdResetListener pwdResetListener;
    private TLSService tlsService;

    public ResetPhonePwdService(Context context,
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
        pwdResetListener = new PwdResetListener();

        btn_requireCheckCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryCode = ResetPhonePwdService.this.txt_countryCode.getText().toString();
                countryCode = countryCode.substring(countryCode.indexOf('+') + 1);  // 解析国家码
                phoneNumber = ResetPhonePwdService.this.txt_phoneNumber.getText().toString();

                if (!Util.validPhoneNumber(countryCode, phoneNumber)) {
                    Util.showToast(ResetPhonePwdService.this.context, "请输入有效的手机号");
                    return;
                }

                Log.e(TAG, Util.getWellFormatMobile(countryCode, phoneNumber));

                tlsService.TLSPwdResetAskCode(countryCode, phoneNumber, pwdResetListener);
            }
        });

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryCode = ResetPhonePwdService.this.txt_countryCode.getText().toString();
                countryCode = countryCode.substring(countryCode.indexOf('+') + 1);  // 解析国家码
                phoneNumber = ResetPhonePwdService.this.txt_phoneNumber.getText().toString();
                checkCode = ResetPhonePwdService.this.txt_checkCode.getText().toString();

                if (!Util.validPhoneNumber(countryCode, phoneNumber)) {
                    Util.showToast(ResetPhonePwdService.this.context, "请输入有效的手机号");
                    return;
                }

                if (checkCode.length() == 0) {
                    Util.showToast(ResetPhonePwdService.this.context, "请输入验证码");
                    return;
                }

                Log.e(TAG, Util.getWellFormatMobile(countryCode, phoneNumber));

                tlsService.TLSPwdResetVerifyCode(checkCode, pwdResetListener);
            }
        });
    }

    class PwdResetListener implements TLSPwdResetListener {
        @Override
        public void OnPwdResetAskCodeSuccess(int reaskDuration, int expireDuration) {
            Util.showToast(context, "请求下发短信成功,验证码" + expireDuration / 60 + "分钟内有效");

            // 在获取验证码按钮上显示重新获取验证码的时间间隔
            Util.startTimer(btn_requireCheckCode, "获取验证码", "重新获取", reaskDuration, 1);
        }

        @Override
        public void OnPwdResetReaskCodeSuccess(int reaskDuration, int expireDuration) {
            Util.showToast(context, "注册短信重新下发,验证码" + expireDuration / 60 + "分钟内有效");
            Util.startTimer(btn_requireCheckCode, "获取验证码", "重新获取", reaskDuration, 1);
        }

        @Override
        public void OnPwdResetVerifyCodeSuccess() {
            Util.showToast(context, "改密验证通过");
            Intent intent = new Intent(context, PhonePwdLoginActivity.class);
            intent.putExtra(Constants.EXTRA_PHONEPWD_REG_RST, Constants.PHONEPWD_RESET);
            intent.putExtra(Constants.COUNTRY_CODE, txt_countryCode.getText().toString());
            intent.putExtra(Constants.PHONE_NUMBER, txt_phoneNumber.getText().toString());
            context.startActivity(intent);
            ((Activity)context).finish();
        }

        @Override
        public void OnPwdResetCommitSuccess(TLSUserInfo userInfo) {}

        @Override
        public void OnPwdResetFail(TLSErrInfo errInfo) {
            Util.notOK(context, errInfo);
        }

        @Override
        public void OnPwdResetTimeout(TLSErrInfo errInfo) {
            Util.notOK(context, errInfo);
        }
    }
}
