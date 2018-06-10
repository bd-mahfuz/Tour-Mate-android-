package com.example.mahfuz.tourmate.Nearby;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.mahfuz.tourmate.MainActivity;
import com.example.mahfuz.tourmate.PlaceDetailsFragment;
import com.example.mahfuz.tourmate.R;
import com.example.mahfuz.tourmate.fragment.Event.EventDetailFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NearbyPlaceAdapter extends RecyclerView.Adapter<NearbyPlaceAdapter.PlaceViewHolder> {

    private List<Result> placeList;
    private Context context;
    private MainActivity activity;

    public NearbyPlaceAdapter(List<Result> placeList, Context context) {
        this.placeList = placeList;
        this.context = context;
        activity = (MainActivity) context;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_list_item, parent, false);

        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        Result currentPlace = placeList.get(position);

        String key = context.getString(R.string.place_api_key);

        if (currentPlace.getPhotos() != null && currentPlace.getPhotos().size() > 0) {
            String url = String.format(
                    "https://maps.googleapis.com/maps/api/place/photo?maxwidth=200&photoreference=%s&key=%s",
                    currentPlace.getPhotos().get(0).getPhotoReference(),
                    key);
            Picasso.get().load(url).into(holder.placeImageView);
        } else {
            holder.placeImageView.setImageResource(R.drawable.ic_menu_camera);
        }

        //holder.placeImageView.setImageResource(R.mipmap.ic_launcher);
        holder.placeNameTV.setText(currentPlace.getName());
//        holder.placeRatingBar.setRating(currentPlace.getRating().floatValue());
        holder.placeAddressTV.setText(currentPlace.getVicinity());
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {

        private ImageView placeImageView;
        private TextView placeNameTV;
        private RatingBar placeRatingBar;
        private TextView placeAddressTV;

        public PlaceViewHolder(View itemView) {
            super(itemView);


            placeImageView = itemView.findViewById(R.id.placeImageView);
            placeNameTV = itemView.findViewById(R.id.placeNameTextView);
            placeRatingBar = itemView.findViewById(R.id.placeRatingBar);
            placeAddressTV = itemView.findViewById(R.id.placeAddressTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int itemPosition = getLayoutPosition();
                    Result clickedResult = placeList.get(itemPosition);

                    PlaceDetailsFragment fragment = new PlaceDetailsFragment();
                    FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("result", clickedResult);
                    fragment.setArguments(bundle);
                    ft.replace(R.id.placeLayout, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });
        }
    }
}