package com.sanjaydevtech.chineseappdetector.adapter;

import com.sanjaydevtech.chineseappdetector.model.AppItem;

@FunctionalInterface
public interface OnAppItemClickListener {
    void onClick(AppItem item);
}
