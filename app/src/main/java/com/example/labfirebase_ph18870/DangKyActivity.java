package com.example.labfirebase_ph18870;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DangKyActivity extends AppCompatActivity {

    EditText edtPassword, edtRePassword, edtEmail;
    public TextInputLayout tilPassword, tilRePassword, tilEmail;
    Button btnDk;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ky);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Đăng Ký");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.teal_200)));

        edtPassword = findViewById(R.id.dk_edt_pass);
        edtRePassword = findViewById(R.id.dk_edt_repass);
        edtEmail = findViewById(R.id.dk_edt_email);

        tilPassword = findViewById(R.id.dk_til_pass);
        tilRePassword = findViewById(R.id.dk_til_repass);
        tilEmail = findViewById(R.id.dk_til_email);

        btnDk = findViewById(R.id.dk_btn_dk);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        btnDk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int temp = 0;
                if (edtEmail.getText().toString().isEmpty()){
                    tilEmail.setError("Email không được để trống");
                    temp++;
                }else {
                    if(!isEmailValid(edtEmail.getText().toString())){
                        tilEmail.setError("Email không đúng");
                        temp++;
                    }else {
                        tilEmail.setError("");
                    }
                }
                if (edtPassword.getText().toString().length() <= 6){
                    tilPassword.setError("Mật khẩu phải lớn 6 ký tự");
                    temp++;
                }else {
                    tilPassword.setError("");
                }
                if (edtRePassword.getText().toString().length() <= 6){
                    tilRePassword.setError("Nhập lại mật khẩu phải lớn 6 ký tự");
                    temp++;
                }else {
                    tilRePassword.setError("");
                }
                if (temp == 0){
                    if (!edtRePassword.getText().toString().equals(edtPassword.getText().toString())){
                        tilPassword.setError("Mật Khẩu không trùng khớp");
                        tilRePassword.setError("Mật Khẩu không trùng khớp");
                    }else {
                        tilPassword.setError("");
                        tilRePassword.setError("");
                        DangKy(edtEmail.getText().toString(),edtPassword.getText().toString());
                    }
                }

            }
        });

    }

    void DangKy(String email,String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            /*FirebaseUser user = mAuth.getCurrentUser();*/
                            Toast.makeText(DangKyActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(DangKyActivity.this,LoginActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(DangKyActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}