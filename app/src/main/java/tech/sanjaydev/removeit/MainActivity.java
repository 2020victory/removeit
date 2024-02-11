package tech.sanjaydev.removeit;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.github.ybq.android.spinkit.style.CubeGrid;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import tech.sanjaydev.removeit.adapter.AppListAdapter;
import tech.sanjaydev.removeit.databinding.ActivityMainBinding;
import tech.sanjaydev.removeit.model.AppItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public static final String TAG = MainActivity.class.getSimpleName();
    private final HashMap<String, Object> mapList = new HashMap<>();
    private final DatabaseReference pkRef = FirebaseDatabase.getInstance().getReference("packages");
    private AppListAdapter adapter;
    private final ArrayList<AppItem> appItems = new ArrayList<>();
    private SharedPreferences defaultPrefs;
    private boolean isCollapsed = false;
    public static final String CHANNEL_ID = "SIMPLE";

    private final ActivityResultLauncher<String> requestNotificationPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), o -> {

    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        defaultPrefs = getSharedPreferences("defaults", MODE_PRIVATE);
        if (!defaultPrefs.contains("intro") || defaultPrefs.getInt("intro", 0) == 0) {
            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
        setSupportActionBar(binding.toolbar);
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(AppCheckWorkerClass.class, 1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .setInitialDelay(15, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(AppCheckWorkerClass.CHANNEL_ID, ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest);

        final Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        binding.progressBar.setVisibility(View.INVISIBLE);
        binding.progressBar.setIndeterminateDrawable(new CubeGrid());
        isThereApps(false, "NO APPS SCANNED YET");
        adapter = new AppListAdapter(this);
        adapter.setOnAppItemClickListener(item -> {
            if (defaultPrefs.getInt("showRemoveWarning", 0) == 0) {
                new AlertDialog.Builder(this)
                        .setTitle("Warning")
                        .setCancelable(false)
                        .setMessage("If you proceed, this could uninstall the following application from your device.")
                        .setPositiveButton("Proceed", (d, _which) -> {
                            d.dismiss();
                            SharedPreferences.Editor editor = defaultPrefs.edit();
                            editor.putInt("showRemoveWarning", 1);
                            editor.apply();
                            Intent intent = new Intent(Intent.ACTION_DELETE);
                            intent.setData(Uri.parse("package:" + item.packageName()));
                            startActivity(intent);
                        })
                        .setNegativeButton("Back", (d, _which) -> d.dismiss())
                        .show();
            } else {
                Intent intent = new Intent(Intent.ACTION_DELETE);
                intent.setData(Uri.parse("package:" + item.packageName()));
                startActivity(intent);
            }
        });
        binding.appListView.setLayoutManager(new GridLayoutManager(this, 2));
        binding.appListView.setAdapter(adapter);
        binding.btnScan.setOnClickListener(v -> scanApp());
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

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                        window.setStatusBarColor(Color.WHITE);
                    }
                } else if (isShow) {
                    isCollapsed = false;
                    binding.collapsingToolbar.setTitle(" ");
                    isShow = false;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        window.getDecorView().setSystemUiVisibility(0);
                        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark));
                    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_info) {
            alertForInfo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void alertForInfo() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("About Us")
                .setMessage("We (Sanjay, Shivani, Rohinth) are the developers of this app.\nOur main intention in developing this app " +
                        "is to create an awareness to Indians about some chinese apps in their device.\n" +
                        "We created with ❤ and lots of lots of caffeine 😂 (just a programming joke)\n" +
                        "If you like to support us, just put a rating and give us your feedback 😇")
                .setPositiveButton("Give us Rating", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent playIntent = new Intent(Intent.ACTION_VIEW);
                        playIntent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.sanjaydevtech.chineseappdetector"));
                        startActivity(playIntent);
                    }
                })
                .setNegativeButton("Nope", (dialog1, which) -> dialog1.dismiss())
                .create();
        dialog.show();
    }

    private void scanApp() {
        binding.progressBar.setVisibility(View.VISIBLE);
        mapList.clear();
        final PackageManager packageManager = getPackageManager();
        List<ApplicationInfo> packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo applicationInfo : packages) {
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                mapList.put(applicationInfo.packageName, null);
            }
        }
        pkRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                appItems.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String packageName = dataSnapshot.getKey().replaceAll("-", ".").trim();
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
                Runnable runnable = () -> {
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    adapter.setAppItems(appItems);
                    binding.appBarLayout.setExpanded(false, true);
                };
                new Handler().postDelayed(runnable, 1500);
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