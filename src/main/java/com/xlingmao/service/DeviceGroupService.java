package com.xlingmao.service;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
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

    List<DeviceGroup> selectDeviceByGroupId(int groupId){
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

}
