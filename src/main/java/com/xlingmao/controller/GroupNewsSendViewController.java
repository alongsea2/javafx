package com.xlingmao.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class GroupNewsSendViewController extends BaseViewController{
	
	@FXML
	private ImageView selectedImage;
	
	/**
	 * 每次点击间隔时间开始
	 */
	@FXML
	private TextField sleepTimeStart;
	
	/**
	 * 每次点击间隔时间结束
	 */
	@FXML
	private TextField sleepTimeEnd;
	
	@FXML
	private ToggleGroup sendGroup;
	
	/**
	 * 单个发送
	 */
	@FXML
	private RadioButton signleSend;
	
	/**
	 * 群发助手
	 */
	@FXML
	private RadioButton groupHelpSend;
	
	/**
	 * 群聊组群发
	 */
	@FXML
	private RadioButton groupTeamSend;
	
	
	/**
	 * 指定标签发消息
	 */
	@FXML
	private RadioButton designatedTabSend;
	
	/**
	 * 群组名称
	 */
	@FXML
	private TextField groupTeamName;
	
	/**
	 * 群发数量
	 */
	@FXML
	private TextField sendNum;
	
	/**
	 * 是否循环发送
	 */
	@FXML
	private CheckBox isLoopSend;
	
	@FXML
	private ToggleGroup contentGroup;
	
	/**
	 * 消息内容
	 */
	@FXML
	private RadioButton contentText;
	
	/**
	 * 需要发送的图片
	 */
	@FXML
	private RadioButton contentPic;
	
	/**
	 * 群发收藏记录
	 */
	@FXML
	private RadioButton groupSendCollection;
	
	/**
	 * 推送名片
	 */
	@FXML
	private RadioButton pushBusinessCard;
	
	@FXML
	private ToggleGroup businessCardGroup;
	
	/**
	 * 公众号名片
	 */
	@FXML
	private RadioButton gzhBusinessCard;
	
	/**
	 * 消息内容textarea
	 */
	@FXML
	private TextArea contentTextArea;
	
	/**
	 * 指定发送收藏序号
	 */
	@FXML
	private TextField collectionNum;
	
	/**
	 * 微信好友名称或公众号名称
	 */
	@FXML
	private TextField friendsOrGzhName;
	
	private File image;
	
	@FXML
	private void handleImportImage() {
		Stage stage = new Stage();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("导入本地图片");
		File file = fileChooser.showOpenDialog(stage);
		initImage(file);
	}
	
	private void initImage(File file) {
		if(null != file){
			image = file;
			try {
				selectedImage.setImage(new Image(new FileInputStream(file)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	private void handleGroupNewsSend() {
		if(isnotSelectedPhone()){
			return;
		}
		JSONObject json = new JSONObject();
		if(contentText.isSelected()){
			if(StringUtils.isBlank(contentTextArea.getText())){
				showErrorDialog("群发消息", "请填写消息内容");
				return;
			}
		}
		if(contentPic.isSelected()){
			if(null == image){
				showErrorDialog("群发消息", "请选择图片");
				return;
			}else{
				String imageName = "groupnewssend." + getFileExtName(image);
				String imageFullName = getCurrentPcPath() + imageName;
				try {
					Files.copy(Paths.get(image.getPath()), Paths.get(imageFullName), StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					e.printStackTrace();
				}  
				json.put("imageName", imageName);
				execPushFileCommondWithDevices(imageFullName);
				sleep(2000);
			}
		}
		if(groupSendCollection.isSelected()){
			if(StringUtils.isBlank(collectionNum.getText())){
				showErrorDialog("群发消息", "请指定发送收藏序号");
				return;
			}
		}
		if(pushBusinessCard.isSelected()){
			if(StringUtils.isBlank(friendsOrGzhName.getText())){
				showErrorDialog("群发消息", "请输入微信好友或公众号名称");
				return;
			}
		}
		if(StringUtils.isNotBlank(sleepTimeStart.getText())){
			json.put("sleepTimeStart", Integer.parseInt(sleepTimeStart.getText()));
		}else{
			json.put("sleepTimeStart", 3);
		}
		if(StringUtils.isNotBlank(sleepTimeEnd.getText())){
			json.put("sleepTimeEnd", Integer.parseInt(sleepTimeEnd.getText()));
		}else{
			json.put("sleepTimeEnd", 5);
		}
		json.put("signleSend", signleSend.isSelected());
		json.put("groupHelpSend", groupHelpSend.isSelected());
		json.put("groupTeamSend", groupTeamSend.isSelected());
		json.put("designatedTabSend", designatedTabSend.isSelected());
		json.put("groupTeamName", groupTeamName.getText());
		if(StringUtils.isNotBlank(sendNum.getText())){
			json.put("sendNum", Integer.parseInt(sendNum.getText()));
		}else{
			json.put("sendNum", 200);
		}
		json.put("isLoopSend", isLoopSend.isSelected());
		json.put("contentText", contentText.isSelected());
		json.put("contentPic", contentPic.isSelected());
		json.put("groupSendCollection", groupSendCollection.isSelected());
		json.put("pushBusinessCard", pushBusinessCard.isSelected());
		json.put("contentTextArea", contentTextArea.getText());
		if(StringUtils.isNotBlank(collectionNum.getText())){
			json.put("collectionNum", Integer.parseInt(collectionNum.getText()));
		}
		json.put("gzhBusinessCard", gzhBusinessCard.isSelected());
		json.put("friendsOrGzhName", friendsOrGzhName.getText());
		writeAndPushFileWithDevices("groupnewssend.txt", json.toString());
		sleep(2000);
		execCommondWithDevices("uiautomator runtest auto.jar -c xlm.ac.wx.autocase.GroupNewSend");
		showInfoDialog("群发消息", "添加到执行任务列表成功");
	}


}
