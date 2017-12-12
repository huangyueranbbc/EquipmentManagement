package com.hyr.equipment.management.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hyr.equipment.management.R;
import com.hyr.equipment.management.adapters.TimestampTypeAdapter;
import com.hyr.equipment.management.domain.TbEqSystemNotice;
import com.hyr.equipment.management.global.GlobalValue;
import com.hyr.equipment.management.utils.UIUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.wajahatkarim3.easyflipview.EasyFlipView;

import java.security.Timestamp;
import java.util.Random;

/**
 * 注意事项和系统公告界面
 */
public class NoticeActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvContext;

    private static final String TAG = "NoticeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        final EasyFlipView easyFlipView = (EasyFlipView) findViewById(R.id.easyFlipView);
        easyFlipView.setFlipDuration(1000);
        easyFlipView.setFlipEnabled(true);

        View view1 = findViewById(R.id.v2);
        tvTitle = (TextView) view1.findViewById(R.id.tv_title);
        
        tvContext = (TextView) view1.findViewById(R.id.tv_context);

        Log.i(TAG, "tvTitle: "+tvTitle);
        Log.i(TAG, "tvContext: "+tvContext);

        loadData();

        findViewById(R.id.v1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(NoticeActivity.this, "Front Card", Toast.LENGTH_SHORT).show();
                easyFlipView.flipTheView();

            }
        });

        findViewById(R.id.v2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(NoticeActivity.this, "Back Card", Toast.LENGTH_SHORT).show();
                easyFlipView.flipTheView();
            }
        });

        easyFlipView.setOnFlipListener(new EasyFlipView.OnFlipAnimationListener() {
            @Override
            public void onViewFlipCompleted(EasyFlipView.FlipState newCurrentSide) {
                Toast.makeText(NoticeActivity.this, "Flip Completed! New Side is: " + newCurrentSide, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 加载网络数据
     */
    private void loadData() {
        final String url = GlobalValue.BASE_URL + "/equipment/getnotice";
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("r", new Random().nextInt() + ""); // 防止重复提交

        // TODO 登录逻辑
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET,
                url, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        // 得到图书馆list集合
                        Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                        TbEqSystemNotice notice = gson.fromJson(responseInfo.result, new TypeToken<TbEqSystemNotice>() {
                        }.getType()); // 登录返回结果

                        if (null == notice) { // 登录失败
                            MyDynamicToast.errorMessage(UIUtils.getContext(), "网络请求错误,请重试!");
                        } else { // 登录成功
                            // 页面加载数据
                            tvTitle.setText(notice.getTitle());
                            tvContext.setText(notice.getContext());
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        MyDynamicToast.errorMessage(UIUtils.getContext(), "网络请求失败,请检查网络后重试!");
                    }
                });
    }
}
