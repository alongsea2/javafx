package com.xlingmao.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.List;

/**
 * Created by dell on 2016/11/15.
 */
public class GroupModel {
    private final IntegerProperty id;
    private final StringProperty groupName;
    private final List<GroupTreeModel> groupTreeModel;


    public GroupModel(int id, String groupName, List<GroupTreeModel> groupTreeModel) {
        this.id = new SimpleIntegerProperty(id);
        this.groupName = new SimpleStringProperty(groupName);
        this.groupTreeModel = groupTreeModel;
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

    public String getGroupName() {
        return groupName.get();
    }

    public StringProperty groupNameProperty() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName.set(groupName);
    }

    public List<GroupTreeModel> getGroupTreeModel() {
        return groupTreeModel;
    }
}
