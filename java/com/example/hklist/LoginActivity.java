package com.example.hklist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private TextView signIn;
    private Button btnLogin;
    TextInputLayout ti_1;
    TextInputLayout ti_2;
    AppCompatEditText et_1;
    AppCompatEditText et_2;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String userEmail;
    private String userPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signIn=(TextView)findViewById(R.id.signIn);
        btnLogin=(Button)findViewById(R.id.btnLogin);

        ti_1 = (TextInputLayout) findViewById(R.id.editEmail);
        ti_2 = (TextInputLayout) findViewById(R.id.editPwd);

        et_1 = (AppCompatEditText) findViewById(R.id.teditEmail);
        et_2 = (AppCompatEditText) findViewById(R.id.teditPwd);

        mAuth = FirebaseAuth.getInstance();

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,SignInActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEmail=et_1.getText().toString();
                userPwd=et_2.getText().toString();
                if(userEmail==null || userPwd==null)
                {
                    Toast.makeText(LoginActivity.this, "아이디와 비밀번호를 입력 하세요.", Toast.LENGTH_SHORT).show();
                }
                else{
                    loginStart(userEmail,userPwd);
                }

            }
        });
    }

    public void loginStart(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (!task.isSuccessful()) {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        Toast.makeText(LoginActivity.this, "존재하지 않는 ID 입니다.", Toast.LENGTH_SHORT).show();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        Toast.makeText(LoginActivity.this, "비밀번호가 일치하지 않습니다..", Toast.LENGTH_SHORT).show();
                    } catch (FirebaseNetworkException e) {
                        Toast.makeText(LoginActivity.this, "Firebase NetworkException", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "Exception", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    currentUser = mAuth.getCurrentUser();
                    //Toast.makeText(LoginActivity.this, "로그인 성공" + "/" + currentUser.getEmail() + "/" + currentUser.getUid(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

}
