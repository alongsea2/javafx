package com.xlingmao.util;

import com.android.chimpchat.adb.CommandOutputCapture;
import com.android.ddmlib.IDevice;


import java.awt.*;


/**
 * Created by dell on 2016/10/31.
 */
public class ClickHelpUtil {
    private static double mZoom = Double.parseDouble(PropertiesUtil.getValue("device.screen.zoom"));
    private static String shellCommand = "input ";

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
        } catch (Exception e) {
        }
    }
}
