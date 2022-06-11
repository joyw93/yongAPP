package com.example.hklist.Fragments;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hklist.AddScheduleActivity;
import com.example.hklist.Constant.Constant;
import com.example.hklist.CustomCalendarView;
import com.example.hklist.LoginActivity;
import com.example.hklist.Model.CalendarDTO;
import com.example.hklist.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;


public class CalendarFragment extends Fragment {

    private RecyclerView recyclerView;
    private CustomCalendarView cv;
    private HashSet<Date> events = new HashSet<>();
    private HashSet<Date> onClickedDay = new HashSet<>();
    private FirebaseDatabase database_calendar = FirebaseDatabase.getInstance();
    private DatabaseReference calendarReference = database_calendar.getReference("CalendarDB");
    private FirebaseDatabase database_event = FirebaseDatabase.getInstance();
    private DatabaseReference eventRef = database_event.getReference("eventDB");
    private FirebaseDatabase tmp = FirebaseDatabase.getInstance();
    private DatabaseReference tmpDate = tmp.getReference("tmpDate");
    private String str_date;
    private FloatingActionButton btnAdd;


    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        btnAdd = (FloatingActionButton) view.findViewById(R.id.btnAdd);
        cv = ((CustomCalendarView) view.findViewById(R.id.calendar_view));
        setCalendar();
        addSchedule();
        addEvent();

        return view;
    }

    class GridFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<CalendarDTO> calendarDTOs;

        public GridFragmentRecyclerViewAdapter(Date date) {

            calendarDTOs = new ArrayList<>();

            SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd");
            str_date = df.format(date);

            calendarReference.child(str_date).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    calendarDTOs.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        calendarDTOs.add(snapshot.getValue(CalendarDTO.class));
                    }

                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_item, parent, false);


            return new CalendarFragment.GridFragmentRecyclerViewAdapter.CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            final CalendarFragment.GridFragmentRecyclerViewAdapter.CustomViewHolder customViewHolder = ((CalendarFragment.GridFragmentRecyclerViewAdapter.CustomViewHolder) holder);


            customViewHolder.text_content.setText(calendarDTOs.get(position).getContent().toString());


            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

//                    calendarReference.child(str_date).child(calendarDTOs.get(position).getKey()).removeValue();
//                    eventRef.child(calendarDTOs.get(position).getKey()).removeValue();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("일정 삭제");
                    builder.setMessage("일정을 삭제하시겠습니까?");
                    builder.setNegativeButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //예 눌렀을때의 이벤트 처리
                            calendarReference.child(str_date).child(calendarDTOs.get(position).getKey()).removeValue();
                            eventRef.child(calendarDTOs.get(position).getKey()).removeValue();

                        }
                    });
                    builder.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //아니오 눌렀을때의 이벤트 처리
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return calendarDTOs.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {

            public TextView text_content;

            public CustomViewHolder(View view) {
                super(view);


                text_content = view.findViewById(R.id.text_content);
            }
        }
    }

    void setCalendar() {
        Locale.setDefault(Locale.KOREAN);


        // assign event handler
        cv.setEventHandler(new CustomCalendarView.EventHandler() {
            @Override
            public void onDayLongPress(Date date) {

                // show returned day
                DateFormat df = SimpleDateFormat.getDateInstance();
                Toast.makeText(getContext(), df.format(date), Toast.LENGTH_SHORT).show();
                events.add(date);
                cv.updateCalendar(events, null);
            }

            @Override
            public void onDayPress(Date date) {
                // show returned day
                SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd");
                String str_date = df.format(date);
                tmpDate.setValue(date);
                onClickedDay.clear();
                onClickedDay.add(date);
                cv.updateCalendar(events, onClickedDay);
                recyclerView.setAdapter(new GridFragmentRecyclerViewAdapter(date));
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                // DateFormat df = SimpleDateFormat.getDateInstance();
                //Toast.makeText(getContext(), df.format(date), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void setEvents() {
                cv.updateCalendar(events, null);
            }
        });

    }

    void addSchedule() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddScheduleActivity.class);
                startActivity(intent);

            }
        });
    }

    void addEvent() {
        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                events.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    events.add(snapshot.getValue(Date.class));
                }
                cv.updateCalendar(events, null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


}
