package com.example.traveltogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends Activity {

    ImageButton btnThoat;
    Button btnDangKy;
    TextView txtHoTen, txtEmail, txtMatKhau, txtXacNhan, txtDieuKhoan;
    EditText edtHoTen, edtEmail, edtMatKhau, edtXacNhan;
    Spanned Text;
    FirebaseAuth mAuth;
    DatabaseReference mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sign_up);

        findViewById(R.id.signupLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                return false;
            }
        });


        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();

        AnhXa();


        btnThoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Signup();

            }
        });


        edtHoTen.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!edtHoTen.getText().toString().isEmpty()) {
                    txtHoTen.setText("");
                } else txtHoTen.setText("Bạn chưa điền Họ Tên ");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!edtEmail.getText().toString().isEmpty()) {
                    txtEmail.setText("");
                } else txtEmail.setText("Bạn chưa điền Email");

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        edtMatKhau.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!edtMatKhau.getText().toString().isEmpty()) {
                    txtMatKhau.setText("");
                } else txtMatKhau.setText("Bạn chưa tạo mật khẩu");

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtXacNhan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!edtXacNhan.getText().toString().isEmpty()) {
                    txtXacNhan.setText("");
                } else txtXacNhan.setText("Bạn chưa xác nhận mật khẩu");

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    void AnhXa() {
        btnThoat = findViewById(R.id.btnClose);
        btnDangKy = findViewById(R.id.btnDangKy);
        txtHoTen = findViewById(R.id.txtHoTen);
        txtEmail = findViewById(R.id.txtEmail);
        txtMatKhau = findViewById(R.id.txtMatKhau);
        txtXacNhan = findViewById(R.id.txtXacNhan);
        edtHoTen = findViewById(R.id.edtHoTen);
        edtEmail = findViewById(R.id.edtEmail);
        edtMatKhau = findViewById(R.id.edtMatKhau);
        edtXacNhan = findViewById(R.id.edtXacNhan);
    }

    private void Signup() {
        if(!isNetworkConnected()) {
            showAlertDialog("Lỗi kết nối mạng ");
            return;
        }
        String email = edtEmail.getText().toString();
        String pass = edtMatKhau.getText().toString();
        if (edtHoTen.getText().toString().isEmpty()) showAlertDialog("Vui lòng nhập họ tên");
        else if (edtMatKhau.getText().toString().equals(edtXacNhan.getText().toString()) && !edtMatKhau.getText().toString().isEmpty() && !edtEmail.getText().toString().isEmpty()) {
            mAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                showAlertDialog("Đăng ký thành công");
                                FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
                                User user = new User(fbuser.getEmail(), edtHoTen.getText().toString().trim(),fbuser.getUid(),"");
                                mData.child("User").child(fbuser.getUid()).setValue(user);
                                //update infor fbuser
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(edtHoTen.getText().toString().trim())
                                        .build();

                                fbuser.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                }



















                                            }
                                        });
                            } else {
                                // If sign in fails, display a message to the user.
                                String noti = task.getException().toString();
                                String notiArray[] = noti.split(":");
                                if (notiArray[0].indexOf("WeakPassword") != -1)
                                    showAlertDialog("Mật khẩu không hợp lệ ( Mật khẩu cần có ít nhất 6 kí tự )");
                                if (notiArray[0].indexOf("UserCollision") != -1)
                                    showAlertDialog("Tài khoản đã tồn tại");
                                if (notiArray[0].indexOf("InvalidCredentials") != -1)
                                    showAlertDialog("Email không hợp lệ");

                            }
                        }
                    });
        } else {
            if (edtEmail.getText().toString().isEmpty())
                showAlertDialog("Vui lòng nhập Email");
            else if (edtMatKhau.getText().toString().isEmpty())
                showAlertDialog("Vui lòng nhập mật khẩu");
            else if (!edtMatKhau.getText().toString().equals(edtXacNhan.getText().toString()))
                showAlertDialog("Mật khẩu xác nhận không đúng");

        }
    }

    public void showAlertDialog(final String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("HFILM");
        builder.setMessage(s);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (s == "Đăng ký thành công") {
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
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
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
