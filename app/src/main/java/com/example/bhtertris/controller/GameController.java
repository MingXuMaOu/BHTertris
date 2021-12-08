package com.example.bhtertris.controller;

import static com.example.bhtertris.config.Config.GOAL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bhtertris.MainActivity;
import com.example.bhtertris.R;
import com.example.bhtertris.config.Config;
import com.example.bhtertris.model.BoxsModel;
import com.example.bhtertris.model.MapsModel;
import com.example.bhtertris.model.ScoreModel;

import pl.droidsonroids.gif.GifImageView;

public class GameController {

    private Handler handler;

    private int boxSize;
    private MapsModel mapsModel;
    private BoxsModel boxsModel;
    public ScoreModel scoreModel;
    public Thread downThread;

    public boolean isPause;
    public boolean isOver;
    private Context context;
    private Animation minuteRotate;
    private Animation hourRotate;
    private Handler handler1;
    private Handler handler2;
    private GifImageView gifImageView;


    public GameController(Handler handler,Context context){
        this.handler = handler;
        initData(context);
    }
    private void initData(Context context){
        this.context = context;
        int width = getScreenWidth(context);
        Config.XWIDTH = width * 2 / 3;
        Config.YHEIGHT = Config.XWIDTH * 2;
        Config.PADDING = width / Config.SPLIT_PADDING;

        boxSize = Config.XWIDTH / Config.MAPX;
        mapsModel = new MapsModel(Config.XWIDTH,Config.YHEIGHT,boxSize,context);
        boxsModel = new BoxsModel(boxSize,context);
        scoreModel = new ScoreModel();
        gifImageView = ((MainActivity)context).findViewById(R.id.gif);
    }

    private static int getScreenWidth(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }



    private void startGame(){
        mapsModel.cleanMaps();
        if(downThread == null){
            downThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        try{
                            Thread.sleep(500);
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                        if(isOver || isPause){
                            continue;
                        }
                        moveBottom();
                        Message msg = new Message();
                        msg.obj = "invalidate";
                        handler.sendMessage(msg);
                    }
                }
            });
            downThread.start();
        }
        isOver = false;
        isPause = false;
        boxsModel.newBoxs();
    }

    public void scheduleGit(){
        handler2 = new Handler();
        handler2.postDelayed(()->{
            GOAL += 10;
            ScoreModel.score = 0;
            gifImageView.setVisibility(View.GONE);
            startGame();
            initHour();
            initMinute();
        },3_000);
    }

    public void scheduleTimeout(){
        handler1 = new Handler();
        handler1.postDelayed(()->{
            isOver = true;
            hourRotate.cancel();
            minuteRotate.cancel();

        },Config.TIMEOUT_MILLS);
    }

    public void cancel(){
        handler1.removeCallbacksAndMessages(null);
    }

    public boolean moveBottom(){
        if(boxsModel.move(0,1,mapsModel)){
            return true;
        }
        for (int i = 0; i < boxsModel.boxs.length; i++) {
            mapsModel.maps[boxsModel.boxs[i].x][boxsModel.boxs[i].y] = true;
        }
        int lines = mapsModel.cleanLine();

        scoreModel.addScore(lines);
        if(scoreModel.score >= GOAL){
            gifImageView.setVisibility(View.VISIBLE);
            setPause();
            hourRotate.cancel();
            minuteRotate.cancel();
            scheduleGit();
        }
        boxsModel.newBoxs();

        isOver = checkOver();
        return false;
    }

    private boolean checkOver() {
        for (int i = 0; i < boxsModel.boxs.length; i++) {
            if(mapsModel.maps[boxsModel.boxs[i].x][boxsModel.boxs[i].y]) {
                hourRotate.cancel();
                minuteRotate.cancel();
                return true;
            }
        }
        return false;
    }


    private void setPause() {
        if(isPause){
            isPause = false;
        }else {
            isPause = true;
        }
    }

    public void onClick(int id){
        switch (id){
            case R.id.btnLeft:
                if(isPause){
                    return;
                }
                boxsModel.move(-1,0,mapsModel);
                break;
            case R.id.btnTop:
                if(isPause){
                    return;
                }
                boxsModel.rotate(mapsModel);
                break;
            case R.id.btnRight:
                if(isPause){
                    return;
                }
                boxsModel.move(1,0,mapsModel);
                break;
            case R.id.btnBottom:
                if(isPause || isOver){
                    return;
                }
                while (moveBottom());
                break;
            case R.id.btnStart:
                startGame();
                if(handler1 != null) {
                    cancel();
                }
                scheduleTimeout();
                initMinute();
                initHour();
                break;
            case R.id.btnPause:
                setPause();
                break;
        }
    }

    private void initMinute(){
        minuteRotate = new RotateAnimation(0f,4320,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        minuteRotate.setFillAfter(true);
        minuteRotate.setDuration(5 * 30_000);
        minuteRotate.setRepeatCount(1);
        minuteRotate.setInterpolator(new LinearInterpolator());
        minuteRotate.setDetachWallpaper(true);
        ImageView imageView = ((MainActivity)context).findViewById(R.id.clock_minute);
        imageView.startAnimation(minuteRotate);
    }
    private void initHour(){
        hourRotate = new RotateAnimation(0f,360,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        hourRotate.setFillAfter(true);
        hourRotate.setDuration(5 * 30_000);
        hourRotate.setRepeatCount(1);
        hourRotate.setInterpolator(new LinearInterpolator());
        hourRotate.setDetachWallpaper(true);
        ImageView imageView = ((MainActivity)context).findViewById(R.id.clock_hour);
        imageView.startAnimation(hourRotate);
    }

    public void drawGame(Canvas canvas){
//        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.glass6);
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//        int newWidth = canvas.getWidth();
//        int newHeight = canvas.getHeight();
//        float scaleWidth = (float)newWidth / width;
//        float scaleHeight = (float)newHeight / height;
//        Matrix matrix = new Matrix();
//        matrix.postScale(scaleWidth,scaleHeight);
//        Bitmap newBm = Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
//        Paint paint = new Paint();
//        paint.setAlpha(50);
//        canvas.drawBitmap(newBm,0,0,paint);
        mapsModel.drawMaps(canvas);
        mapsModel.drawLine(canvas);
        boxsModel.drawBoxs(canvas);
        mapsModel.drawState(canvas,isPause,isOver);
    }

    public void drawNext(Canvas canvas,int width) {
        boxsModel.drawNext(canvas,width);
    }
}
