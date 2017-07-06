package com.lihao.mobilplay.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.lihao.mobilplay.R;
import com.lihao.mobilplay.tools.DensityUtil;
import com.lihao.mobilplay.tools.FileUtils;
import com.lihao.mobilplay.tools.LogUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class showImageActivity extends AppCompatActivity {

    private String ImageUrl;
    private ImageView mIvBig;
    private File dcimFile;
    private int y1,y2;
    private Bitmap mBitmap;
    private BitmapRegionDecoder bitmapRegionDecoder;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                // TODO: 2017/6/24
                case 0:
                    try {
                        bitmapRegionDecoder = BitmapRegionDecoder.newInstance(new FileInputStream(dcimFile), false);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i("info", "图片获取失败");
                    }
                    bindData(y1,y2);
                    break;
                case 1:
                    mBitmap = (Bitmap) msg.obj;
                    mIvBig.setImageBitmap(mBitmap);
                    Toast.makeText(showImageActivity.this, "显示成功", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            String imageName = System.currentTimeMillis() + ".png";

            dcimFile = FileUtils.getDCIMFile(FileUtils.PATH_PHOTOGRAPH, imageName);

            LogUtil.i("bitmap=" + bitmap);
            FileOutputStream ostream = null;
            try {
                ostream = new FileOutputStream(dcimFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                ostream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(showImageActivity.this, "图片下载至:" + dcimFile, Toast.LENGTH_SHORT).show();
            Message msg = Message.obtain();
            msg.what = 0;
            msg.setTarget(handler);
            msg.sendToTarget();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Toast.makeText(showImageActivity.this, "图片下载失败", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            Toast.makeText(showImageActivity.this, "准备现在:", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        initView();
        getDate();
        y2 = (int) DensityUtil.getScreenHeight(this);
    }

    //图片下载完成后调用
    private void bindData(final int y1, final int y2) {

        new Thread(){
            @Override
            public void run() {
                super.run();
                Bitmap bitmap = null;
                if (bitmapRegionDecoder.getHeight() >= DensityUtil.getScreenHeight(showImageActivity.this)) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    bitmap = bitmapRegionDecoder.decodeRegion(new Rect(0, y1,
                                    (int) DensityUtil.getScreenWeight(showImageActivity.this),
                                    y2),
                            options);
                } else {
                    bitmap = BitmapFactory.decodeFile(dcimFile.getName());
                }
                Message msg = Message.obtain();
                msg.what = 1;
                msg.obj = bitmap;
                msg.setTarget(handler);
                msg.sendToTarget();
            }
        }.start();
    }

    private void initView() {
        mIvBig = (ImageView) findViewById(R.id.iv_big);
    }

    private void getDate() {
        ImageUrl = getIntent().getStringExtra("ImageUrl");
        if (ImageUrl!=null) {
            //下载图片
            Picasso.with(this).load(ImageUrl).into(target);
            Toast.makeText(this, "图片下载成功", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "路径有问题", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        float startX = 0;
//        float startY = 0;
//        switch (event.getAction()){
//            case MotionEvent.ACTION_DOWN:
////                startX = event.getX();
//                startY = event.getY();
//                break;
//            case MotionEvent.ACTION_MOVE:
////                float newX = oldX - (event.getX()- startX);
//                float newY1 = y1 - (event.getY()- startY);
//                float newY2 = y2 - (event.getY()- startY);
////                if (newX<=0){
////                    newX=0;
////                }
//                if (newY1<=0){
//                    newY1=0;
//                }
//                if (newY2+DensityUtil.getScreenHeight(this)>mBitmap.getHeight()){
//                    newY2 = (float) (mBitmap.getHeight()-DensityUtil.getScreenHeight(this));
//                }
//                //移动图片
//                bindData((int)newY1,(int)newY2);
//                y1 = (int) newY1;
//                y2 = (int) newY2;
//                break;
//            case MotionEvent.ACTION_UP:
//                break;
//        }
//        return true;
//    }
}
