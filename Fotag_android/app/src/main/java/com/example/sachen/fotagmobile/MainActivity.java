package com.example.sachen.fotagmobile;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.LinearLayout;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer {

    Model mModel;
    MenuItem mIcon1, mIcon2, mIcon3, mIcon4, mIcon5;
    final Context context = this;
    Dialog dialog = null;
    boolean portrait = true;
    boolean loaded = false;
    boolean doingFilter = false;
    LinearLayout llayout, ll;
    GridLayout glayout, gl;
    Toolbar toolbar;
    ImageView imgView1;
    int ratingFilter = 0;
    PopupWindow popupwindow;
    EditText mEdit;
    ArrayList<RatingBar> rating_list = new ArrayList<>();
    ArrayList<Integer> rating_record = new ArrayList<>(); //must keep a record position
    ArrayList<Integer> full_record = new ArrayList<>(); //for the use of filtering
    ArrayList<Integer> view_list = new ArrayList<>();
    ArrayList<Integer> all_images = new ArrayList<>();
    ArrayList<LinearLayout> thumnail_list = new ArrayList<>();

    int [] images = new int []{R.drawable.image,R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4, R.drawable.image5, R.drawable.image6, R.drawable.image7, R.drawable.image8, R.drawable.image9 };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Fotag(sachen)");

        //get model instance
        mModel = Model.getInstance();
        mModel.addObserver(this);

        // Init observers
        mModel.initObservers();

        for (int i =0; i < images.length; i++){
            view_list.add(images[i]);
            all_images.add(images[i]);
            rating_record.add(0);
            full_record.add(0);
        }

    }

    @Override
    protected void onDestroy()
    {
        if(dialog != null) {
            dialog.dismiss();
        }
        super.onDestroy();
        // Remove observer when activity is destroyed.
        mModel.deleteObserver(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.rating, menu);
        inflater.inflate(R.menu.view1, menu);

        mIcon1 = menu.findItem(R.id.rating1);
        mIcon2 = menu.findItem(R.id.rating2);
        mIcon3 = menu.findItem(R.id.rating3);
        mIcon4 = menu.findItem(R.id.rating4);
        mIcon5 = menu.findItem(R.id.rating5);

        setRatingFilter(ratingFilter);

        return super.onCreateOptionsMenu(menu);
    }

    public void portraitLoading(){
        if(!doingFilter){
            setContentView(R.layout.activity_main);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Fotag(sachen)");
        } else{
            if(ll != null) ll.removeAllViews();
            if (gl != null) gl.removeAllViews();
        }
        llayout = findViewById(R.id.layout);
        ll = new LinearLayout(this);

        //make sure we dont repetedly adding to rating list
        boolean proceed = false;
        if(rating_list.size() == 0){
            proceed = true;
        }

        //add scrollbar
        ScrollView scroll = new ScrollView(this);
        scroll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        ll.setLayoutParams(lp);
        ll.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(ll);
        thumnail_list.add(ll);
        llayout.addView(scroll);


        for (int i = 0; i < view_list.size(); i++){
            final ImageView imageview = new ImageView(this);
            final int source = view_list.get(i);
            imageview.setImageResource(source);
            imageview.setAdjustViewBounds(true);
            imageview.setClickable(true);
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp2.setMargins(50, 0, 50, 100);
            imageview.setLayoutParams(lp2);

            imageview.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    Intent intentes = new Intent(MainActivity.this, popup.class);
                    intentes.putExtra("key", source);
                    startActivity(intentes);
                }
            });
            ll.addView(imageview);

            LinearLayout ratingLayout = new LinearLayout(this);
            ratingLayout.setLayoutParams(lp2);
            ratingLayout.setGravity(1);
            RatingBar r = new RatingBar(this);
            r.setNumStars(5);
            r.setRating(rating_record.get(i));
            r.setStepSize(1);
            final int rating_pos = i;
            r.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    rating_record.set(rating_pos,(int)rating);
                    full_record.set(rating_pos,(int)rating);
                }
            });

            ratingLayout.addView(r);
            if(proceed) rating_list.add(r);
            ll.addView(ratingLayout);
        }
    }

    public void landscapeLoading(){
        if(!doingFilter) {
            setContentView(R.layout.activity_main_land);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Fotag(sachen)");
        }else{
            if(ll != null) ll.removeAllViews();
            if (gl != null) gl.removeAllViews();
        }
        glayout = findViewById(R.id.landscape);
        gl = new GridLayout(this);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int h = dm.heightPixels;
        int w = dm.widthPixels;

        //make sure we dont repetedly adding to rating list
        boolean proceed = false;
        if(rating_list.size() == 0){
            proceed = true;
        }

        int total = 10;
        int column = 2;
        int row = total / column;

        gl.setColumnCount(column);
        gl.setRowCount(row+1);

        for (int i = 0, c=0, r=0; i < view_list.size(); i++, c++){
            if(c==column){
                c=0;
                r++;
            }
            final int source = view_list.get(i);
            LinearLayout imgLayout = new LinearLayout(this);
            ImageView imageview = new ImageView(this);
            imageview.setImageResource(view_list.get(i));
            imageview.setAdjustViewBounds(true);
            imageview.setMaxHeight(7*h/10);
            imageview.setMaxWidth(7*w/10);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.width = GridLayout.LayoutParams.WRAP_CONTENT;
            params.setMargins(50, 50, 50, 100);
            params.setGravity(Gravity.CENTER);
            params.columnSpec = GridLayout.spec(c);
            params.rowSpec = GridLayout.spec(r);
            imageview.setLayoutParams(params);

            imageview.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    Intent intentes = new Intent(MainActivity.this, popup.class);
                    intentes.putExtra("key", source);
                    startActivity(intentes);
                }
            });

            imgLayout.setOrientation(LinearLayout.VERTICAL);
            imgLayout.addView(imageview);

            LinearLayout ratingLayout = new LinearLayout(this);
            ratingLayout.setLayoutParams(params);
            ratingLayout.setGravity(1);
            RatingBar ratingStar = new RatingBar(this);
            ratingStar.setNumStars(5);
            ratingStar.setRating(rating_record.get(i));
            ratingStar.setStepSize(1);

            final int rating_pos = i;
            ratingStar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    rating_record.set(rating_pos,(int)rating);
                    full_record.set(rating_pos,(int)rating);
                }
            });

            ratingLayout.addView(ratingStar);
            if(proceed) rating_list.add(ratingStar);
            imgLayout.addView(ratingLayout);
            gl.addView(imgLayout);
        }
        glayout.addView(gl);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.search:

                //create dialog first
                dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog);
                dialog.setTitle("search image");

                Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                mEdit = (EditText)findViewById(R.id.searchImage);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bitmap bitmap;
                        try {
                            ImageView i = (ImageView)findViewById(R.id.imgView1);
                            if(mEdit != null) {
                                bitmap = BitmapFactory.decodeStream((InputStream)new URL(mEdit.getText().toString()).getContent());
                                i.setImageBitmap(bitmap);
                            }

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                Button dialogButtonCancel = (Button) dialog.findViewById(R.id.dialogButtonCancel);
                dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return true;

            case R.id.load:
                if(portrait){
                    portraitLoading();
                } else{
                    landscapeLoading();
                }
                loaded = true;
                return true;

            case R.id.clear:
                if(ll != null) ll.removeAllViews();
                if (gl != null) gl.removeAllViews();
                loaded = false;
                doingFilter = false;
                ratingFilter = 0;
                setRatingFilter(ratingFilter);

                view_list = new ArrayList<>();
                all_images = new ArrayList<>();
                rating_record = new ArrayList<>();
                full_record = new ArrayList<>();
                for (int i =0; i < images.length; i++){
                    view_list.add(images[i]);
                    all_images.add(images[i]);
                    rating_record.add(0);
                    full_record.add(0);
                }
                return true;

            case R.id.rating1:
                mIcon1.setIcon(R.drawable.ic_star_black_24dp);
                mIcon2.setIcon(R.drawable.ic_star_border_black_24dp);
                mIcon3.setIcon(R.drawable.ic_star_border_black_24dp);
                mIcon4.setIcon(R.drawable.ic_star_border_black_24dp);
                mIcon5.setIcon(R.drawable.ic_star_border_black_24dp);
                setFilter(1);
                ratingFilter = 1;
                if(portrait) portraitLoading();
                else landscapeLoading();
                return true;
            case R.id.rating2:
                mIcon1.setIcon(R.drawable.ic_star_black_24dp);
                mIcon2.setIcon(R.drawable.ic_star_black_24dp);
                mIcon3.setIcon(R.drawable.ic_star_border_black_24dp);
                mIcon4.setIcon(R.drawable.ic_star_border_black_24dp);
                mIcon5.setIcon(R.drawable.ic_star_border_black_24dp);
                setFilter(2);
                ratingFilter = 2;
                if(portrait) portraitLoading();
                else landscapeLoading();
                return true;
            case R.id.rating3:
                mIcon1.setIcon(R.drawable.ic_star_black_24dp);
                mIcon2.setIcon(R.drawable.ic_star_black_24dp);
                mIcon3.setIcon(R.drawable.ic_star_black_24dp);
                mIcon4.setIcon(R.drawable.ic_star_border_black_24dp);
                mIcon5.setIcon(R.drawable.ic_star_border_black_24dp);
                setFilter(3);
                ratingFilter = 3;
                if(portrait) portraitLoading();
                else landscapeLoading();
                return true;
            case R.id.rating4:
                mIcon1.setIcon(R.drawable.ic_star_black_24dp);
                mIcon2.setIcon(R.drawable.ic_star_black_24dp);
                mIcon3.setIcon(R.drawable.ic_star_black_24dp);
                mIcon4.setIcon(R.drawable.ic_star_black_24dp);
                mIcon5.setIcon(R.drawable.ic_star_border_black_24dp);
                setFilter(4);
                ratingFilter = 4;
                if(portrait) portraitLoading();
                else landscapeLoading();
                return true;
            case R.id.rating5:
                mIcon1.setIcon(R.drawable.ic_star_black_24dp);
                mIcon2.setIcon(R.drawable.ic_star_black_24dp);
                mIcon3.setIcon(R.drawable.ic_star_black_24dp);
                mIcon4.setIcon(R.drawable.ic_star_black_24dp);
                mIcon5.setIcon(R.drawable.ic_star_black_24dp);
                setFilter(5);
                ratingFilter = 5;
                if(portrait) portraitLoading();
                else landscapeLoading();
                return true;
            case R.id.erase:
                mIcon1.setIcon(R.drawable.ic_star_border_black_24dp);
                mIcon2.setIcon(R.drawable.ic_star_border_black_24dp);
                mIcon3.setIcon(R.drawable.ic_star_border_black_24dp);
                mIcon4.setIcon(R.drawable.ic_star_border_black_24dp);
                mIcon5.setIcon(R.drawable.ic_star_border_black_24dp);
                ratingFilter = 0;
                setFilter(ratingFilter);
                if(doingFilter && loaded){
                    if(portrait) portraitLoading();
                    else landscapeLoading();
                    doingFilter = false;
                }


                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        portrait = !portrait;
        super.onConfigurationChanged(newConfig);

        if (portrait){
            setContentView(R.layout.activity_main);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Fotag(sachen)");
            if(loaded) portraitLoading();
        }else{
            setContentView(R.layout.activity_main_land);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Fotag(sachen)");
            if(loaded) landscapeLoading();
        }
    }

    public void setFilter(Integer r){
        ArrayList<Integer> removeList = new ArrayList<Integer>();
        view_list = new ArrayList<>();
        rating_record = new ArrayList<>();
        for (int i = 0; i < all_images.size(); i++){
            view_list.add(images[i]);
            rating_record.add(full_record.get(i));
        }
        for (int i = 0; i < all_images.size(); i++){
            if (r > rating_record.get(i)){
                removeList.add(i);
            }
        }
        for (int i = removeList.size()-1; i >= 0 ; i--){
            view_list.remove((int)removeList.get(i));
            rating_record.remove((int)removeList.get(i));
        }

        if(r != 0) doingFilter = true;
    }

    public void setRatingFilter(int r){

        switch (r){
            case 0:
                mIcon1.setIcon(R.drawable.ic_star_border_black_24dp);
                mIcon2.setIcon(R.drawable.ic_star_border_black_24dp);
                mIcon3.setIcon(R.drawable.ic_star_border_black_24dp);
                mIcon4.setIcon(R.drawable.ic_star_border_black_24dp);
                mIcon5.setIcon(R.drawable.ic_star_border_black_24dp);
                return;
            case 1:
                mIcon1.setIcon(R.drawable.ic_star_black_24dp);
                mIcon2.setIcon(R.drawable.ic_star_border_black_24dp);
                mIcon3.setIcon(R.drawable.ic_star_border_black_24dp);
                mIcon4.setIcon(R.drawable.ic_star_border_black_24dp);
                mIcon5.setIcon(R.drawable.ic_star_border_black_24dp);
                return;
            case 2:
                mIcon1.setIcon(R.drawable.ic_star_black_24dp);
                mIcon2.setIcon(R.drawable.ic_star_black_24dp);
                mIcon3.setIcon(R.drawable.ic_star_border_black_24dp);
                mIcon4.setIcon(R.drawable.ic_star_border_black_24dp);
                mIcon5.setIcon(R.drawable.ic_star_border_black_24dp);
                return;
            case 3:
                mIcon1.setIcon(R.drawable.ic_star_black_24dp);
                mIcon2.setIcon(R.drawable.ic_star_black_24dp);
                mIcon3.setIcon(R.drawable.ic_star_black_24dp);
                mIcon4.setIcon(R.drawable.ic_star_border_black_24dp);
                mIcon5.setIcon(R.drawable.ic_star_border_black_24dp);
                return;
            case 4:
                mIcon1.setIcon(R.drawable.ic_star_black_24dp);
                mIcon2.setIcon(R.drawable.ic_star_black_24dp);
                mIcon3.setIcon(R.drawable.ic_star_black_24dp);
                mIcon4.setIcon(R.drawable.ic_star_black_24dp);
                mIcon5.setIcon(R.drawable.ic_star_border_black_24dp);
                return;
            case 5:
                mIcon1.setIcon(R.drawable.ic_star_black_24dp);
                mIcon2.setIcon(R.drawable.ic_star_black_24dp);
                mIcon3.setIcon(R.drawable.ic_star_black_24dp);
                mIcon4.setIcon(R.drawable.ic_star_black_24dp);
                mIcon5.setIcon(R.drawable.ic_star_black_24dp);
                return;

        }
    }

    @Override
    public void update(Observable o, Object arg)
    {
        // Update button with click count from model
//        mIncrementButton.setText(String.valueOf(mModel.getCounter()));
    }
}
