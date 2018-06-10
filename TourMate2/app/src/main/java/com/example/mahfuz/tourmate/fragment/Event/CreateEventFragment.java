package com.example.mahfuz.tourmate.fragment.Event;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.mahfuz.tourmate.R;
import com.example.mahfuz.tourmate.SignUpActivity;
import com.example.mahfuz.tourmate.db.TourMateDB;
import com.example.mahfuz.tourmate.model.Event;
import com.example.mahfuz.tourmate.utility.TourUtility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreateEventFragment extends Fragment {


    private EditText eventET, departureET, startET, buggedET, startingEt, destinationEt;
    private Button createEventBT;
    private ProgressBar progressBar;
    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private DatabaseReference eventRef;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private List<Event> events = new ArrayList<>();

    private AwesomeValidation awesomeValidation;

    public CreateEventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_event, container, false);
        eventET = v.findViewById(R.id.eventNameEt);
        departureET = v.findViewById(R.id.departureEt);
        buggedET = v.findViewById(R.id.budgetEt);
        startingEt = v.findViewById(R.id.startingEt);
        destinationEt = v.findViewById(R.id.destinationEt);

        createEventBT = v.findViewById(R.id.createEventBt);

        progressBar = v.findViewById(R.id.eventCreatePb);

        departureET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                departureET.setText(day+"/"+(month+1)+"/"+year);
                            }
                        },2018, 5, 04);
                datePickerDialog.show();
            }
        });


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        /*//initialize rootRef
        rootRef = FirebaseDatabase.getInstance().getReference();

        Toast.makeText(getActivity(), user.getUid()+"", Toast.LENGTH_SHORT).show();

        if (user!=null) {
            eventRef = rootRef.child("users").child(user.getUid()).child("Event");
            //eventRef.setValue("Hello");
        }*/

        createEventBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = eventET.getText().toString();
                String deparuture = departureET.getText().toString();
                String startingLocation = startingEt.getText().toString();
                String destination = destinationEt.getText().toString();
                String budget = buggedET.getText().toString();

                //validating
                awesomeValidation.addValidation(getActivity(), R.id.eventNameEt, "[a-zA-Z\\s]+", R.string.eventName_err);
                awesomeValidation.addValidation(getActivity(), R.id.departureDateEt, RegexTemplate.NOT_EMPTY, R.string.departureDate_err);
                awesomeValidation.addValidation(getActivity(), R.id.startingEt, RegexTemplate.NOT_EMPTY, R.string.startingLocation_err);
                awesomeValidation.addValidation(getActivity(), R.id.destinationEt, RegexTemplate.NOT_EMPTY, R.string.destination_err);
                awesomeValidation.addValidation(getActivity(), R.id.budgetEt, RegexTemplate.NOT_EMPTY, R.string.budget_err);


                if (awesomeValidation.validate()) {

                    progressBar.setVisibility(View.VISIBLE);

                    TourMateDB tourMateDB = new TourMateDB(getActivity(), user);

                    Event s = new Event();
                    s.setCreationDate(TourUtility.getDate(new Date()));
                    s.setEstimateBudget(budget);
                    s.setDepartureDate(deparuture);
                    s.setDestination(destination);
                    s.setStartingLocation(startingLocation);
                    s.setEventName(name);

                    tourMateDB.addEvent(s);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Event created successfully!", Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                } else {
                    Toast.makeText(getContext(), "Validation Failed!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        return v;
    }


}
