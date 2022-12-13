package com.jarkeet.bestpractices.test.canvas;

import android.app.Activity;
import android.os.Bundle;

import com.jarkeet.bestpractices.R;

import androidx.annotation.Nullable;

public class CanvasActivity extends Activity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test_canvas);
    }
}
