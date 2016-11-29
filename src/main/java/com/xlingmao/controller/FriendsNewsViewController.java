package com.xlingmao.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.xlingmao.util.ZipUtil;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FriendsNewsViewController extends BaseViewController{
	
	/**
	 * 图片集
	 */
	@FXML
	private GridPane grid;
	
	/**
	 * 文案信息
	 */
	@FXML
	private TextArea say;
	
	@FXML
	private ToggleGroup sendTypeGroup;
	
	/**
	 * 把文字内容追加到评论里
	 */
	@FXML
	private CheckBox isAddComment;
	
	/**
	 * 标准模式
	 */
	@FXML
	private RadioButton sendStandard;
	
	/**
	 * 快速模式
	 */
	@FXML
	private RadioButton sendQuick;
	
	/**
	 * 是否对图片进行伪原创操作
	 */
	@FXML
	private CheckBox isCopyOperation;
	
	
	private List<File> images = new ArrayList<File>();
	
	private int x = 0;
	
	private int y = 0;
	
	private int z = 1;
	
	@FXML
	private void handleImportImages() {
		if(z >= 10){
			showErrorDialog("添加图片", "最多支持9张图片");
		}else{
			Stage stage = new Stage();
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("导入图片");
			List<File> files = fileChooser.showOpenMultipleDialog(stage);
			try {
				initImageViews(files);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	private void delAllImages() {
		x = 0;
		y = 0;
		z = 1;
		images = new ArrayList<File>();
		grid.getChildren().clear();
	}
	
	@FXML
	private void handleSendPicture(){
		if(isnotSelectedPhone()){
			return;
		}
		if(isAddComment.isSelected()){
			if(StringUtils.isBlank(say.getText())){
				showErrorDialog("文字内容追加到评论", "请输入文案信息");
				return;
			}
		}
		if(z > 1){
			String dirName = getCurrentPcPath() + "friendsnews/";
			File fileDir = new File(dirName);
			if(fileDir.exists()){
				deleteAllFilesOfDir(fileDir);
			}
			fileDir.mkdir();
			FileInputStream fis = null;
			FileOutputStream fos = null;
			for(File file : images){
				try {
					fis = new FileInputStream(file);
					fos = new FileOutputStream(dirName + file.getName());
					int len = 0;
			        byte[] buf = new byte[1024];
			        while ((len = fis.read(buf)) != -1) {
			            fos.write(buf, 0, len);
			        }
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					if (fis != null) {
						try {
							fis.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (fos != null) {
						try {
							fos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			JSONObject json = new JSONObject();
			json.put("say", say.getText());
			json.put("isAddComment", isAddComment.isSelected());
			json.put("sendStandard", sendStandard.isSelected());
			json.put("sendQuick", sendQuick.isSelected());
			json.put("type", "pic");
			writeFile(dirName, "friendsnewssay.txt", json.toString());
			ZipUtil.zip(dirName);
	        execPushFileCommondWithDevices(getCurrentPcPath() + "friendsnews.zip");
	        sleep(2000);
	        execCommondWithDevices("uiautomator runtest auto.jar -c xlm.ac.wx.autocase.FriendsNews");
	        showInfoDialog("发布图片", "添加到执行任务列表成功");
		}else{
			showErrorDialog("发布图片", "请添加图片");
		}
	}
	
	@FXML
	private void handleSendPictureAndText(){
		if(isnotSelectedPhone()){
			return;
		}
		if(StringUtils.isNotBlank(say.getText()) && z > 1){
			String dirName = getCurrentPcPath() + "friendsnews/";
			File fileDir = new File(dirName);
			if(fileDir.exists()){
				deleteAllFilesOfDir(fileDir);
			}
			fileDir.mkdir();
			FileInputStream fis = null;
			FileOutputStream fos = null;
			for(File file : images){
				try {
					fis = new FileInputStream(file);
					fos = new FileOutputStream(dirName + file.getName());
					int len = 0;
			        byte[] buf = new byte[1024];
			        while ((len = fis.read(buf)) != -1) {
			            fos.write(buf, 0, len);
			        }
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					if (fis != null) {
						try {
							fis.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (fos != null) {
						try {
							fos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			JSONObject json = new JSONObject();
			json.put("say", say.getText());
			json.put("isAddComment", isAddComment.isSelected());
			json.put("sendStandard", sendStandard.isSelected());
			json.put("sendQuick", sendQuick.isSelected());
			json.put("type", "picPlusText");
			writeFile(dirName, "friendsnewssay.txt", json.toString());
			ZipUtil.zip(dirName);
	        execPushFileCommondWithDevices(getCurrentPcPath() + "friendsnews.zip");
	        sleep(2000);
	        execCommondWithDevices("uiautomator runtest auto.jar -c xlm.ac.wx.autocase.FriendsNews");
	        showInfoDialog("发布图片+文字", "添加到执行任务列表成功");
		}else{
			showErrorDialog("发布图片+文字", "请添加图片和输入文案信息");
		}
	}
	
	
	private void initImageViews(List<File> files) throws FileNotFoundException{
		if(null != files && files.size() > 0){
			for (int i = 1;i <= files.size();i++) {
				if(z < 10){
					ImageView view = new ImageView();
					view.setImage(new Image(new FileInputStream(files.get(i - 1))));
					view.setFitWidth(140);
					view.setFitHeight(100);
					grid.add(view, x, y);
					images.add(files.get(i - 1));
					if((z % 3) == 0){
						y++;
						x = 0;
					}else{
						x++;
					}
					z++;
				}
			}
		}
	}

}
