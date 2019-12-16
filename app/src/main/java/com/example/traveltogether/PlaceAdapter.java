package com.example.traveltogether;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class PlaceAdapter extends BaseAdapter {

    private Context context;
    private  int layout;
    private ArrayList<Review> reviewArrayList;

    public PlaceAdapter(Context context, int layout, ArrayList<Review> reviewArrayList) {
        this.context = context;
        this.layout = layout;
        this.reviewArrayList = reviewArrayList;
    }

    public class ViewHolder
    {
        TextView txtName ;
        ImageView img ;

    }

    @Override
    public int getCount() {
        return reviewArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;

        if (view == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout, null);
            viewHolder = new ViewHolder();
            viewHolder.txtName = view.findViewById(R.id.txtName);
            viewHolder.img = view.findViewById(R.id.imageView);

            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }
        final Review review = reviewArrayList.get(i);

        viewHolder.txtName.setText(review.getName());
        Glide.with(context).load(review.getImg()).apply(RequestOptions.bitmapTransform(new BlurTransformation(3, 2))).into(viewHolder.img);

        return view;
    }
}
