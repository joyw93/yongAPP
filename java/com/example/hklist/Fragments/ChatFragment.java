package com.example.hklist.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.hklist.LoginActivity;
import com.example.hklist.R;

public class ChatFragment extends Fragment {

    public ChatFragment() {
        // Required empty public constructor
    }

    ImageView i1;
    ImageView i2;
    ImageView i3;
    ImageView i4;
    ImageView i5;
    ImageView i6;
    EditText et;
    ImageView imageArray[]=new ImageView[6];
    final int imageList[]={R.id.imageView1,R.id.imageView2,R.id.imageView3,R.id.imageView4,R.id.imageView5,R.id.imageView6};
    final int draw[]={R.drawable.test1,R.drawable.test2,R.drawable.test3};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_chat, container, false);

        i1=(ImageView)view.findViewById(R.id.imageView1);
        i2=(ImageView)view.findViewById(R.id.imageView2);
        i3=(ImageView)view.findViewById(R.id.imageView3);
        i4=(ImageView)view.findViewById(R.id.imageView4);
        i5=(ImageView)view.findViewById(R.id.imageView5);
        i6=(ImageView)view.findViewById(R.id.imageView6);
        et=(EditText)view.findViewById(R.id.editText2);
        Button btn=(Button)view.findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int count=Integer.parseInt(et.getText().toString());

                for(int i=0;i<6;i++)
                {
                    final int index;
                    index=i;
                    imageArray[i]=(ImageView)view.findViewById(imageList[i]);
                    imageArray[index].setImageResource(R.drawable.calendar_add);
                }

                for(int i=0;i<count;i++)
                {
                    final int index;
                    index=i;
                    imageArray[i]=(ImageView)view.findViewById(imageList[i]);
                    //imageArray[index].setImageResource(draw[0]);
                    if(index<2)
                    {
                        imageArray[index].setImageResource(draw[0]);
                    }

                    else if(index>=2&&index<4)
                    {
                        imageArray[index].setImageResource(draw[1]);
                    }

                    else if(index>=4)
                    {
                        imageArray[index].setImageResource(draw[2]);
                    }
                }
            }
        });
        return view;
    }
}
