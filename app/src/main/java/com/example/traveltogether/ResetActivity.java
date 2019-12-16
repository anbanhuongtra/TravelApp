package com.example.traveltogether;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResetActivity extends Activity {

    Button btnOk, btnGuiLink, btnBack;
    TextView txtKiemTra;
    EditText edtEmail;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_reset);


        findViewById(R.id.resetLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                return false;
            }
        });


        btnOk = findViewById(R.id.btnOk);
        btnGuiLink = findViewById(R.id.btnGuiLink);
        txtKiemTra = findViewById(R.id.txtThongBao2);
        edtEmail = findViewById(R.id.edtEmail);




        auth = FirebaseAuth.getInstance();


        btnOk.setVisibility(View.INVISIBLE);
        txtKiemTra.setVisibility(View.INVISIBLE);



        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnOk.setBackgroundResource(R.drawable.custom_button3_change);
                btnGuiLink.setBackgroundResource(R.drawable.custom_button1);
                btnOk.setVisibility(View.INVISIBLE);
                txtKiemTra.setVisibility(View.INVISIBLE);
            }
        });

        btnGuiLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isNetworkConnected()) {
                    showAlertDialog("Lỗi kết nối mạng ");
                    return;
                }
                if (isEmailValid(edtEmail.getText().toString())) {
                    auth.sendPasswordResetEmail(edtEmail.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        showAlertDialog("Đã gửi thành công");

                                    }
                                }
                            });
                    btnGuiLink.setBackgroundResource(R.drawable.custom_button1_change);
                    btnOk.setVisibility(View.VISIBLE);
                    txtKiemTra.setVisibility(View.VISIBLE);
                } else
                    showAlertDialog("Email không hợp lệ");
            }
        });
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void showAlertDialog(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("HFILM");
        builder.setMessage(s);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
