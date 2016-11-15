package com.xlingmao.util;

import com.android.chimpchat.adb.CommandOutputCapture;
import com.android.ddmlib.IDevice;
import org.apache.log4j.Logger;


import java.awt.*;


/**
 * Created by dell on 2016/10/31.
 */
public class ClickHelpUtil {
    private static double mZoom = Double.parseDouble(PropertiesUtil.getValue("device.screen.zoom"));

    private static String shellCommand = "input ";

    private static final Logger logger = Logger.getLogger(ClickHelpUtil.class);

    public static Point getRealPoint(Point p) {
        int x = p.x;
        int y = p.y;
        //todo 边界
        int realX = (int) ((x) / mZoom);
        int realY = (int) ((y) / mZoom);
        return new Point(realX, realY);
    }

    public static void touch(IDevice iDevice,double x, double y){
        try {
            iDevice.executeShellCommand(shellCommand + "tap " + x + " " + y,new CommandOutputCapture());
            logger.info("tap x : " + x + ", y : " + y);
        } catch (Exception e) {
            logger.error("===== " + e);
        }
    }

    public static void swipe(IDevice iDevice,double x, double y,double distX ,double distY){
        try {
            iDevice.executeShellCommand(shellCommand+ "swipe " + x + " " + y + " " + distX + " " + distY + " 200" , new CommandOutputCapture());
            logger.info("swipe " + x + " " + y + " " + distX + " " + distY + " 200");
        } catch (Exception e) {
            logger.error("===== " + e);
        }
    }

    public static void backHome(IDevice iDevice){
        try {
            iDevice.executeShellCommand(shellCommand + "keyevent 3",new CommandOutputCapture());
        } catch (Exception e) {
            logger.error("===== " + e);
        }
    }
}
