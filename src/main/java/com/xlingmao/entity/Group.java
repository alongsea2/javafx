package com.xlingmao.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by dell on 2016/11/15.
 */
@DatabaseTable(tableName = "XLM_WECHAT_GROUP")
public class Group {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "GROUP_NAME")
    private String groupName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
