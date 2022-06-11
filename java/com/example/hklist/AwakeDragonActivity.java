package com.example.hklist;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class AwakeDragonActivity extends AppCompatActivity {

    private Button btnCall;
    private Button btnAwake;
    private Button btnMessage;

    private FirebaseDatabase database_user=FirebaseDatabase.getInstance();
    private DatabaseReference userReference=database_user.getReference("UserDB");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_awake_dragon);

        btnCall = (Button)findViewById(R.id.btnCall);
        btnMessage = (Button)findViewById(R.id.btnMessage);
        btnAwake = (Button)findViewById(R.id.btnAwake);
        btnClicked();
    }

    void btnClicked(){ btnCall.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sendNoti("알림이 도착 했습니다!","전화걸기.");
            finish();
        }
    });
        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNoti("알림이 도착 했습니다!","메세지 보내기.");
                finish();
            }
        });

        btnAwake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNoti("알림이 도착 했습니다!","용 깨우기.");
                finish();
            }
        });
    };
    void sendNoti(final String title,final String message) {
        userReference
                .child(Constant.YONG_KEY)
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
