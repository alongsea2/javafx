package com.xlingmao.service;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.xlingmao.entity.Config;
import com.xlingmao.util.H2ConnectionSingleUtil;

import java.sql.SQLException;

/**
 * Created by dell on 2016/11/9.
 */
@SuppressWarnings("unchecked")
public class ConfigService {

    private Dao configDao = H2ConnectionSingleUtil.getDaoInstance(Config.class);

    public Config selectByKey(String key){
        PreparedQuery preConfig = null;
        Config config = null;
        try {
            preConfig = configDao.queryBuilder().where().eq("name", key).prepare();
            config = (Config) configDao.queryForFirst(preConfig);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return config;
    }

    public void updateConfig(Config config){
        try {
            configDao.update(config);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
