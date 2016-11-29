package com.xlingmao.controller;


import com.xlingmao.App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by dell on 2016/11/16.
 */
public class BulkManager extends Stage {

    public void shows(){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("/view/initView/BulkOperateLayout.fxml"));
            AnchorPane rootLayout = loader.load();
            this.setScene(new Scene(rootLayout,600,400));
            this.setTitle("执行批量操作");
            this.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
