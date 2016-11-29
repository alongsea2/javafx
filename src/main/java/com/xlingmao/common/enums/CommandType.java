package com.xlingmao.common.enums;

import com.xlingmao.common.AppConfig;

/**
 * Created by dell on 2016/11/17.
 */
public enum CommandType{
    RUN_APP(AppConfig.ADB_SHELL_PREFIX + "am start %s"),
    RUN_JAR(AppConfig.ADB_SHELL_PREFIX + "uiautomator runtest auto.jar -c %s"),
    RUN_ADB_SHELL( "adb -s %s push %s /data/local/tmp")    // TODO: 2016/11/18 下载到本地的地址


    ;

    private String template;

    CommandType(String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public static String getCommandTemplate(CommandType type){
        return type.getTemplate();
    }
}

