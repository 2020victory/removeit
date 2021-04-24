package com.sanjaydevtech.chineseappdetector.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sanjaydevtech.chineseappdetector.databinding.AppItemBinding;
import com.sanjaydevtech.chineseappdetector.model.AppItem;

import java.util.ArrayList;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.AppViewHolder>{

    private final Context context;
    private ArrayList<AppItem> appItems = new ArrayList<>();

    public AppListAdapter(Context context) {
        this.context = context;
    }

    public void setAppItems(ArrayList<AppItem> appItems) {
        this.appItems = appItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AppViewHolder(AppItemBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        final AppItem appItem = appItems.get(position);
        holder.binding.appName.setText(appItem.getName());
        holder.binding.imageView.setImageDrawable(appItem.getIcon());
        holder.binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DELETE);
                intent.setData(Uri.parse("package:"+appItem.getPackageName()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return appItems.size();
    }

    static class AppViewHolder extends RecyclerView.ViewHolder {
        private final AppItemBinding binding;

        public AppViewHolder(AppItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
