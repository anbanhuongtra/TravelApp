package com.example.traveltogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends Activity {

    Button btnLogin, btnSignUp, btnReset;
    EditText edtEmail, edtPass;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        AnhXa();

        //Ẩn bàn phím
        findViewById(R.id.loginLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                return false;
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNetworkConnected()) {
                    showAlertDialog("Lỗi kết nối mạng ");
                    return;
                }
                final String email = edtEmail.getText().toString();
                final String pass = edtPass.getText().toString();
                if (email.isEmpty() || pass.isEmpty())
                    showAlertDialog("Vui lòng nhập email và mật khẩu");
                else
                    mAuth.signInWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(LoginActivity.this, ListActivity.class);
                                        startActivity(intent);

                                    } else {
                                        showAlertDialog("Sai email hoặc mật khẩu");
                                    }
                                }
                            });


            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        edtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                edtEmail.setHint("");
                if (!b) edtEmail.setHint("Email");
            }
        });
        edtPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                edtPass.setHint("");
                if (!b) edtPass.setHint("Password");
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ResetActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        
    }

    private void AnhXa() {
        btnLogin = findViewById(R.id.btnLogin);
        btnReset= findViewById(R.id.btnReset);
        btnSignUp = findViewById(R.id.btnSignup);
        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPassword);
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
    public void showAlertDialog(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Travel Together");
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
}
