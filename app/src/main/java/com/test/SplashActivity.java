package com.test;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


/**
 * Created by Administrator on 2016/5/25.
 */
public class SplashActivity extends Activity {

    private int progress = 0;
    private SimpleDraweeView adview;
    private ImageView zuodao;
    private circleProgressView time_progress;

    String[] url = new String[]{
            "http://img.pconline.com.cn/images/upload/upc/tx/photoblog/1405/06/c6/33940574_33940574_1399384701072.jpg",
            "http://image.tianjimedia.com/uploadImages/2014/064/M89YLQ5P9G2X_1000x500.jpg",
            "http://image.tianjimedia.com/uploadImages/2014/218/57/652482H64SR7.jpg",
            "http://pic45.nipic.com/20140805/7447430_144855605000_2.jpg",
            "http://image40.360doc.com/DownloadImg/2011/10/2620/18786609_14.jpg",
            "http://img4.duitang.com/uploads/item/201307/22/20130722113725_E8Akc.jpeg",
            "http://image75.360doc.com/DownloadImg/2014/06/1520/42623963_3.jpg",
            "http://img.beihai365.com/bbs/Mon_1203/270_439129_885df66f6e05539.jpg",
            "http://img4q.duitang.com/uploads/item/201505/17/20150517114813_vsSc3.jpeg",
            "http://s6.sinaimg.cn/mw690/001tcnnlzy6V5zjIw8lb5&690",
            "http://img002.21cnimg.com/photos/album/20140509/m320/6985DAD360538D907923C815BEBDF1A1.jpeg",
            "http://img15.3lian.com/2015/f2/147/d/76.jpg"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        adview = (SimpleDraweeView) findViewById(R.id.adview);
        zuodao = (ImageView) findViewById(R.id.zuodao);
        time_progress = (circleProgressView) findViewById(R.id.time_progress);
        time_progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        });
        //随机取出地址
        Random rand = new Random();
        int randNum = rand.nextInt(url.length);
        try {

            /**
             * 先下载后后展示方式
             */
            downLoad(url[randNum]);//先下载后展示
            updateUi(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/test/ad.jpg");//展示本地下载的图片

            /**
             * 直接加载网络方式
             */
//            updateUiFresco(url[randNum]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 图片下载到本地
     */
    public void downLoad(final String url) throws Exception {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException throwable) {
                throwable.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "请求失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                final File file = saveFile(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/test/", "ad.jpg", response.body().byteStream());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (file != null) {
                            Toast.makeText(getApplicationContext(), "下载完成", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    /**
     * 更新UI(直接加载网络方式)
     *
     * @param url
     */
    private void updateUiFresco(String url) {
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setControllerListener(new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        time_progress.setVisibility(View.VISIBLE);
                        //开始计时
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (progress <= 100) {
                                    progress += 2;
                                    time_progress.setCurrentProgress(progress);
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    if (progress == 100) {
                                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                        finish();
                                    }
                                }
                            }
                        }).start();
                    }

                    @Override
                    public void onFailure(String id, Throwable throwable) {

                    }
                })
                .setUri(url)
                .build();
        adview.setController(controller);
    }

    /**
     * 更新UI(下载保存本地方式)
     *
     * @param url
     */
    private void updateUi(String url) {
        File file = new File(url);
        if (file.exists()) {
            time_progress.setVisibility(View.VISIBLE);
            //开始计时
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (progress <= 100) {
                        progress += 2;
                        time_progress.setCurrentProgress(progress);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (progress == 100) {
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                }
            }).start();
            adview.setImageURI(Uri.parse("file://" + url));
        }
    }


    /**
     * 保存下载文件
     */
    public File saveFile(String destFileDir, String destFileName, InputStream inputStream) throws IOException {

        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        try {
            is = inputStream;

            File filedir = new File(destFileDir);
            //如果文件夹不存在则创建
            if (!filedir.exists() && !filedir.isDirectory()) {
                filedir.mkdirs();
            }

            File file = new File(destFileDir + File.separator + destFileName);
            if (!file.exists()) {
                file.createNewFile();
            }

            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            return file;

        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
            }
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
            }

        }
    }

}