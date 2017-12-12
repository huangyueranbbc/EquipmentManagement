package com.hyr.equipment.management.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hyr.equipment.management.R;
import com.hyr.equipment.management.adapters.TimestampTypeAdapter;
import com.hyr.equipment.management.adapters.UserHistoryRecordAdapter;
import com.hyr.equipment.management.domain.TbEqUserEquipmentRecord;
import com.hyr.equipment.management.domain.TbEqUserInfo;
import com.hyr.equipment.management.global.GlobalValue;
import com.hyr.equipment.management.utils.CacheUtils;
import com.hyr.equipment.management.utils.StringUtils;
import com.hyr.equipment.management.utils.UIUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.song.refresh_view.PullToRefreshView;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * 设备使用记录Activity
 */
public class EquipmentRecordActivity extends AppCompatActivity {

    private ListView mListView;
    private PullToRefreshView mRefreshView;
    private List<HashMap<String, String>> mData; // POJP
    private UserHistoryRecordAdapter mAdapter;

    private boolean isLoading = false; //防止重复请求 false==不在请求中

    private static final String TAG = "EquipmentRecordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_record);
        // TODO 初始化 listview 正常情况是从网络获取数据
        init();
    }

    private void init() {
        mListView = (ListView) findViewById(R.id.list);
        mRefreshView = (PullToRefreshView) findViewById(R.id.refreshView);
        mRefreshView.setColorSchemeColors(Color.RED, Color.BLUE); // 颜色
//        mRefreshView.setSmileStrokeWidth(8); // 设置绘制的笑脸的宽度
//        mRefreshView.setSmileInterpolator(new LinearInterpolator()); // 笑脸动画转动的插值器
//        mRefreshView.setSmileAnimationDuration(2000); // 设置笑脸旋转动画的时长

        // 取出数据
        ArrayList<TbEqUserEquipmentRecord> records = (ArrayList<TbEqUserEquipmentRecord>) getIntent().getSerializableExtra("recordInfos");

        mRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData();
            }

        });
        LayoutInflater lif = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View headerView = lif.inflate(R.layout.list_header, null);
        mListView.addHeaderView(headerView);

        mData = new ArrayList<HashMap<String, String>>();

        for (TbEqUserEquipmentRecord r : records) {
            HashMap<String, String> map = new HashMap<>();
            map.put("eq_name", r.getEquipmentName());
            map.put("eq_starttime", r.getStarttime().toLocaleString());
            map.put("eq_endtime", r.getEndtime().toLocaleString());
            if (r.getEquipmentStatus() == 1) {
                map.put("eq_status", "使用中");
            } else {
                map.put("eq_status", "使用完毕");
            }
            mData.add(map);
        }
        mAdapter = new UserHistoryRecordAdapter(this, mData);
        mListView.setAdapter(mAdapter);
    }


    private void requestData() {
        mRefreshView.postDelayed(new Runnable() {
            @Override
            public void run() {
                onLoad();
            }
        }, 1000);
    }

    /**
     * 访问网络 重新加载数据
     */
    private void onLoad() {
        Log.i(TAG, "onLoad: ");
        final Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();

        if (!isLoading) {
            isLoading = true;
            // 请求网络数据 获取用户信息和用户设备使用记录
            final String url = GlobalValue.BASE_URL + "/equipment/finduserrecords";
            RequestParams params = new RequestParams();
            String userLoginInfo = CacheUtils.getCacheNotiming(GlobalValue.LOGININFO);

            if (StringUtils.isEmpty(userLoginInfo)) { // 未登录
                // 跳转到登录界面 并提示用户未登录
                MyDynamicToast.errorMessage(UIUtils.getContext(), "未检测到登录信息,请先登录!");
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

            TbEqUserInfo user = gson.fromJson(userLoginInfo, TbEqUserInfo.class);
            params.addQueryStringParameter("userId", user.getUserId().toString());
            params.addQueryStringParameter("r", new Random().nextInt() + ""); // 防止重复提交

            // 获取网络数据
            HttpUtils http = new HttpUtils();
            http.send(HttpRequest.HttpMethod.GET,
                    url, params,
                    new RequestCallBack<String>() {
                        //private ProgressDialog dialog = new ProgressDialog(UserInfoActivity.this);

                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            // 得到图书馆list集合
                            List<TbEqUserEquipmentRecord> result = gson.fromJson(responseInfo.result, new TypeToken<List<TbEqUserEquipmentRecord>>() {
                            }.getType());
                            Log.i(TAG, "onSuccess: " + gson.toString());

                            if (result != null) {// 获取成功
                                // TODO 更新页面
                                mData = new ArrayList<HashMap<String, String>>();
                                for (TbEqUserEquipmentRecord r : result) {
                                    HashMap<String, String> map = new HashMap<>();
                                    map.put("eq_name", r.getEquipmentName());
                                    map.put("eq_starttime", r.getStarttime().toLocaleString());
                                    map.put("eq_endtime", r.getEndtime().toLocaleString());
                                    if (r.getEquipmentStatus() == 1) {
                                        map.put("eq_status", "使用中");
                                    } else {
                                        map.put("eq_status", "使用完毕");
                                    }

                                    mData.add(map);
                                }

                                mAdapter = new UserHistoryRecordAdapter(EquipmentRecordActivity.this, mData);
                                mListView.setAdapter(mAdapter);
                                mAdapter.notifyDataSetChanged(); //
                                isLoading = false;
                                //dialog.dismiss();
                                mRefreshView.setRefreshing(false);
                            }

                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            MyDynamicToast.errorMessage(UIUtils.getContext(), "数据加载失败,请检查网络后重试!");
                            //dialog.dismiss();
                            isLoading = false;
                        }

                        @Override
                        public void onStart() {
                            super.onStart();
                        }

                        @Override
                        public void onLoading(long total, long current, boolean isUploading) {
                            super.onLoading(total, current, isUploading);
                            //dialog.setTitle("正在加载中");
                            //dialog.show();
                        }
                    });

        }
    }
}
