package com.xlingmao;

import java.util.concurrent.Callable;

/**
 * Created by alongsea2 on 2017/1/20.
 */
public class TaskThread implements Callable{

    @Override
    public Object call() throws Exception {
        //Thread.sleep(1000);

        System.out.println(Thread.currentThread().getName() + " 11111");
        for(int i = 0;i<10000;i++){
            System.out.println(i);
        }
        System.out.println(Thread.currentThread().getName() + "sss");
        return "ssssss";
    }
}
