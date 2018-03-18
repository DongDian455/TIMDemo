package com.tencent.qcloud.tlslibrary.customview;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;

import com.tencent.qcloud.tlslibrary.helper.MResource;


/**
 * Created by dgy on 15/7/15.
 */
public class EditTextWithListPopupWindow extends EditText {

    private final int DRAWABLE_RIGHT = 2;
    private final int UP_ARROW = 0;
    private final int DOWN_ARROW = 1;


    private ListPopupWindow listPopupWindow;
    private Drawable rightDrawable;
    private String[] list;

    private OnItemChangedListener onItemChangedListener = new OnItemChangedListener() {
        @Override
        public void onItemChangedListener(String item) {}
    };

    public EditTextWithListPopupWindow(Context context) {
        super(context);
        init(context);
    }

    public EditTextWithListPopupWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EditTextWithListPopupWindow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    @SuppressLint("NewApi")
    private void init(Context context) {
        setInputType(InputType.TYPE_NULL);  // 禁止弹出输入法窗口

        // 获取EditText的DrawableRight，假如没有设置我们使用默认的图片
        rightDrawable = getCompoundDrawables()[DRAWABLE_RIGHT];
        if (rightDrawable == null) {
            rightDrawable = getResources().getDrawable(MResource.getIdByName(
                    getContext(), "drawable", "tencent_tls_ui_down_arrow"));
        }
        rightDrawable.setBounds(0, 0, rightDrawable.getIntrinsicWidth(), rightDrawable.getIntrinsicHeight());
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1], rightDrawable, getCompoundDrawables()[3]);

        list = getResources().getStringArray(MResource.getIdByName(getContext(), "array", "tencent_tls_ui_countryCode"));

        listPopupWindow = new ListPopupWindow(context);
        listPopupWindow.setAdapter(new ArrayAdapter<String>(context,
                MResource.getIdByName(getContext(), "layout", "tencent_tls_ui_item"), list));
        listPopupWindow.setAnchorView(this);
        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = list[position];
                EditTextWithListPopupWindow.this.setText(item);
                listPopupWindow.dismiss();
                onItemChangedListener.onItemChangedListener(item);
            }
        });

        listPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setArrow(DOWN_ARROW);
            }
        });

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // 显示下拉列表
                    setArrow(UP_ARROW);
                    if (!listPopupWindow.isShowing()) {
                        listPopupWindow.show();
                    }
                }
                return true;
            }
        });
    }

    private void setArrow(int flag) {
        Drawable drawable;
        if (UP_ARROW == flag) {
            drawable = getResources().getDrawable(
                    MResource.getIdByName(getContext(), "drawable", "tencent_tls_ui_up_arrow"));
        } else {
            drawable = getResources().getDrawable(
                    MResource.getIdByName(getContext(), "drawable", "tencent_tls_ui_down_arrow"));
        }

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1], drawable, getCompoundDrawables()[3]);
    }

    public void setOnItemChangedListener(OnItemChangedListener onItemChangedListener) {
        this.onItemChangedListener = onItemChangedListener;
    }

    public interface OnItemChangedListener {

        public void onItemChangedListener(String itemValue);

    }
}
