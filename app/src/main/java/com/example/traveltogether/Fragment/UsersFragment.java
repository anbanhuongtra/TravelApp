package com.example.traveltogether.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traveltogether.R;
import com.example.traveltogether.User;
import com.example.traveltogether.UserAdapter;
import com.example.traveltogether.User;
import com.example.traveltogether.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class UsersFragment extends Fragment {

    RecyclerView recyclerView;
    UserAdapter userAdapter;
    ArrayList<User> userList;
    EditText search_bar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        search_bar = view.findViewById(R.id.search);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userList = new ArrayList<>();
        readUser();


        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUser(charSequence.toString().toLowerCase());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        return view;
    }

    private void searchUser(final String s) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("User");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    User user = snapshot.getValue(User.class);
                    assert user != null;
                    assert firebaseUser != null;
                    if (!user.getUid().equals(firebaseUser.getUid()) && user.getHoTen().toLowerCase().indexOf(s) != -1 )
                        userList.add(user);
                }
                userAdapter = new UserAdapter(getContext(),userList,false);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readUser() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
        //userList.clear();

        mData.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (search_bar.getText().toString().equals("")) {
                    userList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        user.setUid(snapshot.getKey());
                        if (snapshot.child("src").getValue() != null)
                            user.setSrc((String) snapshot.child("src").getValue());
                        else user.setSrc("");
                        if (!user.getUid().equals(firebaseUser.getUid()))
                            userList.add(user);

                    }

                    userAdapter = new UserAdapter(getContext(), userList,false);
                    recyclerView.setAdapter(userAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        mData.child("User").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
////
////                User user = dataSnapshot.getValue(User.class);
////                user.setUID(dataSnapshot.getKey());
////                if (dataSnapshot.child("src").getValue() != null)
////                    user.setSrc((String) dataSnapshot.child("src").getValue());
////                else user.setSrc("");
////                if (!dataSnapshot.getKey().equals(firebaseUser.getUid()))
////                    userList.add(user);
////
////                userAdapter = new UserAdapter(getContext(), userList);
////                recyclerView.setAdapter(userAdapter);
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                User user = dataSnapshot.getValue(User.class);
//                user.setUID(dataSnapshot.getKey());
//                if (dataSnapshot.child("src").getValue() != null)
//                    user.setSrc((String) dataSnapshot.child("src").getValue());
//                else user.setSrc("");
//                if (!dataSnapshot.getKey().equals(firebaseUser.getUid()))
//                    userList.add(user);
//
//                userAdapter = new UserAdapter(getContext(), userList);
//                recyclerView.setAdapter(userAdapter);
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

    }

}
