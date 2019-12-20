package com.example.traveltogether;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewReviewActivity extends AppCompatActivity {

    Button btnPost;
    ImageView imgPlace;
    ImageButton btnAdd;
    TextView txtName, txtTime;
    EditText edtTitle, edtContent, edtAddress, edtName;
    DatabaseReference mData;
    StorageReference storageRef;
    FirebaseUser fbUser;
    String currentTime;
    CircleImageView imgUser;
    Uri uriImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_review);

        AnhXa();
        mData = FirebaseDatabase.getInstance().getReference();
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference();
        final Calendar calendar = Calendar.getInstance();
        currentTime = calendar.get(Calendar.DATE) + "/" + (calendar.get(Calendar.MONTH)+1) + "/" + calendar.get(Calendar.YEAR);




        //get and set infor User
        txtName.setText(fbUser.getDisplayName());
        if (fbUser.getPhotoUrl() != null)
            Glide.with(getBaseContext()).load(fbUser.getPhotoUrl()).into(imgUser);

        txtTime.setText("Date: " + currentTime);


        btnAdd.setOnClickListener(new View.OnClickListener() {
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


        //action Post
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uriImg == null) showAlertDialog("You haven't chosen picture");
                else if (edtName.getText().toString().equals(""))
                    showAlertDialog("The name mustn't empty");
                else if (edtTitle.getText().toString().equals(""))
                    showAlertDialog("The title mustn't empty");
                else if (edtContent.getText().toString().equals(""))
                    showAlertDialog("The content mustn't empty");
                else if (edtAddress.getText().toString().equals(""))
                    showAlertDialog("The address mustn't empty");
                else showConfirmDialog("Do you post a review ?");

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
            imgPlace.setImageURI(data.getData());
            uriImg = data.getData();
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


    public void showAlertDialog(final String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Travel Together");
        builder.setMessage(s);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (s == "You haved posted a new review") {
                    Intent intent = new Intent(NewReviewActivity.this, ListActivity.class);
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
        final String key = generateString();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Travel Together");
        builder.setMessage(s);
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                StorageReference profileRef = storageRef.child(key + ".png");
                // Upload picture to storage Firebase
                profileRef.putFile(uriImg)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content

                                storageRef.child(key + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // Got the download URL for database
                                        mData.child("Reviews").child(key + "").setValue(new Review(key + "", edtName.getText().toString(), edtContent.getText().toString(),
                                                edtTitle.getText().toString(), edtAddress.getText().toString(), currentTime
                                                , fbUser.getUid(), uri.toString(),0,0
                                        ));

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
                showAlertDialog("You haved posted a new review");


            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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


    private void AnhXa() {
        btnPost = findViewById(R.id.btnPost);
        imgPlace = findViewById(R.id.imgPlace);
        txtName = findViewById(R.id.txtTitle);
        txtTime = findViewById(R.id.txtTime);
        edtTitle = findViewById(R.id.edtTitle);
        edtContent = findViewById(R.id.edtContent);
        edtAddress = findViewById(R.id.edtAddress);
        btnAdd = findViewById(R.id.btnAdd);
        imgUser = findViewById(R.id.imgUser);
        edtName = findViewById(R.id.edtName);

    }

    public  String generateString() {
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }
}
