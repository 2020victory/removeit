package com.sanjaydevtech.chineseappdetector.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.sanjaydevtech.chineseappdetector.ScreenItem;
import com.sanjaydevtech.chineseappdetector.fragment.IntroFragment;

import java.util.ArrayList;

public class IntroViewPagerAdapter extends FragmentStateAdapter {

    private final ArrayList<ScreenItem> screenItems;
    private final Context context;

    public IntroViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<ScreenItem> screenItems) {
        super(fragmentActivity);
        this.context = fragmentActivity;
        this.screenItems = screenItems;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new IntroFragment(context, screenItems.get(position));
    }

    @Override
    public int getItemCount() {
        return screenItems.size();
    }
}
