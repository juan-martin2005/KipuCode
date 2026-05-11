package com.kipucode.data.local.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "courses")
public class Course {
    @PrimaryKey
    @NonNull
    private String id;
    private String title;
    private String description;
    private int order_index;
    private Long created_at;

    public Course(@NonNull String id, String title, String description, int order_index, Long created_at) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.order_index = order_index;
        this.created_at = created_at;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getOrder_index() {
        return order_index;
    }

    public void setOrder_index(int order_index) {
        this.order_index = order_index;
    }

    public Long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Long created_at) {
        this.created_at = created_at;
    }
}
