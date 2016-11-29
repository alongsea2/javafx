package com.xlingmao.common.thread;

import com.android.ddmlib.IDevice;
import com.xlingmao.common.enums.ButtonFuncEnum;


/**
 * Created by dell on 2016/11/28.
 */
public class ButtonCommandThread implements Runnable {

    private IDevice iDevice;
    private String buttonId;

    public ButtonCommandThread(IDevice iDevice, String buttonId) {
        this.iDevice = iDevice;
        this.buttonId = buttonId;
    }

    @Override
    public void run() {
        ButtonFuncEnum.runButtonFunc(buttonId,iDevice);
    }
}
