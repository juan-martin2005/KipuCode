package com.kipucode.data.local.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "exercises",
        foreignKeys = @ForeignKey(
                entity = Lesson.class,
                parentColumns = "id",
                childColumns = "lesson_id",
                onDelete = ForeignKey.CASCADE))
public class Exercise {
    @PrimaryKey
    @NonNull
    private String id;
    private String lesson_id;
    private String type;
    private String instruction;
    private int order_index;

    public Exercise(@NonNull String id, String lesson_id, String type, String instruction, int order_index) {
        this.id = id;
        this.lesson_id = lesson_id;
        this.type = type;
        this.instruction = instruction;
        this.order_index = order_index;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getLesson_id() {
        return lesson_id;
    }

    public void setLesson_id(String lesson_id) {
        this.lesson_id = lesson_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public int getOrder_index() {
        return order_index;
    }

    public void setOrder_index(int order_index) {
        this.order_index = order_index;
    }
}
