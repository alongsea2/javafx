package com.xlingmao.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by dell on 2016/11/15.
 */
public class GroupTreeModel {
    private final IntegerProperty id;
    private final StringProperty deviceName;
    private final IntegerProperty groupId;
    private final StringProperty mappingName;

    public String getMappingName() {
        return mappingName.get();
    }

    public StringProperty mappingNameProperty() {
        return mappingName;
    }

    public void setMappingName(String mappingName) {
        this.mappingName.set(mappingName);
    }

    public GroupTreeModel(int id, String deviceName, String mappingName, int groupId) {
        this.id = new SimpleIntegerProperty(id);
        this.deviceName = new SimpleStringProperty(deviceName);
        this.groupId = new SimpleIntegerProperty(groupId);
        this.mappingName = new SimpleStringProperty(mappingName);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getDeviceName() {
        return deviceName.get();
    }

    public StringProperty deviceNameProperty() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName.set(deviceName);
    }

    public int getGroupId() {
        return groupId.get();
    }

    public IntegerProperty groupIdProperty() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId.set(groupId);
    }


}
