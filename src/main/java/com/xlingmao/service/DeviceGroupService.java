package com.xlingmao.service;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.xlingmao.entity.DeviceGroup;
import com.xlingmao.util.H2ConnectionSingleUtil;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by dell on 2016/11/15.
 */
@SuppressWarnings("unchecked")
public class DeviceGroupService {
    private Dao deviceGroupDao = H2ConnectionSingleUtil.getDaoInstance(DeviceGroup.class);

    public List<DeviceGroup> selectAllDevice(){
        try {
            return deviceGroupDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<DeviceGroup> selectDeviceByGroupId(int groupId){
        PreparedQuery preConfig = null;
        List<DeviceGroup> list = null;
        try {
            preConfig = deviceGroupDao.queryBuilder().where().eq("GROUP_ID", groupId).prepare();
            list = deviceGroupDao.query(preConfig);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int updateDeviceGroupId(int oldGroupId,int newGroupId){
        int i = 0;
        try {
            UpdateBuilder updateBuilder = deviceGroupDao.updateBuilder();
            updateBuilder.updateColumnValue("GROUP_ID",newGroupId).where().eq("GROUP_ID",oldGroupId);
            i = updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }

    public int updateDeviceGroupIdById(int deviceId,int newGroupId){
        int i = 0;
        try {
            UpdateBuilder updateBuilder = deviceGroupDao.updateBuilder();
            updateBuilder.updateColumnValue("GROUP_ID",newGroupId).where().eq("id",deviceId);
            i = updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }

    public int updateDeviceNameById(int deviceId,String deviceName){
        int i = 0;
        try {
            UpdateBuilder updateBuilder = deviceGroupDao.updateBuilder();
            updateBuilder.updateColumnValue("MAPPING_NAME",deviceName).where().eq("id",deviceId);
            i = updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }

    public DeviceGroup selectDeviceByName(String deviceName){
        PreparedQuery preConfig = null;
        DeviceGroup list = null;
        try {
            preConfig = deviceGroupDao.queryBuilder().where().eq("DEVICE_NAME", deviceName).prepare();
            list = (DeviceGroup) deviceGroupDao.queryForFirst(preConfig);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public DeviceGroup selectDeviceByMappingName(String mappingName){
        PreparedQuery preConfig = null;
        DeviceGroup list = null;
        try {
            preConfig = deviceGroupDao.queryBuilder().where().eq("MAPPING_NAME", mappingName).prepare();
            list = (DeviceGroup) deviceGroupDao.queryForFirst(preConfig);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public DeviceGroup selectPortDesc(){
        PreparedQuery preConfig = null;
        DeviceGroup list = null;
        try {
            preConfig = deviceGroupDao.queryBuilder().orderBy("PORT",true).prepare();
            list = (DeviceGroup) deviceGroupDao.queryForFirst(preConfig);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }



    public int insert(DeviceGroup deviceGroup){
        int i = 0;
        try {
            deviceGroupDao.create(deviceGroup);
            i = deviceGroup.getId();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }
}
