package com.example.bhtertris.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.example.bhtertris.R;
import com.example.bhtertris.config.Config;

public class MapsModel {

    private Paint mapPaint;
    private Paint linePaint;
    private Paint statePaint;
    private int xWidth;
    private int yHeight;
    private int boxSize;
    public boolean[][] maps;
    private Context context;

    public MapsModel(int xWidtn, int yHeight, int boxSize, Context context){
        this.context = context;
        this.xWidth = xWidtn;
        this.yHeight = yHeight;
        this.boxSize = boxSize;
        maps = new boolean[Config.MAPX][Config.MAPY];

        mapPaint = new Paint();
        mapPaint.setColor(0x50000000);
        mapPaint.setAntiAlias(true);

        linePaint = new Paint();
        linePaint.setColor(0xff666666);
        linePaint.setAntiAlias(true);

        statePaint = new Paint();
        statePaint.setColor(0xffff0000);
        statePaint.setAntiAlias(true);
        statePaint.setTextSize(100);
    }

    public void drawLine(Canvas canvas){
        for (int x = 0; x <= maps.length; x++) {
            canvas.drawLine(x * boxSize,0,x * boxSize,yHeight,linePaint);
        }
        for (int y = 0; y <= maps[0].length; y++) {
            canvas.drawLine(0,y * boxSize,xWidth,y * boxSize,linePaint);
        }
    }

    public void drawMaps(Canvas canvas) {
        for (int x = 0; x < maps.length; x++) {
            for (int y = 0; y < maps[x].length; y++) {
                if(maps[x][y] == true){
//                    canvas.drawRect(x * boxSize,y * boxSize, (x + 1) * boxSize,(y + 1) * boxSize,mapPaint);
                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.grass);
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    float scaleWidth = (float)boxSize / width;
                    float scaleHeight = (float)boxSize / height;
                    Matrix matrix = new Matrix();
                    matrix.postScale((float) (scaleWidth + 0.1),(float) scaleHeight);
                    Bitmap newBitmap = Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
                    canvas.drawBitmap(newBitmap,x * boxSize,y * boxSize,mapPaint);
                }
            }
        }
    }
    public void drawState(Canvas canvas, boolean isPause, boolean isOver) {
        if(isOver){
            canvas.drawText("游戏结束",xWidth / 2 - statePaint.measureText("游戏结束") / 2,yHeight / 2,statePaint);
        }
        if(isPause && !isOver){
            if(ScoreModel.score >= Config.GOAL){
                canvas.drawText("恭喜通过", xWidth / 2 - statePaint.measureText("恭喜通过") / 2, yHeight / 2, statePaint);
            }else {
                canvas.drawText("暂停", xWidth / 2 - statePaint.measureText("暂停") / 2, yHeight / 2, statePaint);
            }
        }
    }

    public int cleanLine(){
        int lines = 0;
        for (int y = 0; y < maps[0].length; y++) {
            if(checkLine(y)){
                deleteLine(y);
                y--;
                lines++;
            }
        }
        return lines;
    }

    private boolean checkLine(int y) {
        for (int x = 0; x < maps.length; x++) {
            if(!maps[x][y]){
                return false;
            }
        }
        return true;
    }
    private void deleteLine(int dy){
        for (int y = dy; y > 0; y--) {
            for (int x = 0; x < maps.length; x++) {
                maps[x][y] = maps[x][y - 1];
            }
        }
    }


    public void cleanMaps() {
        for (int y = 0; y < maps[0].length; y++) {
            for (int x = 0; x < maps.length; x++) {
                maps[x][y] = false;
            }
        }
    }
}
