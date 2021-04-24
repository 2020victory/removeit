package com.sanjaydevtech.chineseappdetector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;

import com.google.android.material.snackbar.Snackbar;
import com.sanjaydevtech.chineseappdetector.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {


    private ActivitySettingsBinding binding;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private boolean isValid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferences = getSharedPreferences("settings", MODE_PRIVATE);
        setSupportActionBar(binding.toolbar);
        boolean isOn = preferences.getBoolean("alert", false);
        binding.switchCheck.setChecked(isOn);
        isValid = true;
        editor = preferences.edit();
        binding.switchCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isValid) {
                    return;
                }

                editor.putBoolean("alert", isChecked);
                editor.apply();
                Snackbar.make(binding.base, "Changes saved", Snackbar.LENGTH_SHORT).show();


            }
        });
    }
}