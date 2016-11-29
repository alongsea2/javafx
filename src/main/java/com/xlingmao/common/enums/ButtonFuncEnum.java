package com.xlingmao.common.enums;

import com.android.ddmlib.IDevice;
import com.xlingmao.common.AppConfig;
import com.xlingmao.common.LayoutId;
import com.xlingmao.util.ClickHelpUtil;
import com.xlingmao.util.JavaAdbShellUtil;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * Created by dell on 2016/11/17.
 */
public enum ButtonFuncEnum{
    //回主页
    BACK_HOME(LayoutId.LABEL_BACK_HOME){
        @Override
        public void adbShell(IDevice iDevice) {
            ClickHelpUtil.backHome(iDevice);
        }
    },
    //找手机
    FIND_DEVICE(LayoutId.FIND_DEVICE_BUTTON){
        @Override
        public void adbShell(IDevice iDevice) {
            JavaAdbShellUtil.buildCommandAndRun(CommandType.RUN_APP,iDevice.getSerialNumber(),"com.xlingmao.herehere/com.xlingmao.herehere.MainActivity");
        }
    },
    //打开微信
    OPEN_WECHAT(LayoutId.OPEN_WECHAT) {
        @Override
        public void adbShell(IDevice iDevice) {
            JavaAdbShellUtil.buildCommandAndRun(CommandType.RUN_JAR,iDevice.getSerialNumber(), AppConfig.JAR_PATH + "OpenWechat");
        }
    },
    //打开qq
    OPEN_QQ(LayoutId.OPEN_QQ){
        @Override
        public void adbShell(IDevice iDevice) {
            JavaAdbShellUtil.buildCommandAndRun(CommandType.RUN_JAR,iDevice.getSerialNumber(),AppConfig.JAR_PATH + "OpenQQ");
        }
    },
    //关闭屏幕
    CLOSE_SCREEN(LayoutId.CLOSE_SCREEN) {
        @Override
        public void adbShell(IDevice iDevice) {
            JavaAdbShellUtil.buildCommandAndRun(CommandType.RUN_JAR,iDevice.getSerialNumber(),AppConfig.JAR_PATH + "CloseScreen");
        }
    },
    //唤醒屏幕
    WAKE_SCREEN(LayoutId.WAKE_SCREEN) {
        @Override
        public void adbShell(IDevice iDevice) {
            JavaAdbShellUtil.buildCommandAndRun(CommandType.RUN_JAR,iDevice.getSerialNumber(),AppConfig.JAR_PATH + "OpenScreen");
        }
    },
    //重启
    REBOOT(LayoutId.REBOOT) {
        @Override
        public void adbShell(IDevice iDevice) {
            JavaAdbShellUtil.buildCommandAndRun(CommandType.RUN_JAR,iDevice.getSerialNumber(),AppConfig.JAR_PATH + "RestartPhone");
        }
    },
    //后台加速
    SPEED_UP(LayoutId.SPEED_UP){
        @Override
        public void adbShell(IDevice iDevice) {
            JavaAdbShellUtil.buildCommandAndRun(CommandType.RUN_JAR,iDevice.getSerialNumber(),AppConfig.JAR_PATH + "OneStepClear");
        }
    },
    //群体更新auto.jar
    UPDATE_AUTO_JAR(LayoutId.UPDATE_AUTO_JAR) {
        @Override
        public void adbShell(IDevice iDevice) {
            File file = new File(AppConfig.AUTO_JAR_LOCATION);
            if(file.exists()){
                JavaAdbShellUtil.buildCommandAndRun(CommandType.RUN_ADB_SHELL,iDevice.getSerialNumber(), AppConfig.AUTO_JAR_LOCATION);
            }else {
                Platform.runLater(() -> new Alert(Alert.AlertType.ERROR,"更新文件不存在").show());
            }
        }
    };

    private String t;

    ButtonFuncEnum(String t) {
        this.t = t;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }
    public abstract void adbShell(IDevice iDevice);

    public static void runButtonFunc(String buttonId,IDevice iDevice){
        for (ButtonFuncEnum buttonFunc : ButtonFuncEnum.values()) {
            if(buttonFunc.getT().equals(buttonId)){
                buttonFunc.adbShell(iDevice);
            }
        }
    }
}
