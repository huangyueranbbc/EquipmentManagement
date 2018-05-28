package com.hyr.equipment.management.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hyr.equipment.management.R;
import com.hyr.equipment.management.adapters.TimestampTypeAdapter;
import com.hyr.equipment.management.domain.LMSResult;
import com.hyr.equipment.management.domain.TbUserExt;
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

import java.security.Timestamp;
import java.util.Random;

import id.arieridwan.lib.PageLoader;

public class LaunchActivity extends BaseActivity {

    private static final String TAG = "LaunchActivity";

    public PageLoader pageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        pageLoader = (PageLoader) findViewById(R.id.pageloader);
        // pageLoader.setImageLoading(); // TODO 设置loading图标
        pageLoader.setLoadingAnimationMode(pageLoader.FLIP_MODE);
        pageLoader.startProgress();

        // TODO 判断是否登录
        // 未登录 跳转到登录界面
        // 判断进入登录还是主界面
        // 从缓存中取
        final Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        String userExtJson = CacheUtils.getCacheNotiming(GlobalValue.TBUSEREXTINFO);
        Log.i(TAG, "onAnimationEnd: " + userExtJson);
        if (StringUtils.isEmpty(userExtJson)) { // 没有登录 跳转到登录页面
            startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
            LaunchActivity.this.finish();
        } else { // 有登录记录 取网络中验证
            TbUserExt userExt = gson.fromJson(userExtJson, new TypeToken<TbUserExt>() {
            }.getType());

            final String url = GlobalValue.BASE_URL + "/user/islogin";
            RequestParams params = new RequestParams();
            params.addQueryStringParameter("username", String.valueOf(userExt.getUser().getUserStudentNo()));
            params.addQueryStringParameter("login_token", String.valueOf(userExt.getUser_login_token()));
            params.addQueryStringParameter("r", new Random().nextInt() + ""); // 防止重复提交

            HttpUtils http = new HttpUtils();
            http.send(HttpRequest.HttpMethod.GET,
                    url, params,
                    new RequestCallBack<String>() {

                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            // 得到图书馆list集合
                            LMSResult result = gson.fromJson(responseInfo.result, new TypeToken<LMSResult>() {
                            }.getType());
                            if (result.getStatus() == 200) { // 已经登录
                                // 跳转主页
                                startActivity(new Intent(LaunchActivity.this, MainActivity.class));
                                LaunchActivity.this.finish();
                            } else {
                                // 跳转登录页面
                                startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
                                LaunchActivity.this.finish();
                            }
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            pageLoader.stopProgressAndFailed();
                            MyDynamicToast.errorMessage(UIUtils.getContext(), "登录失败,请重新登录!");
                            startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
                            LaunchActivity.this.finish();
                        }
                    });
        }
    }
}
