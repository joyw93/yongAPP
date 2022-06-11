package com.example.hklist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.hklist.Constant.Constant;
import com.example.hklist.Fragments.CalendarFragment;
import com.example.hklist.Model.CalendarDTO;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class AddScheduleActivity extends AppCompatActivity  {

    private EditText editContent;
    private Button btnConfirm;
    private String str_date;
    private FirebaseDatabase database_calendar=FirebaseDatabase.getInstance();
    private DatabaseReference calendarReference=database_calendar.getReference("CalendarDB");
    private FirebaseDatabase database_event=FirebaseDatabase.getInstance();
    private DatabaseReference eventRef=database_calendar.getReference("eventDB");
    private FirebaseDatabase tmp=FirebaseDatabase.getInstance();
    private DatabaseReference tmpDate=tmp.getReference("tmpDate");


    private Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_schedule);

        editContent=(EditText)findViewById(R.id.editContent);
        btnConfirm=(Button)findViewById(R.id.btnConfirm);

        tmpDate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                date=dataSnapshot.getValue(Date.class);
                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd");
                        String str_date = df.format(date);
                        long dDay=date.getTime()/ Constant.ONE_DAY;
                        String content=editContent.getText().toString();
                        DatabaseReference cal=calendarReference.child(str_date).push();
                        CalendarDTO calendarDTO=new CalendarDTO();
                        calendarDTO.setContent(content);
                        calendarDTO.setKey(cal.getKey());
                        calendarDTO.setStr_date(str_date);
                        calendarDTO.setdDay(dDay);
                        cal.setValue(calendarDTO);
                        eventRef.child(cal.getKey()).setValue(date);
                        finish();
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }


        });


    }




}
