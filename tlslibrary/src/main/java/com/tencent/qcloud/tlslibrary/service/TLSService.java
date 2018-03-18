package com.tencent.qcloud.tlslibrary.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;

import com.tencent.qalsdk.QALSDKManager;
import com.tencent.qcloud.tlslibrary.helper.Util;

import tencent.tls.platform.TLSAccountHelper;
import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSGuestLoginListener;
import tencent.tls.platform.TLSLoginHelper;
import tencent.tls.platform.TLSPwdLoginListener;
import tencent.tls.platform.TLSPwdRegListener;
import tencent.tls.platform.TLSPwdResetListener;
import tencent.tls.platform.TLSRefreshUserSigListener;
import tencent.tls.platform.TLSSmsLoginListener;
import tencent.tls.platform.TLSSmsRegListener;
import tencent.tls.platform.TLSStrAccRegListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * Created by dgy on 15/7/8.
 */
public class TLSService {

    private TLSLoginHelper loginHelper;
    private TLSAccountHelper accountHelper;

    private SmsLoginService smsLoginService;
    private SmsRegisterService smsRegisterService;
    private QQLoginService qqLoginService;
    private WXLoginService wxLoginService;
    private AccountLoginService accountLoginService;
    private AccountRegisterService accountRegisterService;
    private PhonePwdLoginService phonePwdLoginService;
    private PhonePwdRegisterService phonePwdRegisterService;
    private ResetPhonePwdService resetPhonePwdService;
    private static int lastErrno = -1;

    private static TLSService tlsService = null;

    private TLSService(){}

    private RefreshUserSigListener refreshUserSigListener = new RefreshUserSigListener() {
        @Override
        public void onUserSigNotExist() {}
        @Override
        public void OnRefreshUserSigSuccess(TLSUserInfo tlsUserInfo) {}
        @Override
        public void OnRefreshUserSigFail(TLSErrInfo tlsErrInfo) {}
        @Override
        public void OnRefreshUserSigTimeout(TLSErrInfo tlsErrInfo) {}
    };

    private GuestLoginListener guestLoginListener = new GuestLoginListener() {
        @Override
        public void OnGuestLoginSuccess(TLSUserInfo userInfo) {}

        @Override
        public void OnGuestLoginFail(TLSErrInfo errInfo) {}

        @Override
        public void OnGuestLoginTimeout(TLSErrInfo errInfo) {}
    };

    public static TLSService getInstance() {
        if (tlsService == null) {
            tlsService = new TLSService();
        }
        return tlsService;
    }

    public static void setLastErrno(int errno) {
        lastErrno = errno;
    }

    public static int getLastErrno() {
        return lastErrno;
    }

    /**
     * @function: 初始化TLS SDK, 必须在使用TLS SDK相关服务之前调用
     * @param context: 关联的activity
     * */
    public void initTlsSdk(Context context) {
//        QALSDKManager.getInstance().init(context.getApplicationContext(), (int)TLSConfiguration.SDK_APPID);
//        QALSDKManager.getInstance().setEnv(0);     // 0: sso正式环境 1: sso测试环境, 即beta环境

//        TIMManager.getInstance().init(context.getApplicationContext());
//        TIMManager.getInstance().setEnv(1);

        loginHelper = TLSLoginHelper.getInstance().init(context.getApplicationContext(),
                TLSConfiguration.SDK_APPID, TLSConfiguration.ACCOUNT_TYPE, TLSConfiguration.APP_VERSION);
        loginHelper.setTimeOut(TLSConfiguration.TIMEOUT);
        loginHelper.setLocalId(TLSConfiguration.LANGUAGE_CODE);
        loginHelper.setTestHost("", true);                   // 走sso
//        loginHelper.setTestHost("113.108.64.238", false);      // 不走sso

        accountHelper = TLSAccountHelper.getInstance().init(context.getApplicationContext(),
                TLSConfiguration.SDK_APPID, TLSConfiguration.ACCOUNT_TYPE, TLSConfiguration.APP_VERSION);
        accountHelper.setCountry(Integer.parseInt(TLSConfiguration.COUNTRY_CODE)); // 存储注册时所在国家，只须在初始化时调用一次
        accountHelper.setTimeOut(TLSConfiguration.TIMEOUT);
        accountHelper.setLocalId(TLSConfiguration.LANGUAGE_CODE);
        accountHelper.setTestHost("", true);                 // 走sso
//        accountHelper.setTestHost("113.108.64.238", false);    // 不走sso

    }

    /**
     * 代理TLSLoginHelper的接口
     */

    public void initSmsLoginService(Context context,
                                    EditText txtCountryCode,
                                    EditText txtPhoneNumber,
                                    EditText txtCheckCode,
                                    Button btn_requireCheckCode,
                                    Button btn_login) { // 初始化短信登录服务
        smsLoginService = new SmsLoginService(context, txtCountryCode, txtPhoneNumber, txtCheckCode,
                btn_requireCheckCode, btn_login);

    }

    public void initAccountLoginService(Context context,
                                        EditText txt_username,
                                        EditText txt_password,
                                        Button btn_login) {
        accountLoginService = new AccountLoginService(context, txt_username, txt_password, btn_login);
    }

    public void initPhonePwdLoginService(Context context,
                                         EditText txt_countrycode,
                                         EditText txt_phone,
                                         EditText txt_pwd,
                                         Button btn_login) {
        phonePwdLoginService = new PhonePwdLoginService(context, txt_countrycode, txt_phone, txt_pwd, btn_login);
    }

    public void initPhonePwdRegisterService(Context context,
                                            EditText txt_countryCode,
                                            EditText txt_phoneNumber,
                                            EditText txt_checkCode,
                                            Button btn_requireCheckCode,
                                            Button btn_verify) {
        phonePwdRegisterService = new PhonePwdRegisterService(context, txt_countryCode, txt_phoneNumber, txt_checkCode, btn_requireCheckCode, btn_verify);
    }

    public void initResetPhonePwdService(Context context,
                                            EditText txt_countryCode,
                                            EditText txt_phoneNumber,
                                            EditText txt_checkCode,
                                            Button btn_requireCheckCode,
                                            Button btn_verify) {
        resetPhonePwdService = new ResetPhonePwdService(context, txt_countryCode, txt_phoneNumber, txt_checkCode, btn_requireCheckCode, btn_verify);
    }

    public void initGuestLoginService(Context context, Button button) {
        new GuestLoginService(context, button);
    }


    public int smsLogin(String countryCode, String phoneNumber, TLSSmsLoginListener smsLoginListener) {
        return loginHelper.TLSSmsLogin(Util.getWellFormatMobile(countryCode, phoneNumber), smsLoginListener);
    }

    public int smsLoginAskCode(String countryCode, String phoneNumber, TLSSmsLoginListener listener) {
        return loginHelper.TLSSmsLoginAskCode(Util.getWellFormatMobile(countryCode, phoneNumber), listener);
    }

    public int smsLoginVerifyCode(String code, TLSSmsLoginListener listener) {
        return loginHelper.TLSSmsLoginVerifyCode(code, listener);
    }

    public String getUserSig(String identify) {
        return loginHelper.getUserSig(identify);
    }

    public TLSUserInfo getLastUserInfo() {
        return loginHelper.getLastUserInfo();
    }

    public String getLastUserIdentifier() {
        TLSUserInfo userInfo = getLastUserInfo();
        if (userInfo != null)
            return userInfo.identifier;
        else
            return null;
    }

    public void clearUserInfo(String identifier) {
        loginHelper.clearUserInfo(identifier);
        lastErrno = -1;
    }

    public boolean needLogin(String identifier) {
        if (identifier == null)
            return true;
        return loginHelper.needLogin(identifier);
    }

    public String getSDKVersion() {
        return loginHelper.getSDKVersion();
    }

    public void refreshUserSig(String identifier, RefreshUserSigListener refreshUserSigListener) {
        if (null == refreshUserSigListener)
            refreshUserSigListener = this.refreshUserSigListener;

        if (!needLogin(identifier))
            loginHelper.TLSRefreshUserSig(identifier, refreshUserSigListener);
        else
            refreshUserSigListener.onUserSigNotExist();
    }

    public int TLSPwdLogin(String identifier, String password, TLSPwdLoginListener listener) {
        return loginHelper.TLSPwdLogin(identifier, password.getBytes(), listener);
    }

    public int TLSPwdLoginVerifyImgcode(String imgCode, TLSPwdLoginListener listener) {
        return loginHelper.TLSPwdLoginVerifyImgcode(imgCode, listener);
    }

    public int TLSPwdLoginReaskImgcode(TLSPwdLoginListener listener) {
        return loginHelper.TLSPwdLoginReaskImgcode(listener);
    }

    public int TLSGuestLogin(TLSGuestLoginListener listener) {
        if (null == listener) {
            listener = this.guestLoginListener;
        }

        return loginHelper.TLSGuestLogin(listener);
    }

    public String getGuestIdentifier() {
        return loginHelper.getGuestIdentifier();
    }

    /**
     * 代理TLSAccountHelper的接口
     */

    public void initSmsRegisterService(Context context,
                                       EditText txtCountryCode,
                                       EditText txtPhoneNumber,
                                       EditText txtCheckCode,
                                       Button btn_requireCheckCode,
                                       Button btn_register) { // 初始化短信注册服务
        smsRegisterService = new SmsRegisterService(context, txtCountryCode, txtPhoneNumber, txtCheckCode,
                btn_requireCheckCode, btn_register);
    }

    public int smsRegAskCode(String countryCode, String phoneNumber, TLSSmsRegListener listener) {
        return accountHelper.TLSSmsRegAskCode(Util.getWellFormatMobile(countryCode, phoneNumber), listener);
    }

    public int TLSPwdRegVerifyCode(String code, TLSPwdRegListener listener) {
        return accountHelper.TLSPwdRegVerifyCode(code, listener);
    }

    public int TLSPwdRegCommit(String password, TLSPwdRegListener listener) {
        return accountHelper.TLSPwdRegCommit(password, listener);
    }

    public int TLSPwdResetCommit(String password, TLSPwdResetListener listener) {
        return accountHelper.TLSPwdResetCommit(password, listener);

    }

    public int smsRegVerifyCode(String code, TLSSmsRegListener listener) {
        return accountHelper.TLSSmsRegVerifyCode(code, listener);
    }

    public int smsRegCommit(TLSSmsRegListener listener) {
        return accountHelper.TLSSmsRegCommit(listener);
    }

    public int TLSStrAccReg(String account, String password, TLSStrAccRegListener listener) {
        return accountHelper.TLSStrAccReg(account, password, listener);
    }

    public int TLSPwdRegAskCode(String countryCode, String phoneNumber, TLSPwdRegListener listener) {
        return accountHelper.TLSPwdRegAskCode(Util.getWellFormatMobile(countryCode, phoneNumber), listener);
    }

    public void initAccountRegisterService(Context context,
                                           EditText txt_username,
                                           EditText txt_password,
                                           EditText txt_repassword,
                                           Button btn_register) {
        accountRegisterService = new AccountRegisterService(context, txt_username, txt_password, txt_repassword, btn_register);
    }

    public int TLSPwdResetAskCode(String countryCode, String phoneNumber, TLSPwdResetListener listener) {
        return accountHelper.TLSPwdResetAskCode(Util.getWellFormatMobile(countryCode, phoneNumber), listener);
    }

    public int TLSPwdResetVerifyCode(String code, TLSPwdResetListener listener) {
        return accountHelper.TLSPwdResetVerifyCode(code, listener);
    }

    /**
    * 代理QQ登录的接口
    * */
    public void initQQLoginService(Activity activity, Button btn_qqlogin) {
        qqLoginService = new QQLoginService(activity, btn_qqlogin);
    }

    public boolean qqHasLogined() {
        return qqLoginService.qqHasLogined();
    }

    public void onActivityResultForQQLogin(int requestCode, int resultCode, Intent data) {
        qqLoginService.onActivityResult(requestCode, resultCode, data);
    }

    public void qqLogOut() {
        try {
            qqLoginService.qqLogout();
        } catch (Exception e) {}
    }

    /**
     * 代理微信登录的接口
     * */
    public void initWXLoginService(Context context, Button btn_wxlogin) {
        wxLoginService = new WXLoginService(context, btn_wxlogin);
    }


    public interface RefreshUserSigListener extends TLSRefreshUserSigListener {
        public void onUserSigNotExist();
    }

    public interface GuestLoginListener extends TLSGuestLoginListener{}

}
