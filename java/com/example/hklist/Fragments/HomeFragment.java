package com.example.hklist.Fragments;

import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import com.example.hklist.AwakeDragonActivity;
import com.example.hklist.Constant.Constant;
import com.example.hklist.LoginActivity;
import com.example.hklist.Model.CalendarDTO;
import com.example.hklist.R;
import com.example.hklist.SetDragonStateActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ImageView imageYong;
    private ImageView imageJisu;
    private ImageView imageDragon;
    private ImageView btnDragon;
    private TextView d_dayPlus;
    private TextView d_dayMinus;
    private Calendar mCalendar;
    private FirebaseDatabase database=FirebaseDatabase.getInstance();
    private DatabaseReference dragonReference=database.getReference("dragonState");
    private FirebaseDatabase database_calendar = FirebaseDatabase.getInstance();
    private DatabaseReference calendarReference = database_calendar.getReference("CalendarDB");
    private FirebaseDatabase database_event = FirebaseDatabase.getInstance();
    private DatabaseReference eventRef = database_event.getReference("eventDB");
    FirebaseDatabase databaseMeet=FirebaseDatabase.getInstance();;
    DatabaseReference meetSoon=databaseMeet.getReference("meetSoon");
    private ArrayList<Date> mArrayList;
    private ArrayList<Long> longArrayList;
    private ArrayList<String> keyArrayList;
    private long str;
    private long min=0;
    int count;
    final long today_m = Calendar.getInstance().getTimeInMillis() / Constant.ONE_DAY;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Locale.setDefault(Locale.KOREAN);
        imageYong=(ImageView)view.findViewById(R.id.imageYong);
        imageJisu=(ImageView)view.findViewById(R.id.imageJisu);
        imageDragon=(ImageView)view.findViewById(R.id.imageDragon);
        d_dayPlus = (TextView)view.findViewById(R.id.d_day_plus);
        d_dayMinus = (TextView)view.findViewById(R.id.d_day_minus);
        btnDragon=(ImageView)view.findViewById(R.id.btnDragon);
        mAuth = FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        mArrayList=new ArrayList<Date>();
        longArrayList=new ArrayList<Long>();
        keyArrayList=new ArrayList<String>();

        setDday(2020,2,15);
        setRoundImage(imageYong,imageJisu);
        setDragonState();
        changeDragonStae();

        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mArrayList.clear();
                longArrayList.clear();
                keyArrayList.clear();
                count=0;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    // child 내에 있는 데이터만큼 반복합니다.
                    mArrayList.add(data.getValue(Date.class));
                    keyArrayList.add(data.getKey());
                }
                count=mArrayList.size();
                for(int i=0;i<count;i++)
                {
                    longArrayList.add(mArrayList.get(i).getTime()/Constant.ONE_DAY);
                }

                if(longArrayList.isEmpty()){
                    min=0;
                }
                else {
                    min = Collections.min(longArrayList);
                }
                meetSoon.setValue(min);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        meetSoon.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                str=dataSnapshot.getValue(long.class);

                long result=str-today_m;
                if(str==0)
                {
                    String strCount_m = "?일";
                    d_dayMinus.setText(strCount_m);
                }
                else {
                    String strCount_m = (String.format("%d일", result));
                    d_dayMinus.setText(strCount_m);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }

    void setRoundImage(ImageView imageMan, ImageView imageWoman){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imageMan.setBackground(new ShapeDrawable(new OvalShape()));
            imageWoman.setBackground(new ShapeDrawable(new OvalShape()));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageWoman.setClipToOutline(true);
            imageMan.setClipToOutline(true);
        }
    }
    void setDday(int Year, int Month, int Day) {
        mCalendar = new GregorianCalendar(Year,Month,Day);
        final int year = mCalendar.get(Calendar.YEAR);
        final int month = mCalendar.get(Calendar.MONTH)-1;
        final int day = mCalendar.get(Calendar.DAY_OF_MONTH);


        final Calendar ddayCalendar = Calendar.getInstance();
        ddayCalendar.set(year, month, day);

        // D-day 를 구하기 위해 millisecond 으로 환산하여 d-day 에서 today 의 차를 구한다.
        final long dday = ddayCalendar.getTimeInMillis() / Constant.ONE_DAY;
        final long today = Calendar.getInstance().getTimeInMillis() / Constant.ONE_DAY;
        long result =today-dday;

        // 출력 시 d-day 에 맞게 표시
        final String strFormat ="%d일째";
        final String strCount = (String.format(strFormat, result));

        d_dayPlus.setText(strCount);
    }
    void setDragonState(){
        btnDragon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intentDragon=new Intent(getContext(), SetDragonStateActivity.class);
                Intent intentDidu=new Intent(getContext(), AwakeDragonActivity.class);
                if(currentUser.getUid().equals(Constant.YONG_KEY)) {
                    startActivity(intentDragon);
                }
                else if(currentUser.getUid().equals(Constant.DIDU_KEY)){
                    startActivity(intentDidu);
                }

            }
        });

    }
    void changeDragonStae(){
        dragonReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String state = dataSnapshot.getValue(String.class);
                switch(state)
                {
                    case "move":
                        imageDragon.setImageResource(R.drawable.move_dragon);
                        break;

                    case "sleep":
                        imageDragon.setImageResource(R.drawable.sleep_dragon);
                        break;

                    case "drink":
                        imageDragon.setImageResource(R.drawable.drink_dragon);
                        break;
                    case "teach":
                        imageDragon.setImageResource(R.drawable.teach_dragon);
                        break;

                    case "coding":
                        imageDragon.setImageResource(R.drawable.coding_dragon);
                        break;
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });
    }

}