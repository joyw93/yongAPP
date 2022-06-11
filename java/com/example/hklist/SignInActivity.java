package com.example.hklist;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hklist.Model.UserDTO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class SignInActivity extends AppCompatActivity {

    private EditText editEmail;
    private EditText editPwd;
    private EditText confirmPwd;
    private EditText editName;
    private EditText editBirth;
    private Button btnMan;
    private Button btnWoman;
    private Button btnConfirm;
    private String userEmail;
    private String userPwd;
    private String userName;
    private String userBirth;
    private String str_confirmPwd;
    private String sex;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference userDb;
    UserDTO userDTO=new UserDTO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        editEmail = (EditText) findViewById(R.id.editEmail);
        editPwd = (EditText) findViewById(R.id.editPwd);
        confirmPwd = (EditText) findViewById(R.id.confirmPwd);
        editName = (EditText) findViewById(R.id.editName);
        editBirth = (EditText) findViewById(R.id.editBirth);
        btnMan = (Button) findViewById(R.id.btnMan);
        btnWoman = (Button) findViewById(R.id.btnWoman);
        btnConfirm = (Button) findViewById(R.id.btnConfirm);
        mAuth = FirebaseAuth.getInstance();


        database=FirebaseDatabase.getInstance();
        userDb=database.getReference("UserDB");

        btnMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnMan.setBackgroundResource(R.drawable.custombutton);
                btnMan.setTextColor(getResources().getColorStateList(R.color.colorPrimary));
                btnWoman.setBackgroundResource(R.drawable.uncheckedbutton);
                btnWoman.setTextColor(getResources().getColorStateList(R.color.colorLightGray));
                sex="man";
            }
        });

        btnWoman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnWoman.setBackgroundResource(R.drawable.custombutton);
                btnWoman.setTextColor(getResources().getColorStateList(R.color.colorPrimary));
                btnMan.setBackgroundResource(R.drawable.uncheckedbutton);
                btnMan.setTextColor(getResources().getColorStateList(R.color.colorLightGray));
                sex="woman";
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userEmail=editEmail.getText().toString();
                userPwd=editPwd.getText().toString();
                str_confirmPwd=confirmPwd.getText().toString();
                userName=editName.getText().toString();
                userBirth=editBirth.getText().toString();
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    return;
                                }
                                userDTO.setUserEmail(userEmail);
                                userDTO.setUserPwd(userPwd);
                                userDTO.setUserBirth(userBirth);
                                userDTO.setUserSex(sex);
                                userDTO.setFcmToken(task.getResult().getToken());
                            }
                        });

                joinStart(userEmail,userName,userPwd);
            }
        });



    }
    public void joinStart(String email, final String name, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                Toast.makeText(SignInActivity.this, "비밀번호가 너무 단순합니다.", Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                Toast.makeText(SignInActivity.this, "E-mail 형식에 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthUserCollisionException e) {
                                Toast.makeText(SignInActivity.this, "이미존재하는 E-mail 입니다.", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(SignInActivity.this, "다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            currentUser = mAuth.getCurrentUser();
                            userDTO.setUserKey(currentUser.getUid());
                            userDb.child(currentUser.getUid()).setValue(userDTO);
                            Toast.makeText(SignInActivity.this, userName+"님, 회원가입이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignInActivity.this, LoginActivity.class));
                            finish();
                        }
                    }
                });
    }



}