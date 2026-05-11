package com.kipucode.data.local.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_progress", foreignKeys = {
        @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "user_id",
                onDelete = ForeignKey.CASCADE),
        @ForeignKey(
                entity = Lesson.class,
                parentColumns = "id",
                childColumns = "lesson_id",
                onDelete = ForeignKey.CASCADE)
})
public class UserProgress {

    @PrimaryKey
    @NonNull
    private String id;
    private String user_id;
    private String lesson_id;
    private String status;
    private int score;
    private Long completed_at;

    public UserProgress(@NonNull String id, String user_id, String lesson_id, String status, int score, Long completed_at) {
        this.id = id;
        this.user_id = user_id;
        this.lesson_id = lesson_id;
        this.status = status;
        this.score = score;
        this.completed_at = completed_at;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getLesson_id() {
        return lesson_id;
    }

    public void setLesson_id(String lesson_id) {
        this.lesson_id = lesson_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Long getCompleted_at() {
        return completed_at;
    }

    public void setCompleted_at(Long completed_at) {
        this.completed_at = completed_at;
    }
}
