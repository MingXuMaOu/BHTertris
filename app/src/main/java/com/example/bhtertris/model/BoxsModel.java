package com.example.bhtertris.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;

import com.example.bhtertris.R;

import java.util.Random;

public class BoxsModel {

    private int boxType;
    public Point[] boxs = new Point[]{};
    private int boxSize;
    private Paint boxPaint;
    private Point[] boxsNext;
    private int boxNextType;
    private int boxNextSize;
    private Context context;

    public BoxsModel(int boxSize, Context context){
        this.context = context;
        this.boxSize = boxSize;
        boxPaint = new Paint();
        boxPaint.setColor(0xff000000);
        boxPaint.setAntiAlias(true);
    }

    public void newBoxs() {
        if (boxsNext == null) {
            newBoxsNext();
        }
        boxs = boxsNext;
        boxType = boxNextType;
        newBoxsNext();
    }

    private void newBoxsNext(){
        Random random = new Random();
        boxNextType = random.nextInt(7);
        switch (boxNextType){
            case 0:
                boxsNext = new Point[]{new Point(4,0),new Point(5,0),new Point(4,1),new Point(5,1)};
                break;
            case 1:
                boxsNext = new Point[]{new Point(4,1),new Point(5,0),new Point(3,1),new Point(5,1)};
                break;
            case 2:
                boxsNext= new Point[]{new Point(4,1),new Point(3,0),new Point(3,1),new Point(5,1)};
                break;
            case 3:
                boxsNext = new Point[]{new Point(4,0),new Point(3,1),new Point(4,1),new Point(5,1)};
                break;
            case 4:
                boxsNext = new Point[]{new Point(4,0),new Point(3,0),new Point(5,0),new Point(6,0)};
                break;
            case 5:
                boxsNext = new Point[]{new Point(4,0),new Point(4,1),new Point(5,1),new Point(5,2)};
                break;
            case 6:
                boxsNext = new Point[]{new Point(4,1),new Point(4,2),new Point(5,1),new Point(5,0)};
                break;
        }
    }
    public void drawBoxs(Canvas canvas){
        if(boxs != null){
            for (int i = 0; i < boxs.length; i++) {
//                canvas.drawRect(boxs[i].x * boxSize,boxs[i].y * boxSize,boxs[i].x * boxSize+ boxSize,boxs[i].y * boxSize+ boxSize,boxPaint);
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.grass);
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                float scaleWidth = (float)boxSize / width;
                float scaleHeight = (float)boxSize / height;
                Matrix matrix = new Matrix();
                matrix.postScale((float) (scaleWidth + 0.1),scaleHeight);
                Bitmap newBitmap = Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
                canvas.drawBitmap(newBitmap,boxs[i].x * boxSize,boxs[i].y * boxSize,boxPaint);
            }
        }
    }
    public boolean move(int x,int y,MapsModel mapsModel){
        for (int i = 0; i < boxs.length; i++) {
            if(checkBoundary(boxs[i].x + x,boxs[i].y + y,mapsModel)){
                return false;
            }
        }
        for (int i = 0; i < boxs.length; i++) {
            boxs[i].x += x;
            boxs[i].y += y;
        }
        return true;
    }

    public boolean rotate(MapsModel mapsModel){
        if(boxType == 0){
            return false;
        }
        for (int i = 0; i < boxs.length; i++) {
            int checkX = -boxs[i].y + boxs[0].y + boxs[0].x;
            int checkY = boxs[i].x - boxs[0].x + boxs[0].y;

            if(checkBoundary(checkX,checkY,mapsModel)){
                return false;
            }
        }

        for (int i = 0; i < boxs.length; i++) {
            int checkX = -boxs[i].y + boxs[0].y + boxs[0].x;
            int checkY = boxs[i].x - boxs[0].x + boxs[0].y;

            boxs[i].x = checkX;
            boxs[i].y = checkY;
        }
        return true;
    }

    private boolean checkBoundary(int x,int y,MapsModel mapsModel){
        return (x < 0 || y < 0 || x >= mapsModel.maps.length || y >= mapsModel.maps[0].length || mapsModel.maps[x][y] == true);
    }


    public void drawNext(Canvas canvas,int w) {
        if(boxsNext != null) {
            if (boxNextSize == 0) {
                boxNextSize = w / 5;
            }
            for (int i = 0; i < boxsNext.length; i++) {
//                canvas.drawRect((boxsNext[i].x - 3) * boxNextSize, (boxsNext[i].y + 1) * boxNextSize, (boxsNext[i].x - 3) * boxNextSize + boxNextSize, boxsNext[i].y * boxNextSize, boxPaint);
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.grass);
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                float scaleWidth = (float)boxSize / width;
                float scaleHeight = (float)boxSize / height;
                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth - 0.1f,scaleHeight - 0.1f);
                Bitmap newBitmap = Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
                canvas.drawBitmap(newBitmap,(boxsNext[i].x - 3) * boxSize,(boxsNext[i].y )* boxSize,boxPaint);

            }
        }
    }
}
