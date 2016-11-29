package com.xlingmao.controller;

import java.io.IOException;
import java.util.HashSet;

import com.xlingmao.App;
import com.xlingmao.service.UpdateWechatPhoneGroupService;
import com.xlingmao.util.ServiceSingleUtil;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class WechatManager extends Stage{

	private BorderPane borderPane;

	private HashSet<String> selectedPhoneSet; 

	public void shows(){
		try {
			initRootButtons();
			showCenter();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initRootButtons() throws IOException{
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(App.class.getResource("/view/initView/RootView.fxml"));
		borderPane = loader.load();
		RootViewController controller = loader.getController();
		controller.getAddressAddButton().setStyle(RootViewController.ButtonStyle);
		this.selectedPhoneSet = controller.getSelectedPhoneSet();
		UpdateWechatPhoneGroupService groupService = (UpdateWechatPhoneGroupService)ServiceSingleUtil.getService(UpdateWechatPhoneGroupService.class);
		groupService.setSelectedPhoneNum(controller.getSelectedPhoneNum());
		groupService.setSelectedPhoneSet(controller.getSelectedPhoneSet());
		groupService.setSelectPhoneBox(controller.getSelectPhoneBox());
		Scene scene = new Scene(borderPane);
		setScene(scene);
		show();
	}
	
	private void showCenter() throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(App.class.getResource("/view/initView/AddressAddView.fxml"));
        AnchorPane addressAdd = loader.load();
        AddressAddViewController controller = loader.getController();
        controller.setSelectedPhoneSet(selectedPhoneSet);
        borderPane.setCenter(addressAdd);
	}
}
