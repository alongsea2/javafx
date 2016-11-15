package com.xlingmao.util;

import javafx.scene.Node;

import java.io.IOException;
import java.util.List;

/**
 * Created by dell on 2016/11/9.
 */
public class NodeUtil {

    public static Node findById(List<Node> nodeList,String id){
        for (Node node : nodeList) {
            if(id.equals(node.getId()))
                return node;
        }
        return null;
    }
}
