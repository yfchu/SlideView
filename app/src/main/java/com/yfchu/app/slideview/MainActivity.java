package com.yfchu.app.slideview;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import com.yfchu.app.customview.slidelayout;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private slidelayout mSlidelayout;
    private RelativeLayout mBottom_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        mSlidelayout = (slidelayout) findViewById(R.id.slidelayout);
        mSlidelayout.bringToFront();
        mBottom_layout = (RelativeLayout) findViewById(R.id.bottom_layout);
        mSlidelayout.setBottom_layer(mBottom_layout);
        mSlidelayout.setHandler(mHandler);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    new Handler().postDelayed(new Runnable() {  //延时是为了让view滑动完毕
                        @Override
                        public void run() {
                            Intent intent = new Intent(mContext, GoAcitivity.class);
                            intent.putExtra("goHere", 1);
                            mContext.startActivity(intent);
                            finish();
                        }
                    }, 800);
                    break;
                case 2:
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent1 = new Intent(mContext, GoAcitivity.class);
                            intent1.putExtra("goHere", 2);
                            mContext.startActivity(intent1);
                            finish();
                        }
                    }, 800);
                    break;
            }
        }
    };
}
