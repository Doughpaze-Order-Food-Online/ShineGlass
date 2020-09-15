package com.mg.shineglass.adapters;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.mg.shineglass.R;
import com.mg.shineglass.models.Banners;
import com.mg.shineglass.utils.constants;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BannerAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;
    private List<Banners> bannersList;

    //gloabals
    final Handler handler = new Handler();
    public Timer swipeTimer ;

    public BannerAdapter(Context context, List<Banners> bannersList) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.bannersList=bannersList;
    }


    @Override
    public int getCount() {
        if(bannersList != null){
            return bannersList.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;

    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = layoutInflater.inflate(R.layout.banner_item, container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.banner_image);
        String url= constants.BANNER_URL+bannersList.get(position).getUrl();
        Glide
                .with(context)
                .load(url)
//                .placeholder()
                .fitCenter()
                .into(imageView);


        container.addView(itemView);
        return itemView;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        container.removeView((CardView) object);
    }

    public void setTimer(final ViewPager myPager, int time, final int numPages, final int curPage){

        final Runnable Update = new Runnable() {
            int NUM_PAGES =numPages;
            int currentPage = curPage ;
            public void run() {
                if (currentPage == NUM_PAGES ) {
                    currentPage = 0;
                }
                myPager.setCurrentItem(currentPage++, true);
            }
        };

        swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(Update);
            }
        }, 1000, time*1000);

    }


}