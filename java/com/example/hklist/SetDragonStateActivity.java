package com.example.hklist;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.hklist.Constant.Constant;
import com.example.hklist.Model.UserDTO;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SetDragonStateActivity extends AppCompatActivity {

    private Button btnMove;
    private Button btnSleep;
    private Button btnTeach;
    private Button btnDrink;
    private Button btnCoding;
    private FirebaseDatabase database_dragon=FirebaseDatabase.getInstance();
    private DatabaseReference dragonReference=database_dragon.getReference("dragonState");
    private FirebaseDatabase database_user=FirebaseDatabase.getInstance();
    private DatabaseReference userReference=database_user.getReference("UserDB");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_dragon_state);
        btnMove = (Button)findViewById(R.id.btnCall);
        btnTeach = (Button)findViewById(R.id.btnMessage);
        btnSleep = (Button)findViewById(R.id.btnAwake);
        btnDrink = (Button)findViewById(R.id.btnNotify);
        btnCoding = (Button)findViewById(R.id.btnCoding);
        btnClicked();
    }

void setDragonState(String state){
        dragonReference.setValue(state);
        Toast.makeText(SetDragonStateActivity.this, "상태가 변경 되었습니다.", Toast.LENGTH_SHORT).show();
        finish();
}
void btnClicked(){
    btnMove.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sendNoti("알림이 도착 했습니다!","용이 이동을 시작했습니다.");
            setDragonState("move");
        }
    });

    btnSleep.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sendNoti("알림이 도착 했습니다!","용이 잠들었습니다.");
            setDragonState("sleep");
        }
    });

    btnTeach.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sendNoti("알림이 도착 했습니다!","용이 과외를 시작했습니다.");
            setDragonState("teach");
        }
    });

    btnDrink.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sendNoti("알림이 도착 했습니다!","용이 음주를 시작했습니다.");
            setDragonState("drink");
        }
    });

    btnCoding.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sendNoti("알림이 도착 했습니다!","용이 코딩을 시작했습니다.");
            setDragonState("coding");
        }
    });
}
void sendNoti(final String title,final String message) {
        userReference
                .child(Constant.DIDU_KEY)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final UserDTO userData = dataSnapshot.getValue(UserDTO.class);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // FMC 메시지 생성 start
                                    JSONObject root = new JSONObject();
                                    JSONObject notification = new JSONObject();
                                    notification.put("body", message);
                                    notification.put("title", title);
                                    root.put("data", notification);
                                    root.put("to", userData.getFcmToken());

                                    // FMC 메시지 생성 end

                                    URL Url = new URL(Constant.FCM_MESSAGE_URL);
                                    HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
                                    conn.setRequestMethod("POST");
                                    conn.setDoOutput(true);
                                    conn.setDoInput(true);
                                    conn.addRequestProperty("Authorization", "key=" + Constant.SERVER_KEY);
                                    conn.setRequestProperty("Accept", "application/json");
                                    conn.setRequestProperty("Content-type", "application/json");
                                    OutputStream os = conn.getOutputStream();
                                    os.write(root.toString().getBytes("utf-8"));
                                    os.flush();
                                    conn.getResponseCode();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }



}
