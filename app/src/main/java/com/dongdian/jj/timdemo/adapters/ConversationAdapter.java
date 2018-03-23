package com.dongdian.jj.timdemo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.dongdian.jj.timdemo.R;
import com.dongdian.jj.timdemo.model.Conversation;
import com.dongdian.jj.timdemo.model.UserDto;
import com.dongdian.jj.timdemo.utils.TimeUtil;
import com.tencent.qcloud.ui.CircleImageView;

import java.util.List;

/**
 * 会话界面adapter
 */
public class ConversationAdapter extends ArrayAdapter<Conversation> {

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
    public ConversationAdapter(Context context, int resource, List<Conversation> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null){
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }else{
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) view.findViewById(R.id.name);
            viewHolder.avatar = (CircleImageView) view.findViewById(R.id.avatar);
            viewHolder.lastMessage = (TextView) view.findViewById(R.id.last_message);
            viewHolder.time = (TextView) view.findViewById(R.id.message_time);
            viewHolder.unread = (TextView) view.findViewById(R.id.unread_num);
            view.setTag(viewHolder);
        }
        final Conversation data = getItem(position);
        UserDto userDto=data.getUserDto();
        if(userDto!=null){
            viewHolder.tvName.setText(userDto.getName());
            //这里说明一下
            //1.一般会话列表我们是只显示对方的头像和昵称，所以这里你可以自定扩展一个id来识别,demo这里不展示了
            //2.还有个问题，如果做了1的步骤，当我们发送消息给对方，而对方没有回复我们的时候是没有头像显示的，这时候就应该在点入聊天的时候
            //保存一个用户id,然后查询数据库，如果数据库没有的话就通过网络获取
            //3.当然，上面也只是建议，你有自己的实现思路也是可以的
            Glide.with(getContext()).load(userDto.getAvatar()).dontAnimate().into(viewHolder.avatar);
        }else {
            viewHolder.tvName.setText(data.getName());
            viewHolder.avatar.setImageResource(data.getAvatar());
        }
        viewHolder.lastMessage.setText(data.getLastMessageSummary());
        viewHolder.time.setText(TimeUtil.getTimeStr(data.getLastMessageTime()));
        long unRead = data.getUnreadNum();
        if (unRead <= 0){
            viewHolder.unread.setVisibility(View.INVISIBLE);
        }else{
            viewHolder.unread.setVisibility(View.VISIBLE);
            String unReadStr = String.valueOf(unRead);
            if (unRead < 10){
                viewHolder.unread.setBackground(getContext().getResources().getDrawable(R.drawable.point1));
            }else{
                viewHolder.unread.setBackground(getContext().getResources().getDrawable(R.drawable.point2));
                if (unRead > 99){
                    unReadStr = getContext().getResources().getString(R.string.time_more);
                }
            }
            viewHolder.unread.setText(unReadStr);
        }
        return view;
    }

    public class ViewHolder{
        public TextView tvName;
        public CircleImageView avatar;
        public TextView lastMessage;
        public TextView time;
        public TextView unread;

    }
}
