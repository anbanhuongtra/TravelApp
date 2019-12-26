package com.example.traveltogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailTripActivity extends AppCompatActivity {

    TextView txtUserName, edtStart, edtEnd, edtDes, edtOrigin, edtAmount, edtTrans, edtTitle, edtContent;
    Button btnContact;
    CheckBox isLike;
    CircleImageView imgUser;
    Trip trip;
    DatabaseReference mData;
    FirebaseUser fbUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_trip);


        AnhXa();
        mData = FirebaseDatabase.getInstance().getReference();
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        GetData();

        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), MessageActivity.class);
                intent.putExtra("userid", trip.getHost());
                startActivity(intent);
            }
        });

        // set Like
        isLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (isLike.isChecked())
                    mData.child("Trips").child(trip.getId()).child("userLike").child(fbUser.getUid()).setValue("true");

                else
                    mData.child("Trips").child(trip.getId()).child("userLike").child(fbUser.getUid()).removeValue();

            }
        });

        //get Like
        mData.child("Trips").child(trip.getId()).child("userLike").child(fbUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                isLike.setChecked(true);
                else  isLike.setChecked(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void GetData() {
        trip = (Trip) getIntent().getSerializableExtra("dataTrip");
        edtStart.setText(trip.getDateStart());
        edtEnd.setText(trip.getDateEnd());
        edtDes.setText(trip.getDestination());
        edtOrigin.setText(trip.getOrigin());
        edtAmount.setText(String.valueOf(trip.getNumOfMember()));
        edtTitle.setText(trip.getTitle());
        edtContent.setText(trip.getContent());
        mData.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    if(snapshot.getValue(User.class).getUid().equals(trip.getHost()))
                    {
                        txtUserName.setText(snapshot.getValue(User.class).getHoTen());
                        if(!snapshot.getValue(User.class).getSrc().equals(""))
                        Glide.with(getBaseContext()).load((snapshot.getValue(User.class).getSrc())).into(imgUser);
                        else imgUser.setImageResource(R.drawable.null_avata);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void AnhXa() {
        edtStart = findViewById(R.id.edtStart);
        edtEnd = findViewById(R.id.edtEnd);
        edtDes = findViewById(R.id.edtDes);
        edtOrigin = findViewById(R.id.edtOrigin);
        edtAmount = findViewById(R.id.edtAmount);
        edtTrans = findViewById(R.id.edtTrans);
        edtTitle = findViewById(R.id.edtTitle);
        edtContent = findViewById(R.id.edtContent);
        btnContact = findViewById(R.id.btnContact);
        txtUserName = findViewById(R.id.txtUserName);
        imgUser = findViewById(R.id.imgUser);
        isLike = findViewById(R.id.Like);

    }

}
