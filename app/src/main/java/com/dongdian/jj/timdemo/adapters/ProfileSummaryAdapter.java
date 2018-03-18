package com.dongdian.jj.timdemo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.dongdian.jj.timdemo.R;
import com.dongdian.jj.timdemo.model.ProfileSummary;
import com.tencent.qcloud.ui.CircleImageView;

import java.util.List;

/**
 * 好友或群等资料摘要列表的adapter
 */
public class ProfileSummaryAdapter extends ArrayAdapter<ProfileSummary> {


    private int resourceId;
    private View view;
    private ViewHolder viewHolder;

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public ProfileSummaryAdapter(Context context, int resource, List<ProfileSummary> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null) {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.avatar = (CircleImageView) view.findViewById(R.id.avatar);
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            viewHolder.des = (TextView) view.findViewById(R.id.description);
            view.setTag(viewHolder);
        }
        ProfileSummary data = getItem(position);
        viewHolder.avatar.setImageResource(data.getAvatarRes());
        viewHolder.name.setText(data.getName());
        return view;
    }


    public class ViewHolder{
        public ImageView avatar;
        public TextView name;
        public TextView des;
    }
}
