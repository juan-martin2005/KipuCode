package com.kipucode.data.local.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "lessons",
        foreignKeys = @ForeignKey(
                entity = Course.class,
                parentColumns = "id",
                childColumns = "course_id",
                onDelete = ForeignKey.CASCADE))
public class Lesson {
    @PrimaryKey
    @NonNull
    private String id;
    private String course_id;
    private String title;
    private String theory_content;
    private int order_index;

    public Lesson(@NonNull String id, String course_id, String title, String theory_content, int order_index) {
        this.id = id;
        this.course_id = course_id;
        this.title = title;
        this.theory_content = theory_content;
        this.order_index = order_index;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTheory_content() {
        return theory_content;
    }

    public void setTheory_content(String theory_content) {
        this.theory_content = theory_content;
    }

    public int getOrder_index() {
        return order_index;
    }

    public void setOrder_index(int order_index) {
        this.order_index = order_index;
    }
}
