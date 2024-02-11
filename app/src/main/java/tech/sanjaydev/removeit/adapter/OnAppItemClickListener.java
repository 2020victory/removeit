package tech.sanjaydev.removeit.adapter;

import tech.sanjaydev.removeit.model.AppItem;

@FunctionalInterface
public interface OnAppItemClickListener {
    void onClick(AppItem item);
}
