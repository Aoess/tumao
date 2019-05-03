package com.example.myapplication.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.myapplication.entity.ImageRepository;
import com.example.myapplication.Utils.PagerSnapHelperAdapter;
import com.example.myapplication.R;
import com.facebook.drawee.backends.pipeline.Fresco;

public class MainActivity extends AppCompatActivity {

    // recycleView
    private RecyclerView mRecyclerView = null;
    // adapter
    private PagerSnapHelperAdapter mMyadapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化Fresco
        Fresco.initialize(this);
        System.out.println(ImageRepository.IMAGE_REPOSITORY.size());
        initUI();


    }

    public void initUI() {
        //获取RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_vertical);
        mRecyclerView.setNestedScrollingEnabled(false);
//        //创建PagerSnapHelper
//        PagerSnapHelper snapHelper = new PagerSnapHelper() {
//            // 在 Adapter的 onBindViewHolder 之后执行
//            @Override
//            public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
//                //找到对应的Index
//                int targetPos = super.findTargetSnapPosition(layoutManager, velocityX, velocityY);
//                //显示一个toast
//                Toast.makeText(MainActivity.this, "滑到到 " + targetPos + "位置", Toast.LENGTH_SHORT).show();
//                return targetPos;
//            }
//
//            @Override
//            public View findSnapView(RecyclerView.LayoutManager layoutManager) {
//                //找到对应的View
//                View view = super.findSnapView(layoutManager);
//                return view;
//            }
//        };
//        snapHelper.attachToRecyclerView(mRecyclerView);
        // 设置布局管理器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 创建Adapter，并指定数据集
        mMyadapter = new PagerSnapHelperAdapter(ImageRepository.IMAGE_REPOSITORY, getResources().getDisplayMetrics().widthPixels);
        // 设置Adapter
        mRecyclerView.setAdapter(mMyadapter);

    }
}
