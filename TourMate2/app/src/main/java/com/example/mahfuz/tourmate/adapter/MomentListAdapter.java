package com.example.mahfuz.tourmate.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mahfuz.tourmate.R;
import com.example.mahfuz.tourmate.apiPojo.ForecastWeather;
import com.example.mahfuz.tourmate.db.TourMateDB;
import com.example.mahfuz.tourmate.fragment.Event.UpdateEventFragment;
import com.example.mahfuz.tourmate.model.Event;
import com.example.mahfuz.tourmate.model.Moment;
import com.example.mahfuz.tourmate.utility.TourUtility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MomentListAdapter extends RecyclerView.Adapter<MomentListAdapter.MomentViewHolder> {

    private List<Moment> momentList;
    private ActionMode mActionMode;

    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private DatabaseReference eventRef;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private String eventId;

    public MomentListAdapter(List<Moment> momentList, String eventId) {
        this.momentList = momentList;
        this.eventId = eventId;
    }


    @NonNull
    @Override
    public MomentListAdapter.MomentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_moment_list, parent, false);

        MomentListAdapter.MomentViewHolder viewHolder = new MomentListAdapter.MomentViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MomentListAdapter.MomentViewHolder holder, int position) {

        Moment moment = momentList.get(position);
        Bitmap bitmap = BitmapFactory.decodeFile(moment.getPhotoPath());
        holder.imageView.setImageBitmap(bitmap);

        holder.titleTv.setText("Capture "+position);
        holder.dateTv.setText(TourUtility.milliToDate(moment.getDate())+"");
        holder.fileNameTv.setText(moment.getFileName()+""+moment.getFormatName());
    }

    @Override
    public int getItemCount() {
        return momentList.size();
    }

    public class MomentViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView fileNameTv, titleTv, dateTv;

        public MomentViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.momentIv);
            fileNameTv = itemView.findViewById(R.id.fileNameTv);
            titleTv = itemView.findViewById(R.id.momentTitleTv);
            dateTv = itemView.findViewById(R.id.dateTv);

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
                actionMode.getMenuInflater().inflate(R.menu.menu_moment_delete, menu);
                actionMode.setTitle("Delete Moment");
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
                    case R.id.momentDeleteMenu :
                        auth = FirebaseAuth.getInstance();
                        user = auth.getCurrentUser();

                        try{
                            TourMateDB tourMateDB = new TourMateDB(itemView.getContext(), user);
                            tourMateDB.deleteMoment(momentList.get(getAdapterPosition()).getId(), eventId);
                            Toast.makeText(itemView.getContext(), "Moment Deleted Successfully!", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(itemView.getContext(),
                                    "Something went wrong! Please contact with developer", Toast.LENGTH_SHORT).show();
                        }
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


        };
    }
}
