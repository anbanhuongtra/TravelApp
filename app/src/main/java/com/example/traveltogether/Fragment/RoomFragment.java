package com.example.traveltogether.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traveltogether.Chatlist;
import com.example.traveltogether.R;
import com.example.traveltogether.User;
import com.example.traveltogether.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class RoomFragment extends Fragment {

    RecyclerView recyclerView;
    UserAdapter userAdapter;
    List<User> userList;
    List<Chatlist> userIDList;

    FirebaseUser firebaseUser;
    DatabaseReference mData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userIDList = new ArrayList<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mData = FirebaseDatabase.getInstance().getReference();

        mData.child("Chatlist").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userIDList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chatlist chatlist = snapshot.getValue(Chatlist.class);
                    userIDList.add(chatlist);
                }

                showChatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void showChatList() {
        userList = new ArrayList<>();
        mData.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               userList.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    User user = snapshot.getValue(User.class);
                    for (Chatlist chatlist: userIDList)
                    {
                        if(user.getUid().equals(chatlist.getId()))
                            userList.add(user);
                    }

                }
                userAdapter = new UserAdapter(getContext(),userList,true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


}
