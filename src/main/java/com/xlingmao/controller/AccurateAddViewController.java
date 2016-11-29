package com.xlingmao.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.xlingmao.common.Address;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AccurateAddViewController extends BaseViewController{
	
	@FXML
	private TableView<Address> accurateTable;
	
	@FXML
	private TableColumn<Address, String> numberColumn;
	
	@FXML
	private TableColumn<Address, String> accurateColumn;
	
	@FXML
	private TableColumn<Address, String> greetColumn;
	
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
	
	private String addressText;
	
	@FXML
	private void handleImportAccurateNum() {
		Stage stage = new Stage();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("导入联系人");
		File file = fileChooser.showOpenDialog(stage);
		tableDataInit(file);
	}
	
	@FXML
	private void handleEmptyAccurateNum() {
		addressText = null;
		accurateTable.setItems(null);
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
					produceAddressText(address);
					items.add(address);
				}
			} catch (Exception e) {
				e.printStackTrace();
				showErrorDialog("导入本地文件", "导入失败");
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			accurateTable.setItems(items);
		}
		numberColumn.setCellValueFactory(cellData -> cellData.getValue().numberProperty());
		accurateColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
		greetColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
	}
	
	@FXML
	private void handleAccurateAdd() {
		if(isnotSelectedPhone()){
			return;
		}
		if(StringUtils.isBlank(addressText)){
			showErrorDialog("添加通讯录好友", "请先导入本地数据");
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
				json.put("addressText", addressText);
				writeAndPushFileWithDevices("accurateadd.txt", json.toString());
				sleep(2000);
				execCommondWithDevices("uiautomator runtest auto.jar -c xlm.ac.wx.autocase.AccurateAdd");
				/*if(StringUtils.isNotBlank(sleepTimeStart.getText()) && StringUtils.isNotBlank(sleepTimeEnd.getText())){
					int time = RandomUtils.nextInt(Integer.parseInt(sleepTimeStart.getText()), Integer.parseInt(sleepTimeEnd.getText()));
					sleep(time * 1000);
				}*/
			//}
		//}
		showInfoDialog("添加通讯录好友", "添加到执行任务列表成功");
	}

	private void produceAddressText(Address address){
		if(StringUtils.isNotBlank(addressText)){
			addressText = addressText + ";" + address.getPhone() + "," + address.getName();
		}else{
			addressText = address.getPhone() + "," + address.getName();
		}
	}
}
