package com.sanjaydevtech.chineseappdetector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.github.ybq.android.spinkit.style.CubeGrid;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sanjaydevtech.chineseappdetector.adapter.AppListAdapter;
import com.sanjaydevtech.chineseappdetector.databinding.ActivityMainBinding;
import com.sanjaydevtech.chineseappdetector.model.AppItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public static final String TAG = MainActivity.class.getSimpleName();
    private HashMap<String, Object> mapList = new HashMap<>();
    private DatabaseReference pkRef = FirebaseDatabase.getInstance().getReference("packages");
    private AppListAdapter adapter;
    private ArrayList<AppItem> appItems = new ArrayList<>();
    private SharedPreferences preferences;
    private boolean isCollapsed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferences = getSharedPreferences("intro", MODE_PRIVATE);
        if (!preferences.contains("intro") || preferences.getInt("intro", 0) == 0) {
            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
            finish();
        }
        setSupportActionBar(binding.toolbar);

        binding.progressBar.setVisibility(View.GONE);
        binding.progressBar.setIndeterminateDrawable(new CubeGrid());
        isThereApps(false, "NO APPS SCANNED YET");
        adapter = new AppListAdapter(this);
        binding.appListView.setLayoutManager(new LinearLayoutManager(this));
        binding.appListView.setAdapter(adapter);
        binding.btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                scanApp();
            }
        });
        binding.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isCollapsed = true;
                    binding.collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    isCollapsed = false;
                    binding.collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (isCollapsed) {
            binding.appBarLayout.setExpanded(true, true);
        } else {
            super.onBackPressed();
        }

    }

    private void scanApp() {
        binding.progressBar.setVisibility(View.VISIBLE);
        mapList.clear();
        final PackageManager packageManager = getPackageManager();
        List<ApplicationInfo> packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo applicationInfo : packages) {
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
            } else {
                mapList.put(applicationInfo.packageName, null);
            }
        }
        pkRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                appItems.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String packageName = dataSnapshot.getKey().replaceAll("-",".");
                    if (mapList.containsKey(packageName)) {
                        ApplicationInfo applicationInfo;
                        try {
                            applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
                            appItems.add(new AppItem(applicationInfo.loadLabel(packageManager).toString(), applicationInfo.packageName, applicationInfo.loadIcon(packageManager)));
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (appItems.size() > 0) {
                    isThereApps(true, null);

                } else {
                    isThereApps(false, "GREAT NO CHINESE APPS");
                }
                Runnable runnable = new Runnable(){

                    @Override
                    public void run() {
                        binding.progressBar.setVisibility(View.GONE);
                        adapter.setAppItems(appItems);
                        binding.appBarLayout.setExpanded(false, true);
                    }
                };
                new Handler().postDelayed(runnable, 1000);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, error.getMessage());
            }
        });
    }

    private void isThereApps(boolean b, String info) {
        if (b) {
            binding.appNoData.setVisibility(View.GONE);
            binding.appListView.setVisibility(View.VISIBLE);
        } else {
            binding.appDataInfo.setText(info);
            binding.appNoData.setVisibility(View.VISIBLE);
            binding.appListView.setVisibility(View.GONE);
        }
    }


}