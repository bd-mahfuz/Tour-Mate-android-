package com.example.mahfuz.tourmate.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mahfuz.tourmate.R;
import com.example.mahfuz.tourmate.fragment.Moment.FullImageViewFragment;
import com.example.mahfuz.tourmate.fragment.Moment.ImageGalleryFragment;
import com.example.mahfuz.tourmate.model.Moment;

import java.util.List;

public class PhotoGalleryAdapter extends RecyclerView.Adapter<PhotoGalleryAdapter.MomentViewHolder>  {

    private List<Moment> momentList;

    public PhotoGalleryAdapter(List<Moment> momentList) {
        this.momentList = momentList;
    }


    @NonNull
    @Override
    public PhotoGalleryAdapter.MomentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_gallery_photo, parent, false);

        MomentViewHolder viewHolder = new MomentViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoGalleryAdapter.MomentViewHolder holder, int position) {
        Moment moment = momentList.get(position);
        Log.d("photoPath:", moment.getPhotoPath());
        Bitmap bitmap = BitmapFactory.decodeFile(moment.getPhotoPath());
        //holder.imageView.setRotationX(90);
        holder.imageView.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return momentList.size();
    }

    public class MomentViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public MomentViewHolder(final View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.galleryIv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AppCompatActivity activity = (AppCompatActivity) itemView.getContext();

                    android.support.v4.app.FragmentManager fragmentManager = activity.getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putString("photoPath", momentList.get(getAdapterPosition()).getPhotoPath());
                    FullImageViewFragment fullImageViewFragment = new FullImageViewFragment();
                    fullImageViewFragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragment_container, fullImageViewFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    Toast.makeText(itemView.getContext(), momentList.get(getAdapterPosition()).getDate()+"", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
