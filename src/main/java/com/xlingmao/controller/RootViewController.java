package com.xlingmao.controller;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import com.xlingmao.common.thread.DeviceSocketThread;
import com.xlingmao.dto.ImageDeviceDto;
import com.xlingmao.model.GroupModel;
import com.xlingmao.model.GroupTreeModel;
import com.xlingmao.service.GroupService;
import com.xlingmao.service.ScreenMonitorService;
import com.xlingmao.util.ServiceSingleUtil;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.Label;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
public class RootViewController {
	
	@FXML
	private BorderPane borderPane;
	
	@FXML
	private VBox selectPhoneBox;
	
	@FXML
	private Label selectedPhoneNum;
	
	
	private HashSet<String> selectedPhoneSet = new HashSet<String>();
	

	public HashSet<String> getSelectedPhoneSet() {
		return selectedPhoneSet;
	}
	
	@FXML
	private Button addressAddButton;
	

	public Button getAddressAddButton() {
		return addressAddButton;
	}

	@FXML
	private Button friendsNewsButton;
	
	@FXML
	private Button forwardLinkButton;
	
	@FXML
	private Button commentSelfButton;
	
	@FXML
	private Button accutareAddButton;
	
	@FXML
	private Button locationStreetButton;
	
	@FXML
	private Button sayHelloButton;
	
	@FXML
	private Button groupNewsSendButton;
	
	@FXML
	private Button acceptRequestButton;
	
	public static final String ButtonStyle = "-fx-background-color:#AEEEEE;";

	@FXML
	private void handleAddressAdd() throws IOException{
		 handleButtonSelected("addressAddButton");
		 FXMLLoader loader = new FXMLLoader();
	     loader.setLocation(RootViewController.class.getResource("/view/initView/AddressAddView.fxml"));
	     AnchorPane addressAdd = (AnchorPane) loader.load();
	     AddressAddViewController controller = loader.getController();
	     controller.setSelectedPhoneSet(selectedPhoneSet);
	     borderPane.setCenter(addressAdd);
	}
	
	@FXML
	private void handleFriendsNews() throws IOException{
		handleButtonSelected("friendsNewsButton");
		FXMLLoader loader = new FXMLLoader();
	    loader.setLocation(RootViewController.class.getResource("/view/initView/FriendsNewsView.fxml"));
	    AnchorPane friendsNews = (AnchorPane) loader.load();
	    //向controlller传递选中的手机
	    FriendsNewsViewController controller = loader.getController();
	    controller.setSelectedPhoneSet(selectedPhoneSet);
	    borderPane.setCenter(friendsNews);
	}
	
	@FXML
	private void handleForwardLink() throws IOException{
		handleButtonSelected("forwardLinkButton");
		FXMLLoader loader = new FXMLLoader();
	    loader.setLocation(RootViewController.class.getResource("/view/initView/ForwardLinkView.fxml"));
	    AnchorPane forwardLink = (AnchorPane) loader.load();
	    ForwardLinkViewController controller = loader.getController();
	    controller.setSelectedPhoneSet(selectedPhoneSet);
	    borderPane.setCenter(forwardLink);
	}
	
	@FXML
	private void handleCommentSelf() throws IOException{
		handleButtonSelected("commentSelfButton");
		FXMLLoader loader = new FXMLLoader();
	    loader.setLocation(RootViewController.class.getResource("/view/initView/CommentSelfView.fxml"));
	    AnchorPane locationStreet = (AnchorPane) loader.load();
	    CommentSelfViewController controller = loader.getController();
	    controller.setSelectedPhoneSet(selectedPhoneSet);
	    borderPane.setCenter(locationStreet);
	}
	
	@FXML
	private void handleAccutareAdd() throws IOException{
		handleButtonSelected("accutareAddButton");
		FXMLLoader loader = new FXMLLoader();
	    loader.setLocation(RootViewController.class.getResource("/view/initView/AccurateAddView.fxml"));
	    AnchorPane accutareAdd = (AnchorPane) loader.load();
	    AccurateAddViewController controller = loader.getController();
	    controller.setSelectedPhoneSet(selectedPhoneSet);
	    borderPane.setCenter(accutareAdd);
	}
	
	@FXML
	private void handleLocationStreet() throws IOException{
		handleButtonSelected("locationStreetButton");
		FXMLLoader loader = new FXMLLoader();
	    loader.setLocation(RootViewController.class.getResource("/view/initView/LocationStreetView.fxml"));
	    AnchorPane locationStreet = (AnchorPane) loader.load();
	    LocationStreetViewController controller = loader.getController();
	    controller.setSelectedPhoneSet(selectedPhoneSet);
	    borderPane.setCenter(locationStreet);
	}
	
	@FXML
	private void handleSayHello() throws IOException{
		handleButtonSelected("sayHelloButton");
		FXMLLoader loader = new FXMLLoader();
	    loader.setLocation(RootViewController.class.getResource("/view/initView/SayHelloView.fxml"));
	    AnchorPane locationStreet = (AnchorPane) loader.load();
	    SayHelloViewController controller = loader.getController();
	    controller.setSelectedPhoneSet(selectedPhoneSet);
	    borderPane.setCenter(locationStreet);
	}
	
	/*@FXML
	private void handleFriendsAdd() throws IOException{
		FXMLLoader loader = new FXMLLoader();
	    loader.setLocation(RootViewController.class.getResource("/view/initView/FriendsAddView.fxml"));
	    AnchorPane locationStreet = (AnchorPane) loader.load();
	    FriendsAddViewController controller = loader.getController();
	    controller.setSelectedPhoneSet(selectedPhoneSet);
	    borderPane.setCenter(locationStreet);
	}*/
	
	@FXML
	private void handleGroupNewsSend() throws IOException{
		handleButtonSelected("groupNewsSendButton");
		FXMLLoader loader = new FXMLLoader();
	    loader.setLocation(RootViewController.class.getResource("/view/initView/GroupNewsSendView.fxml"));
	    AnchorPane locationStreet = (AnchorPane) loader.load();
	    GroupNewsSendViewController controller = loader.getController();
	    controller.setSelectedPhoneSet(selectedPhoneSet);
	    borderPane.setCenter(locationStreet);
	}
	
	@FXML
	private void handleAcceptRequest() throws IOException{
		handleButtonSelected("acceptRequestButton");
		FXMLLoader loader = new FXMLLoader();
	    loader.setLocation(RootViewController.class.getResource("/view/initView/AcceptRequestView.fxml"));
	    AnchorPane locationStreet = (AnchorPane) loader.load();
	    AcceptRequestViewController controller = loader.getController();
	    controller.setSelectedPhoneSet(selectedPhoneSet);
	    borderPane.setCenter(locationStreet);
	}
	
	@FXML
	private void handleSelectAll() {
		ObservableList<Node> rootItems = selectPhoneBox.getChildren();
		for (Node node : rootItems) {
			@SuppressWarnings("unchecked")
			TreeView<String> tree = (TreeView<String>)node;
			CheckBoxTreeItem<String> rootItem = (CheckBoxTreeItem<String>) tree.getRoot();
			rootItem.setSelected(true);
		}
	}
	
	@FXML
	private void handleUnSelectAll() {
		ObservableList<Node> rootItems = selectPhoneBox.getChildren();
		for (Node node : rootItems) {
			@SuppressWarnings("unchecked")
			TreeView<String> tree = (TreeView<String>)node;
			CheckBoxTreeItem<String> rootItem = (CheckBoxTreeItem<String>) tree.getRoot();
			rootItem.setSelected(false);
		}
	}
	
	@FXML
	private void initialize(){
		List<GroupModel> models = getAllModels();
		for (GroupModel groupModel : models) {
			CheckBoxTreeItem<String> rootItem = new CheckBoxTreeItem<String>(groupModel.getGroupName());
			rootItem.setExpanded(true);
			TreeView<String> tree = new TreeView<String>(rootItem);  
			tree.setEditable(true);
			tree.setCellFactory(CheckBoxTreeCell.forTreeView());
			List<GroupTreeModel> groupTreeModels = groupModel.getGroupTreeModel();
			for (GroupTreeModel groupTreeModel : groupTreeModels) {
				ImageDeviceDto deviceDto = DeviceSocketThread.getImageMap().get(groupTreeModel.getDeviceName());
				if(null != deviceDto && !deviceDto.isOffLine()){
					CheckBoxTreeItem<String> checkBoxTreeItem = new CheckBoxTreeItem<String>(groupTreeModel.getMappingName());
					checkBoxTreeItem.selectedProperty().addListener(new ChangeListener<Boolean>() {
						 public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
							 if (newValue) {
								 selectedPhoneSet.add(checkBoxTreeItem.valueProperty().get());
					         }else{
					        	 selectedPhoneSet.remove(checkBoxTreeItem.valueProperty().get());
					         }
							 selectedPhoneNum.setText(selectedPhoneSet.size() + "");
					     }
						 });
					rootItem.getChildren().add(checkBoxTreeItem);
				}
			}
			tree.setRoot(rootItem);
			tree.setShowRoot(true);
			selectPhoneBox.getChildren().add(tree);
		}
	}
	
	private List<GroupModel> getAllModels(){
		GroupService groupService = (GroupService)ServiceSingleUtil.getService(GroupService.class);
		List<GroupModel> models = groupService.buildGroupTree();
		return models;
	}


	public VBox getSelectPhoneBox() {
		return selectPhoneBox;
	}


	public Label getSelectedPhoneNum() {
		return selectedPhoneNum;
	}
	
	private void handleButtonSelected(String buttonName){
		addressAddButton.setStyle("");
		friendsNewsButton.setStyle("");
		forwardLinkButton.setStyle("");
		commentSelfButton.setStyle("");
		accutareAddButton.setStyle("");
		locationStreetButton.setStyle("");
		sayHelloButton.setStyle("");
		groupNewsSendButton.setStyle("");
		acceptRequestButton.setStyle("");
		if("addressAddButton".equals(buttonName)){
			addressAddButton.setStyle(ButtonStyle);
		}else if("friendsNewsButton".equals(buttonName)){
			friendsNewsButton.setStyle(ButtonStyle);
		}else if("forwardLinkButton".equals(buttonName)){
			forwardLinkButton.setStyle(ButtonStyle);
		}else if("commentSelfButton".equals(buttonName)){
			commentSelfButton.setStyle(ButtonStyle);
		}else if("accutareAddButton".equals(buttonName)){
			accutareAddButton.setStyle(ButtonStyle);
		}else if("locationStreetButton".equals(buttonName)){
			locationStreetButton.setStyle(ButtonStyle);
		}else if("sayHelloButton".equals(buttonName)){
			sayHelloButton.setStyle(ButtonStyle);
		}else if("groupNewsSendButton".equals(buttonName)){
			groupNewsSendButton.setStyle(ButtonStyle);
		}else if("acceptRequestButton".equals(buttonName)){
			acceptRequestButton.setStyle(ButtonStyle);
		}
	}
}
