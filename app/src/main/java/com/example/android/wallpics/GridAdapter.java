package com.example.android.wallpics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridView = convertView;
        if (gridView == null) {
            gridView = LayoutInflater.from(getContext()).inflate(
                    R.layout.grid_item, parent, false);
        }
        String downloadUrl = getItem(position);

        ImageView imageView = (ImageView) gridView.findViewById(R.id.image);

        Uri imageUri = Uri.parse(downloadUrl);
        Picasso.with(getContext()).load(imageUri).placeholder(R.drawable.default_image).into(imageView);
        return gridView;
    }
}
