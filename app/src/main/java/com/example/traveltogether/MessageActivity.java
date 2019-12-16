package com.example.traveltogether;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.traveltogether.User;
import com.google.firebase.storage.UploadTask;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    ImageButton btnSend, btnAddImg, btnAddVideo;
    CircleImageView profile_image;
    CircleImageView img_on;
    CircleImageView img_off;
    TextView profile_name;
    EditText txtSend;
    FirebaseUser firebaseUser;
    DatabaseReference mData;
    MessageAdapter messageAdapter;
    List<Chat> chatList;
    RecyclerView recyclerView;
    String userid;
    StorageReference storageRef;
    ValueEventListener seenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        AnhXa();

        userid = getIntent().getStringExtra("userid");

        //Check status

        mData.child("User").child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getStatus().equals("online")) {
                    img_on.setVisibility(View.VISIBLE);
                    img_off.setVisibility(View.GONE);
                } else {
                    img_on.setVisibility(View.GONE);
                    img_off.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        btnAddImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, 10001);
                    } else {
                        pickImageFromGallery(1001);
                    }
                } else pickImageFromGallery(1001);
            }
        });

        btnAddVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, 10002);
                    } else {
                        pickVideoFromGallery(1002);
                    }
                } else pickVideoFromGallery(1002);
            }
        });


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = txtSend.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(firebaseUser.getUid(), userid, msg, "msg");
                }
                txtSend.setText("");
            }
        });

//
//        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
//
//
//                recyclerView.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        recyclerView.smoothScrollToPosition(chatList.size()-1);
//                    }
//                }, 100);
//            }
//
//        });
        srollLastMsg();


        mData.child("User").child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                profile_name.setText(user.getHoTen());
                if (!user.getSrc().equals(""))
                    Glide.with(getBaseContext()).load(user.getSrc()).into(profile_image);
                else profile_image.setImageResource(R.drawable.null_avata);
                readMessage(firebaseUser.getUid(), userid, user.getSrc());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        seenMessage(userid);

    }

    private void pickImageFromGallery(int requestcode) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, requestcode);
    }

    private void pickVideoFromGallery(int requestcode) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(intent, requestcode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1001 && data != null && data.getData() != null) {
            UploadSendImage(data.getData());

        }
        if (resultCode == RESULT_OK && requestCode == 1002 && data != null && data.getData() != null) {
            UploadSendVideo(data.getData());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void UploadSendImage(Uri uri) {

        // Upload picture to storage Firebase
        storageRef = FirebaseStorage.getInstance().getReference().child(Random(25));
        storageRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //Send message
                                sendMessage(firebaseUser.getUid(), userid, uri.toString(), "img");
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(MessageActivity.this, "Có lỗi", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void UploadSendVideo(Uri uri) {
        // Upload picture to storage Firebase
        storageRef = FirebaseStorage.getInstance().getReference().child(Random(25));
        storageRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //Send message
                                sendMessage(firebaseUser.getUid(), userid, uri.toString(), "vid");
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(MessageActivity.this, "Có lỗi", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void AnhXa() {
        btnSend = findViewById(R.id.btnSend);
        btnAddVideo = findViewById(R.id.addVideo);
        txtSend = findViewById(R.id.edtSend);
        img_on = findViewById(R.id.online);
        img_off = findViewById(R.id.offline);
        btnAddImg = findViewById(R.id.addImage);
        profile_name = findViewById(R.id.profile_name);
        profile_image = findViewById(R.id.profile_image);
        recyclerView = findViewById(R.id.recycleView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mData = FirebaseDatabase.getInstance().getReference();

    }

    private void seenMessage(final String userid) {

        seenListener = mData.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen", "true");
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void srollLastMsg() {
        // Scroll down last message
        mData.child("Chats").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                recyclerView.smoothScrollToPosition(chatList.size() - 1);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        KeyboardVisibilityEvent.setEventListener(
                MessageActivity.this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen) recyclerView.smoothScrollToPosition(chatList.size() );
                    }
                });

        //
    }

    private void sendMessage(String sender, String receiver, String mess, String type) {
        final DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("msg", mess);
        hashMap.put("type", type);
        hashMap.put("isSeen", "false");
        mData.child("Chats").push().setValue(hashMap);

        //add user to chat fragment
        mData.child("Chatlist").child(firebaseUser.getUid()).child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                    mData.child("Chatlist").child(firebaseUser.getUid()).child(userid).child("id").setValue(userid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mData.child("Chatlist").child(userid).child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                    mData.child("Chatlist").child(userid).child(firebaseUser.getUid()).child("id").setValue(firebaseUser.getUid());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMessage(final String myid, final String userid, final String imgurl) {
        chatList = new ArrayList<>();

        mData.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) || chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                        chatList.add(chat);
                    }


                    messageAdapter = new MessageAdapter(MessageActivity.this, chatList, imgurl);
                    recyclerView.setAdapter(messageAdapter);
                    recyclerView.smoothScrollToPosition(chatList.size());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//
    }

    private void status(String status) {
        mData.child("User").child(firebaseUser.getUid()).child("status").setValue(status);
//        HashMap<String,Object> hashMap = new HashMap<>();
//        hashMap.put("status",status);
//        mData.child("User").child(firebaseUser.getUid()).updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // FirebaseDatabase.getInstance().getReference("Chats").removeEventListener(seenListener);
        status("offline");
    }

    public String Random(int n) {

        // length is bounded by 256 Character
        byte[] array = new byte[256];
        new Random().nextBytes(array);

        String randomString
                = new String(array, Charset.forName("UTF-8"));

        // Create a StringBuffer to store the result
        StringBuffer r = new StringBuffer();

        // Append first 20 alphanumeric characters
        // from the generated random String into the result
        for (int k = 0; k < randomString.length(); k++) {

            char ch = randomString.charAt(k);

            if (((ch >= 'a' && ch <= 'z')
                    || (ch >= 'A' && ch <= 'Z')
                    || (ch >= '0' && ch <= '9'))
                    && (n > 0)) {

                r.append(ch);
                n--;
            }
        }

        // return the resultant string
        return r.toString();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == 10001)
            pickImageFromGallery(1001);
        else
            Toast.makeText(this, "Truy cập bị từ chối", Toast.LENGTH_LONG).show();
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == 10002)
            pickVideoFromGallery(1002);
        else
            Toast.makeText(this, "Truy cập bị từ chối", Toast.LENGTH_LONG).show();
    }

}
