package com.kipucode.data.local.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "blocks_options", foreignKeys = @ForeignKey(
        entity = Exercise.class,
        parentColumns = "id",
        childColumns = "exercise_id",
        onDelete = ForeignKey.CASCADE
))
public class BlockOption {
    @PrimaryKey
    @NonNull
    private String id;
    private String exercise_id;
    private String content;
    private boolean is_correct;
    private int correct_position;

    public BlockOption(@NonNull String id, String exercise_id, String content, boolean is_correct, int correct_position) {
        this.id = id;
        this.exercise_id = exercise_id;
        this.content = content;
        this.is_correct = is_correct;
        this.correct_position = correct_position;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getExercise_id() {
        return exercise_id;
    }

    public void setExercise_id(String exercise_id) {
        this.exercise_id = exercise_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isIs_correct() {
        return is_correct;
    }

    public void setIs_correct(boolean is_correct) {
        this.is_correct = is_correct;
    }

    public int getCorrect_position() {
        return correct_position;
    }

    public void setCorrect_position(int correct_position) {
        this.correct_position = correct_position;
    }
}
