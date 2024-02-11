package tech.sanjaydev.removeit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import tech.sanjaydev.removeit.databinding.AppItemBinding;
import tech.sanjaydev.removeit.model.AppItem;

import java.util.ArrayList;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.AppViewHolder> {

    private OnAppItemClickListener onAppItemClickListener;
    private final Context context;
    private ArrayList<AppItem> appItems = new ArrayList<>();

    public AppListAdapter(Context context) {
        this.context = context;
    }

    public void setAppItems(ArrayList<AppItem> appItems) {
        this.appItems = appItems;
        notifyDataSetChanged();
    }

    public void setOnAppItemClickListener(OnAppItemClickListener onAppItemClickListener) {
        this.onAppItemClickListener = onAppItemClickListener;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AppViewHolder(AppItemBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        final AppItem appItem = appItems.get(position);
        holder.binding.appName.setText(appItem.name());
        holder.binding.imageView.setImageDrawable(appItem.icon());
        holder.binding.delete.setOnClickListener(v -> {
            onAppItemClickListener.onClick(appItem);
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
