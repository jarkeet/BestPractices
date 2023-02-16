package com.jarkeet.bestpractices.test.snaphelper;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.jarkeet.bestpractices.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SnapHelperTestActivity extends AppCompatActivity {

    private RecyclerView mRv;
    private Adapter mAdapter;
    private List<String> mList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_snaphelper_test);
        initView();
    }



    private void initView() {

        mRv = findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        new NLinearSnapHelper().attachToRecyclerView(mRv);

        for(int i = 0; i < 100; ++i) {
            mList.add(i + "");
        }
        mAdapter = new Adapter(mList);
        mRv.setAdapter(mAdapter);


    }

    public class Adapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public Adapter( @Nullable List<String> data) {
            super(R.layout.layout_item_list_snap_tv_simple_text, data);
        }


        @Override
        protected int getDefItemCount() {
            return super.getDefItemCount() - 1;
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, String s) {


            TextView textView = holder.getView(R.id.tv_item);
            int itemPosition = getItemPosition(s);

            ViewGroup.LayoutParams layoutParams = holder.getView(R.id.tv_item).getLayoutParams();
            if(itemPosition == 0) {
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                layoutParams.width = SizeUtils.dp2px(50);
            }
            textView.setLayoutParams(layoutParams);

            textView.setText(itemPosition + "");
        }


    }



}
