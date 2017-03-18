package com.example.android.wallpics;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Rachit on 2/18/2017.
 */
public class GridAdapter extends ArrayAdapter<String> {
    public GridAdapter(Context context, ArrayList<String> grid) {
        super(context, 0,grid);
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View gridView = convertView;
        if (gridView == null) {
            gridView = LayoutInflater.from(getContext()).inflate(
                    R.layout.grid_item, parent, false);
        }
        final String downloadUrl = getItem(position);

        final ImageView imageView = (ImageView) gridView.findViewById(R.id.imageView);
         final Uri imageUri = Uri.parse(downloadUrl);

        Glide.with(getContext()).load(imageUri).placeholder(R.drawable.default_image)
                .into(imageView);
        return gridView;
    }
}
