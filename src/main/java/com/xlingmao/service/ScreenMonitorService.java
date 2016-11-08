package com.xlingmao.service;

import java.awt.Point;
import java.sql.Time;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import com.android.chimpchat.adb.CommandOutputCapture;
import com.android.ddmlib.IDevice;
import com.xlingmao.dto.ImageDeviceDto;
import com.xlingmao.util.ClickHelpUtil;
import com.xlingmao.util.PropertiesUtil;
import javafx.application.Platform;
import com.xlingmao.asserts.ADB;
import com.xlingmao.asserts.FBImage;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import org.apache.log4j.Logger;

public class ScreenMonitorService extends ScheduledService{

	private static final Logger logger = Logger.getLogger(ScreenMonitorService.class);
	private static ConcurrentHashMap<String,ImageDeviceDto> imageMap = new ConcurrentHashMap<>();
	private FlowPane flowPane;
	private int screenWidth = PropertiesUtil.getValueForInt("device.screen.width");
	private int screenHeight = PropertiesUtil.getValueForInt("device.screen.height");
	private double screenZoom = Double.parseDouble(PropertiesUtil.getValue("device.screen.zoom"));
	private static double tempX;
	private static double tempY;

	public void setFlowPane(FlowPane flowPane) {
		this.flowPane = flowPane;
	}

	@Override
	protected Task createTask() {
		return new Task(){
			@Override
			protected Object call() throws Exception {
				try{
					List<IDevice> iDevices = ADB.getInstance().getDevices();
					int i = 0;
					FBImage image;
					ImageDeviceDto imageViewDto;
					if( iDevices != null ) {
						//如果设备被拔掉从m	ap移除

						removeOfflineDevice(imageMap,iDevices);

						for (IDevice re : iDevices) {
							i++;
							image = ADB.getInstance().getDeviceImage(re);
							Image fxImage = SwingFXUtils.toFXImage(image,null);
							if(null == imageMap.get(re.getName())){
								//存放监控上的机器实例
								imageViewDto = new ImageDeviceDto();
								AnchorPane anchorPane = new AnchorPane();
								ImageView imageView = new ImageView(fxImage);
								//存放图片实例
								imageView.setFitWidth(screenZoom * screenWidth);
								imageView.setFitHeight(screenZoom * screenHeight);
								imageViewDto.setImageView(imageView);
								imageViewDto.setImageViewIndex(i);
								//存放device实例
								imageViewDto.setDevice(re);
								//存放包裹imageView的实例
								anchorPane.getStyleClass().add("device-img-view");
								anchorPane.setPrefHeight(imageView.getFitHeight() + 20);
								//存放小label
								FXMLLoader fxmlLoader = new FXMLLoader();
								fxmlLoader.setLocation(ScreenMonitorService.class.getResource("/view/initView/DeviceSmallButtonLayout.fxml"));
								HBox smallLabel = fxmlLoader.load();
								smallLabel.setLayoutY(imageView.getFitHeight());
								anchorPane.getChildren().addAll(imageView,smallLabel);
								imageViewDto.setAnchorPane(anchorPane);
								imageMap.put(re.getName(),imageViewDto);
								boolean[] eventFlag = {false};//[0]-->是否在drag

								imageView.setOnDragDetected(e->{
									eventFlag[0] = true;
									Point point = new Point((int) e.getX(), (int) e.getY());
									Point realPoint = ClickHelpUtil.getRealPoint(point);
									tempX = realPoint.getX();
									tempY = realPoint.getY();
									logger.info("dragged detected " + tempX + " " + tempY);
									e.consume();
								});

								//设置点击事件
								//当拖曳的时候释放为拖曳
								//非拖曳状态为点击事件
								imageView.setOnMouseReleased(e -> {
									if(!eventFlag[0]){
										Point point = new Point((int) e.getX(), (int) e.getY());
										Point realPoint = ClickHelpUtil.getRealPoint(point);
										try {
											ClickHelpUtil.touch(re,realPoint.getX(),realPoint.getY());
											logger.info("x : " + realPoint.getX() + ", y : " + realPoint.getY());
										} catch (Exception e1) {
											logger.error("===== touch fail");
										}
										e.consume();
									}else{
										try {
											Point point1 = new Point((int) e.getX(), (int) e.getY());
											Point realPoint1 = ClickHelpUtil.getRealPoint(point1);
											re.executeShellCommand("input swipe " + tempX + " " + tempY + " " + realPoint1.getX() + " " + realPoint1.getY() + " 200" , new CommandOutputCapture());
											logger.info("input swipe " + tempX + " " + tempY + " " + realPoint1.getX() + " " + realPoint1.getY() + " 200");
											e.consume();
											new Timer().schedule(new TimerTask() {
												@Override
												public void run() {
													eventFlag[0] = false;
												}
											},500);
										}catch (Exception ex){
											ex.printStackTrace();
										}
									}
								});



								Platform.runLater(() -> flowPane.getChildren().add(anchorPane));
							}else{
								ImageDeviceDto imageDeviceDto = imageMap.get(re.getName());
								imageDeviceDto.getImageView().setImage(fxImage);
							}
						}
					}else{
						removeAllDevice(imageMap);
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

	public static void setImageMap(ConcurrentHashMap<String, ImageDeviceDto> imageMap) {
		ScreenMonitorService.imageMap = imageMap;
	}

	private void removeOfflineDevice(ConcurrentHashMap<String,ImageDeviceDto> imageMap,List<IDevice> iDevices){
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

}
