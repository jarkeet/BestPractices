package com.jarkeet.bestpractices.test;

import android.app.Activity;
import android.os.Bundle;

import com.jarkeet.bestpractices.R;

import androidx.annotation.Nullable;

public class TestCanvasActivity extends Activity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test_canvas);
    }
}
