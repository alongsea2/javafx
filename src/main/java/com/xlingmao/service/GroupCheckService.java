package com.xlingmao.service;

import com.android.ddmlib.IDevice;
import com.xlingmao.controller.RightConsoleController;
import com.xlingmao.entity.DeviceGroup;
import com.xlingmao.util.ServiceSingleUtil;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by dell on 2016/11/18.
 */
public class GroupCheckService extends ScheduledService {
    private RightConsoleController rightConsoleController;
    private DeviceGroupService deviceGroupService;
    private static Set<String> existDevice = new HashSet<>();
    private static boolean isNeedFresh = false;


    public GroupCheckService(RightConsoleController rightConsoleController,DeviceGroupService deviceGroupService) {
        this.rightConsoleController = rightConsoleController;
        this.deviceGroupService = deviceGroupService;
    }

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                if(isNeedFresh){
                    isNeedFresh = false;
                    List<DeviceGroup> allDevice = deviceGroupService.selectAllDevice();
                    List<IDevice> connectDevice = ScreenMonitorService.getDevices();
                    if(connectDevice != null && !connectDevice.isEmpty()){
                        existDevice.addAll(allDevice.stream().map(DeviceGroup::getDeviceName).collect(Collectors.toList()));
                        Set<String> connectSet = connectDevice.stream().map(IDevice::getSerialNumber).collect(Collectors.toSet());
                        connectSet.removeAll(existDevice);
                        if(!connectSet.isEmpty()){
                            DeviceGroup deviceGroup;
                            for (String s : connectSet) {
                                deviceGroup = new DeviceGroup();
                                deviceGroup.setDeviceName(s);
                                deviceGroup.setGroupId(1);
                                deviceGroup.setMappingName(s);
                                DeviceGroup portDesc = deviceGroupService.selectPortDesc();
                                deviceGroup.setPort(portDesc == null ? 9000 : deviceGroup.getPort() + 1);
                                deviceGroupService.insert(deviceGroup);
                            }
                            existDevice.addAll(connectSet);
                        }
                    }
                    Platform.runLater(() -> {
                        rightConsoleController.refreshTreeGroup();
                        UpdateWechatPhoneGroupService service = (UpdateWechatPhoneGroupService)ServiceSingleUtil.getService(UpdateWechatPhoneGroupService.class);
                        service.updateGroup();
                    });
                }
                return null;
            }
        };
    }

    public static void setIsNeedFresh(boolean isNeedFresh) {
        GroupCheckService.isNeedFresh = isNeedFresh;
    }

    public static Set<String> getExistDevice() {
        return existDevice;
    }
}
