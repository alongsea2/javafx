package com.xlingmao.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.xlingmao.common.Address;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AddressAddViewController extends BaseViewController{

	@FXML
	private TableView<Address> addressTable;
	
	@FXML
	private TableColumn<Address, String> numberColumn;
	
	@FXML
	private TableColumn<Address, String> phoneColumn;
	
	@FXML
	private TableColumn<Address, String> nameColumn;
	
	/**
	 * 是否清空通讯录
	 */
	@FXML
	private CheckBox emptyAddreses;
	
	/**
	 * 导入几个号码
	 */
	@FXML
	private TextField importAddressNum;
	
	/**
	 * 每个手机添加几次
	 */
	@FXML
	private TextField addTimes;
	
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
	private ToggleGroup friendsFromGroup;
	
	@FXML
	private ToggleGroup addFromGroup;
	
	/**
	 * 新朋友推荐
	 */
	@FXML
	private RadioButton newFriendsReCommmond;
	
	/**
	 * 手机联系人
	 */
	@FXML
	private RadioButton phoneAddresses;
	
	/**
	 * 清除已添加
	 */
	@FXML
	private RadioButton clearHasAdd;
	
	/**
	 * 清除等待添加
	 */
	@FXML
	private RadioButton clearWaitAdd;
	
	/**
	 * 直接点击添加
	 */
	@FXML
	private RadioButton noUseAuthCode;
	
	@FXML
	private TextField authInfo;

	@FXML
	private void handleImportAddress() {
		Stage stage = new Stage();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("导入联系人");
		File file = fileChooser.showOpenDialog(stage);
		tableDataInit(file);
	}
	
	@FXML
	private void handleEmptyAddress() {
		addressTable.setItems(null);
	}
	
	private void tableDataInit(File file) {
		if(null != file){
			ObservableList<Address> items = FXCollections.observableArrayList();
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(file));
				String line = null;
				Address address = null;
				while ((line = br.readLine()) != null) {
					String[] data = line.split(",");
					address = new Address(data[0],data[1],data[2]);
					items.add(address);
				}
			} catch (Exception e) {
				e.printStackTrace();
				showErrorDialog("导入本地号码文件", "导入失败");
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			addressTable.setItems(items);
		}
		numberColumn.setCellValueFactory(cellData -> cellData.getValue().numberProperty());
		phoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
		nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
	}
	
	@FXML
	private void handleImportAddressToPhone(){
		if(isnotSelectedPhone()){
			return;
		}
		ObservableList<Address> addresses = addressTable.getItems();
		if(null != addresses && addresses.size() > 0){
			String fileName = "contacts.vcf";
			String filePath = getCurrentPcPath() + fileName;
			BufferedWriter writer = null;
			try {
				FileWriter file = new FileWriter(filePath);
				writer = new BufferedWriter(file);
				for (int i=0;i < addresses.size();i++ ) {
					if(i < Integer.parseInt(importAddressNum.getText())){
						writeContactsFile(writer,addresses.get(i));
					}
				}
				writer.flush();  
				writer.close();
			}catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			execPushFileCommondWithDevices(filePath);
			sleep(2000);
			JSONObject json = new JSONObject();
			json.put("emptyAddreses", emptyAddreses.isSelected());
			json.put("fileName", fileName);
			writeAndPushFileWithDevices("importaddress.txt", json.toString());
			sleep(2000);
			execCommondWithDevices("uiautomator runtest auto.jar -c xlm.ac.wx.autocase.ImportAddress");
	        showInfoDialog("导入通讯录", "添加到执行任务列表成功");
		}else{
			showErrorDialog("导入通讯录", "请先导入本地数据");
		}
	}
	
	private void writeContactsFile(BufferedWriter writer,Address address) throws IOException{
		writer.write("BEGIN:VCARD");
		writer.newLine();
		writer.write("VERSION:2.1");
		writer.newLine();
		writer.write("X_SECRET_COOLPAD:0");
		writer.newLine();
		writer.write("N:;" + address.getName() +";;;");
		writer.newLine();
		writer.write("FN:" + address.getName());
		writer.newLine();
		writer.write("TEL;CELL;PREF:" + address.getPhone());
		writer.newLine();
		writer.write("X-STAR:0");
		writer.newLine();
		writer.write("END:VCARD");
		writer.newLine();
	}
	
	@FXML
	private void handleAddFriends(){
		if(isnotSelectedPhone()){
			return;
		}
		if(!noUseAuthCode.isSelected() && StringUtils.isBlank(authInfo.getText())){
			showErrorDialog("添加通讯录好友", "请填写验证信息");
			return;
		}
		//if(StringUtils.isNotBlank(addTimes.getText())){
			//for(int i=0;i < Integer.parseInt(addTimes.getText());i++){
				JSONObject json = new JSONObject();
				if(StringUtils.isNotBlank(addTimes.getText())){
					json.put("addTimes", Integer.parseInt(addTimes.getText()));
				}else{
					json.put("addTimes", 5);
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
				if(StringUtils.isNotBlank(authInfo.getText())){
					json.put("authInfo", authInfo.getText());
				}
				json.put("newFriendsReCommmond", newFriendsReCommmond.isSelected());
				json.put("phoneAddresses", phoneAddresses.isSelected());
				json.put("clearHasAdd", clearHasAdd.isSelected());
				json.put("clearWaitAdd", clearWaitAdd.isSelected());
				json.put("noUseAuthCode", noUseAuthCode.isSelected());
				writeAndPushFileWithDevices("addressadd.txt",json.toString());
				sleep(2000);
				execCommondWithDevices("uiautomator runtest auto.jar -c xlm.ac.wx.autocase.AddressAdd");
				/*if(StringUtils.isNotBlank(sleepTimeStart.getText()) && StringUtils.isNotBlank(sleepTimeEnd.getText())){
					int time = RandomUtils.nextInt(Integer.parseInt(sleepTimeStart.getText()), Integer.parseInt(sleepTimeEnd.getText()));
					sleep(time * 1000);
				}
				execCommond("adb shell input keyevent 3");*/
			//}
		//}
		showInfoDialog("添加通讯录好友", "添加到执行任务列表成功");
	}
	
}
