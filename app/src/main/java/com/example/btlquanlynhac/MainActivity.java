package com.example.btlquanlynhac;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;

public class MainActivity extends Activity {
    private MusicDatabase db;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        db = new MusicDatabase(this);
        findViewById(R.id.goGenres).setOnClickListener(v ->
                startActivity(new Intent(this, GenreActivity.class)));
        findViewById(R.id.goSongs).setOnClickListener(v ->
                startActivity(new Intent(this, SongActivity.class)));
        findViewById(R.id.goReports).setOnClickListener(v ->
                startActivity(new Intent(this, ReportActivity.class)));
    }

    @Override protected void onResume() {
        super.onResume();
        ((TextView) findViewById(R.id.tvGenreCount)).setText(String.valueOf(db.countGenres()));
        ((TextView) findViewById(R.id.tvSongCount)).setText(String.valueOf(db.countSongs()));
        LinearLayout container = findViewById(R.id.recentContainer);
        container.removeAllViews();
        List<Song> recent = db.getSongs("", -1, false, 3);
        if (recent.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("Chưa có file nhạc. Hãy bắt đầu từ chức năng Quản lý file nhạc.");
            empty.setTextColor(getColor(R.color.subtle));
            empty.setTextSize(14);
            empty.setPadding(14, 18, 14, 18);
            empty.setBackgroundResource(R.drawable.bg_card);
            container.addView(empty);
            return;
        }
        for (Song song : recent) {
            TextView row = new TextView(this);
            row.setText("♫   " + song.name + "\n       " + song.genreName + " · " + song.fileType);
            row.setTextColor(getColor(R.color.ink));
            row.setTextSize(14);
            row.setLineSpacing(6, 1);
            row.setPadding(20, 16, 20, 16);
            row.setBackgroundResource(R.drawable.bg_card);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
            params.bottomMargin = (int) (8 * getResources().getDisplayMetrics().density);
            container.addView(row, params);
            row.setOnClickListener(v -> startActivity(new Intent(this, SongActivity.class)));
        }
    }

    @Override protected void onDestroy() {
        if (db != null) db.close();
        super.onDestroy();
    }
}
