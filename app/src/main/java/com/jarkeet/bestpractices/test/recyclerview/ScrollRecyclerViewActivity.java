package com.jarkeet.bestpractices.test.recyclerview;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.jarkeet.bestpractices.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class ScrollRecyclerViewActivity extends AppCompatActivity {

    private RecyclerView mRv;
    private Button mAddBtn;
    private Button mStopBtn;
    private Adapter mAdapter;
    private List<String> mList = new ArrayList<>();
    private boolean stop = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_scroll_recyclerview);
        initView();
    }

    private void initView() {

        mAddBtn = findViewById(R.id.btn_add);
        mStopBtn = findViewById(R.id.btn_stop);
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop = false;
                extracted();

//                mList.add(0,  "0");
//                mList.remove(mList.size() -1);
//                mAdapter.notifyDataSetChanged();

//                mAdapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.SlideInBottom);
//                mAdapter.isAnimationFirstOnly()
//                mAdapter.setAdapterAnimation();
            }

            private void extracted() {
                if(stop) {
                    return;
                }
                int interval = new Random().nextInt(100);

                mAddBtn.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        mAdapter.addData( 0, new Random().nextInt(100) + "");
                        int i = new Random().nextInt(100);
                        Log.d("testLog",i + "");
                        mAdapter.addData( 0,  i + "");
                        mAdapter.removeAt( mList.size() - 1);
//                        mRv.smoothScrollBy(0, -mRv.getHeight() / 5);
                        mRv.smoothScrollToPosition(0);


                        extracted();
                    }
                } ,50 + interval);
            }
        });

        mStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop = true;
            }
        });

        mRv = findViewById(R.id.rv);
//        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.setLayoutManager(new SmoothScrollLayoutManager(this));
        for(int i = 0; i < 7; ++i) {
            mList.add(i + "");
        }
        mAdapter = new Adapter(mList);
        mRv.setAdapter(mAdapter);
//        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
//        defaultItemAnimator.setAddDuration(50);
//        defaultItemAnimator.setRemoveDuration(50);
//        defaultItemAnimator.setMoveDuration(50);
//        defaultItemAnimator.setChangeDuration(50);
        mRv.setItemAnimator(null);
        mRv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mAdapter.setAnimationEnable(false);

    }

    public class Adapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public Adapter( @Nullable List<String> data) {
            super(R.layout.layout_item_tv_simple_text, data);
        }


        @Override
        protected int getDefItemCount() {
            return super.getDefItemCount() - 1;
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, String s) {
            ViewGroup.LayoutParams layoutParams = holder.getView(R.id.tv_item).getLayoutParams();
            layoutParams.height = mRv.getHeight() / 5;
            holder.getView(R.id.tv_item).setLayoutParams(layoutParams);

            TextView textView = holder.getView(R.id.tv_item);
            Date date = Calendar.getInstance().getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:MM:ss");
            String format = simpleDateFormat.format(date);
            textView.setText(format + ">>>> " + s);
        }

        @Override
        public void onViewAttachedToWindow(@NonNull BaseViewHolder holder) {
            super.onViewAttachedToWindow(holder);
//            BaseAnimation adapterAnimation = getAdapterAnimation() == null ? new AlphaInAnimation() : getAdapterAnimation();
//            adapterAnimation.animators(holder.getView(R.id.tv_item));
        }
    }



}
