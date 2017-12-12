package com.hyr.equipment.management.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hyr.equipment.management.R;
import com.hyr.equipment.management.adapters.TimestampTypeAdapter;
import com.hyr.equipment.management.domain.LMSResult;
import com.hyr.equipment.management.domain.TbEqEquipmentInfo;
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
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.decoding.CaptureActivityHandler;

import java.security.Timestamp;
import java.util.Random;

import cn.refactor.lib.colordialog.ColorDialog;
import cn.refactor.lib.colordialog.PromptDialog;

/**
 * 定制化显示扫描界面
 */
public class SecondActivity extends BaseActivity {

    private boolean isLoading = false; // 防止重复登录

    private boolean isLoading2 = false; // 防止重复登录

    private CaptureFragment captureFragment;

    private static final String TAG = "SecondActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        captureFragment = new CaptureFragment();
        // 为二维码扫描界面设置定制化界面
        CodeUtils.setFragmentArgs(captureFragment, R.layout.my_camera);
        captureFragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container, captureFragment).commit();
        initView();
    }

    public static boolean isOpen = false;

    private void initView() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear1);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOpen) {
                    CodeUtils.isLightEnable(true);
                    isOpen = true;
                } else {
                    CodeUtils.isLightEnable(false);
                    isOpen = false;
                }

            }
        });
    }


    /**
     * 二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {

        private String equipmentId;

        /**
         *
         * @param mBitmap 图
         * @param result 解析的结果 扫描到的字符串
         */
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
            bundle.putString(CodeUtils.RESULT_STRING, result);
            resultIntent.putExtras(bundle);
            SecondActivity.this.setResult(RESULT_OK, resultIntent);

            // 访问网络
            if (!isLoading) {
                if (StringUtils.isEmpty(result)) {
                    MyDynamicToast.warningMessage(UIUtils.getContext(), "未扫描到信息,请重试!");
                    isLoading = false;
                    restartErWeiMaSaoMiao();
                } else if (!result.split(":")[0].equals("eqdv") || result.split(":").length != 2) { // 判断是否是正确的扫码信息
                    MyDynamicToast.warningMessage(UIUtils.getContext(), "扫码失败,请稍后重试!");
                    isLoading = false;
                    restartErWeiMaSaoMiao();
                } else { // 扫码成功 获取到了正确的信息
                    // 查询该设备使用状态
                    final String url = GlobalValue.BASE_URL + "/equipment/findequipment";
                    RequestParams params = new RequestParams();
                    params.addQueryStringParameter("equipmentId", result.split(":")[1]);
                    equipmentId = result.split(":")[1];
                    params.addQueryStringParameter("r", new Random().nextInt() + ""); // 防止重复提交

                    // 查询后台 获取设备的状态
                    HttpUtils http = new HttpUtils();
                    http.send(HttpRequest.HttpMethod.GET,
                            url, params,
                            new RequestCallBack<String>() {

                                @Override
                                public void onSuccess(ResponseInfo<String> responseInfo) {
                                    // 得到图书馆list集合
                                    Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                                    TbEqEquipmentInfo eqEquipmentInfo = gson.fromJson(responseInfo.result, new TypeToken<TbEqEquipmentInfo>() {
                                    }.getType()); // 获取设备信息

                                    if (eqEquipmentInfo == null) { // 获取失败
                                        MyDynamicToast.errorMessage(UIUtils.getContext(), "扫码失败,请检查网络服务后重试!");
                                        isLoading = false;
                                    } else { // 获取设备状态成功
                                        // 根据设备状态进行判断
                                        Integer status = eqEquipmentInfo.getEquipmentUsedStatus(); //设备使用状态 1使用中 0未使用
                                        if (status == 1) { // 使用中

                                            // TODO 提示用户该设备正在使用中
                                            PromptDialog promptDialog = new PromptDialog(SecondActivity.this)
                                                    .setDialogType(PromptDialog.DIALOG_TYPE_WARNING)
                                                    .setAnimationEnable(true)
                                                    .setTitleText("操作失败")
                                                    .setContentText("该设备正在使用中!")
                                                    .setPositiveListener("确认", new PromptDialog.OnPositiveListener() {
                                                        @Override
                                                        public void onClick(PromptDialog _dialog) {
                                                            _dialog.dismiss();
                                                            restartErWeiMaSaoMiaoNoWait();
                                                        }
                                                    });
                                            promptDialog.setCancelable(false);
                                            promptDialog.show();

                                        } else if (status == 0) { // 未使用

                                            // TODO 弹出提示框 让用户选择是否使用
                                            ColorDialog dialog = new ColorDialog(SecondActivity.this);
                                            dialog.setCancelable(false); // 取消周围点击事件
                                            dialog.setTitle("是否使用该设备");
                                            dialog.setAnimationEnable(true);
                                            dialog.setContentText("确认开始使用该设备?");
                                            dialog.setContentImage(getResources().getDrawable(R.drawable.sample_img));
                                            dialog.setPositiveListener("确认", new ColorDialog.OnPositiveListener() {
                                                @Override
                                                public void onClick(final ColorDialog dialog) {
                                                    Log.i(TAG, "onClick: " + "我点击啦!!! 应该弹出提示框!!");
                                                    // 判断是否登录
                                                    final Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                                                    String userExtJson = CacheUtils.getCacheNotiming(GlobalValue.TBUSEREXTINFO);

                                                    if (!isLoading2) {
                                                        if (StringUtils.isEmpty(userExtJson)) { // 没有登录 跳转到登录页面
                                                            startActivity(new Intent(SecondActivity.this, LoginActivity.class));
                                                            SecondActivity.this.finish();
                                                        } else { // 如果有登录信息 访问网络进行使用设备
                                                            TbUserExt userExt = gson.fromJson(userExtJson, new TypeToken<TbUserExt>() {
                                                            }.getType());

                                                            final String url = GlobalValue.BASE_URL + "/equipment/insert";
                                                            RequestParams params = new RequestParams();
                                                            params.addQueryStringParameter("username", String.valueOf(userExt.getUser().getUserStudentNo()));
                                                            params.addQueryStringParameter("login_token", String.valueOf(userExt.getUser_login_token()));
                                                            params.addQueryStringParameter("equipmentId", equipmentId);
                                                            params.addQueryStringParameter("r", new Random().nextInt() + ""); // 防止重复提交

                                                            // TODO 登录逻辑
                                                            HttpUtils http = new HttpUtils();
                                                            http.send(HttpRequest.HttpMethod.GET,
                                                                    url, params,
                                                                    new RequestCallBack<String>() {

                                                                        @Override
                                                                        public void onSuccess(ResponseInfo<String> responseInfo) {
                                                                            Log.i(TAG, "onSuccess:  成功啦!!");
                                                                            // 使用成功
                                                                            LMSResult result = gson.fromJson(responseInfo.result, new TypeToken<LMSResult>() {
                                                                            }.getType());
                                                                            if (result.getStatus() == 200) {
                                                                                // 设备使用成功
                                                                                PromptDialog promptDialog = new PromptDialog(SecondActivity.this)
                                                                                        .setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS)
                                                                                        .setAnimationEnable(true)
                                                                                        .setTitleText("操作成功")
                                                                                        .setContentText("成功录入设备使用记录!请开始使用!")
                                                                                        .setPositiveListener("确认", new PromptDialog.OnPositiveListener() {
                                                                                            @Override
                                                                                            public void onClick(PromptDialog _dialog) {
                                                                                                _dialog.dismiss();
                                                                                                SecondActivity.this.finish();
                                                                                            }
                                                                                        });
                                                                                promptDialog.setCancelable(false);
                                                                                dialog.dismiss();
                                                                                promptDialog.show();
                                                                            } else {
                                                                                // 使用失败 提示用户
                                                                                PromptDialog promptDialog = new PromptDialog(SecondActivity.this)
                                                                                        .setDialogType(PromptDialog.DIALOG_TYPE_WRONG)
                                                                                        .setAnimationEnable(true)
                                                                                        .setTitleText("操作失败")
                                                                                        .setContentText("未检测到登录记录,请登录!")
                                                                                        .setPositiveListener("确认", new PromptDialog.OnPositiveListener() {
                                                                                            @Override
                                                                                            public void onClick(PromptDialog _dialog) {
                                                                                                _dialog.dismiss();
                                                                                                startActivity(new Intent(SecondActivity.this, LoginActivity.class));
                                                                                                SecondActivity.this.finish();
                                                                                            }
                                                                                        });
                                                                                promptDialog.setCancelable(false);
                                                                                dialog.dismiss();
                                                                                promptDialog.show();
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onFailure(HttpException e, String s) {
                                                                            MyDynamicToast.errorMessage(UIUtils.getContext(), "登录失败,请稍后重试!");
                                                                            isLoading2 = false;
                                                                        }
                                                                    });

                                                        }

                                                    }
                                                }
                                            }).setNegativeListener(getString(R.string.cancel), new ColorDialog.OnNegativeListener() {
                                                @Override
                                                public void onClick(ColorDialog dialog) {
                                                    dialog.dismiss();
                                                    restartErWeiMaSaoMiaoNoWait();
                                                }
                                            }).show();

                                        }
                                    }
                                }

                                @Override
                                public void onFailure(HttpException e, String s) {
                                    MyDynamicToast.errorMessage(UIUtils.getContext(), "请求网络服务失败,请检查网络后重试!");
                                    isLoading = false;
                                }
                            }

                    );

                }

            }


        }

        @Override
        public void onAnalyzeFailed() {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
            bundle.putString(CodeUtils.RESULT_STRING, "");
            resultIntent.putExtras(bundle);
            SecondActivity.this.setResult(RESULT_OK, resultIntent);
            SecondActivity.this.finish();
        }
    };

    /**
     * 重启二维码扫码
     */
    private void restartErWeiMaSaoMiao() {
        try {
            Thread.sleep(2000); // 休息2秒
            CaptureActivityHandler mHandler = (CaptureActivityHandler) captureFragment.getHandler();
            if (mHandler != null)
                mHandler.restartPreviewAndDecode();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 重启二维码扫码 无需等待
     */
    private void restartErWeiMaSaoMiaoNoWait() {
        CaptureActivityHandler mHandler = (CaptureActivityHandler) captureFragment.getHandler();
        if (mHandler != null)
            mHandler.restartPreviewAndDecode();

    }

}
