package com.xlingmao.util;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dell on 2016/11/10.
 */
public class H2ConnectionSingleUtil {
    // TODO: 2016/11/10 pom
    private static String url = "jdbc:h2:tcp://localhost/~/XLM_WECHAT";

    private static String account = "sa";

    private static String password = "";

    private static ConcurrentHashMap<String,Dao> daoMap = new ConcurrentHashMap<>();

    private static final Logger logger = Logger.getLogger(H2ConnectionSingleUtil.class);

    private H2ConnectionSingleUtil(){
    }

    public static JdbcPooledConnectionSource getPoolConnectionInstance(){
        return H2ConnectionSingleInner.connectionSource;
    }

    public static Dao getDaoInstance(Class<?> clazz){
        try {
            if(daoMap.containsKey(clazz.getName())){
                return daoMap.get(clazz.getName());
            }
            Dao dao = DaoManager.createDao(getPoolConnectionInstance(),clazz);
            daoMap.put(clazz.getName(),dao);
            return dao;
        } catch (Exception e) {
            logger.error("=====" + e);
            return null;
        }
    }

    private static class H2ConnectionSingleInner{
        private static JdbcPooledConnectionSource connectionSource;
        static {
            try {
                connectionSource = new JdbcPooledConnectionSource(url);
                connectionSource.setUsername(account);
                connectionSource.setPassword(password);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
