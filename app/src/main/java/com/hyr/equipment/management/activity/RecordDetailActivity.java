package com.hyr.equipment.management.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hyr.equipment.management.R;
import com.hyr.equipment.management.adapters.TimestampTypeAdapter;
import com.hyr.equipment.management.domain.LMSResult;
import com.hyr.equipment.management.domain.TbEqUserEquipmentRecord;
import com.hyr.equipment.management.domain.TbUserExt;
import com.hyr.equipment.management.global.GlobalValue;
import com.hyr.equipment.management.utils.CacheUtils;
import com.hyr.equipment.management.utils.StringUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Random;

import cn.refactor.lib.colordialog.ColorDialog;
import cn.refactor.lib.colordialog.PromptDialog;
import ru.katso.livebutton.LiveButton;

public class RecordDetailActivity extends AppCompatActivity {

    private Button btn02;

    private static final String TAG = "RecordDetailActivity";

    private boolean isLoading = false; //防止重复请求 false==不在请求中
    private TbEqUserEquipmentRecord record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_detail);

        Intent intent = getIntent();
        record = (TbEqUserEquipmentRecord) intent.getSerializableExtra("record");

        // 获取屏幕大小
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

        LiveButton button = (LiveButton) findViewById(R.id.button);
        ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
        layoutParams.width = screenWidth / 2;
        button.setLayoutParams(layoutParams);

        // 报修
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO 提示
                ColorDialog dialog = new ColorDialog(RecordDetailActivity.this);
                dialog.setAnimationEnable(true);
                dialog.setTitle("报修设备");
                dialog.setContentText("确认进行报修该设备吗?");
                dialog.setCancelable(false); // 取消周围点击事件
                dialog.setPositiveListener("确定", new ColorDialog.OnPositiveListener() {
                    @Override
                    public void onClick(final ColorDialog colorDialog) {

                        final Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                        String userExtJson = CacheUtils.getCacheNotiming(GlobalValue.TBUSEREXTINFO);

                        if (!isLoading) {
                            if (StringUtils.isEmpty(userExtJson)) { // 没有登录 跳转到登录页面
                                startActivity(new Intent(RecordDetailActivity.this, LoginActivity.class));
                                finish();
                            } else { // 如果有登录信息 访问网络进行使用设备
                                TbUserExt userExt = gson.fromJson(userExtJson, new TypeToken<TbUserExt>() {
                                }.getType());

                                final String url = GlobalValue.BASE_URL + "/equipment/repair";
                                RequestParams params = new RequestParams();
                                params.addQueryStringParameter("username", String.valueOf(userExt.getUser().getUserStudentNo()));
                                params.addQueryStringParameter("login_token", String.valueOf(userExt.getUser_login_token()));
                                Long equipmentId = record.getEquipmentId();
                                params.addQueryStringParameter("equipmentId", String.valueOf(equipmentId));
                                params.addQueryStringParameter("recordId", record.getRecordId().toString());
                                params.addQueryStringParameter("r", new Random().nextInt() + ""); // 防止重复提交

                                // TODO 登录逻辑
                                HttpUtils http = new HttpUtils();
                                http.send(HttpRequest.HttpMethod.GET,
                                        url, params,
                                        new RequestCallBack<String>() {

                                            @Override
                                            public void onSuccess(ResponseInfo<String> responseInfo) {
                                                // 使用成功
                                                LMSResult result = gson.fromJson(responseInfo.result, new TypeToken<LMSResult>() {
                                                }.getType());
                                                if (420 == result.getStatus()) { // 不是组长
                                                    MyDynamicToast.errorMessage(RecordDetailActivity.this, "操作失败,该操作只允许组长进行!");
                                                } else if (406 == result.getStatus()) { // 未登录
                                                    MyDynamicToast.errorMessage(RecordDetailActivity.this, "用户未登录,请先登录!");
                                                    startActivity(new Intent(RecordDetailActivity.this, LoginActivity.class));
                                                    finish();
                                                } else if (result.getStatus() == 550) {
                                                    MyDynamicToast.errorMessage(RecordDetailActivity.this, "报修失败,请重试!");
                                                } else if (result.getStatus() == 400) {
                                                    MyDynamicToast.errorMessage(RecordDetailActivity.this, "报修失败,请检查网络后重试!");
                                                } else if (result.getStatus() == 200) { // 报修成功
                                                    PromptDialog promptDialog = new PromptDialog(RecordDetailActivity.this)
                                                            .setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS)
                                                            .setAnimationEnable(true)
                                                            .setTitleText("报修成功")
                                                            .setContentText("设备报修成功!请等待管理员确认!")
                                                            .setPositiveListener("确认", new PromptDialog.OnPositiveListener() {
                                                                @Override
                                                                public void onClick(PromptDialog _dialog) {
                                                                    _dialog.dismiss();
                                                                }
                                                            });

                                                    promptDialog.setCancelable(false);
                                                    promptDialog.show();
                                                }
                                                colorDialog.dismiss();
                                            }

                                            @Override
                                            public void onFailure(HttpException e, String s) {
                                                MyDynamicToast.errorMessage(RecordDetailActivity.this, "报修失败,请检查网络后重试!");
                                                isLoading = false;
                                            }
                                        });

                            }
                        }

                    }
                }).setNegativeListener("取消", new ColorDialog.OnNegativeListener() {
                    @Override
                    public void onClick(ColorDialog dialog) {
                        dialog.dismiss();
                    }
                }).show();

            }
        });

        btn02 = (Button) findViewById(R.id.btn02);
        btn02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView tvEqName = (TextView) findViewById(R.id.tv_eqName);
        TextView tvStartTime = (TextView) findViewById(R.id.tv_startTime);
        TextView tvStatus = (TextView) findViewById(R.id.tv_status);
        TextView tvGroupLeader = (TextView) findViewById(R.id.tv_groupLeader);
        TextView tvMember = (TextView) findViewById(R.id.tv_member);
        TextView tvRoom = (TextView) findViewById(R.id.tv_room);
        TextView tvClass = (TextView) findViewById(R.id.tv_class);

        tvEqName.setText(record.getEquipmentName());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = formatter.format(record.getStarttime());
        tvStartTime.setText(date);
        if (record.getEquipmentStatus() == 1) {
            tvStatus.setText("使用中");
        } else {
            tvStatus.setText("未使用");
        }
        String[] members = record.getUserNames().split(",");
        tvGroupLeader.setText(members[0]);

        if (members.length > 1) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < members.length; i++) {
                Log.i(TAG, "onCreate: members:" + members[i]);
                sb.append(members[i] + "  ");
            }
            tvMember.setText(sb.toString());
        } else {
            tvMember.setText("没有组员");
        }

        tvRoom.setText(record.getEquipmentRoomName());

        tvClass.setText(record.getUserClasses());
    }
}
