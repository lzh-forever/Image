package com.example.image.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.image.R
import com.example.image.databinding.FragmentHomeBinding
import kotlin.concurrent.timerTask


class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var viewmodel: HomeViewModel
    lateinit var adapter: ViewPagerAdapter
    val list = listOf(
        R.drawable.national_park1,
        R.drawable.china6,
        R.drawable.food16,
        R.drawable.liuyifei4,
        R.drawable.london1,
        R.drawable.mountain4,
        R.drawable.national_park1,
        R.drawable.china6
    )
    var isScrolling = false
    val handler = Handler(Looper.getMainLooper())
    val interval = 7000L
    val loop = Runnable {
        if(!isScrolling){
            binding.viewPager.setCurrentItem(++viewmodel.curPosition)
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewmodel = ViewModelProvider(this).get(HomeViewModel::class.java)
        adapter = ViewPagerAdapter(list, requireContext())
        binding.viewPager.adapter = adapter
        Log.d("home","create view")
        binding.viewPager.setCurrentItem(viewmodel.curPosition, false)
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    isScrolling = false
                    if (viewmodel.curPosition == 0) {
                        binding.viewPager.setCurrentItem(list.lastIndex - 1, false)
                    } else if (viewmodel.curPosition == list.lastIndex) {
                        binding.viewPager.setCurrentItem(1, false)
                    }
                    handler.postDelayed(loop,interval)
                } else if( state == ViewPager2.SCROLL_STATE_DRAGGING){
                    isScrolling = true
                    handler.removeCallbacks(loop)
                }
                Log.d("home","state changed: $state")

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
//                Log.d("home","scrolled")
            }

            override fun onPageSelected(position: Int) {
                viewmodel.curPosition = position
                Log.d("home", "$position")
            }
        }
        )

        handler.postDelayed(loop,interval)

        return binding.root
    }


    override fun onDestroyView() {
        handler.removeCallbacks(loop)
        Log.d("home","destroy view")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.d("home","destroy")

        super.onDestroy()
    }

    override fun onDetach() {
        Log.d("home","detach")
        super.onDetach()
    }
}