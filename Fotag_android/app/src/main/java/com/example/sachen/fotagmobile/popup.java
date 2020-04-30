package com.example.sachen.fotagmobile;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

public class popup extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popup_layout);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int h = dm.heightPixels;
        int w = dm.widthPixels;

        getWindow().setLayout(w,h);

        ImageView imageview = findViewById(R.id.popup_img);
        imageview.setClickable(true);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            imageview.setImageResource(extras.getInt("key"));
        }
        imageview.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
