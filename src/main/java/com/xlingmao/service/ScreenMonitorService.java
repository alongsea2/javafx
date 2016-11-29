package com.xlingmao.service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import com.android.ddmlib.IDevice;
import com.xlingmao.common.thread.DeviceSocketThread;
import com.xlingmao.dto.ImageDeviceDto;
import javafx.application.Platform;
import com.xlingmao.asserts.ADB;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import org.apache.log4j.Logger;

public class ScreenMonitorService extends ScheduledService{

    private static final Logger logger = Logger.getLogger(ScreenMonitorService.class);

    private static List<IDevice> iDevices = new ArrayList<>();

    private static boolean isConnectionAll = true;

    private Label label;

    private FlowPane flowPane;

    public ScreenMonitorService(FlowPane flowPane, Label label) {
        this.flowPane = flowPane;
        this.label = label;
    }

    @Override
    protected Task createTask() {
        return new Task(){
            @Override
            protected Object call() throws Exception {
                try{
                    if (isConnectionAll) {
                        iDevices = ADB.getInstance().getDevices();
                    } else {
                        iDevices = null;
                    }
                    updateLabelDeviceNum();
                    ConcurrentHashMap<String, ImageDeviceDto> imageMap = DeviceSocketThread.getImageMap();
                    if( iDevices != null ) {
                        //如果设备被拔掉从m	ap移除
                        removeOfflineDevice(imageMap,iDevices);
                        int freshFlag = 0;
                        for (IDevice device : iDevices) {
                            ImageDeviceDto imageInstance = imageMap.get(device.getSerialNumber());
                            if( null == imageInstance ){
                                //存放监控上的机器实例
                                new Thread(new DeviceSocketThread(device,flowPane)).start();
                                freshFlag++;
                            }else {
                                if(imageInstance.isOffLine()){
                                    new Thread(new DeviceSocketThread(device,flowPane)).start();
                                }
                            }
                        }
                        if(freshFlag > 0){
                            GroupCheckService.setIsNeedFresh(true);
                        }
                    }else{
                        removeAllDevice(imageMap);
                        updateLabelDeviceNum();
                    }
                }catch(Exception e){
                    GroupCheckService.setIsNeedFresh(true);
                    logger.error(e);
                }
                return null;
            }
        };
    }


    public static List<IDevice> getDevices() {
        return iDevices;
    }

    public static void setIsConnectionAll(boolean isConnectionAll) {
        ScreenMonitorService.isConnectionAll = isConnectionAll;
    }

    /* =========================== private function  =============================*/
    private void updateLabelDeviceNum(){
        Platform.runLater(() -> {
            if(iDevices != null && iDevices.size() > 0){
                label.setText("现在连接设备" + iDevices.size() + "台");
            }else{
                label.setText("现在连接设备0台");
            }
        });
    }

    private void removeOfflineDevice(ConcurrentHashMap<String,ImageDeviceDto> imageMap, List<IDevice> iDevices){
        if(imageMap.size() > 0){
            /*InputStream inputStream = ScreenMonitorService.class.getClass().getResourceAsStream("/view/css/Capture001.png");
            Image image = new Image(inputStream);*/
            Set<String> deviceSet = new HashSet<>();
            for (IDevice iDevice : iDevices) {
                deviceSet.add(iDevice.getSerialNumber());
            }
            int freshFlag = 0;
            for (String s : imageMap.keySet()) {
                if(!deviceSet.contains(s)){
                    ImageDeviceDto imageDto = imageMap.get(s);
                    imageDto.setOffLine(true);
                    imageDto.getImageView().setImage(null);
                    imageDto.setSocket(null);
                    GroupCheckService.getExistDevice().remove(s);
                    freshFlag++;
                }
            }
            if(freshFlag > 0){
                GroupCheckService.setIsNeedFresh(true);
            }
        }
    }

    private void removeAllDevice(ConcurrentHashMap<String,ImageDeviceDto> imageMap){
        Platform.runLater(() -> {
            for (String key : imageMap.keySet()) {
                imageMap.get(key).getImageView().setImage(null);
                //imageMap.clear();
            }
        });
        //logger.info("===== idd:" + idd + "===== del : " + key);
    }
}
