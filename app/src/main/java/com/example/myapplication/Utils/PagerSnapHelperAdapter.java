package com.example.myapplication.Utils;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.myapplication.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

public class PagerSnapHelperAdapter extends RecyclerView.Adapter<PagerSnapHelperAdapter.ViewHolder> {

    // 数据集
    private ArrayList<String> mDataList;

    private int mWidth;

    public void add(String uri) {
        mDataList.add(uri);
    }
    //
    public PagerSnapHelperAdapter(ArrayList<String> dataset, int mWidth) {
        super();
        this.mDataList = dataset;
        this.mWidth = mWidth;
    }

    @Nullable
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = View.inflate(viewGroup.getContext(), R.layout.recycle_pager_item, null);

        View contentView = view.findViewById(R.id.id_main_sdv);
        RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) contentView.getLayoutParams();
        rl.width = mWidth;
        contentView.setLayoutParams(rl);

        // 创建一个ViewHolder
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        // 绑定数据到ViewHolder上
        viewHolder.itemView.setTag(position);

        FrescoUtils.setControllerListener(viewHolder.mImage , Uri.parse(mDataList.get(position)),
                mWidth);
        float aspectRatio = viewHolder.mImage.getAspectRatio();
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public SimpleDraweeView mImage;

        public ViewHolder(View itemView) {
            super(itemView);
            mImage = (SimpleDraweeView) itemView.findViewById(R.id.id_main_sdv);
        }
    }
}
