package com.kipucode.data.local.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @NonNull
    private String id;
    private String name;
    private String email;
    private int total_xp;
    private int streak_day;
    private String token;

    public User(@NonNull String id, String name, String email, int total_xp, int streak_day, String token) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.total_xp = total_xp;
        this.streak_day = streak_day;
        this.token = token;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getTotal_xp() {
        return total_xp;
    }

    public void setTotal_xp(int total_xp) {
        this.total_xp = total_xp;
    }

    public int getStreak_day() {
        return streak_day;
    }

    public void setStreak_day(int streak_day) {
        this.streak_day = streak_day;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
