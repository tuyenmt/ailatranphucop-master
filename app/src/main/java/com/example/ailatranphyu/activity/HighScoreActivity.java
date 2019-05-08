package com.example.ailatranphyu.activity;

import android.app.ListActivity;
import android.os.Bundle;

import com.example.ailatranphyu.App;
import com.example.ailatranphyu.R;
import com.example.ailatranphyu.adapter.HighScoreAdapter;
import com.example.ailatranphyu.manager.DatabaseManager;


public class HighScoreActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        DatabaseManager databaseManager = new DatabaseManager(App.getContext());
        getListView().setAdapter(new HighScoreAdapter(databaseManager.getHighScore()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        App.getMusicPlayer().pauseBgMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        App.getMusicPlayer().resumeBgMusic();

    }
}
//update 12/4/2019
