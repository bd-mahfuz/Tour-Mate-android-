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
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.mahfuz.tourmate.R;
import com.example.mahfuz.tourmate.db.TourMateDB;
import com.example.mahfuz.tourmate.model.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class UpdateEventFragment extends Fragment {

    private EditText eventET, departureET, startET, budgetET, startingEt, destinationEt;
    private Button updateEventBt;
    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private DatabaseReference eventRef;
    private FirebaseAuth auth;
    private FirebaseUser user;

    private Event event;

    private AwesomeValidation awesomeValidation;

    public UpdateEventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_update_event, container, false);
        eventET = v.findViewById(R.id.eventNameEt);
        departureET = v.findViewById(R.id.departureEt);
        budgetET = v.findViewById(R.id.budgetEt);
        startingEt = v.findViewById(R.id.startingEt);
        destinationEt = v.findViewById(R.id.destinationEt);

        updateEventBt = v.findViewById(R.id.updateEventButton);

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


        //getting event from bundle
        Bundle bundle = getArguments();
        event = (Event) bundle.getSerializable("event");

        //setting the event value into edit text
        eventET.setText(event.getEventName());
        departureET.setText(event.getDepartureDate());
        destinationEt.setText(event.getDestination());
        startingEt.setText(event.getStartingLocation());
        budgetET.setText(event.getEstimateBudget());


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        /*//initialize rootRef
        rootRef = FirebaseDatabase.getInstance().getReference();

        //Toast.makeText(this, user.getUid(), Toast.LENGTH_SHORT).show();

        if (user!=null) {
            userRef = rootRef.child("users").child(user.getUid());
            eventRef = userRef.child("Event");
            //eventRef.setValue("Hello");
        }*/

        // updating event
        updateEvent();

        return v;
    }


    public void updateEvent() {
        updateEventBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //validating
                awesomeValidation.addValidation(getActivity(), R.id.eventNameEt, "[a-zA-Z\\s]+", R.string.eventName_err);
                awesomeValidation.addValidation(getActivity(), R.id.departureDateEt, RegexTemplate.NOT_EMPTY, R.string.departureDate_err);
                awesomeValidation.addValidation(getActivity(), R.id.startingEt, RegexTemplate.NOT_EMPTY, R.string.startingLocation_err);
                awesomeValidation.addValidation(getActivity(), R.id.destinationEt, RegexTemplate.NOT_EMPTY, R.string.destination_err);
                awesomeValidation.addValidation(getActivity(), R.id.budgetEt, RegexTemplate.NOT_EMPTY, R.string.budget_err);


                if (awesomeValidation.validate()) {
                    event.setEventName(eventET.getText().toString());
                    event.setDepartureDate(departureET.getText().toString());
                    event.setStartingLocation(startingEt.getText().toString());
                    event.setDestination(destinationEt.getText().toString());
                    event.setEstimateBudget(budgetET.getText().toString());
                    //String keyId = event.getEventId();

                    TourMateDB tourMateDB = new TourMateDB(getActivity(), user);
                    tourMateDB.updateEvent(event);

                    Toast.makeText(getActivity(), "Updated Successfully!", Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                } else {
                    Toast.makeText(getContext(), "Validation Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
