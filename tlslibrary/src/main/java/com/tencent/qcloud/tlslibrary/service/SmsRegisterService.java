package com.tencent.qcloud.tlslibrary.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tencent.qcloud.tlslibrary.activity.HostLoginActivity;
import com.tencent.qcloud.tlslibrary.helper.Util;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSSmsRegListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * Created by dgy on 15/7/9.
 * 处理短信注册的业务
 */
public class SmsRegisterService {

    private EditText txtCountryCode;                // 包含国家码的EditView控件
    private TextView txtPhoneNumber;                // 包含手机号的EditView控件
    private TextView txtCheckCode;                  // 包含验证码的EditView控件
    private Button btn_requireCheckCode;            // 获取验证码的按钮
    private Button btn_register;                    // 注册按钮
    private View.OnClickListener onClickListener;   // 处理登录按钮的点击事件
    private SmsRegListener smsRegListener;          // 处理短信登录过程中遇到的各种情况
    private Context context;                        // Activity
    private TLSService tlsService;

    private String phoneNumber;
    private String checkCode;
    private String countryCode;                     // 手机号所属国家代码

    public SmsRegisterService(final Context context,
                              EditText txtCountryCode,
                              EditText txtPhoneNumber,
                              EditText txtCheckCode,
                              Button btn_requireCheckCode,
                              Button btn_register)
    {
        this.context = context;
        this.txtCountryCode = txtCountryCode;
        this.txtPhoneNumber = txtPhoneNumber;
        this.txtCheckCode = txtCheckCode;
        this.btn_requireCheckCode = btn_requireCheckCode;
        this.btn_register = btn_register;
        this.smsRegListener = new SmsRegListener();
        this.tlsService = TLSService.getInstance();

        this.btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryCode = SmsRegisterService.this.txtCountryCode.getText().toString();
                countryCode = countryCode.substring(countryCode.indexOf('+') + 1);              // 解析国家码
                phoneNumber = SmsRegisterService.this.txtPhoneNumber.getText().toString();      // 获取手机号
                checkCode = SmsRegisterService.this.txtCheckCode.getText().toString();          // 获取验证码

                // 1. 判断手机号是否有效
                if (!Util.validPhoneNumber(countryCode, phoneNumber)) {
                    Util.showToast(SmsRegisterService.this.context, "请输入有效的手机号");
                    return;
                }

                // 2. 判断验证码是否为空（是否请求了验证码由上层控制）
                if (checkCode.length() == 0) {
                    Util.showToast(SmsRegisterService.this.context, "请输入验证码");
                    return;
                }

                // 3. 向TLS验证手机号和验证码
                SmsRegisterService.this.tlsService.smsRegVerifyCode(checkCode, smsRegListener);
            }
        });

        this.btn_requireCheckCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryCode = SmsRegisterService.this.txtCountryCode.getText().toString();
                countryCode = countryCode.substring(countryCode.indexOf('+') + 1);              // 解析国家码
                phoneNumber = SmsRegisterService.this.txtPhoneNumber.getText().toString();      // 获取手机号

                // 1. 判断手机号是否有效
                if (!Util.validPhoneNumber(countryCode, phoneNumber)) {
                    Util.showToast(SmsRegisterService.this.context, "请输入有效的手机号");
                    return;
                }

                // 2. 请求验证码
                SmsRegisterService.this.tlsService.smsRegAskCode(countryCode, phoneNumber, smsRegListener);
            }
        });
    }

    /**
     * 短信注册监听器
     */
    class SmsRegListener implements TLSSmsRegListener {

        // 请求下发短信成功
        @Override
        public void OnSmsRegAskCodeSuccess(int reaskDuration, int expireDuration) {
            Util.showToast(context, "请求下发短信成功,验证码" + expireDuration / 60 + "分钟内有效");

            // 在获取验证码按钮上显示重新获取验证码的时间间隔
            Util.startTimer(btn_requireCheckCode, "获取验证码", "重新获取", reaskDuration, 1);
        }

        // 重新请求下发短信成功
        @Override
        public void OnSmsRegReaskCodeSuccess(int reaskDuration, int expireDuration) {
            Util.showToast(context, "注册短信重新下发,验证码" + expireDuration / 60 + "分钟内有效");
        }

        // 短信验证成功，接下来只需要用户确认操作，然后调用SmsRegCommit 完成注册流程
        @Override
        public void OnSmsRegVerifyCodeSuccess() {
            tlsService.smsRegCommit(smsRegListener);
        }

        // 最终注册成功，接下来可以引导用户进行短信登录
        @Override
        public void OnSmsRegCommitSuccess(TLSUserInfo userInfo) {
            Util.showToast(context, "短信注册成功！");
            Intent intent = new Intent(context, HostLoginActivity.class);
            intent.putExtra(Constants.EXTRA_SMS_REG, Constants.SMS_REG_SUCCESS);
            intent.putExtra(Constants.COUNTRY_CODE, countryCode);
            intent.putExtra(Constants.PHONE_NUMBER, phoneNumber);
            intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            context.startActivity(intent);
            ((Activity) context).finish();
        }

        // 无密码注册过程中任意一步都可以到达这里
        // 可以根据tlsErrInfo 中ErrCode, Title, Msg 给用户弹提示语，引导相关操作
        @Override
        public void OnSmsRegFail(TLSErrInfo errInfo) {
            Util.notOK(context, errInfo);
        }

        // 无密码注册过程中任意一步都可以到达这里
        // 顾名思义，网络超时，可能是用户网络环境不稳定，一般让用户重试即可
        @Override
        public void OnSmsRegTimeout(TLSErrInfo errInfo) {
            Util.notOK(context, errInfo);
        }
    }
}
