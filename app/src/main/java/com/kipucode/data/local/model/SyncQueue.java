package com.kipucode.data.local.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sync_queue")
public class SyncQueue {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String entity_type;
    private String entity_id;
    private String payload;
    private long crated_at;

    public SyncQueue(long id, String entity_type, String entity_id, String payload, long crated_at) {
        this.id = id;
        this.entity_type = entity_type;
        this.entity_id = entity_id;
        this.payload = payload;
        this.crated_at = crated_at;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEntity_type() {
        return entity_type;
    }

    public void setEntity_type(String entity_type) {
        this.entity_type = entity_type;
    }

    public String getEntity_id() {
        return entity_id;
    }

    public void setEntity_id(String entity_id) {
        this.entity_id = entity_id;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public long getCrated_at() {
        return crated_at;
    }

    public void setCrated_at(long crated_at) {
        this.crated_at = crated_at;
    }
}
