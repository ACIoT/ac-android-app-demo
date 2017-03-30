package com.accloud.ac_service_android_demo.model;

/**
 * Created by wangkun on 04/01/2017.
 */
public class PropertyRecord {
    /**
     * 逻辑id
     */
    public long id;

    /**
     * Timestamp when the record created
     */
    public long timestamp;

    @Override
    public String toString() {
        return "PropertyRecord{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                '}';
    }
}
