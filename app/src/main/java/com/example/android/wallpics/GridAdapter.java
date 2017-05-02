package com.example.android.wallpics;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

class GridAdapter extends ArrayAdapter<String> {
    GridAdapter(Context context, ArrayList<String> grid) {
        super(context, 0, grid);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        View gridView = convertView;
        if (gridView == null) {
            gridView = LayoutInflater.from(getContext()).inflate(
                    R.layout.grid_item, parent, false);
        }
        final String downloadUrl = getItem(position);

        final ImageView imageView = (ImageView) gridView.findViewById(R.id.imageView);
        final Uri imageUri = Uri.parse(downloadUrl);

        Glide.with(getContext()).load(imageUri)
                .into(imageView);
        return gridView;
    }
}
