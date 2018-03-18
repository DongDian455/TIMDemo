package com.dongdian.jj.timdemo.model;

import android.content.Context;

import com.dongdian.jj.timdemo.adapters.ProfileSummaryAdapter;

/**
 * {@link ProfileSummaryAdapter}的数据元素,可提取获取资料的摘要
 */
public interface ProfileSummary {


    /**
     * 获取头像资源
     */
    int getAvatarRes();


    /**
     * 获取头像地址
     */
    String getAvatarUrl();


    /**
     * 获取名字
     */
    String getName();


    /**
     * 获取描述信息
     */
    String getDescription();


    /**
     * 获取id
     */
    String getIdentify();


    /**
     * 显示详情等点击事件
     *
     * @param context 上下文
     */
    void onClick(Context context);


}
