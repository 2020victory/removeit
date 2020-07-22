package com.sanjaydevtech.chineseappdetector.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sanjaydevtech.chineseappdetector.ScreenItem;
import com.sanjaydevtech.chineseappdetector.databinding.LayoutScreenBinding;

public class IntroFragment extends Fragment {
    private ScreenItem screenItem;
    private Context context;
    private LayoutScreenBinding binding;

    public IntroFragment() {
    }

    public IntroFragment(Context context, ScreenItem screenItem) {
        this.context = context;
        this.screenItem = screenItem;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutScreenBinding.inflate(inflater, container, false);
        binding.title.setText(screenItem.getTitle());
        binding.desc.setText(screenItem.getDescription());
        binding.img.setImageResource(screenItem.getImg());
        return binding.getRoot();
    }
}
