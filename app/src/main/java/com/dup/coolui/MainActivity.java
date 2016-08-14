package com.dup.coolui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dup.library.FlipCardViewGroup;


public class MainActivity extends AppCompatActivity {

    private FlipCardViewGroup group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        group = (FlipCardViewGroup) findViewById(R.id.group);

        Button btn1 = (Button) findViewById(R.id.btn_first);
        Button btn2 = (Button) findViewById(R.id.btn_second);
        Button btn3 = (Button) findViewById(R.id.btn_third);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                group.changeToItem(1);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "退出登录", Toast.LENGTH_SHORT).show();
                group.changeToItem(2);
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                group.reset();
            }
        });

        final FlipCardViewGroup group1 = (FlipCardViewGroup) findViewById(R.id.group1);
        Button btnStop = (Button) findViewById(R.id.btn_stop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                group1.stopAnimation();
            }
        });

        Button btnRestart = (Button) findViewById(R.id.btn_restart);
        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                group1.reStartAnimation();
            }
        });

        final Button btnReset = (Button) findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                group.reset();
            }
        });

    }
}
