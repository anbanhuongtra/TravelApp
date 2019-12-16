package com.example.traveltogether;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {
    TextView txtName,txtDescription;
    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Place place = (Place) getIntent().getSerializableExtra("data");
        txtName = findViewById(R.id.txtName);
        txtDescription = findViewById(R.id.txtDescription);
        img = findViewById(R.id.imgPlace);

        img.setImageResource(place.getImg());
        txtName.setText(place.getName());
        //txtDescription.setText(place.getProvince());


    }
}
