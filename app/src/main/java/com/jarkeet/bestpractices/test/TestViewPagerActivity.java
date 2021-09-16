package com.jarkeet.bestpractices.test;

import android.os.Bundle;

import com.jarkeet.bestpractices.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;

public class TestViewPagerActivity extends FragmentActivity {

    @BindView(R.id.vp)
    ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_viewpager);


        initViewPager();

    }

    private void initViewPager() {
    }
}
