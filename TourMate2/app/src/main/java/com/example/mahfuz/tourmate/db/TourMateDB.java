package com.example.mahfuz.tourmate.db;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.mahfuz.tourmate.model.Event;
import com.example.mahfuz.tourmate.model.Expense;
import com.example.mahfuz.tourmate.model.Moment;
import com.example.mahfuz.tourmate.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TourMateDB {

    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private DatabaseReference eventRef;
    private DatabaseReference expenseRef;
    private DatabaseReference momentRef;

    private FirebaseAuth auth;
    private FirebaseUser user;

    private String eventId;
    private String userId;

    private Context context;

    public TourMateDB(Context context, FirebaseUser user) {
        this.context = context;
        this.user = user;
        // root reference of db
        rootRef = FirebaseDatabase.getInstance().getReference();


        userId = user.getUid();

        if (user != null) {
            userRef = rootRef.child("User").child(user.getUid());
            eventRef = userRef.child("Event");
        }
    }

    public void addEvent(Event event) {
        String key = eventRef.push().getKey();
        event.setEventId(key);
        eventRef.child(key).setValue(event);
    }
    public void deleteEvent(String eventId) {
        eventRef.child(eventId).removeValue();
    }

    public void deleteMoment(String momentId, String eventId) {
        momentRef = eventRef.child(eventId).child("Moment");
        momentRef.child(momentId).removeValue();
    }

    public void addUser(User user) {
        user.setId(userId);
        userRef.setValue(user);
    }

    public void addExpense(Expense expense, String eventId) {
        expenseRef = eventRef.child(eventId).child("Expense");
        String key = expenseRef.push().getKey();
        expense.setExpenseId(key);
        expenseRef.child(key).setValue(expense);
    }

    public void addMoment(Moment moment, String eventId) {
        momentRef = eventRef.child(eventId).child("Moment");
        String key = momentRef.push().getKey();
        moment.setId(key);
        momentRef.child(key).setValue(moment);
    }



    public void updateEvent(Event event) {

        String eventId = event.getEventId();
        Log.d("event Id:",eventId);
        eventRef.child(eventId).child("eventName").setValue(event.getEventName());
        eventRef.child(eventId).child("startingLocation").setValue(event.getStartingLocation());
        eventRef.child(eventId).child("destination").setValue(event.getDestination());
        eventRef.child(eventId).child("departureDate").setValue(event.getDepartureDate());
        eventRef.child(eventId).child("estimateBudget").setValue(event.getEstimateBudget());
    }



    public String getEventId() {
        return eventRef.push().getKey();
    }


    public List<Event> getAllEvent(){
        final List<Event> events = new ArrayList<>();
        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                events.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Event event = data.getValue(Event.class);
                    events.add(event);
                    Log.d("Event size: " , events.size()+"");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return events;
    }




}
