package com.xlingmao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by alongsea2 on 2017/1/20.
 */
public class ServiceUtil {

    public void runFunc(){

        ThreadPoolUtil.getPoolA().execute(()->{
            List<Callable<Object>> list = new ArrayList<>(10);

            long b = System.currentTimeMillis();
            for(int i =0;i<10;i++){
                list.add(new TaskThread());
            }
            try {
                List<Future<Object>> r = ThreadPoolUtil.getPoolA().invokeAll(list);
                System.out.println("====>" + (System.currentTimeMillis() - b));
                for (Future<Object> objectFuture : r) {
                    try {
                        objectFuture.get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }


            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });


    }
}
