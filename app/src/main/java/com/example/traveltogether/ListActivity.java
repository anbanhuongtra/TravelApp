package com.example.traveltogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    PlaceAdapter placeAdapter;
    ArrayList<Place> placeArrayList;
    ArrayList<Review> reviewArrayList;
    ListView listPlace;
    androidx.appcompat.widget.Toolbar toolbar;
    DrawerLayout mDrawerLayout;
    NavigationView navigationView;
    DatabaseReference dbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listPlace = findViewById(R.id.listPlace);

        reviewArrayList = new ArrayList<>();
        toolbar = findViewById(R.id.toolbarBottom);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        dbReference = FirebaseDatabase.getInstance().getReference();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Button btnOption = toolbar.findViewById(R.id.add);

//        placeArrayList.add(new Place("Taj Mahal", "Agra", R.drawable.tajmahal));
//        placeArrayList.add(new Place("Vạn lý trường thành", "Hoài Nhu - Bắc Kinh", R.drawable.vltt));
//        placeArrayList.add(new Place("Tháp Eiffel", "Thành phố Paris", R.drawable.eiffel_tower));
//        placeArrayList.add(new Place("Biển Kỳ Co", "Thành phố Quy Nhơn", R.drawable.kyco));

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);

        LoadReview();


        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reviewArrayList.clear();
                LoadReview();
                placeAdapter.notifyDataSetChanged();
                pullToRefresh.setRefreshing(false);
            }
        });


        btnOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListActivity.this, NewReviewActivity.class);
                startActivity(intent);

            }
        });


//        listPlace.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Place place = placeArrayList.get(i);
//                Intent intent = new Intent(ListActivity.this, DetailActivity.class);
//                intent.putExtra("data", place);
//                startActivity(intent);
//
//            }
//        });
    }

    public void LoadReview() {
        dbReference.child("Reviews").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reviewArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Review review = snapshot.getValue(Review.class);
                    reviewArrayList.add(review);
                    placeAdapter = new PlaceAdapter(getBaseContext(), R.layout.information_place, reviewArrayList);
                    listPlace.setAdapter(placeAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Intent intent;
        switch (menuItem.getItemId()) {
            case R.id.chat:
                intent = new Intent(ListActivity.this, ChatActivity.class);
                startActivity(intent);
                break;
            case R.id.profile:
                intent = new Intent(ListActivity.this, ProfileActivity.class);
                startActivity(intent);
                break;
        }

        return true;
    }
}
