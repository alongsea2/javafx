package com.xlingmao.service;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.xlingmao.entity.DeviceGroup;
import com.xlingmao.entity.Group;
import com.xlingmao.model.GroupModel;
import com.xlingmao.model.GroupTreeModel;
import com.xlingmao.util.H2ConnectionSingleUtil;
import com.xlingmao.util.ServiceSingleUtil;
import javafx.scene.control.Alert;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2016/11/15.
 */
@SuppressWarnings("unchecked")
public class GroupService {
    private Dao groupService = H2ConnectionSingleUtil.getDaoInstance(Group.class);
    private DeviceGroupService deviceGroupService = (DeviceGroupService) ServiceSingleUtil.getService(DeviceGroupService.class);

    public List<Group> selectGroups(){
        PreparedQuery preConfig = null;
        List<Group> list = null;
        try {
            preConfig = groupService.queryBuilder().prepare();
            list = groupService.query(preConfig);
        } catch (SQLException e) {
//            new Alert(Alert.AlertType.ERROR,"请开启数据库").showAndWait().ifPresent(buttonType -> System.exit(0));
//            e.printStackTrace();
        }
        return list;
    }

    public int deleteGroup(int id){
        int i = 0;
        try {
            DeleteBuilder deleteBuilder = groupService.deleteBuilder();
            deleteBuilder.where().eq("ID",id);
            deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }

    public int insert(Group group){
        try {
            groupService.create(group);
            return group.getId();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }


    public List<GroupModel> buildGroupTree(){
        List<Group> parent = selectGroups();
        List<GroupModel> list = new ArrayList<>();
        GroupTreeModel groupTreeModel;
        GroupModel groupModel = null;
        for (Group group : parent) {
            List<DeviceGroup> deviceList = deviceGroupService.selectDeviceByGroupId(group.getId());
            List<GroupTreeModel> groupTreeModelList = new ArrayList<>();
            for (DeviceGroup deviceGroup : deviceList) {
                groupTreeModel = new GroupTreeModel(deviceGroup.getId(),deviceGroup.getDeviceName(), StringUtils.isNotEmpty(deviceGroup.getMappingName()) ? deviceGroup.getMappingName() : deviceGroup.getDeviceName(),group.getId());
                groupTreeModelList.add(groupTreeModel);
            }
            groupModel = new GroupModel(group.getId(),group.getGroupName(),groupTreeModelList);
            list.add(groupModel);
        }
        return list;
    }
}
