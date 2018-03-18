package com.dongdian.jj.timdemo.model;


import android.util.Log;

import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.ext.sns.TIMFriendGroup;
import com.tencent.imsdk.ext.sns.TIMFriendshipProxy;
import com.tencent.qcloud.presentation.event.FriendshipEvent;
import com.tencent.qcloud.presentation.event.RefreshEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * 好友列表缓存数据结构
 */
public class FriendshipInfo extends Observable implements Observer {

    private final String TAG = "FriendshipInfo";


    private List<String> groups;
    private Map<String, List<FriendProfile>> friends;

    private static FriendshipInfo instance;

    private FriendshipInfo(){
        groups = new ArrayList<>();
        friends = new HashMap<>();
        FriendshipEvent.getInstance().addObserver(this);
        RefreshEvent.getInstance().addObserver(this);
        refresh();
    }

    public synchronized static FriendshipInfo getInstance(){
        if (instance == null){
            instance = new FriendshipInfo();
        }
        return instance;
    }

    /**
     * This method is called if the specified {@code Observable} object's
     * {@code notifyObservers} method is called (because the {@code Observable}
     * object has been updated.
     *
     * @param observable the {@link Observable} object.
     * @param data       the data passed to {@link Observable#notifyObservers(Object)}.
     */
    @Override
    public void update(Observable observable, Object data) {
        TIMManager.getInstance().getEnv();
        if (observable instanceof FriendshipEvent){
            if (data instanceof FriendshipEvent.NotifyCmd){
                FriendshipEvent.NotifyCmd cmd = (FriendshipEvent.NotifyCmd) data;
                Log.d(TAG, "get notify type:" + cmd.type);
                switch (cmd.type){
                    case REFRESH:
                    case DEL:
                    case ADD:
                    case PROFILE_UPDATE:
                    case ADD_REQ:
                    case GROUP_UPDATE:
                        refresh();
                        break;
                    default:
                        break;

                }
            }
        }else{
            refresh();
        }
    }


    private void refresh(){
        groups.clear();
        friends.clear();
        Log.d(TAG, "get friendship info id :" + UserInfo.getInstance().getId());
        List<TIMFriendGroup> timFriendGroups = TIMFriendshipProxy.getInstance().getFriendsByGroups(null);
        if (timFriendGroups == null) return;
        for (TIMFriendGroup group : timFriendGroups){
            groups.add(group.getGroupName());
            List<FriendProfile> friendItemList = new ArrayList<>();
            for (TIMUserProfile profile : group.getProfiles()){
                friendItemList.add(new FriendProfile(profile));
            }
            friends.put(group.getGroupName(), friendItemList);
        }
        setChanged();
        notifyObservers();
    }

    /**
     * 获取分组列表
     */
    public List<String> getGroups(){
        return groups;
    }

    public String[] getGroupsArray(){
        return groups.toArray(new String[groups.size()]);
    }


    /**
     * 获取好友列表摘要
     */
    public Map<String, List<FriendProfile>> getFriends(){
        return friends;
    }

    /**
     * 判断是否是好友
     *
     * @param identify 需判断的identify
     */
    public boolean isFriend(String identify){
        for (String key : friends.keySet()){
            for (FriendProfile profile : friends.get(key)){
                if (identify.equals(profile.getIdentify())) return true;
            }
        }
        return false;
    }


    /**
     * 获取好友资料
     *
     * @param identify 好友id
     */
    public FriendProfile getProfile(String identify){
        for (String key : friends.keySet()){
            for (FriendProfile profile : friends.get(key)){
                if (identify.equals(profile.getIdentify())) return profile;
            }
        }
        return null;
    }

    /**
     * 清除数据
     */
    public void clear(){
        if (instance == null) return;
        groups.clear();
        friends.clear();
        instance = null;
    }


}
