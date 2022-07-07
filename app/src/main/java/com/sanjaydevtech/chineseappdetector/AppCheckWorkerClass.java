package com.sanjaydevtech.chineseappdetector;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class AppCheckWorkerClass extends Worker {

    private final Context context;
    private final DatabaseReference pkRef = FirebaseDatabase.getInstance().getReference("packages");
    public static final String CHANNEL_ID = "ALERT";
    private final SharedPreferences preferences;

    public AppCheckWorkerClass(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        preferences = context.getSharedPreferences("settings", MODE_PRIVATE);
    }

    @NonNull
    @Override
    public Result doWork() {
        if (!preferences.getBoolean("alert", false)) {
            return Result.success();
        }
        Set<String> userApps = new HashSet<>();
        final PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo applicationInfo : packages) {
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                userApps.add(applicationInfo.packageName);
            }
        }
        pkRef.addListenerForSingleValueEvent(new ValueEventListener() {
            boolean isPresent = false;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String packageName = dataSnapshot.getKey().replaceAll("-", ".").trim();
                    if (userApps.contains(packageName)) {
                        isPresent = true;
                        break;
                    }
                }
                if (isPresent) {
                    sendNotification();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return Result.success();
    }

    private void sendNotification() {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Chinese apps installation alert", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle("Chinese App Alert")
                .setContentText("There are some of the apps in your device which has its origin in China")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_ALARM);
        notificationManager.notify(0, notifyBuilder.build());
    }
}
