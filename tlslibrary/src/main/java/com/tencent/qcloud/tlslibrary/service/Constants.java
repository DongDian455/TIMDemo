package com.tencent.qcloud.tlslibrary.service;

/**
 * Created by dgy on 15/8/13.
 */
public class Constants {

    // 自动填写短信验证的相关常量
    public final static String SMS_INBOX_URI = "content://sms/inbox";
    public final static String SMS_LOGIN_SENDER             = "10655020069275";
    public final static String SMS_REGISTER_SENDER          = "10655020051075";
    public final static String PHONEPWD_RESET_SENDER        = "10655020051075";
    public final static String PHONEPWD_REGISTER_SENDER     = "10655020051075";

    public final static int    CHECKCODE_LENGTH = 4;

    // 配置信息
    public final static String TLS_SETTING = "tencent.tls.ui.TLS_SETTING"; // 配置名
    public final static String SETTING_LOGIN_WAY = "tencent.tls.ui.SETTING_LOGIN_WAY"; // Key

    // 定义业务数据键值
    public final static String COUNTRY_CODE = "tencent.tls.ui.COUNTRY_CODE";
    public final static String PHONE_NUMBER = "tencent.tls.ui.PHONE_NUMBER";
    public final static String USERNAME = "tencent.tls.ui.USERNAME";
    public final static String PASSWORD = "tencent.tls.ui.PASSWORD";

    // 登录方式
    public final static String EXTRA_LOGIN_WAY = "com.tencent.tls.LOGIN_WAY";
    public final static int NON_LOGIN = 0;
    public final static int SMS_LOGIN = (1 << 0);
    public final static int QQ_LOGIN = (1 << 1);
    public final static int WX_LOGIN = (1 << 2);
    public final static int USRPWD_LOGIN = (1 << 3);
    public final static int PHONEPWD_LOGIN = (1 << 4);
    public final static int GUEST_LOGIN = (1 << 5);

    // 短信注册的相关常量
    public final static int SMS_REG_REQUEST_CODE = 0;
    public final static String EXTRA_SMS_REG = "com.tencent.tls.SMS_REG";
    public final static int SMS_REG_FAIL = 0;
    public final static int SMS_REG_SUCCESS = 1;
    public final static int SMS_REG_NON = 2;


    // 短信登录的相关常量
    public final static String EXTRA_MOBILE = "com.tencent.tls.MOBILE";
    public final static String EXTRA_SMS_LOGIN = "com.tencent.tls.SMS_LOGIN";
    public final static int SMS_LOGIN_FAIL = 0;
    public final static int SMS_LOGIN_SUCCESS = 1;
    public final static int SMS_LOGIN_NON = 2;

    // QQ相关的常量
    public final static String EXTRA_QQ_LOGIN = "com.tencent.tls.QQ_LOGIN";
    public final static int QQ_LOGIN_FAIL = 0;
    public final static int QQ_LOGIN_SUCCESS = 1;
    public final static int QQ_LOGIN_NON = 2;
    public final static String EXTRA_QQ_OPENID = "com.tencent.tls.QQ_OPENID";
    public final static String EXTRA_QQ_ACCESS_TOKEN = "com.tencent.tls.QQ_ACCESS_TOKEN";

    // 微信相关的常量
    public final static String EXTRA_WX_LOGIN = "com.tencent.tls.WX_LOGIN";
    public final static int WX_LOGIN_FAIL = 0;
    public final static int WX_LOGIN_SUCCESS = 1;
    public final static int WX_LOGIN_NON = 2;
    public final static String EXTRA_WX_OPENID = "com.tencent.tls.WX_OPENID";
    public final static String EXTRA_WX_ACCESS_TOKEN = "com.tencent.tls.WX_ACCESS_TOKEN";

    // 账号密码注册相关常量
    public final static int USRPWD_REG_REQUEST_CODE = 1;
    public final static String EXTRA_USRPWD_REG = "com.tencent.tls.SMS_REG";
    public final static int USRPWD_REG_FAIL = 0;
    public final static int USRPWD_REG_SUCCESS = 1;
    public final static int USRPWD_REG_NON = 2;

    // 账号密码登录的相关常量
    public final static String EXTRA_USRPWD_LOGIN = "com.tencent.tls.USRPWD_LOGIN";
    public final static int USRPWD_LOGIN_FAIL = 0;
    public final static int USRPWD_LOGIN_SUCCESS = 1;
    public final static int USRPWD_LOGIN_NON = 2;

    // 手机密码注册的相关常量
    public final static String EXTRA_PHONEPWD_REG_RST = "com.tencent.tls.PHONEPWD_REG_RST";
    public final static int PHONEPWD_NON = 0;
    public final static int PHONEPWD_REGISTER = 1;
    public final static int PHONEPWD_RESET = 2;
    public final static int PHONEPWD_REG_REQUEST_CODE = 2;

    // 手机密码重置的相关常量
    public final static int PHONEPWD_RESET_REQUEST_CODE = 3;

    // 手机密码登录的相关常量
    public final static String EXTRA_PHONEPWD_LOGIN = "com.tencent.tls.EXTRA_PHONEPWD_LOGIN";
    public final static int PHONEPWD_LOGIN_FAIL = 0;
    public final static int PHONEPWD_LOGIN_SUCCESS = 1;
    public final static int PHONEPWD_LOGIN_NON = 2;

    // 定义登陆成功或失败时跳转的界面（PackageName + ActivityClassName）
    public final static String EXTRA_THIRDAPP_PACKAGE_NAME_SUCC = "com.tencent.tls.THIRDAPP_PACKAGE_NAME_SUCC";
//    public final static String EXTRA_THIRDAPP_CLASS_NAME_SUCC = "com.tencent.tls.THIRDAPP_CLASS_NAME_SUCC";
    public final static String EXTRA_THIRDAPP_CLASS_NAME_SUCC = null;
    public final static String EXTRA_THIRDAPP_PACKAGE_NAME_FAIL = "com.tencent.tls.THIRDAPP_PACKAGE_NAME_FAIL";
    public final static String EXTRA_THIRDAPP_CLASS_NAME_FAIL = "com.tencent.tls.THIRDAPP_CLASS_NAME_FAIL";

    public final static String EXTRA_IMG_CHECKCODE = "com.tencent.tls.EXTRA_IMG_CHECKCODE";

    // 通过<包名，完整类名>的形式指定登录成功或失败时需要跳转的界面
    public static String thirdappPackageNameSucc;
    public static String thirdappClassNameSucc;
    public static String thirdappPackageNameFail;
    public static String thirdappClassNameFail;
}
