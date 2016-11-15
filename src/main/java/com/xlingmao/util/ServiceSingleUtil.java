package com.xlingmao.util;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dell on 2016/11/15.
 */
public class ServiceSingleUtil {

    private static ConcurrentHashMap<String,Object> daoMap = new ConcurrentHashMap<>();

    private ServiceSingleUtil(){

    }

    public static Object getService(Class<?> clazz){
        try {
            Object service = daoMap.get(clazz.getName());
            if(service == null){
                service = clazz.newInstance();
                daoMap.put(clazz.getName(),service);
            }
            return service;
        } catch (InstantiationException | IllegalAccessException e) {
            return null;
        }
    }

}
