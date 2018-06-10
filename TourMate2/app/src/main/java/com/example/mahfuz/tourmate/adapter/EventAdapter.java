package com.example.mahfuz.tourmate.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mahfuz.tourmate.R;
import com.example.mahfuz.tourmate.db.TourMateDB;
import com.example.mahfuz.tourmate.fragment.Event.CreateEventFragment;
import com.example.mahfuz.tourmate.fragment.Event.EventDetailFragment;
import com.example.mahfuz.tourmate.fragment.Event.UpdateEventFragment;
import com.example.mahfuz.tourmate.model.Event;
import com.example.mahfuz.tourmate.utility.TourUtility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder>{

    private List<Event> events;

    private ActionMode mActionMode;

    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private DatabaseReference eventRef;

    private FirebaseAuth auth;
    private FirebaseUser user;


    public EventAdapter(List<Event> events) {
        this.events = events;
    }

    @Override
    public EventAdapter.EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_event_item,
                parent, false);

        EventViewHolder eventViewHolder = new EventViewHolder(view);

        return eventViewHolder;
    }

    @Override
    public void onBindViewHolder(EventAdapter.EventViewHolder holder, int position) {

        String departureDate = events.get(position).getDepartureDate();
        Date deDate = null;
        try {
            deDate = new SimpleDateFormat("dd/MM/yyyy").parse(departureDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.eventNameTv.setText(events.get(position).getEventName());
        holder.createdDateTv.setText("Created On: "+events.get(position).getCreationDate());
        holder.departureDateTv.setText("Starts On: "+departureDate);

        long currentDate = (new Date().getTime())/1000;
        long startDate = deDate.getTime();
        startDate = startDate/1000;
        int dayLeft = (int) ((startDate - currentDate)/86400);

        holder.dayLeftTv.setText(dayLeft+"");

        if (dayLeft>0){
            holder.dayLeftTv.setText(dayLeft+" Day Left");
        } else if (dayLeft<0){
            dayLeft = Math.abs(dayLeft);
            holder.dayLeftTv.setText(dayLeft+" Day Past");
        }else {
            holder.dayLeftTv.setText("Today");
        }

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {

        TextView eventNameTv, createdDateTv, departureDateTv, dayLeftTv;
        private Context context;

        public EventViewHolder(View itemView) {
            super(itemView);

            eventNameTv = itemView.findViewById(R.id.showEventNameTv);
            createdDateTv = itemView.findViewById(R.id.createdDateTv);
            departureDateTv = itemView.findViewById(R.id.departureDateTv);
            dayLeftTv = itemView.findViewById(R.id.dayLeftTv);

            this.context = itemView.getContext();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(view.getContext(), "Clicked on "+getAdapterPosition(), Toast.LENGTH_LONG).show();
                    Event event = events.get(getAdapterPosition());

                    AppCompatActivity activity = (AppCompatActivity) context;

                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    EventDetailFragment fragment = new EventDetailFragment();

                    //sending event to UpdateEventFragment using bundle
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("event", event);
                    fragment.setArguments(bundle);

                    fragmentTransaction.replace(R.id.fragment_container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (mActionMode != null) {
                        return false;
                    }
                    mActionMode = view.startActionMode(mActionModeCallback);
                    mActionMode.setTag(getAdapterPosition());
                    view.setSelected(true);
                    return true;
                }
            });

        }

        private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {




            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                actionMode.getMenuInflater().inflate(R.menu.event_context_menu, menu);
                actionMode.setTitle("Choose Your Option");
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                int position = Integer.parseInt(actionMode.getTag().toString());
                switch (menuItem.getItemId()) {
                    case R.id.editMenu :

                        Event event = events.get(getAdapterPosition());

                        AppCompatActivity activity = (AppCompatActivity) context;

                        FragmentManager fragmentManager = activity.getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        UpdateEventFragment fragment = new UpdateEventFragment();

                        //sending event to UpdateEventFragment using bundle
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("event", event);
                        fragment.setArguments(bundle);

                        fragmentTransaction.replace(R.id.fragment_container, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        actionMode.finish();
                        return true;
                    case R.id.deleteMenu :
                        auth = FirebaseAuth.getInstance();
                        user = auth.getCurrentUser();

                        TourMateDB tourMateDB = new TourMateDB(context, user);
                        tourMateDB.deleteEvent(events.get(getAdapterPosition()).getEventId());
                        actionMode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                mActionMode = null;
            }

            public void deleteUser() {

                auth = FirebaseAuth.getInstance();
                user = auth.getCurrentUser();

                //initialize rootRef
                rootRef = FirebaseDatabase.getInstance().getReference();

                //Toast.makeText(this, user.getUid(), Toast.LENGTH_SHORT).show();

                if (user!=null) {
                    userRef = rootRef.child("User").child(user.getUid());
                    eventRef = userRef.child("Event");
                    //eventRef.setValue("Hello");
                    String keyId = events.get(getAdapterPosition()).getEventId();
                    eventRef.child(keyId).removeValue();
                    Toast.makeText(context, "key :"+keyId, Toast.LENGTH_SHORT).show();
                }
            }


        };
    }
}


