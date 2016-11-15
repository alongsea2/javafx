package com.xlingmao.controller;


import com.xlingmao.entity.Group;
import com.xlingmao.model.GroupModel;
import com.xlingmao.model.GroupTreeModel;
import com.xlingmao.service.GroupService;
import com.xlingmao.service.ScreenMonitorService;
import com.xlingmao.util.ServiceSingleUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;


import java.util.List;

/**
 * Created by dell on 2016/11/10.
 */
public class RightConsoleController {

    @FXML
    private CheckBox allControl;

    @FXML
    private Accordion groupTree;

    private GroupService groupService = (GroupService) ServiceSingleUtil.getService(GroupService.class);


    @FXML
    public void connectAll(){
        ScreenMonitorService.setIsConnectionAll(true);
    }

    @FXML
    public void disconnectAll(){
        ScreenMonitorService.setIsConnectionAll(false);
    }

    @FXML
    public void controlAll(){
        if(allControl.isSelected()){
            ScreenMonitorService.setAll(true);
        }else {
            ScreenMonitorService.setAll(false);
        }
    }

    public void initialize(){
        buildGroupTree();
    }

    private void buildGroupTree(){
        List<GroupModel> groups = groupService.buildGroupTree();
        for (GroupModel group : groups) {
            ObservableList<GroupTreeModel> deviceTreeModel = FXCollections.observableArrayList();
            ListView<GroupTreeModel> listView = new ListView<>();
            TitledPane titlePane = new TitledPane();
            titlePane.setText(group.getGroupName());
            titlePane.setContextMenu(buildAddGroupMenu());
            titlePane.setOnMouseClicked(event -> {
                
            });
            deviceTreeModel.setAll(group.getGroupTreeModel());
            listView.setItems(deviceTreeModel);
            listView.setCellFactory((list) -> new ListCell<GroupTreeModel>() {
                @Override
                protected void updateItem(GroupTreeModel item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item.getId() + " " + item.getDeviceName());
                    }
                }
            });
            listView.setContextMenu(buildDeviceMenu());
            titlePane.setContent(listView);
            groupTree.getPanes().add(titlePane);
        }
    }

    private ContextMenu buildAddGroupMenu(){
        ContextMenu contextMenu = new ContextMenu();
        final Line line1 = new Line(60, 10, 150, 10);
        final Line line2 = new Line(60, 10, 150, 10);
        MenuItem addMenu = getMenuItemForLine(GroupEvent.ADD_GROUP,line1);
        MenuItem delMenu = getMenuItemForLine(GroupEvent.DEL_GROUP,line2);
        contextMenu.getItems().addAll(addMenu,delMenu);
        return contextMenu;
    }

    private ContextMenu buildDeviceMenu(){
        ContextMenu contextMenu = new ContextMenu();
        final Line line1 = new Line(60, 10, 150, 10);
        final Line line2 = new Line(60, 10, 150, 10);
        MenuItem cut = getMenuItemForLine(GroupEvent.MOVE_GROUP,line1);
        MenuItem copy= getMenuItemForLine(GroupEvent.REMOVE_FROM_GROUP, line2);
        contextMenu.getItems().addAll(cut,copy);
        return contextMenu;
    }

    private MenuItem getMenuItemForLine(GroupEvent groupEvent, final Line line) {
        Label menuLabel = new Label(groupEvent.getDescriptions());
        menuLabel.setStyle("-fx-padding: 5 10 5 10");
        MenuItem mi = new MenuItem();
        mi.setGraphic(menuLabel);
        line.setStroke(Color.BLUE);
        menuLabel.addEventHandler(MouseEvent.MOUSE_PRESSED,groupEvent.getHandler());
        return mi;
    }




    private enum GroupEvent{
        ADD_GROUP("addGroup","新增分组",event -> {
            System.out.println("新增分组");
        }),
        DEL_GROUP("delGroup","删除分组",event -> {
            new Alert(Alert.AlertType.CONFIRMATION,"你确定要删除这个分组吗?").showAndWait().ifPresent(response ->{
                if (response == ButtonType.OK) {

                }
            });
        }),
        MOVE_GROUP("moveGroup","移动分组",event -> {
            System.out.println("移动分组");
        }),
        REMOVE_FROM_GROUP("removeFromGroup","从此分组移除",event -> {
            System.out.println("从此分组移除");
        })
        ;
        private String eventType;
        private String descriptions;
        private EventHandler<MouseEvent> handler;

        GroupEvent(String eventType,String descriptions, EventHandler<MouseEvent> handler) {
            this.eventType = eventType;
            this.handler = handler;
            this.descriptions = descriptions;
        }

        public static GroupEvent getEvent(String eventType){
            for (GroupEvent groupEvent : GroupEvent.values()) {
                if(eventType.equals(groupEvent.getEventType())){
                    return groupEvent;
                }
            }
            return null;
        }




        public String getEventType() {
            return eventType;
        }

        public void setEventType(String eventType) {
            this.eventType = eventType;
        }

        public String getDescriptions() {
            return descriptions;
        }

        public void setDescriptions(String descriptions) {
            this.descriptions = descriptions;
        }

        public EventHandler<MouseEvent> getHandler() {
            return handler;
        }

        public void setHandler(EventHandler<MouseEvent> handler) {
            this.handler = handler;
        }
    }
}
