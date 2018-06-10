package com.example.mahfuz.tourmate.fragment.Moment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mahfuz.tourmate.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MomentAddFragment extends Fragment {

    public String photoPath = "Android/data/com.example.mahfuz.tourmate/files/Pictures";


    public MomentAddFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_moment_add, container, false);
    }

}
