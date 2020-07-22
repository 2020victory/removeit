package com.sanjaydevtech.chineseappdetector;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.sanjaydevtech.chineseappdetector.adapter.IntroViewPagerAdapter;
import com.sanjaydevtech.chineseappdetector.databinding.ActivityIntroBinding;

import java.util.ArrayList;

public class IntroActivity extends AppCompatActivity {

    private ActivityIntroBinding binding;
    private IntroViewPagerAdapter adapter;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferences = getSharedPreferences("intro", MODE_PRIVATE);
        ArrayList<ScreenItem> screenItems = new ArrayList<>();
        screenItems.add(new ScreenItem("Hi there", "Welcome to Chinese app detector", R.mipmap.ic_launcher));
        screenItems.add(new ScreenItem("Stay Safe, Stay Secure", "Protect yourself from chinese apps", R.drawable.screen01));
        screenItems.add(new ScreenItem("Just one tap protect", "Click the scan button to scan your mobile", R.drawable.screen02));
        adapter = new IntroViewPagerAdapter(this,screenItems);
        setUpIndicators();
        setCurrentIndicator(0);
        binding.viewPager2.setAdapter(adapter);
        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
            }
        });
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = binding.viewPager2.getCurrentItem();
                if (currentItem == adapter.getItemCount() - 1) {
                    editor = preferences.edit();
                    editor.putInt("intro", 1);
                    editor.apply();
                    Intent intent = new Intent(IntroActivity.this, SplashActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    binding.viewPager2.setCurrentItem(currentItem+1 , true);
                }
            }
        });
    }

    private void setUpIndicators() {
        ImageView[] imageViews = new ImageView[adapter.getItemCount()];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(8, 0, 8, 0);
        for (int i = 0; i < imageViews.length; i++) {
            imageViews[i] = new ImageView(this);
            imageViews[i].setImageResource(R.drawable.onboarding_inactive_indicator);
            imageViews[i].setLayoutParams(params);
            binding.tabLayout2.addView(imageViews[i]);
        }
    }

    private void setCurrentIndicator(int index) {
        int childCounts = binding.tabLayout2.getChildCount();
        if (index == childCounts - 1) {
            binding.button.setText("Done");
        } else {
            binding.button.setText("Next");
        }
        for (int i = 0; i < childCounts; i++) {
            ImageView imageView = (ImageView)binding.tabLayout2.getChildAt(i);
            if (i == index) {
                imageView.setImageResource(R.drawable.onboarding_active_indicator);
            } else {
                imageView.setImageResource(R.drawable.onboarding_inactive_indicator);
            }
        }

    }


}