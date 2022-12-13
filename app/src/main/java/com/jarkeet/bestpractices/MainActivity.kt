package com.jarkeet.bestpractices

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jarkeet.bestpractices.databinding.ActivityMainBinding
import com.jarkeet.bestpractices.test.KotlinTest
import com.jarkeet.bestpractices.test.canvas.CanvasActivity
import com.jarkeet.bestpractices.test.icon.IconicsActivity
import com.jarkeet.bestpractices.test.recyclerview.ScrollRecyclerViewActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {
        binding.rv.layoutManager = GridLayoutManager(this, 2)
        var items : MutableList<Adapter.ItemBean> = mutableListOf()

        items.add(Adapter.ItemBean("Iconics", IconicsActivity::class.java))
        items.add(Adapter.ItemBean("Canvas", CanvasActivity::class.java))
        items.add(Adapter.ItemBean("KotlinTest", KotlinTest::class.java))
        items.add(Adapter.ItemBean("ScrollRecyclerView", ScrollRecyclerViewActivity::class.java))

        var adapter = Adapter( R.layout.layout_item_simple_text, items)
        binding.rv.adapter = adapter

    }

    class Adapter( layoutResId: Int , data: MutableList<ItemBean>?, )
        : BaseQuickAdapter<Adapter.ItemBean, BaseViewHolder>(layoutResId, data) {

        override fun convert(holder: BaseViewHolder, item: ItemBean) {

            var btn = holder.getView<Button>(R.id.btn_item)
            btn.text = item.name
            btn.setOnClickListener(View.OnClickListener { context.startActivity( Intent(context, item.clazz)) })
        }

        data class ItemBean (var name : String, var clazz : Class<*>)

    }
}