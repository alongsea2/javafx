package com.xlingmao.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by dell on 2016/11/15.
 */
@DatabaseTable(tableName = "XLM_WECHAT_DEVICE_GROUP")
public class DeviceGroup {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "DEVICE_NAME")
    private String deviceName;
    @DatabaseField(columnName = "GROUP_ID")
    private int groupId;

    public DeviceGroup() {

    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
