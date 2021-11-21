package com.example.bhtertris;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bhtertris.config.Config;
import com.example.bhtertris.controller.GameController;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private View view;
    private View nextPanel;
    private TextView scoreText;
    private GameController gameController;
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            String type = ((String) msg.obj);
            if(type == null){
                return;
            }
            if(type.equals("invalidate")){
                view.invalidate();
                nextPanel.invalidate();
                scoreText.setText(gameController.scoreModel.score + "");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        gameController = new GameController(handler,this);

        initView();
        initListener();
    }


    private void initView(){
        FrameLayout layoutGame = findViewById(R.id.layoutGame);

        view = new View(this){
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                gameController.drawGame(canvas);
            }
        };
        view.setLayoutParams(new FrameLayout.LayoutParams(Config.XWIDTH,Config.YHEIGHT));
        view.setBackgroundColor(0x10000000);
        layoutGame.setPadding(Config.PADDING,Config.PADDING,Config.PADDING,Config.PADDING);
        layoutGame.addView(view);

        nextPanel = new View(this){
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                gameController.drawNext(canvas,nextPanel.getWidth());
            }
        };
        LinearLayout layoutInfo = findViewById(R.id.layoutInfo);
        layoutInfo.setPadding(0,Config.PADDING,Config.PADDING,Config.PADDING);
        nextPanel.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,200));
        nextPanel.setBackgroundColor(0x20000000);
        FrameLayout layoutNext = findViewById(R.id.layoutNext);
        layoutNext.addView(nextPanel);

        scoreText = findViewById(R.id.textNowScore);

    }

    private void initListener(){
        findViewById(R.id.btnLeft).setOnClickListener(this);
        findViewById(R.id.btnTop).setOnClickListener(this);
        findViewById(R.id.btnRight).setOnClickListener(this);
        findViewById(R.id.btnBottom).setOnClickListener(this);
        findViewById(R.id.btnBottom).setOnClickListener(this);
        findViewById(R.id.btnStart).setOnClickListener(this);
        findViewById(R.id.btnPause).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        gameController.onClick(v.getId());
        view.invalidate();
        nextPanel.invalidate();
    }
}