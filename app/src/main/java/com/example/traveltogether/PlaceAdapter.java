package com.example.traveltogether;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class PlaceAdapter extends BaseAdapter {

    private Context context;
    private  int layout;
    private ArrayList<Review> reviewArrayList;
    private DatabaseReference dbRef;

    public PlaceAdapter(Context context, int layout, ArrayList<Review> reviewArrayList) {
        this.context = context;
        this.layout = layout;
        this.reviewArrayList = reviewArrayList;
        dbRef = FirebaseDatabase.getInstance().getReference();

    }

    public class ViewHolder
    {
        TextView txtName,txtUser,txtTime, txtLike, txtView ;
        ImageView img ;
        CircleImageView imgUser;


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
            viewHolder.txtName = view.findViewById(R.id.txtUserName);
            viewHolder.txtLike = view.findViewById(R.id.txtLike);
            viewHolder.txtUser = view.findViewById(R.id.txtTitle);
            viewHolder.txtTime = view.findViewById(R.id.txtTime);
            viewHolder.txtView = view.findViewById(R.id.txtView);
            viewHolder.img = view.findViewById(R.id.imageView);
            viewHolder.imgUser = view.findViewById(R.id.imgUser);

            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }
        final Review review = reviewArrayList.get(i);


        viewHolder.txtName.setText(review.getName());
        viewHolder.txtLike.setText(String.valueOf(review.getLike()));
        viewHolder.txtView.setText(String.valueOf(review.getView()));
        viewHolder.txtTime.setText(review.getTime());
        Glide.with(context).load(review.getImg()).apply(RequestOptions.bitmapTransform(new BlurTransformation(3, 2))).into(viewHolder.img);

        dbRef.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    if(review.getAuthor().equals(snapshot.getValue(User.class).getUid()))
                    {
                        Glide.with(context).load(snapshot.getValue(User.class).getSrc()).into(viewHolder.imgUser);
                        viewHolder.txtUser.setText(snapshot.getValue(User.class).getHoTen());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }
}
