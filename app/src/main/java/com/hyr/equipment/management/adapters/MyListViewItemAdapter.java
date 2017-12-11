package com.hyr.equipment.management.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyr.equipment.management.R;

import java.util.HashMap;
import java.util.List;

//创建自定义adapter
public class MyListViewItemAdapter extends BaseAdapter {
    private LayoutInflater mlayoutInflater;
    private List<HashMap<String, String>> mData;  //填充的数据 pojo

    public MyListViewItemAdapter(Context context, List<HashMap<String, String>> data) {
        mlayoutInflater = LayoutInflater.from(context);  //动态布局映射
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mlayoutInflater.inflate(R.layout.list_item, null);  //  根据布局文件实例化view
        TextView tv1 = (TextView) convertView.findViewById(R.id.textView1);
        tv1.setText(mData.get(position).get("key1").toString());
        TextView tv2 = (TextView) convertView.findViewById(R.id.textView2);
        tv2.setText(mData.get(position).get("key2").toString());
        TextView tv3 = (TextView) convertView.findViewById(R.id.textView3);
        tv3.setText(mData.get(position).get("key3").toString());
        TextView tv4 = (TextView) convertView.findViewById(R.id.textView4);
        tv4.setText(mData.get(position).get("key4").toString());
        return convertView;
    }
}