package com.xlingmao.common.thread;

import com.android.ddmlib.IDevice;
import com.xlingmao.util.ClickHelpUtil;

/**
 * Created by dell on 2016/11/28.
 */
public class ClickThread implements Runnable {
    private IDevice iDevice;
    private double x;
    private double y;
    private double tmpx;
    private double tmpy;

    public ClickThread(IDevice iDevice, double x, double y, double tmpx, double tmpy) {
        this.iDevice = iDevice;
        this.x = x;
        this.y = y;
        this.tmpx = tmpx;
        this.tmpy = tmpy;
    }

    public ClickThread(IDevice iDevice, double x, double y) {
        this.iDevice = iDevice;
        this.x = x;
        this.y = y;
    }

    @Override
    public void run() {
        if(tmpx == 0 || tmpy == 0){
            ClickHelpUtil.touch(iDevice,x,y);
        }else{
            ClickHelpUtil.swipe(iDevice,tmpx,tmpy,x,y);
        }
    }
}
