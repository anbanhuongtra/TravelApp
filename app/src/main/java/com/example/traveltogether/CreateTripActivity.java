package com.example.traveltogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class CreateTripActivity extends AppCompatActivity {

    EditText edtStart, edtEnd, edtDes, edtOrigin, edtAmount, edtTrans, edtTitle, edtContent;
    Button btnPost, btnStart, btnEnd;
    DatabaseReference mData;
    FirebaseUser fbUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);

        AnhXa();
        mData = FirebaseDatabase.getInstance().getReference();
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetStartDate();
            }
        });
        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetEndDate();
            }
        });
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtStart.getText().toString().equals("")) showAlertDialog("You haven't set departure date");
                else if (edtEnd.getText().toString().equals(""))
                    showAlertDialog("You haven't set return date");
                else if (edtOrigin.getText().toString().equals(""))
                    showAlertDialog("The origin mustn't empty");
                else if (edtDes.getText().toString().equals(""))
                    showAlertDialog("The destination mustn't empty");
                else if (edtTrans.getText().toString().equals(""))
                    showAlertDialog("The transportation mustn't empty");
                else if (edtAmount.getText().toString().equals(""))
                    showAlertDialog("The amount mustn't empty");
                else showConfirmDialog("Do you create a trip ?");

            }
        });

    }

    private void SetStartDate() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(i, i1, i2);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                edtStart.setText(simpleDateFormat.format(calendar.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)
        );
        datePicker.show();
    }

    private void SetEndDate() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(i, i1, i2);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                edtEnd.setText(simpleDateFormat.format(calendar.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)
        );
        datePicker.show();
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
        btnPost = findViewById(R.id.btnPost);
        btnStart = findViewById(R.id.btnStart);
        btnEnd = findViewById(R.id.btnEnd);
    }
    public void showAlertDialog(final String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Travel Together");
        builder.setMessage(s);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (s == "You haved created a new trip") {
                    Intent intent = new Intent(CreateTripActivity.this, TripActivity.class);
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
                String key = generateString();
                mData.child("Trips").child(key).setValue(new Trip(key,edtTitle.getText().toString(),edtOrigin.getText().toString(),edtDes.getText().toString(),Integer.parseInt(edtAmount.getText().toString()),edtTrans.getText().toString(),edtContent.getText().toString(),
                        edtStart.getText().toString(),edtEnd.getText().toString(),fbUser.getUid()));

                showAlertDialog("You haved created a new trip");


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
    public  String generateString() {
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }
}
