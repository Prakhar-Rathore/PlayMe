package com.example.playme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<Audio> mFiles;

    public MusicAdapter(Context mContext, ArrayList<Audio> mFiles) {
        this.mContext = mContext;
        this.mFiles = mFiles;
    }

    @NonNull
    @Override
    public MusicAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicAdapter.MyViewHolder holder, final int position) {
        Typeface typeface = ResourcesCompat.getFont(mContext, R.font.open_sans_regular);
        holder.song_title.setText(mFiles.get(position).getTitle());
        holder.artist.setText(mFiles.get(position).getArtist());
        holder.song_title.setTypeface(typeface);
        holder.artist.setTypeface(typeface);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PlayerActivity.class);
                Log.e("position", String.valueOf(position));
                intent.putExtra("position", position);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView song_title, artist;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            artist = itemView.findViewById(R.id.artist);
            song_title = itemView.findViewById(R.id.song_title);
        }
    }
}
