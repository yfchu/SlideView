package com.yfchu.app.slideview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class GoAcitivity extends AppCompatActivity {

    private TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_acitivity);
        txt = (TextView) findViewById(R.id.txt);
        if (getIntent().getIntExtra("goHere", 0) == 1)
            txt.setText("短信");
        else if (getIntent().getIntExtra("goHere", 0) == 2)
            txt.setText("高清通话");
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
