package com.example.traveltogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailActivity extends AppCompatActivity {
    TextView txtUserName, txtDescription, txtView, txtLike, txtTitle, txtTime, txtAddress;
    ImageView img, imgUser;
    CheckBox isLike;
    DatabaseReference dbRef;
    FirebaseUser fbUser;
    Review review, review2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        dbRef = FirebaseDatabase.getInstance().getReference();
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        AnhXa();
        GetData();

        isLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (isLike.isChecked())
                    dbRef.child("Reviews").child(review.getId()).child("userLike").child(fbUser.getUid()).setValue("true");

                else
                    dbRef.child("Reviews").child(review.getId()).child("userLike").child(fbUser.getUid()).removeValue();

            }
        });

    }

    void AnhXa() {
        txtUserName = findViewById(R.id.txtUserName);
        txtTitle = findViewById(R.id.txtTitle);
        txtDescription = findViewById(R.id.txtDescription);
        txtTime = findViewById(R.id.txtTime);
        txtLike = findViewById(R.id.txtLike);
        txtView = findViewById(R.id.txtView);
        img = findViewById(R.id.imgPlace);
        imgUser = findViewById(R.id.imgUser);
        txtAddress = findViewById(R.id.txtAddress);
        isLike = findViewById(R.id.Like);


    }

    void GetData() {
        review = (Review) getIntent().getSerializableExtra("data");

        dbRef.child("Reviews").child(review.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                review = dataSnapshot.getValue(Review.class);
                txtTitle.setText(review.getTitle());
                txtTime.setText(review.getTime());
                txtDescription.setText(review.getContent());
                txtView.setText(String.valueOf(review.getView()));
                txtAddress.setText(review.getAddress());
                Glide.with(getBaseContext()).load(review.getImg()).into(img);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // get info User
        dbRef.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (review.getAuthor().equals(snapshot.getValue(User.class).getUid())) {
                        Glide.with(getBaseContext()).load(snapshot.getValue(User.class).getSrc()).into(imgUser);
                        txtUserName.setText(snapshot.getValue(User.class).getHoTen());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //get check
        dbRef.child("Reviews").child(review.getId()).child("userLike").child(fbUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    isLike.setChecked(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //get like
        dbRef.child("Reviews").child(review.getId()).child("userLike").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                txtLike.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
