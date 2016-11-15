package com.xlingmao.service;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import com.android.ddmlib.IDevice;
import com.xlingmao.common.LayoutId;
import com.xlingmao.dto.ImageDeviceDto;
import com.xlingmao.util.ClickHelpUtil;
import com.xlingmao.util.NodeUtil;
import com.xlingmao.util.PropertiesUtil;
import javafx.application.Platform;
import com.xlingmao.asserts.ADB;
import com.xlingmao.asserts.FBImage;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import org.apache.log4j.Logger;

public class ScreenMonitorService extends ScheduledService{

    private static final Logger logger = Logger.getLogger(ScreenMonitorService.class);

    private static ConcurrentHashMap<String,ImageDeviceDto> imageMap = new ConcurrentHashMap<>();

    private static boolean isAll = false;

    private static List<IDevice> iDevices = new ArrayList<>();

    private static boolean isConnectionAll = true;

    private FlowPane flowPane;

    private Label label;

    private int screenWidth = PropertiesUtil.getValueForInt("device.screen.width");

    private int screenHeight = PropertiesUtil.getValueForInt("device.screen.height");

    private double screenZoom = Double.parseDouble(PropertiesUtil.getValue("device.screen.zoom"));

    private double tempX;

    private double tempY;

    private boolean[] eventFlag = {false};//[0]-->是否在drag

    public void setFlowPane(FlowPane flowPane) {
        this.flowPane = flowPane;
    }

    @Override
    protected Task createTask() {
        return new Task(){
            @Override
            protected Object call() throws Exception {
                try{
                    if(isConnectionAll){
                        iDevices = ADB.getInstance().getDevices();
                    }else{
                        iDevices = null;
                    }
                    int i = 0;
                    FBImage image;
                    ImageDeviceDto imageViewDto;
                    if( iDevices != null ) {
                        //如果设备被拔掉从m	ap移除
                        removeOfflineDevice(imageMap,iDevices);
                        for (IDevice re : iDevices) {
                            ImageDeviceDto imageInstance = imageMap.get(re.getName());
                            image = ADB.getInstance().getDeviceImage(re);
                            Image fxImage = SwingFXUtils.toFXImage(image,null);
                            if( null == imageInstance ){
                                //存放监控上的机器实例
                                imageViewDto = new ImageDeviceDto();
                                //主页面对象
                                FXMLLoader fxmlLoader = new FXMLLoader();
                                fxmlLoader.setLocation(ScreenMonitorService.class.getResource("/view/initView/DeviceSmallButtonLayout.fxml"));
                                AnchorPane anchorPane = fxmlLoader.load();
                                ObservableList<Node> anchorPaneChildren = anchorPane.getChildren();

                                //图片节点
                                ImageView imageView = (ImageView) NodeUtil.findById(anchorPaneChildren,LayoutId.DEVICE_IMAGE_VIEW);
                                //头部bar
                                HBox headBar = (HBox) NodeUtil.findById(anchorPaneChildren,LayoutId.HEAD_BAR);
                                ObservableList<Node> headBarChildren = headBar.getChildren();

                                //左边设置名称
                                Label leftLabel = (Label) NodeUtil.findById(headBarChildren, LayoutId.HEAD_LEFT_LABEL);
                                leftLabel.setText(re.getName());
                                /*//右边绑定关闭该实例
                                Label rightLabel = (Label) NodeUtil.findById(headBarChildren,LayoutId.HEAD_RIGHT_LABLE);
                                rightLabel.setOnMouseClicked(e->{
                                    removeOneDevice(re.getName(),imageMap);
                                });*/

                                //底部bar
                                HBox bottomBar = (HBox) NodeUtil.findById(anchorPaneChildren, LayoutId.BOTTOM_BAR);
                                ObservableList<Node> bottomBarChildren = bottomBar.getChildren();
                                bindAllBottomBarLabelClickEvent(bottomBarChildren,re);

                                //存放图片实例
                                imageView.setFitWidth(screenZoom * screenWidth);
                                imageView.setFitHeight(screenZoom * screenHeight);
                                imageViewDto.setImageView(imageView);
                                imageViewDto.setImageViewIndex(i);
                                imageView.setImage(fxImage);

                                //存放device实例
                                imageViewDto.setDevice(re);
                                //存放包裹imageView的实例
                                anchorPane.setPrefHeight(imageView.getFitHeight() + 50);
                                imageViewDto.setAnchorPane(anchorPane);
                                imageMap.put(re.getName(),imageViewDto);

                                //绑定drag事件
                                bindImageViewDragEvent(imageView);

                                //绑定drag release 和 普通release事件
                                bindImageViewDragReleaseAndNormalReleaseEvent(imageView,re);

                                Platform.runLater(() -> flowPane.getChildren().add(anchorPane));
                            }else{
                                if(imageInstance.isOffLine())continue;
                                imageInstance.getImageView().setImage(fxImage);
                            }
                            i++;
                        }
                        updateLabelDeviceNum();
                    }else{
                        removeAllDevice(imageMap);
                        updateLabelDeviceNum();
                    }
                }catch(Exception e){
                    logger.error(e);
                }
                return null;
            }
        };
    }

    public static ConcurrentHashMap<String, ImageDeviceDto> getImageMap() {
        return imageMap;
    }

    public static List<IDevice> getiDevices() {
        return iDevices;
    }

    public static void setIsConnectionAll(boolean isConnectionAll) {
        ScreenMonitorService.isConnectionAll = isConnectionAll;
    }

    public static void setAll(boolean all) {
        isAll = all;
    }

    public void setAccordion(Label label) {
        this.label = label;
    }


    private void updateLabelDeviceNum(){
        Platform.runLater(() -> {
            label.setText("现在连接设备" + imageMap.size() + "台");
        });
    }

    private void removeOfflineDevice(ConcurrentHashMap<String,ImageDeviceDto> imageMap, List<IDevice> iDevices){
        imageMap.keySet().stream().filter(key -> !iDevices.contains(imageMap.get(key).getDevice())).forEach(key -> {
            AnchorPane idd = imageMap.get(key).getAnchorPane();
            Platform.runLater(() -> flowPane.getChildren().remove(idd));
            imageMap.remove(key);
        });
    }

    private void removeAllDevice(ConcurrentHashMap<String,ImageDeviceDto> imageMap){
        for (String key : imageMap.keySet()) {
            AnchorPane idd = imageMap.get(key).getAnchorPane();
            Platform.runLater(() -> flowPane.getChildren().remove(idd));
            logger.info("===== idd:" + idd + "===== del : " + key);
        }
        imageMap.clear();
    }

    private void removeOneDevice(String deviceName,ConcurrentHashMap<String,ImageDeviceDto> imageMap){
        ImageDeviceDto removeObj = imageMap.get(deviceName);
        removeObj.setOffLine(true);
        Platform.runLater(() -> {
            removeObj.getImageView().setImage(null);
        });
    }

    private void bindBottomBarLabelClickEvent(List<Node> bottomBarLabelList,String id,EventHandler<? super MouseEvent> event){
        Node node = NodeUtil.findById(bottomBarLabelList, id);
        if(node != null){
            node.setOnMouseClicked(event);
        }
    }

    private void bindAllBottomBarLabelClickEvent(ObservableList<Node> bottomBarChildren,IDevice re){
        //返回home功能
        //绑定回主界面方法
        bindBottomBarLabelClickEvent(bottomBarChildren, LayoutId.LABEL_BACK_HOME,event -> {
            if(isAll){
                iDevices.forEach(ClickHelpUtil::backHome);
            }else{
                ClickHelpUtil.backHome(re);
            }
        });
        //找到指定device
        bindBottomBarLabelClickEvent(bottomBarChildren,LayoutId.FIND_DEVICE_BUTTON,event -> {
            ImageDeviceDto dto = imageMap.get(re.getName());
            // TODO: 2016/11/15 唤起预置的app
        });
    }


    private void bindImageViewDragEvent(ImageView imageView){
        imageView.setOnDragDetected(e->{
            eventFlag[0] = true;
            Point point = new Point((int) e.getX(), (int) e.getY());
            Point realPoint = ClickHelpUtil.getRealPoint(point);
            tempX = realPoint.getX();
            tempY = realPoint.getY();
            logger.info("dragged detected " + tempX + " " + tempY);
            e.consume();
        });
    }

    private void bindImageViewDragReleaseAndNormalReleaseEvent(ImageView imageView,IDevice iDevice){
        //设置点击事件
        //当拖曳的时候释放为拖曳
        //非拖曳状态为点击事件
        imageView.setOnMouseReleased(e -> {
            if(!eventFlag[0]){
                Point point = new Point((int) e.getX(), (int) e.getY());
                Point realPoint = ClickHelpUtil.getRealPoint(point);
                try {
                    if(isAll){
                        for (IDevice device : iDevices) {
                            ClickHelpUtil.touch(device,realPoint.getX(),realPoint.getY());
                        }
                    }else{
                        ClickHelpUtil.touch(iDevice,realPoint.getX(),realPoint.getY());
                    }
                } catch (Exception ex) {
                    logger.error("=====" + ex);
                }
                e.consume();
            }else{
                try {
                    Point point1 = new Point((int) e.getX(), (int) e.getY());
                    Point realPoint1 = ClickHelpUtil.getRealPoint(point1);
                    if(isAll){
                        for (IDevice device : iDevices) {
                            ClickHelpUtil.swipe(device,tempX,tempY,realPoint1.getX(),realPoint1.getY());
                        }
                    }else{
                        ClickHelpUtil.swipe(iDevice,tempX,tempY,realPoint1.getX(),realPoint1.getY());
                    }
                    e.consume();
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            eventFlag[0] = false;
                        }
                    },500);
                }catch (Exception ex){
                    logger.error("=====" + ex);
                }
            }
        });
    }

}
