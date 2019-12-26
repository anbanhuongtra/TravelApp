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

public class TripAdapter extends BaseAdapter {

    private Context context;
    private  int layout;
    private ArrayList<Trip> tripArrayList;
    private DatabaseReference dbRef;

    public TripAdapter(Context context, int layout, ArrayList<Trip> tripArrayList) {
        this.context = context;
        this.layout = layout;
        this.tripArrayList = tripArrayList;
        dbRef = FirebaseDatabase.getInstance().getReference();

    }

    public class ViewHolder
    {
        TextView txtTitle,txtStart,txtEnd, txtDes, txtOrigin,txtUserName ;
        CircleImageView imgUser;


    }

    @Override
    public int getCount() {
        return tripArrayList.size();
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
            viewHolder.txtTitle = view.findViewById(R.id.txtTitle);
            viewHolder.txtStart = view.findViewById(R.id.txtStart);
            viewHolder.txtEnd = view.findViewById(R.id.txtEnd);
            viewHolder.txtDes = view.findViewById(R.id.txtDes);
            viewHolder.txtOrigin = view.findViewById(R.id.txtOrigin);
            viewHolder.txtUserName = view.findViewById(R.id.txtUserName);
            viewHolder.imgUser = view.findViewById(R.id.imgUser);

            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }
        final Trip trip = tripArrayList.get(i);


        viewHolder.txtTitle.setText(trip.getTitle());
        viewHolder.txtStart.setText(trip.getDateStart());
        viewHolder.txtEnd.setText(trip.getDateEnd());
        viewHolder.txtOrigin.setText(trip.getOrigin());
        viewHolder.txtDes.setText(trip.getDestination());

        dbRef.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    if(trip.getHost().equals(snapshot.getValue(User.class).getUid()))
                    {
                        if (!snapshot.getValue(User.class).getSrc().equals(""))
                        Glide.with(context).load(snapshot.getValue(User.class).getSrc()).into(viewHolder.imgUser);
                        else  viewHolder.imgUser.setImageResource(R.drawable.null_avata);
                        viewHolder.txtUserName.setText(snapshot.getValue(User.class).getHoTen());
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
