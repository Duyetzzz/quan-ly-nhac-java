package com.example.btlquanlynhac;

public class Song {
    public final long id, genreId;
    public final String code, name, imageUri, fileType, downloadUrl, description, genreName;
    public final int minAge, maxAge;

    public Song(long id, String code, String name, String imageUri, String fileType,
                long genreId, String downloadUrl, String description, String genreName,
                int minAge, int maxAge) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.imageUri = imageUri;
        this.fileType = fileType;
        this.genreId = genreId;
        this.downloadUrl = downloadUrl;
        this.description = description;
        this.genreName = genreName;
        this.minAge = minAge;
        this.maxAge = maxAge;
    }
}
