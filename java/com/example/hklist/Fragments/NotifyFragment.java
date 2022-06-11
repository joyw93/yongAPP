package com.example.hklist.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import com.example.hklist.LoginActivity;
import com.example.hklist.MainActivity;
import com.example.hklist.R;
import com.google.firebase.auth.FirebaseAuth;

public class NotifyFragment extends Fragment {
    public NotifyFragment() {
        // Required empty public constructor
    }
     private Button btnLogout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notify, container, false);
           btnLogout=(Button)view.findViewById(R.id.button);
           btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

}
