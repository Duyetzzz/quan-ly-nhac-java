package com.example.btlquanlynhac;

public class Genre {
    public final long id;
    public final String code, name;
    public final int minAge, maxAge, songCount;

    public Genre(long id, String code, String name, int minAge, int maxAge, int songCount) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.songCount = songCount;
    }
    @Override public String toString() { return name; }
}
