package com.tencent.qcloud.tlslibrary.helper;

import android.app.Activity;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.widget.EditText;

import com.tencent.qcloud.tlslibrary.service.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dgy on 15/8/15.
 * 监听短信数据库
 */


public class SmsContentObserver extends ContentObserver {
    private Cursor cursor;
    private Activity activity;
    private EditText editText;
    private String phoneNumber;

    public SmsContentObserver(Handler handler, Activity activity, EditText editText, String phoneNumber) {
        super(handler);

        this.cursor = null;
        this.activity = activity;
        this.editText = editText;
        this.phoneNumber = phoneNumber;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        // 读取收件箱中指定号码的短信
        cursor = activity.managedQuery(Uri.parse(Constants.SMS_INBOX_URI),
                new String[]{"_id", "address", "read", "body"},
                " address=? and read=?",
                new String[]{phoneNumber, "0"}, "_id desc");
        // 按id排序，如果按date排序的话，修改手机时间后，读取的短信就不准了
        if (cursor != null && cursor.getCount() > 0) {
            ContentValues values = new ContentValues();
            values.put("read", "1"); // 修改短信为已读模式
            cursor.moveToNext();
            int smsbodyColumn = cursor.getColumnIndex("body");
            String smsBody = cursor.getString(smsbodyColumn);
            editText.setText(getDynamicPassword(smsBody));

        }
        // 在用managedQuery的时候，不能主动调用close()方法, 否则在Android 4.0+的系统上, 会发生崩溃
        if (Build.VERSION.SDK_INT < 14) {
            cursor.close();
        }
    }

    /**
     * 从字符串中截取连续4位数字组合 ([0-9]{" + 6 + "})截取六位数字 进行前后断言不能出现数字 用于从短信中获取动态密码
     *
     * @param str 短信内容
     * @return 截取得到的6位动态密码
     */
    public String getDynamicPassword(String str) {
        // 6是验证码的位数一般为六位
        Pattern continuousNumberPattern = Pattern.compile("(?<![0-9])([0-9]{" + Constants.CHECKCODE_LENGTH + "})(?![0-9])");
        Matcher m = continuousNumberPattern.matcher(str);
        String dynamicPassword = "";
        while (m.find()) {
            System.out.println(m.group());
            dynamicPassword = m.group();
        }

        return dynamicPassword;
    }

}
