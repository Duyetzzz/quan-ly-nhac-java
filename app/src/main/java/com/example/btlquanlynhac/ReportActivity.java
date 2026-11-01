package com.example.btlquanlynhac;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends Activity {
    private MusicDatabase db;
    private LinearLayout summaries, results, genreSongs;
    private Spinner filter, genreReportFilter;
    private EditText search;
    private TextView count40, genreReportCount;
    private List<Genre> filterItems = new ArrayList<>();
    private long selectedGenreId = -1;
    private long genreReportId = -1;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        db = new MusicDatabase(this);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        summaries = findViewById(R.id.summaryContainer);
        results = findViewById(R.id.links40Container);
        genreSongs = findViewById(R.id.reportGenreSongs);
        genreReportFilter = findViewById(R.id.filterGenreReport);
        genreReportCount = findViewById(R.id.reportGenreCount);
        filter = findViewById(R.id.filterGenre40);
        search = findViewById(R.id.search40);
        count40 = findViewById(R.id.reportCount40);
        showSummary();
        setupFilter();
        genreReportFilter.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, filterItems));
        genreReportFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                genreReportId = filterItems.get(position).id;
                loadGenreSongs();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });
        loadGenreSongs();
        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { loadOver40(); }
            @Override public void afterTextChanged(Editable s) { }
        });
        filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGenreId = filterItems.get(position).id;
                loadOver40();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });
        loadOver40();
    }

    private void showSummary() {
        int total = db.countSongs();
        ((TextView) findViewById(R.id.reportTotal)).setText(String.valueOf(total));
        summaries.removeAllViews();
        List<Genre> genres = db.getGenres();
        if (genres.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("Chưa có thể loại để thống kê.");
            summaries.addView(empty);
        }
        for (Genre genre : genres) {
            View row = LayoutInflater.from(this).inflate(R.layout.row_summary, summaries, false);
            ((TextView) row.findViewById(R.id.summaryName)).setText(genre.name);
            ((TextView) row.findViewById(R.id.summaryCount)).setText(genre.songCount + " file");
            ((TextView) row.findViewById(R.id.summaryAge)).setText(
                    "Độ tuổi nghe: " + genre.minAge + " – " + genre.maxAge + " tuổi");
            int percentage = total == 0 ? 0 : Math.round(100f * genre.songCount / total);
            ((ProgressBar) row.findViewById(R.id.summaryBar)).setProgress(percentage);
            summaries.addView(row);
        }
    }

    private void setupFilter() {
        filterItems = new ArrayList<>();
        filterItems.add(new Genre(-1, "ALL", "Tất cả thể loại", 0, 120, 0));
        filterItems.addAll(db.getGenres());
        filter.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, filterItems));
    }

    private void loadGenreSongs() {
        if (genreSongs == null) return;
        List<Song> songs = db.getSongs("", genreReportId, false, 0);
        genreReportCount.setText(songs.size() + " bài nhạc thuộc thể loại đã chọn");
        renderSongCards(genreSongs, songs, "Không có file nhạc trong thể loại này.");
    }

    private void loadOver40() {
        if (results == null || search == null) return;
        List<Song> songs = db.getSongs(Ui.value(search), selectedGenreId, true, 0);
        count40.setText(songs.size() + " file nhạc dành cho người trên 40 tuổi");
        renderSongCards(results, songs,
                "Không có file nhạc phù hợp với điều kiện tra cứu.\nHãy thêm bài vào thể loại có độ tuổi tối đa từ 41 trở lên.");
    }

    private void renderSongCards(LinearLayout container, List<Song> songs, String emptyMessage) {
        container.removeAllViews();
        if (songs.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText(emptyMessage);
            empty.setTextColor(getColor(R.color.subtle));
            empty.setTextSize(14);
            empty.setPadding(20, 22, 20, 22);
            empty.setBackgroundResource(R.drawable.bg_card);
            container.addView(empty);
            return;
        }
        int gap = (int) (10 * getResources().getDisplayMetrics().density);
        for (Song song : songs) {
            View row = LayoutInflater.from(this).inflate(R.layout.row_song, container, false);
            SongAdapter.bindView(this, row, song, true, null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
            params.bottomMargin = gap;
            container.addView(row, params);
        }
    }

    @Override protected void onDestroy() {
        if (db != null) db.close();
        super.onDestroy();
    }
}
