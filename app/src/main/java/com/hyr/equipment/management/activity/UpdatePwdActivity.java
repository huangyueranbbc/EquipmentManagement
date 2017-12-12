package com.hyr.equipment.management.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
import java.util.UUID;

public class UpdatePwdActivity extends AppCompatActivity {

    private EditText pwd1;
    private EditText pwd2;
    private Button btn_login;

    private boolean isLoading = false; // 防止重复登录

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pwd);

        pwd1 = (EditText) findViewById(R.id.et_pwd1);
        pwd2 = (EditText) findViewById(R.id.et_pwd2);
        btn_login = (Button) findViewById(R.id.login_bt);


        btn_login.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             String p1 = pwd1.getText().toString();
                                             String p2 = pwd2.getText().toString();
                                             //  取出用户登录信息
                                             final Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                                             String userExtJson = CacheUtils.getCacheNotiming(GlobalValue.TBUSEREXTINFO);
                                             if (!isLoading) {
                                                 if (StringUtils.isEmpty(p1) || StringUtils.isEmpty(p2)) {
                                                     MyDynamicToast.errorMessage(UIUtils.getContext(), "请输入密码!");
                                                     isLoading = false;
                                                 } else if (p1.length() > 15 || p2.length() > 15) {
                                                     MyDynamicToast.errorMessage(UIUtils.getContext(), "密码位数不能超过15位!");
                                                     isLoading = false;
                                                 } else if (!p1.equals(p2)) {
                                                     MyDynamicToast.errorMessage(UIUtils.getContext(), "两次输入的密码不一致!");
                                                     isLoading = false;
                                                 } else if (StringUtils.isEmpty(userExtJson)) { // 没有登录
                                                     MyDynamicToast.errorMessage(UIUtils.getContext(), "没有登录信息,请先登录!");
                                                     isLoading = false;
                                                     startActivity(new Intent(UpdatePwdActivity.this, LoginActivity.class));
                                                     UpdatePwdActivity.this.finish();
                                                 } else {
                                                     // 修改密码
                                                     final String url = GlobalValue.BASE_URL + "/user/changepwd";
                                                     //String localMacAddress = new PhoneDeviceUtil().getLocalMacAddress(); // 用户手机Mac唯一地址
                                                     //String login_token = localMacAddress + System.currentTimeMillis(); // 用户登录token
                                                     String login_token = UUID.randomUUID().toString();
                                                     RequestParams params = new RequestParams();
                                                     TbUserExt userExt = gson.fromJson(userExtJson, new TypeToken<TbUserExt>() {
                                                     }.getType());
                                                     params.addQueryStringParameter("studentNo", userExt.getUser().getUserStudentNo());
                                                     params.addQueryStringParameter("password", p1);
                                                     params.addQueryStringParameter("login_token", login_token);
                                                     params.addQueryStringParameter("r", new Random().nextInt() + ""); // 防止重复提交

                                                     // TODO 登录逻辑
                                                     HttpUtils http = new HttpUtils();
                                                     http.send(HttpRequest.HttpMethod.GET,
                                                             url, params,
                                                             new RequestCallBack<String>() {

                                                                 @Override
                                                                 public void onSuccess(ResponseInfo<String> responseInfo) {
                                                                     LMSResult result = gson.fromJson(responseInfo.result, new TypeToken<LMSResult>() {
                                                                     }.getType());
                                                                     if (result.getStatus() == 200) { // 成功
                                                                         MyDynamicToast.successMessage(UIUtils.getContext(), "修改成功!");
                                                                         startActivity(new Intent(UpdatePwdActivity.this, LoginActivity.class));
                                                                         finish(); // 销毁该页面 返回到Launch界面
                                                                     } else { // 失败
                                                                         MyDynamicToast.errorMessage(UIUtils.getContext(), "修改密码失败,请稍后重试!");
                                                                     }

                                                                 }

                                                                 @Override
                                                                 public void onFailure(HttpException e, String s) {
                                                                     MyDynamicToast.errorMessage(UIUtils.getContext(), "修改密码失败,请检查网络后重试!");
                                                                     isLoading = false;
                                                                 }
                                                             });

                                                 }

                                             }


                                         }
                                     }

        );
    }
}
