package com.xlingmao.service;

import java.awt.Point;
import java.util.List;
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
import javafx.geometry.Pos;
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
    private static double releaseX;
    private static double releaseY;

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

								final boolean[] testFlag = {true};
								imageViewDto.setAnchorPane(anchorPane);
								imageMap.put(re.getName(),imageViewDto);
								//设置点击事件 TODO: 2016/11/7  封装
								imageView.setOnMousePressed(e -> {
									System.out.println(testFlag[0]);
									if(testFlag[0]){
										Point point = new Point((int) e.getX(), (int) e.getY());
										Point realPoint = ClickHelpUtil.getRealPoint(point);
										try {
											ClickHelpUtil.touch(re,realPoint.getX(),realPoint.getY());
											logger.info("x : " + realPoint.getX() + ", y : " + realPoint.getY());
										} catch (Exception e1) {
											logger.error("===== touch fail");
										}
									}
								});

                                imageView.setOnDragDetected(e->{
									testFlag[0] = false;
                                    Point point = new Point((int) e.getX(), (int) e.getY());
                                    Point realPoint = ClickHelpUtil.getRealPoint(point);
                                    tempX = realPoint.getX();
                                    tempY = realPoint.getY();
                                    System.out.println("dragged detected " + tempX + " " + tempY);
                                    imageView.setOnMouseMoved(f->{
                                        Point point2 = new Point((int) f.getX(), (int) f.getY());
                                        Point realPoint2 = ClickHelpUtil.getRealPoint(point2);
                                        releaseX = realPoint2.getX();
                                        releaseY = realPoint2.getY();
                                        System.out.println("dragged move " + releaseX + " " + releaseY);
                                        imageView.setOnMouseMoved(null);
                                    });
                                });
                                imageView.setOnMouseReleased(e->{
                                    try {
                                        imageView.setOnMouseClicked(null);
                                        re.executeShellCommand("input swipe " + tempX + " " + tempY + " " + releaseX + " " + releaseY + " 200" , new CommandOutputCapture());
                                        System.out.println("input swipe " + tempX + " " + tempY + " " + releaseX + " " + releaseY + " 200");
                                    	testFlag[0] = true;
									}catch (Exception ex){
                                        System.out.println(ex);
                                    }
                                });


                                /*imageView.setOnMouseReleased(e->{
                                    try {
                                        Point point = new Point((int) e.getX(), (int) e.getY());
                                        Point realPoint = ClickHelpUtil.getRealPoint(point);
                                        re.executeShellCommand("input swipe " + tempX +" " + tempY + " " + realPoint.getX() + " " + realPoint.getY() + " 200",new CommandOutputCapture());
                                        logger.info("release x : " + realPoint.getX() + ", y : " + realPoint.getY() + "tempxy  " +tempX + tempY);
                                    } catch (Exception e1) {
                                        logger.error("===== touch fail");
                                    }
                                });*/



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
