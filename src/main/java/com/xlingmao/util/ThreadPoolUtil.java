package com.xlingmao.util;

import com.android.ddmlib.IDevice;
import com.xlingmao.common.thread.ButtonCommandThread;
import com.xlingmao.common.thread.ClickThread;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by dell on 2016/11/17.
 * 群控的时候命令异步发送给所有机器
 */
public class ThreadPoolUtil {
    private static ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(30);

    public static ExecutorService getThreadPool(){
        return threadPoolExecutor;
    }

    public static void bulkRunFuncOperation(String buttonId,List<IDevice> list){
        for (IDevice iDevice : list) {
            threadPoolRunFunc(buttonId,iDevice);
        }
    }

    public static void threadPoolRunFunc(String buttonId,IDevice device){
        getThreadPool().execute(new ButtonCommandThread(device,buttonId));
    }

    public static void bulkClickOperation(double x ,double y ,List<IDevice> list){
        for (IDevice iDevice : list) {
            threadPollRunMouseEvent(x,y,iDevice);
        }
    }

    public static void bulkSwipeOperation(double x ,double y , double tempX, double tempY ,List<IDevice> list){
        for (IDevice iDevice : list) {
            threadPollRunMouseEvent(x,y,tempX,tempY,iDevice);
        }
    }

    public static void threadPollRunMouseEvent(double x ,double y ,IDevice device){
        getThreadPool().execute(new ClickThread(device,x,y));
    }

    public static void threadPollRunMouseEvent(double x ,double y , double tempX, double tempY ,IDevice device){
        getThreadPool().execute(new ClickThread(device,x,y,tempX,tempY));
    }


}
