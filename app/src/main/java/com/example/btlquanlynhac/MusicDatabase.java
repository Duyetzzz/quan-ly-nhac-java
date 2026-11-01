package com.example.btlquanlynhac;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** All SQL uses parameter binding for user-entered values. */
public class MusicDatabase extends SQLiteOpenHelper {
    private static final String NAME = "music_manager.db";
    // Version 2 is compatible with version 1 database from the original BTL.
    private static final int VERSION = 2;

    public MusicDatabase(Context context) { super(context, NAME, null, VERSION); }

    @Override public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE genres (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "code TEXT NOT NULL UNIQUE COLLATE NOCASE, name TEXT NOT NULL, " +
                "min_age INTEGER NOT NULL CHECK(min_age BETWEEN 0 AND 120), " +
                "max_age INTEGER NOT NULL CHECK(max_age BETWEEN min_age AND 120))");
        db.execSQL("CREATE TABLE songs (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "code TEXT NOT NULL UNIQUE COLLATE NOCASE, name TEXT NOT NULL, " +
                "image_uri TEXT, file_type TEXT NOT NULL, genre_id INTEGER NOT NULL, " +
                "download_url TEXT NOT NULL, description TEXT, " +
                "FOREIGN KEY(genre_id) REFERENCES genres(id) ON DELETE RESTRICT)");
        createIndexes(db);
        // Only initial genres are seeded. The app never invents working music URLs.
        db.execSQL("INSERT INTO genres(code,name,min_age,max_age) VALUES " +
                "('NHACTRE','Nhạc trẻ',12,40), " +
                "('NHACVANG','Nhạc vàng',35,100), " +
                "('TRUTINH','Nhạc trữ tình',30,100), " +
                "('THIEUNHI','Nhạc thiếu nhi',0,14)");
    }

    private void createIndexes(SQLiteDatabase db) {
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_songs_genre ON songs(genre_id)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_songs_name ON songs(name)");
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) createIndexes(db); // Keep user's v1 data intact.
    }

    public List<Genre> getGenres() {
        List<Genre> output = new ArrayList<>();
        String sql = "SELECT g.id,g.code,g.name,g.min_age,g.max_age,COUNT(s.id) AS song_count " +
                "FROM genres g LEFT JOIN songs s ON g.id=s.genre_id " +
                "GROUP BY g.id ORDER BY g.name COLLATE NOCASE";
        try (Cursor c = getReadableDatabase().rawQuery(sql, null)) {
            while (c.moveToNext()) {
                output.add(new Genre(c.getLong(0), c.getString(1), c.getString(2),
                        c.getInt(3), c.getInt(4), c.getInt(5)));
            }
        }
        return output;
    }

    public List<Song> getSongs(String search, long genreId, boolean over40, int limit) {
        List<Song> output = new ArrayList<>();
        List<String> args = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT s.id,s.code,s.name,s.image_uri,s.file_type," +
                "s.genre_id,s.download_url,s.description,g.name,g.min_age,g.max_age " +
                "FROM songs s INNER JOIN genres g ON s.genre_id=g.id WHERE 1=1");
        if (genreId >= 0) {
            sql.append(" AND s.genre_id=?");
            args.add(String.valueOf(genreId));
        }
        if (over40) sql.append(" AND g.max_age >= 41");
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (s.name LIKE ? OR s.code LIKE ?)");
            String term = "%" + search.trim() + "%";
            args.add(term);
            args.add(term);
        }
        sql.append(" ORDER BY s.id DESC");
        if (limit > 0) {
            sql.append(" LIMIT ?");
            args.add(String.valueOf(limit));
        }
        try (Cursor c = getReadableDatabase().rawQuery(sql.toString(), args.toArray(new String[0]))) {
            while (c.moveToNext()) {
                output.add(new Song(c.getLong(0), c.getString(1), c.getString(2),
                        c.getString(3), c.getString(4), c.getLong(5), c.getString(6),
                        c.getString(7), c.getString(8), c.getInt(9), c.getInt(10)));
            }
        }
        return output;
    }

    public int countSongs() {
        try (Cursor c = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM songs", null)) {
            return c.moveToFirst() ? c.getInt(0) : 0;
        }
    }

    public int countGenres() {
        try (Cursor c = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM genres", null)) {
            return c.moveToFirst() ? c.getInt(0) : 0;
        }
    }

    public long saveGenre(long id, String code, String name, int minAge, int maxAge) {
        ContentValues v = new ContentValues();
        v.put("code", code.trim().toUpperCase(Locale.ROOT));
        v.put("name", name.trim());
        v.put("min_age", minAge);
        v.put("max_age", maxAge);
        SQLiteDatabase db = getWritableDatabase();
        if (id < 0) return db.insertOrThrow("genres", null, v);
        if (db.update("genres", v, "id=?", new String[]{String.valueOf(id)}) != 1)
            throw new IllegalStateException("Không tìm thấy thể loại cần cập nhật.");
        return id;
    }

    public void deleteGenre(Genre genre) {
        if (genre.songCount > 0)
            throw new IllegalStateException("Thể loại có " + genre.songCount +
                    " file nhạc. Hãy chuyển hoặc xóa các bài nhạc trước.");
        int rows = getWritableDatabase().delete("genres", "id=?",
                new String[]{String.valueOf(genre.id)});
        if (rows != 1) throw new IllegalStateException("Không tìm thấy thể loại cần xóa.");
    }

    public long saveSong(long id, String code, String name, String imageUri, String fileType,
                         long genreId, String downloadUrl, String description) {
        ContentValues v = new ContentValues();
        v.put("code", code.trim().toUpperCase(Locale.ROOT));
        v.put("name", name.trim());
        v.put("image_uri", imageUri == null ? "" : imageUri);
        v.put("file_type", fileType.trim().toUpperCase(Locale.ROOT));
        v.put("genre_id", genreId);
        v.put("download_url", downloadUrl.trim());
        v.put("description", description.trim());
        SQLiteDatabase db = getWritableDatabase();
        if (id < 0) return db.insertOrThrow("songs", null, v);
        if (db.update("songs", v, "id=?", new String[]{String.valueOf(id)}) != 1)
            throw new IllegalStateException("Không tìm thấy file nhạc cần cập nhật.");
        return id;
    }

    public void deleteSong(long songId) {
        int rows = getWritableDatabase().delete("songs", "id=?",
                new String[]{String.valueOf(songId)});
        if (rows != 1) throw new IllegalStateException("Không tìm thấy bài nhạc cần xóa.");
    }
}
