package com.xlingmao.common;

/**
 * Created by dell on 2016/11/10.
 */
public interface AppConfig {

    int DEFAULT_GROUP_ID = 1;

    String JAR_PATH = "xlm.ac.wx.autocase.";

    String ADB_SHELL_PREFIX = "adb -s %s shell ";
    
    String AUTO_JAR_LOCATION = "/software/auto.jar";

    String CONNECT_OK = "已连接";

    String CONNECT_NO = "未连接";
}
