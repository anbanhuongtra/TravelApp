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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.TreeMap;

public class TripActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TripAdapter tripAdapter;
    ArrayList<Trip> tripArrayList;
    ListView listTrip;
    androidx.appcompat.widget.Toolbar toolbar;
    DrawerLayout mDrawerLayout;
    NavigationView navigationView;
    DatabaseReference dbReference;
    FirebaseUser fbUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        listTrip = findViewById(R.id.listTrip);

        tripArrayList = new ArrayList<>();
        toolbar = findViewById(R.id.toolbarBottom);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        dbReference = FirebaseDatabase.getInstance().getReference();
        fbUser = FirebaseAuth.getInstance().getCurrentUser();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Button btnNew = toolbar.findViewById(R.id.add);
        Button btnOption = toolbar.findViewById(R.id.search);
        EditText edtSearch = toolbar.findViewById(R.id.edtSearch);
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);

        LoadTrip();


        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tripArrayList.clear();
                LoadTrip();
                tripAdapter.notifyDataSetChanged();
                pullToRefresh.setRefreshing(false);
            }
        });


        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TripActivity.this, CreateTripActivity.class);
                startActivity(intent);

            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {
                tripArrayList.clear();
                dbReference.child("Trips").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Trip trip = snapshot.getValue(Trip.class);
                            if(trip.getTitle().toLowerCase().indexOf(charSequence.toString().toLowerCase()) != -1)
                                tripArrayList.add(trip);
                        }
                        tripAdapter = new TripAdapter(getBaseContext(), R.layout.trip_item, tripArrayList);
                        listTrip.setAdapter(tripAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        listTrip.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Trip trip = tripArrayList.get(i);
                Intent intent = new Intent(TripActivity.this, DetailTripActivity.class);
                intent.putExtra("dataTrip", trip);
                startActivity(intent);

            }
        });
    }

    public void LoadTrip() {
        dbReference.child("Trips").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tripArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Trip trip = snapshot.getValue(Trip.class);
                    tripArrayList.add(trip);
                    tripAdapter = new TripAdapter(getBaseContext(), R.layout.trip_item, tripArrayList);
                    listTrip.setAdapter(tripAdapter);
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
                intent = new Intent(TripActivity.this, ChatActivity.class);
                startActivity(intent);
                break;
            case R.id.profile:
                intent = new Intent(TripActivity.this, ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.review:
                intent = new Intent(TripActivity.this, ListActivity.class);
                startActivity(intent);
                break;
            case R.id.favourite:
                intent = new Intent(TripActivity.this, FavouriteActivity.class);
                startActivity(intent);
                break;
            case R.id.logOut:
                intent = new Intent(TripActivity.this, LoginActivity.class);
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
