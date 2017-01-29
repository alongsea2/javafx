package com.xlingmao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by alongsea2 on 2017/1/20.
 */
public class ThreadPoolUtil {

    private static ExecutorService poolA = Executors.newFixedThreadPool(20);


    public static ExecutorService getPoolA() {
        return poolA;
    }
}
