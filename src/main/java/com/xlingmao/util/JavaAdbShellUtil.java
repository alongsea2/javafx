package com.xlingmao.util;

import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Created by dell on 2016/11/9.
 */
public class JavaAdbShellUtil {
    private static final Logger logger = Logger.getLogger(JavaAdbShellUtil.class);

    public static Process runAdbShellScript(String command){
        try {
            return Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            logger.error(e);
            return null;
        }
    }
}
