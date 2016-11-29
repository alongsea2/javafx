package com.xlingmao.util;

import com.xlingmao.common.enums.CommandType;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Created by dell on 2016/11/9.
 */
public class JavaAdbShellUtil {

    private static final Logger logger = Logger.getLogger(JavaAdbShellUtil.class);

    public static Process runAdbShellScript(String command){
        try {
            logger.info("===== run command " + command);
            return Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            logger.error(e);
            return null;
        }
    }

    public static Process buildCommandAndRun(CommandType s,Object... command){
        String template = CommandType.getCommandTemplate(s);
        String commands = String.format(template,command);
        logger.info("===== run command " + commands);
        Process p = runAdbShellScript(commands);
        if(p != null){
            int i ;
            try {
                StringBuilder stringBuilder = new StringBuilder();
                while ((i = p.getInputStream().read()) != -1){
                    stringBuilder.append((char)i);
                }
                logger.info("===== exec info " + stringBuilder.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return p;
    }


}
