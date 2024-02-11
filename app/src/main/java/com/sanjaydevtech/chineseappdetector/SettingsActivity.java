package com.sanjaydevtech.chineseappdetector;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.sanjaydevtech.chineseappdetector.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {


    private ActivitySettingsBinding binding;
    private SharedPreferences.Editor editor;
    private boolean isValid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        setSupportActionBar(binding.toolbar);
        boolean isOn = preferences.getBoolean("alert", false);
        binding.switchCheck.setChecked(isOn);
        isValid = true;
        editor = preferences.edit();
        binding.switchCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isValid) {
                return;
            }
            editor.putBoolean("alert", isChecked);
            editor.apply();
            Snackbar.make(binding.base, "Changes saved", Snackbar.LENGTH_SHORT).show();


        });
    }
}