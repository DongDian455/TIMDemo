package com.tencent.qcloud.tlslibrary.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tencent.qcloud.tlslibrary.helper.MResource;
import com.tencent.qcloud.tlslibrary.service.TLSService;
import com.tencent.qcloud.tlslibrary.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QQWXFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QQWXFragment extends Fragment {

    private TLSService tlsService;
    private Activity activity;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QQWXFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QQWXFragment newInstance(String param1, String param2) {
        QQWXFragment fragment = new QQWXFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public QQWXFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tlsService = TLSService.getInstance();
        activity = getActivity();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.tencent_tls_ui_fragment_qqwx, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        tlsService.initGuestLoginService(activity, (Button) view.findViewById(MResource.getIdByName(activity, "id", "btn_guestlogin")));
        tlsService.initQQLoginService(activity, (Button) view.findViewById(MResource.getIdByName(activity, "id", "btn_qqlogin")));
        tlsService.initWXLoginService(activity, (Button) view.findViewById(MResource.getIdByName(activity, "id", "btn_wxlogin")));
    }

}
