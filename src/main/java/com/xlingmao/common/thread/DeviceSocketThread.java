package com.xlingmao.common.thread;

import com.android.ddmlib.IDevice;
import com.xlingmao.common.LayoutId;
import com.xlingmao.dto.ImageDeviceDto;
import com.xlingmao.entity.DeviceGroup;
import com.xlingmao.service.DeviceGroupService;
import com.xlingmao.service.GroupCheckService;
import com.xlingmao.service.ScreenMonitorService;
import com.xlingmao.util.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dell on 2016/11/29.
 */
public class DeviceSocketThread implements Runnable{
    private static final Logger logger = Logger.getLogger(DeviceSocketThread.class);

    private static ConcurrentHashMap<String,ImageDeviceDto> imageMap = new ConcurrentHashMap<>();

    private IDevice device;

    private DeviceGroupService deviceGroupService = (DeviceGroupService) ServiceSingleUtil.getService(DeviceGroupService.class);

    private FlowPane flowPane;

    private int screenWidth = PropertiesUtil.getValueForInt("device.screen.width");

    private int screenHeight = PropertiesUtil.getValueForInt("device.screen.height");

    private double screenZoom = Double.parseDouble(PropertiesUtil.getValue("device.screen.zoom"));

    private double tempX;

    private double tempY;

    private boolean[] eventFlag = {false};//[0]-->是否在drag

    public DeviceSocketThread(IDevice device,FlowPane flowPane) {
        this.flowPane = flowPane;
        this.device = device;
    }

    @Override
    public void run() {
        ImageDeviceDto imageInstance = imageMap.get(device.getSerialNumber());
        if(imageInstance == null){
            ImageDeviceDto imageDeviceDto = new ImageDeviceDto();
            DeviceGroup deviceGroup = deviceGroupService.selectDeviceByName(device.getSerialNumber());
            // TODO: 2016/11/29 唤起service 服务
            //连接启动的service
            try {
                //主页面对象
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(DeviceSocketThread.class.getResource("/view/initView/DeviceSmallButtonLayout.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();
                ObservableList<Node> anchorPaneChildren = anchorPane.getChildren();
                Socket socket = connectSocket(deviceGroup.getPort());
                //图片节点
                ImageView imageView = (ImageView) NodeUtil.findById(anchorPaneChildren,LayoutId.DEVICE_IMAGE_VIEW);

                imageDeviceDto.setSocket(socket);
                imageDeviceDto.setDevice(device);
                imageDeviceDto.setPort(deviceGroup.getPort());
                imageDeviceDto.setImageView(imageView);

                //头部bar
                HBox headBar = (HBox) NodeUtil.findById(anchorPaneChildren,LayoutId.HEAD_BAR);
                ObservableList<Node> headBarChildren = headBar.getChildren();

                //左边设置名称
                Label leftLabel = (Label) NodeUtil.findById(headBarChildren, LayoutId.HEAD_LEFT_LABEL);
                Platform.runLater(()->{
                    leftLabel.setText(deviceGroup.getMappingName() == null ? device.getSerialNumber() : deviceGroup.getMappingName() );
                });

//                右边绑定关闭该实例
//                Label rightLabel = (Label) NodeUtil.findById(headBarChildren,LayoutId.HEAD_RIGHT_LABEL);
//                rightLabel.setOnMouseClicked(e->{
//                    removeOneDevice(re.getName(),imageMap);
//                });

                //底部bar
                HBox bottomBar = (HBox) NodeUtil.findById(anchorPaneChildren, LayoutId.BOTTOM_BAR);
                ObservableList<Node> bottomBarChildren = bottomBar.getChildren();
                bindAllBottomBarLabelClickEvent(bottomBarChildren,device);


                //存放图片实例
                //开始从socket读取图片数据

                imageView.setFitWidth(screenZoom * screenWidth);
                imageView.setFitHeight(screenZoom * screenHeight);
                imageView.setLayoutY(25);


                //存放device实例
                imageDeviceDto.setDevice(device);
                //存放包裹imageView的实例
                anchorPane.setPrefHeight(imageView.getFitHeight() + 50);
                imageMap.put(device.getSerialNumber(),imageDeviceDto);

                //绑定drag事件
                bindImageViewDragEvent(imageView);

                //绑定drag release 和 普通release事件
                bindImageViewDragReleaseAndNormalReleaseEvent(imageView,device);

                Platform.runLater(() -> flowPane.getChildren().add(anchorPane));
                InputStream inputStream = null;
                try {
                    inputStream = socket.getInputStream();
                    while (true){// TODO: 2016/11/29 flag 控制连接
                        //一次读的字节数
                        int fileLen = readInt(inputStream);
                        int readLength = fileLen;
                        int tempLength = 0;
                        int n;
                        byte[] bt = new byte[fileLen];
                        ByteArrayOutputStream bout = new ByteArrayOutputStream();
                        while ((n = inputStream.read(bt,0,readLength)) > 0) {
                            tempLength += n;
                            readLength = tempLength + n > fileLen ? fileLen - tempLength : readLength;
                            bout.write(bt,0,n);
                        }
                        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
                        BufferedImage image = ImageIO.read(bin);
                        Image fxImage = SwingFXUtils.toFXImage(image,null);
                        imageView.setImage(fxImage);
                        //作为简略的心跳信息返回
                        writeInt(socket.getOutputStream(),1);
                    }
                } catch (Exception e) {
                    logger.error("===== error " + e);
                    try {
                        //关闭连接
                        socket.close();
                    } catch (IOException e1) {
                        logger.error("===== socket is closed" + e1);
                    }
                }
            } catch (IOException e) {
                logger.error("===== connect " + device.getSerialNumber() + " fail " );
            }
        }else{
            try {
                Socket socket = connectSocket(imageInstance.getPort());
                InputStream inputStream = null;
                try {
                    inputStream = socket.getInputStream();
                    while (true){// TODO: 2016/11/29 flag 控制连接
                        //一次读的字节数
                        int fileLen = readInt(inputStream);
                        int readLength = fileLen;
                        int tempLength = 0;
                        int n;
                        byte[] bt = new byte[fileLen];
                        ByteArrayOutputStream bout = new ByteArrayOutputStream();
                        while ((n = inputStream.read(bt,0,readLength)) > 0) {
                            tempLength += n;
                            readLength = tempLength + n > fileLen ? fileLen - tempLength : readLength;
                            bout.write(bt,0,n);
                        }
                        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
                        BufferedImage image = ImageIO.read(bin);
                        Image fxImage = SwingFXUtils.toFXImage(image,null);
                        imageInstance.getImageView().setImage(fxImage);
                        //作为简略的心跳信息返回
                        writeInt(socket.getOutputStream(),1);
                    }
                } catch (Exception e) {
                    logger.error("===== error " + e);
                    try {
                        //关闭连接
                        socket.close();
                    } catch (IOException e1) {
                        logger.error("===== socket is closed" + e1);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static ConcurrentHashMap<String, ImageDeviceDto> getImageMap() {
        return imageMap;
    }

    private Socket connectSocket(int port) throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("localhost",port));
        return socket;
    }

    private static int readInt(InputStream in) throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        int ch3 = in.read();
        int ch4 = in.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0){
            logger.info("===== read error");
        }
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }

    private static void writeInt(OutputStream out, int v) throws IOException {
        out.write((v >>> 24) & 0xFF);
        out.write((v >>> 16) & 0xFF);
        out.write((v >>>  8) & 0xFF);
        out.write((v >>>  0) & 0xFF);
        logger.info("===== send " + v);
    }

    private void bindBottomBarLabelClickEvent(List<Node> bottomBarLabelList, String id, EventHandler<? super MouseEvent> event){
        Node node = NodeUtil.findById(bottomBarLabelList, id);
        if(node != null){
            node.setOnMouseClicked(event);
        }
    }

    private void bindAllBottomBarLabelClickEvent(ObservableList<Node> bottomBarChildren, IDevice re){
        //返回home功能
        //绑定回主界面方法
        bindBottomBarLabelClickEvent(bottomBarChildren, LayoutId.LABEL_BACK_HOME, event -> {
            /*if(isAll){
                ThreadPoolUtil.bulkRunFuncOperation(LayoutId.LABEL_BACK_HOME,iDevices);
            }else{*/
            ThreadPoolUtil.threadPoolRunFunc(LayoutId.LABEL_BACK_HOME,re);
            //}
        });
        //找到指定device
        bindBottomBarLabelClickEvent(bottomBarChildren,LayoutId.FIND_DEVICE_BUTTON,event -> {
           /* if(isAll){
                ThreadPoolUtil.bulkRunFuncOperation(LayoutId.FIND_DEVICE_BUTTON,iDevices);
            }else{*/
            ImageDeviceDto dto = imageMap.get(re.getSerialNumber());
            ThreadPoolUtil.threadPoolRunFunc(LayoutId.FIND_DEVICE_BUTTON,dto.getDevice());
            //}
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
    //
    private void bindImageViewDragReleaseAndNormalReleaseEvent(ImageView imageView,IDevice iDevice){
        //设置点击事件
        //当拖曳的时候释放为拖曳
        //非拖曳状态为点击事件
        imageView.setOnMouseReleased(e -> {
            if(!eventFlag[0]){
                Point point = new Point((int) e.getX(), (int) e.getY());
                Point realPoint = ClickHelpUtil.getRealPoint(point);
                try {
                   /* if(isAll){
                        ThreadPoolUtil.bulkClickOperation(realPoint.getX(),realPoint.getY(),iDevices);
                    }else{*/
                    ThreadPoolUtil.threadPollRunMouseEvent(realPoint.getX(),realPoint.getY(),iDevice);
                    //}
                } catch (Exception ex) {
                    logger.error("=====" + ex);
                }
                e.consume();
            }else{
                try {
                    Point point1 = new Point((int) e.getX(), (int) e.getY());
                    Point realPoint1 = ClickHelpUtil.getRealPoint(point1);
                   /* if(isAll){
                        ThreadPoolUtil.bulkSwipeOperation(realPoint1.getX(),realPoint1.getY(),tempX,tempY,iDevices);
                    }else{*/
                    ThreadPoolUtil.threadPollRunMouseEvent(realPoint1.getX(),realPoint1.getY(),tempX,tempY,iDevice);
                    //}
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
