package com.example.mahfuz.tourmate.fragment.Event;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mahfuz.tourmate.R;
import com.example.mahfuz.tourmate.adapter.EventAdapter;
import com.example.mahfuz.tourmate.db.TourMateDB;
import com.example.mahfuz.tourmate.model.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventListFragment extends Fragment {

    private TextView messageTv;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private List<Event> events = new ArrayList<>();

    private EventAdapter eventAdapter;

    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private DatabaseReference eventRef;

    private FirebaseAuth auth;
    private FirebaseUser user;


    public EventListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        messageTv = ((TextView)view.findViewById(R.id.emptyListMsg));

        progressBar = view.findViewById(R.id.eventListPb);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        LinearLayoutManager l = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(l);
        recyclerView.setHasFixedSize(true);

        //getting event list from database
        getEventList();





        // button action for creating a new event
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                CreateEventFragment fragment = new CreateEventFragment();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });

        return view;
    }


    public void getEventList() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        //initialize rootRef
        rootRef = FirebaseDatabase.getInstance().getReference();

        Toast.makeText(getActivity(), user.getUid(), Toast.LENGTH_SHORT).show();

        if (user!=null) {
            userRef = rootRef.child("User").child(user.getUid());
            eventRef = userRef.child("Event");
            //eventRef.setValue("Hello");
        }


        progressBar.setVisibility(View.VISIBLE);

        // getting event list from firebase
        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // clearing the event list for repeating data issue
                events.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Event event = d.getValue(Event.class);
                    events.add(event);
                }

                //displaying proper message for empty event
                if (events.size() == 0) {
                    messageTv.setVisibility(View.VISIBLE);
                }

                //setting the list to adapter
                eventAdapter = new EventAdapter(events);
                recyclerView.setAdapter(eventAdapter);

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle("Event List");
    }

}
