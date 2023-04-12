package com.example.labfirebase_ph18870;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class LoginActivity extends AppCompatActivity {

    EditText edtUserName, edtPass;
    public TextInputLayout tilUserName, tilPass;

    Button btnLogin;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.teal_200)));


        edtUserName = findViewById(R.id.login_edt_username);
        edtPass = findViewById(R.id.login_edt_pass);

        tilUserName = findViewById(R.id.login_til_username);
        tilPass = findViewById(R.id.login_til_pass);

        btnLogin = findViewById(R.id.login_btn_dn);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.login_tv_dk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,DangKyActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int temp = 0;
                if (edtUserName.getText().toString().isEmpty()){
                    tilUserName.setError("Email không được để trống");
                    temp++;
                }else {
                    if(!isEmailValid(edtUserName.getText().toString())){
                        tilUserName.setError("Email không đúng");
                        temp++;
                    }else {
                        tilUserName.setError("");
                    }
                }
                if (edtPass.getText().toString().isEmpty()){
                    tilPass.setError("Mật khẩu không được để trống");
                    temp++;
                }else {
                    tilPass.setError("");
                }

                if (temp == 0){
                    Login(edtUserName.getText().toString(),edtPass.getText().toString());
                }
            }
        });

    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    void Login(String email,String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            /*FirebaseUser user = mAuth.getCurrentUser();*/
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        } else {
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
        }
    }
}