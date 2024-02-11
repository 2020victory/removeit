package com.sanjaydevtech.chineseappdetector.fragment;

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
    private LayoutScreenBinding binding;

    public IntroFragment() {
    }

    public IntroFragment(ScreenItem screenItem) {
        this.screenItem = screenItem;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.title.setText(screenItem.title());
        binding.desc.setText(screenItem.description());
        binding.img.setImageResource(screenItem.img());
    }
}
