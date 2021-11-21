package com.example.bhtertris.controller;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.example.bhtertris.MainActivity;
import com.example.bhtertris.R;
import com.example.bhtertris.config.Config;
import com.example.bhtertris.model.BoxsModel;
import com.example.bhtertris.model.MapsModel;
import com.example.bhtertris.model.ScoreModel;

public class GameController {

    private Handler handler;

    private int boxSize;
    private MapsModel mapsModel;
    private BoxsModel boxsModel;
    public ScoreModel scoreModel;
    public Thread downThread;

    public boolean isPause;
    public boolean isOver;

    public GameController(Handler handler,Context context){
        this.handler = handler;
        initData(context);
    }
    private void initData(Context context){
        int width = getScreenWidth(context);
        Config.XWIDTH = width * 2 / 3;
        Config.YHEIGHT = Config.XWIDTH * 2;
        Config.PADDING = width / Config.SPLIT_PADDING;

        boxSize = Config.XWIDTH / Config.MAPX;
        mapsModel = new MapsModel(Config.XWIDTH,Config.YHEIGHT,boxSize);
        boxsModel = new BoxsModel(boxSize);
        scoreModel = new ScoreModel();

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

    public boolean moveBottom(){
        if(boxsModel.move(0,1,mapsModel)){
            return true;
        }
        for (int i = 0; i < boxsModel.boxs.length; i++) {
            mapsModel.maps[boxsModel.boxs[i].x][boxsModel.boxs[i].y] = true;
        }
        int lines = mapsModel.cleanLine();

        scoreModel.addScore(lines);
        boxsModel.newBoxs();

        isOver = checkOver();
        return false;
    }

    private boolean checkOver() {
        for (int i = 0; i < boxsModel.boxs.length; i++) {
            if(mapsModel.maps[boxsModel.boxs[i].x][boxsModel.boxs[i].y])
                return true;
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
                break;
            case R.id.btnPause:
                setPause();
                break;
        }
    }
    public void drawGame(Canvas canvas){
        mapsModel.drawMaps(canvas);
        mapsModel.drawLine(canvas);
        boxsModel.drawBoxs(canvas);
        mapsModel.drawState(canvas,isPause,isOver);
    }

    public void drawNext(Canvas canvas,int width) {
        boxsModel.drawNext(canvas,width);
    }
}
