package com.stuff.doujin.h2r;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class DoujinReaderActivity extends AppCompatActivity {

    private ImageView currentImageView;
    private ImageView nextImageView;
    private ImageView prevImageView;
    private ArrayList<String> pageList;
    private RequestOptions options = new RequestOptions();
    private Button leftButton;
    private Button rightButton;
    private int viewIndex;
    private String baseImageUrl = "https://static.hentaicdn.com/hentai";
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_doujin_reader);
        getSupportActionBar().hide();

        Intent intent = getIntent();
        pageList = intent.getStringArrayListExtra("PAGES");

        currentImageView =  findViewById(R.id.image_view_1);
        nextImageView =  findViewById(R.id.image_view_2);
        prevImageView =  findViewById(R.id.image_view_3);

        leftButton = findViewById(R.id.left_button);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextPage();
            }
        });

        rightButton = findViewById(R.id.right_button);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevPage();
            }
        });

        options.fitCenter();

        Glide.with(currentImageView.getContext()).clear(currentImageView);
        Glide.with(currentImageView.getContext()).load(baseImageUrl + pageList.get(viewIndex)).apply(options).into(currentImageView);
        currentImageView.setVisibility(View.VISIBLE);
        toast = Toast.makeText(getBaseContext(), (viewIndex + 1) + "/" + pageList.size() , Toast.LENGTH_SHORT);
        toast.show();

        Glide.with(nextImageView.getContext()).clear(nextImageView);
        if(pageList.size() > 1) {
            Glide.with(nextImageView.getContext()).load(baseImageUrl + pageList.get(viewIndex + 1)).apply(options).into(nextImageView);
        }
    }


    private void nextPage() {
        if(viewIndex + 1 >= pageList.size()) {
            return;
        }
        viewIndex++;
        toast.cancel();
        toast = Toast.makeText(getBaseContext(), (viewIndex + 1) + "/" + pageList.size() , Toast.LENGTH_SHORT);
        toast.show();
        ImageView temp = prevImageView;
        prevImageView = currentImageView;
        prevImageView.setVisibility(View.GONE);
        currentImageView = nextImageView;
        currentImageView.setVisibility(View.VISIBLE);
        nextImageView = temp;
        Glide.with(nextImageView.getContext()).clear(nextImageView);
        if(viewIndex + 1 >= pageList.size()) {
            return;
        }
        Glide.with(nextImageView.getContext()).load(baseImageUrl + pageList.get(viewIndex + 1)).apply(options).into(nextImageView);
    }

    private void prevPage() {
        if(viewIndex <= 0) {
            return;
        }
        viewIndex--;
        toast.cancel();
        toast = Toast.makeText(getBaseContext(), (viewIndex + 1) + "/" + pageList.size() , Toast.LENGTH_SHORT);
        toast.show();
        ImageView temp = nextImageView;
        nextImageView = currentImageView;
        nextImageView.setVisibility(View.GONE);
        currentImageView = prevImageView;
        prevImageView = temp;
        currentImageView.setVisibility(View.VISIBLE);
        Glide.with(prevImageView.getContext()).clear(prevImageView);
        if(viewIndex - 1 < 0) {
            return;
        }
        Glide.with(prevImageView.getContext()).load(baseImageUrl + pageList.get(viewIndex - 1)).apply(options).into(prevImageView);
    }

    private void jumpToPage(int index) {
        if(index == viewIndex) {
            return;
        }

        if(index < 0 || index >= pageList.size()) {
            Toast.makeText(getBaseContext(), "Page Out Of Range", Toast.LENGTH_SHORT).show();
        }
    }
}