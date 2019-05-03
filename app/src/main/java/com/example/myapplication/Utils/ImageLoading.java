package com.example.myapplication.Utils;

import android.util.Log;

import com.example.myapplication.R;
import com.example.myapplication.Utils.HttpUtils;
import com.example.myapplication.Utils.Reptile;
import com.example.myapplication.entity.ImageRepository;
import com.example.myapplication.entity.ParsingObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Response;

public class ImageLoading {
    
    //爬虫爬取的网站
    private String searchUrl = "http://www.daimg.com/search.php?channeltype=0&orderby=&kwtype=0&pagesize=500&ext=0&pdi=12&size=15&free=0&searchtype=titlekeyword&typeid=0&keyword=";
    
    public void fillImageList(String input) throws IOException {
        String url = getUrl(input);
        Response response = HttpUtils.doblockGetRequest(url,null);
        Observer<ParsingObject> observer = responseParsing(response, url);
        asynchronousFillImage(observer,new ParsingObject(response));
    }

    //根据条件产生Url
    private String getUrl(String input) {

        StringBuilder url = new StringBuilder(300);
        if(input.startsWith("https://") || input.startsWith("http://")) {
            url.append(input);
        }
        else if(input.startsWith("www.")) {
            url.append("http://");


            url.append(input);
        }
        else {
            url.append(searchUrl);
            try {
                url.append(URLEncoder.encode(stringCuter(input,10), "GBK"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                url.append("%C7%EF%CC%EC");
            }
        }
        return url.toString();
    }

    //字符串裁剪
    private String stringCuter(String input, int length) {
        if(input.length() > length) {
            return input.substring(0,length);
        }
        return input;
    }

    //填充网络图片,解析response
    private Observer<ParsingObject> responseParsing(final Response res, String input){
        Log.d("ImageUtils","爬取图片");
        //根据contenttype确定解析类型
        String header = res.header("Content-Type");
        Observer<ParsingObject> observer = null;
        if(header.contains("image") || header.contains("jpg") || header.contains("jpeg") || header.contains("jif")) {
            ImageRepository.IMAGE_REPOSITORY.add(input);
            //加入结尾图片
            ImageRepository.IMAGE_REPOSITORY.add("res://com.example.myapplication/" + R.drawable.finallyimage);
            return null;
        }
        else if(header.contains("html")) {
            //使用小爬虫解析
            observer = new Observer<ParsingObject>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(ParsingObject parsingObject) {
                    Response result = parsingObject.getResponse();
                    if(result == null) {
                        return;
                    }
                    List<String> imgUrl = null;
                    try {
                        //获取图片标签
                        imgUrl = Reptile.getImageUrl(result.body().string());
                        //获取图片src地址
                        List<String> imgSrc = Reptile.getImageSrc(imgUrl);
                        Log.d("img标签", imgSrc.toString());
                        if(imgSrc != null) {
                            for(int i = 0; i < imgSrc.size(); i++) {
                                ImageRepository.IMAGE_REPOSITORY.add(imgSrc.get(i));
                            }
                        }
                        //加入结尾图片
                        ImageRepository.IMAGE_REPOSITORY.add("res://com.example.myapplication/" + R.drawable.finallyimage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                }

                @Override
                public void onComplete() {

                }
            };

        }
        return observer;
    }

    //使用RxJava异步填充图片
    private void asynchronousFillImage(Observer<ParsingObject> observer , final ParsingObject ParsingObject) {
        if(observer != null && ParsingObject != null) {
            //用RxJava开启异步解析
            Observable.create(new ObservableOnSubscribe<ParsingObject>() {
                @Override
                public void subscribe(ObservableEmitter<ParsingObject> emitter) throws Exception {
                    emitter.onNext(ParsingObject);
                }
            }).observeOn(Schedulers.newThread()).subscribe(observer);
        }
    }

    //加载类型判断
    public Type typeJudge(String input) {

        //根据输入选择加载策略
        if(input.startsWith("http://") || input.startsWith("https://") || input.startsWith("www.")) {
            return Type.URL;
        }
        else {
            return Type.SEARCH;
        }
    }


    private enum Type {
        URL,
        SEARCH,
    }

}
