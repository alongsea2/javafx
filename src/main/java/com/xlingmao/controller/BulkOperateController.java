package com.xlingmao.controller;

import com.android.ddmlib.IDevice;
import com.xlingmao.common.thread.ButtonCommandThread;
import com.xlingmao.service.ScreenMonitorService;
import com.xlingmao.util.ThreadPoolUtil;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;

import java.util.List;

/**
 * Created by dell on 2016/11/17.
 */
public class BulkOperateController  {

    @FXML
    private AnchorPane bulkPane;

    @FXML
    private FlowPane buttonFlowPane;

    public void initialize(){
        initButton();
    }

    /**
     * 初始化适配和点击事件
     */
    @FXML
    private void initButton(){
        ObservableList<Node> buttons = buttonFlowPane.getChildren();
        //一行4个button
        bulkPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            for (Node button : buttons) {
                Button button1 = (Button) button;
                button1.setPrefWidth(newValue.doubleValue() / 4);
                button1.setOnMouseClicked(event -> {
                    List<IDevice> devices = ScreenMonitorService.getDevices();
                    if(devices != null && !devices.isEmpty()){
                        ThreadPoolUtil.bulkRunFuncOperation(button1.getId(),devices);
                    }
                });
            }
        });
    }
}
