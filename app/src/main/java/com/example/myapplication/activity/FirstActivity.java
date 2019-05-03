package com.example.myapplication.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Utils.ImageLoading;
import com.example.myapplication.entity.ImageRepository;
import com.example.myapplication.R;

import java.io.IOException;

public class FirstActivity extends AppCompatActivity {

    private EditText editText = null;
    private ProgressBar progressBar = null;
    private ImageLoading imageLoading = new ImageLoading();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        progressBar = findViewById(R.id.progress);
        editText = findViewById(R.id.input);

        iniEditText();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImageRepository.IMAGE_REPOSITORY.clear();
    }

    public boolean permissonJugde() {
        if (ContextCompat.checkSelfPermission(FirstActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    public void iniEditText() {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            //监听回车
            @Override
            public boolean onEditorAction(final TextView v, int actionId, KeyEvent event) {
                //获取输入框中字符串
                final String text = String.valueOf(v.getText()).trim();
                //如果回车被按下并且字符串不为null就进行处理
                if ((actionId == 0 || actionId == 6) && !"".equals(text)) {
                    //网络访问权限判断
                    if (!permissonJugde()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(FirstActivity.this, "求求你了，给我网络权限吧", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        progressBar.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.VISIBLE);
                            }
                        });
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    imageLoading.fillImageList(text);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    //如果用户没开启网络就Toast提示
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(FirstActivity.this, "请确认您的网络并重试", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }).start();
                        //自选等待图片集合填充,如果自选十次还没有填充,就说明该关键字没找到图
                        for (int i = 0; i < 30; i++) {
                            if (ImageRepository.IMAGE_REPOSITORY.size() > 1) {
                                System.out.println(ImageRepository.IMAGE_REPOSITORY.size());
                                startActivity(new Intent(FirstActivity.this, MainActivity.class));
                                editText.setText("");
                                break;
                            }
                            if(i == 29) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(FirstActivity.this, "我太笨了,这个关键字我一无所知", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        progressBar.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                    }

                }
                return false;

            }
        });
    }

}
