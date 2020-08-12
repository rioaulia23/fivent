package com.example.fiventapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.ViewPager
import com.example.fiventapp.R
import com.synnapps.carouselview.ViewListener

class ViewPagerAdapter(private val context: Context) : ViewListener {
    private var layoutInflater: LayoutInflater? = null
    private val images = arrayOf(R.drawable.gunung, R.drawable.gunung, R.drawable.gunung)
    fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`

    }

    fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val item = layoutInflater!!.inflate(R.layout.item_slider, null)
        val image = item.findViewById<View>(R.id.img_slider) as ImageView
//        val btnfav = item.findViewById<View>(R.id.btn_fav) as FloatingActionButton
        image.setImageResource(images[position])


        val vp = container as ViewPager
        vp.addView(item, 0)
        return item
    }

    override fun setViewForPosition(position: Int): View {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}