package com.example.playme;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
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
        holder.more_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        switch (menuItem.getItemId()) {
                            case R.id.delete:
                                deleteFile(position);
                                break;
                        }
                        return true;
                    }
                });
            }
        });
    }

    private void deleteFile(int position) {
        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                Long.parseLong(mFiles.get(position).getId()));

        File file = new File(mFiles.get(position).getData());
        boolean isDeleted = file.delete();
        if (isDeleted) {
            mContext.getContentResolver().delete(uri, null, null);
            mFiles.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mFiles.size());
            Toast.makeText(mContext, "File deleted successfully", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(mContext, "Unable to delete file", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView song_title, artist;
        ImageView more_btn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            artist = itemView.findViewById(R.id.artist);
            song_title = itemView.findViewById(R.id.song_title);
            more_btn = itemView.findViewById(R.id.more_btn);
        }
    }
}
