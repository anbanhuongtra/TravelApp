package com.example.traveltogether;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    PlaceAdapter placeAdapter;
    ArrayList<Review> reviewArrayList;
    ListView listReview;
    androidx.appcompat.widget.Toolbar toolbar;
    DrawerLayout mDrawerLayout;
    NavigationView navigationView;
    DatabaseReference dbReference;
    FirebaseUser fbUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listReview = findViewById(R.id.listReview);

        reviewArrayList = new ArrayList<>();
        toolbar = findViewById(R.id.toolbarBottom);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        dbReference = FirebaseDatabase.getInstance().getReference();
        fbUser = FirebaseAuth.getInstance().getCurrentUser();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Button btnOption = toolbar.findViewById(R.id.add);

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


        listReview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Review review = reviewArrayList.get(i);
                dbReference.child("Reviews").child(review.getId()).child("view").setValue(review.getView()+1);
                Intent intent = new Intent(ListActivity.this, DetailActivity.class);
                intent.putExtra("data", review);
                startActivity(intent);

            }
        });
    }

    public void LoadReview() {
        dbReference.child("Reviews").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reviewArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Review review = snapshot.getValue(Review.class);
                    if(snapshot.child("userLike").getChildrenCount() != 0)
                        review.setLike(snapshot.child("userLike").getChildrenCount());
                    else review.setLike(0);
                    reviewArrayList.add(review);
                    placeAdapter = new PlaceAdapter(getBaseContext(), R.layout.information_place, reviewArrayList);
                    listReview.setAdapter(placeAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//

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

    private void status(String status)
    {
        dbReference.child("User").child(fbUser.getUid()).child("status").setValue(status);
//
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}
