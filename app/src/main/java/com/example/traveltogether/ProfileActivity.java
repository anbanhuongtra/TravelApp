package com.example.traveltogether;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileActivity extends AppCompatActivity {
    FirebaseUser user;
    EditText edtHoTen, edtEmail, edtMatKhau, edtXacNhan;
    Button btnEdit, btnSave, btnImg;
    ImageView img;
    SharedPreferences sharedPreferences;
    DatabaseReference mData;
    FirebaseStorage storage;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        findViewById(R.id.profileLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                return false;
            }
        });


        user = FirebaseAuth.getInstance().getCurrentUser();
        final String email = user.getEmail();

        String uid = user.getUid();
        mData = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        sharedPreferences = getSharedPreferences("savedData", this.MODE_PRIVATE);

        AnhXa();
        edtEmail.setText(email);
        ActionBar actionBar = getSupportActionBar();

        //Load profile image
        storageRef.child(user.getUid() + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Glide.with(getBaseContext()).load(uri).into(img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                img.setImageResource(R.drawable.null_avata);
            }
        });

        mData.child("User").child(uid).child("hoTen").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                edtHoTen.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtHoTen.setEnabled(true);
                edtMatKhau.setEnabled(true);
                edtXacNhan.setEnabled(true);
                btnSave.setEnabled(true);
                edtHoTen.setTextColor(Color.parseColor("#ffffff"));
                edtMatKhau.setHintTextColor(Color.parseColor("#b3ffffff"));
                edtXacNhan.setHintTextColor(Color.parseColor("#b3ffffff"));
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if ((edtMatKhau.getText().toString().equals(edtXacNhan.getText().toString()) && edtMatKhau.getText().toString().length() >= 6 && !edtHoTen.getText().toString().equals(""))
                        || (!edtHoTen.getText().toString().equals("") && edtMatKhau.getText().toString().equals("") && edtXacNhan.getText().toString().equals("") ))
                    showConfirmDialog("Bạn có muốn lưu những thay đổi ?");
                else if (edtHoTen.getText().toString().equals(""))
                    showAlertDialog("Vui lòng nhập họ tên");
                else if (!edtMatKhau.getText().toString().equals(edtXacNhan.getText().toString()))
                    showAlertDialog("Mật khẩu xác nhận không chính xác");
                else if (edtMatKhau.getText().toString().length() < 6)
                    showAlertDialog("Mật khẩu không hợp lệ (Mật khẩu cần có ít nhất 6 kí tự)");


            }
        });


        btnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, 1000);
                    } else {
                        pickImageFromGallery(1001);
                    }
                } else pickImageFromGallery(1001);


            }
        });

    }

    private void pickImageFromGallery(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1001) {
            img.setImageURI(data.getData());

            StorageReference profileRef = storageRef.child(user.getUid() + ".png");

            // Upload picture to storage Firebase
            profileRef.putFile(data.getData())
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            storageRef.child(user.getUid() + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Got the download URL for 'users/me/profile.png'

                                    //update infor fbuser
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setPhotoUri(uri)
                                            .build();

                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                    }
                                                }
                                            });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                        }
                    });

            //update infor fbuser


        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == 1000)
            pickImageFromGallery(1001);
        else
            Toast.makeText(this, "Truy cập bị từ chối", Toast.LENGTH_LONG).show();
    }

    private void AnhXa() {
        edtHoTen = findViewById(R.id.edtHoTen);
        edtEmail = findViewById(R.id.edtEmail);
        edtMatKhau = findViewById(R.id.edtMatKhau);
        edtXacNhan = findViewById(R.id.edtXacNhan);
        btnEdit = findViewById(R.id.btnEdit);
        btnSave = findViewById(R.id.btnSave);
        btnImg = findViewById(R.id.btnImg);
        img = findViewById(R.id.profile_image);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void showAlertDialog(final String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("HFILM");
        builder.setMessage(s);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (s == "Cập nhật thành công") {
                    Intent intent = new Intent(ProfileActivity.this, ListActivity.class);
                    startActivity(intent);
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
        positiveButtonLL.gravity = Gravity.CENTER;
        positiveButton.setLayoutParams(positiveButtonLL);


    }

    public void showConfirmDialog(String s) {
        Log.d("app","ok");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("HFILM");
        builder.setMessage(s);
        builder.setCancelable(false);
        builder.setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                btnSave.setBackgroundResource(R.drawable.custom_button1_change);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnSave.setBackgroundResource(R.drawable.custom_button);
                    }
                }, 1000);
                if(!isNetworkConnected()) {
                    showAlertDialog("Lỗi kết nối mạng ");
                    return;
                }
                //Cập nhật mật khẩu
                if (edtMatKhau.getText().toString().equals(edtXacNhan.getText().toString()) && !edtMatKhau.getText().toString().isEmpty())
                    user.updatePassword(edtMatKhau.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        donothing();// Toast.makeText(getBaseContext(), "Cập nhật mật khẩu thành công", Toast.LENGTH_LONG).show();
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.clear();
                                        editor.putString("savedEmail", edtEmail.getText().toString());
                                        editor.putString("savedPass", edtMatKhau.getText().toString());
                                        editor.commit();
                                    } else {
                                        // Toast.makeText(getBaseContext(), "Mật khẩu không hợp lệ", Toast.LENGTH_LONG).show();
                                        showAlertDialog("Mật khẩu không hợp lệ");
                                        return;
                                    }
                                }
                            });
                else if (!(edtMatKhau.getText().toString().isEmpty() && edtXacNhan.getText().toString().isEmpty())) {
                    // Toast.makeText(getBaseContext(), "Mật khẩu xác nhận không khớp", Toast.LENGTH_LONG).show();
                    showAlertDialog("Mật khẩu xác nhận không chính xác");
                    return;
                }
                //Cập nhật tên
                if (!edtHoTen.getText().toString().isEmpty()) {
                    mData.child("User").child(user.getUid()).child("hoTen").setValue(edtHoTen.getText().toString().trim());
                    //update infor fbuser
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(edtHoTen.getText().toString().trim())
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("app","ok r");

                                    }
                                }
                            });


                } else {
                    //   Toast.makeText(getBaseContext(), "Vui lòng nhập họ tên", Toast.LENGTH_LONG).show();
                    showAlertDialog("Vui lòng nhập họ tên");
                    return;
                }
                showAlertDialog("Cập nhật thành công");

            }
        });



        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
        positiveButtonLL.gravity = Gravity.CENTER;
        positiveButton.setLayoutParams(positiveButtonLL);


    }

    private void donothing() {
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
    private void status(String status)
    {
        mData.child("User").child(user.getUid()).child("status").setValue(status);
//        HashMap<String,Object> hashMap = new HashMap<>();
//        hashMap.put("status",status);
//        mData.child("User").child(user.getUid()).updateChildren(hashMap);
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
