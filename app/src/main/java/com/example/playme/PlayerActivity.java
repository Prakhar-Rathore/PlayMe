package com.example.playme;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

import static com.example.playme.MainActivity.audioList;
import static com.example.playme.MainActivity.isrepeated;
import static com.example.playme.MainActivity.isshuffled;

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    TextView music_name, artist_name, current_time, total_time;
    ImageView back_arrow, menu_btn, play_previous, play_pause, play_next, music_img,
            shuffle, repeat;
    SeekBar seekbar;
    static ArrayList<Audio> list = new ArrayList<>();
    int position = -1;
    static Uri uri;
    static MediaPlayer mp;
    private Handler handler = new Handler();
    private Thread play, prev, next;
//    Animation animout;
//    Animation animin;
    Animation rotation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        initViews();
        rotation = AnimationUtils.loadAnimation(this, R.anim.rotation);

        getIntentMethod();
        mp.setOnCompletionListener(this);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mp != null && b) {
                    mp.seekTo(i*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mp.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.start();
            }
        });
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mp != null) {
                    int current_position = mp.getCurrentPosition() / 1000;
                    seekbar.setProgress(current_position);
                    current_time.setText(formattedTime(current_position));
                }
                handler.postDelayed(this, 1000);
            }
        });
        music_name.setText(list.get(position).getTitle());
        artist_name.setText(list.get(position).getArtist());

        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isshuffled) {
                    isshuffled = false;
                    shuffle.setAlpha(0.5f);
                }
                else {
                    isshuffled = true;
                    shuffle.setAlpha(1f);
                }
            }
        });

        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isrepeated) {
                    isrepeated = false;
                    repeat.setAlpha(0.5f);
                }
                else {
                    isrepeated = true;
                    repeat.setAlpha(1f);
                }
            }
        });
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "It doesn't work for now", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initViews() {
        music_name = findViewById(R.id.music_name);
        artist_name = findViewById(R.id.artist_name);
        current_time = findViewById(R.id.current_time);
        total_time = findViewById(R.id.total_time);
        back_arrow = findViewById(R.id.back_arrow);
        menu_btn = findViewById(R.id.menu_btn);
        play_previous = findViewById(R.id.play_previous);
        play_pause = findViewById(R.id.play_pause);
        play_next = findViewById(R.id.play_next);
        seekbar = findViewById(R.id.seekbar);
        music_img = findViewById(R.id.music_img);
        shuffle = findViewById(R.id.shuffle);
        repeat = findViewById(R.id.repeat);
    }

    private String formattedTime(int current_position) {
        String seconds = String.valueOf(current_position % 60);
        String minutes = String.valueOf(current_position / 60);
        String totalOut = minutes + ":" + seconds;
        String totalNew = minutes + ":" + "0" + seconds;
        if (seconds.length() == 1) {
            return totalNew;
        }
        else {
            return totalOut;
        }
    }

    private void getIntentMethod() {
        position = getIntent().getIntExtra("position", 0);
        Log.e("pa_position", String.valueOf(position));
        list = audioList;
        if (list != null) {
            uri = Uri.parse(list.get(position).getData());
        }
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = MediaPlayer.create(getApplicationContext(), uri);
            mp.start();
            music_img.startAnimation(rotation);
        }
        else {
            mp = MediaPlayer.create(getApplicationContext(), uri);
            mp.start();
            music_img.startAnimation(rotation);
        }
        seekbar.setMax(mp.getDuration() / 1000);
        metaData(uri);
    }

    private void metaData(Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int duration = Integer.parseInt(list.get(position).getDuration());
        total_time.setText(formattedTime(duration / 1000));
    }

    @Override
    protected void onResume() {
        playbtn();
        nextbtn();
        prevbtn();
        super.onResume();
    }

    private void prevbtn() {
        prev = new Thread() {
            @Override
            public void run() {
                super.run();

                play_previous.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        prevClicked();
                    }
                });

            }
        };
        prev.start();
    }

    private void prevClicked() {

        if (mp.isPlaying()) {
            mp.stop();
            mp.release();
            if (isshuffled && !isrepeated) {
                position = getRandom(list.size() - 1);
            }
            else if (!isshuffled && !isrepeated) {
                position = (position - 1) < 0 ? (list.size() - 1) : position - 1;
            }
            uri = Uri.parse(list.get(position).getData());
            mp = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            music_name.setText(list.get(position).getTitle());
            artist_name.setText(list.get(position).getArtist());
            seekbar.setMax(mp.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mp != null) {
                        int current_position = mp.getCurrentPosition() / 1000;
                        seekbar.setProgress(current_position);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mp.setOnCompletionListener(this);
            play_pause.setImageResource(R.drawable.ic_baseline_pause);
            mp.start();
        }
        else {
            if (isshuffled && !isrepeated) {
                position = getRandom(list.size() - 1);
            }
            else if (!isshuffled && !isrepeated) {
                position = (position - 1) < 0 ? (list.size() - 1) : position - 1;
            }
            uri = Uri.parse(list.get(position).getData());
            mp = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            music_name.setText(list.get(position).getTitle());
            artist_name.setText(list.get(position).getArtist());
            seekbar.setMax(mp.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mp != null) {
                        int current_position = mp.getCurrentPosition() / 1000;
                        seekbar.setProgress(current_position);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mp.setOnCompletionListener(this);
            play_pause.setImageResource(R.drawable.ic_baseline_play_arrow);
            mp.start();
        }
    }

    private void nextbtn() {
        next = new Thread() {
            @Override
            public void run() {
                super.run();

                play_next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nextClicked();
                    }
                });

            }
        };
        next.start();
    }

    private void nextClicked() {

        if (mp.isPlaying()) {
            mp.stop();
            mp.release();
            if (isshuffled && !isrepeated) {
                position = getRandom(list.size() - 1);
            }
            else if (!isshuffled && !isrepeated) {
                position = (position + 1) % list.size();
            }
            uri = Uri.parse(list.get(position).getData());
            mp = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            music_name.setText(list.get(position).getTitle());
            artist_name.setText(list.get(position).getArtist());
            seekbar.setMax(mp.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mp != null) {
                        int current_position = mp.getCurrentPosition() / 1000;
                        seekbar.setProgress(current_position);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            play_pause.setImageResource(R.drawable.ic_baseline_pause);
            mp.setOnCompletionListener(this);
            mp.start();
        }
        else {
            if (isshuffled && !isrepeated) {
                position = getRandom(list.size() - 1);
            }
            else if (!isshuffled && !isrepeated) {
                position = (position + 1) % list.size();
            }
            uri = Uri.parse(list.get(position).getData());
            mp = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            music_name.setText(list.get(position).getTitle());
            artist_name.setText(list.get(position).getArtist());
            seekbar.setMax(mp.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mp != null) {
                        int current_position = mp.getCurrentPosition() / 1000;
                        seekbar.setProgress(current_position);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            play_pause.setImageResource(R.drawable.ic_baseline_pause);
            mp.setOnCompletionListener(this);
            mp.start();
        }
    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }


    private void playbtn() {
        play = new Thread() {
            @Override
            public void run() {
                super.run();

                play_pause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playPauseClicked();
                    }
                });

            }
        };
        play.start();
    }

    private void playPauseClicked() {
        if (mp.isPlaying()) {
            play_pause.setImageResource(R.drawable.ic_baseline_play_arrow);
            mp.pause();
            music_img.clearAnimation();
            seekbar.setMax(mp.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mp != null) {
                        int current_position = mp.getCurrentPosition() / 1000;
                        seekbar.setProgress(current_position);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        }
        else {
            play_pause.setImageResource(R.drawable.ic_baseline_pause);
            mp.start();
            music_img.startAnimation(rotation);
            seekbar.setMax(mp.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mp != null) {
                        int current_position = mp.getCurrentPosition() / 1000;
                        seekbar.setProgress(current_position);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        nextClicked();
//        if (mp != null) {
//            mp = MediaPlayer.create(getApplicationContext(), uri);
//            mp.start();
//            mp.setOnCompletionListener(this);
//        }
    }
}