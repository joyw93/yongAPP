package com.example.hklist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.example.hklist.Fragments.ChatFragment;
import com.example.hklist.Fragments.HomeFragment;
import com.example.hklist.Fragments.CalendarFragment;
import com.example.hklist.Fragments.NotifyFragment;
import com.example.hklist.Fragments.PictureFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private HomeFragment fragmentHome = new HomeFragment();
    private CalendarFragment fragmentCalendar = new CalendarFragment();
    private ChatFragment fragmentChat = new ChatFragment();
    private PictureFragment fragmentPicture = new PictureFragment();
    private NotifyFragment fragmentNotify = new NotifyFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragmentHome).commitAllowingStateLoss();
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());
    }

    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch(menuItem.getItemId())
            {
                case R.id.action_home:
                    transaction.replace(R.id.frameLayout, fragmentHome).commitAllowingStateLoss();
                    break;
                case R.id.action_calendar:
                    transaction.replace(R.id.frameLayout, fragmentCalendar).commitAllowingStateLoss();
                    break;

                    case R.id.action_chat:
                    transaction.replace(R.id.frameLayout, fragmentChat).commitAllowingStateLoss();
                    break;
                case R.id.action_pictures:
                    transaction.replace(R.id.frameLayout, fragmentPicture).commitAllowingStateLoss();
                    break;

                case R.id.action_notify:
                    transaction.replace(R.id.frameLayout, fragmentNotify).commitAllowingStateLoss();
                    break;
            }
            return true;
        }
    }
}
