package com.xlingmao.dto;

import com.android.ddmlib.IDevice;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

/**
 * Created by dell on 2016/11/3.
 */
public class ImageDeviceDto {
    private ImageView imageView;
    private int imageViewIndex;
    private AnchorPane anchorPane;
    private IDevice device;
    private boolean isOffLine = false;

    public boolean isOffLine() {
        return isOffLine;
    }

    public void setOffLine(boolean offLine) {
        isOffLine = offLine;
    }

    public IDevice getDevice() {
        return device;
    }

    public void setDevice(IDevice iDevice) {
        this.device = iDevice;
    }

    public AnchorPane getAnchorPane() {
        return anchorPane;
    }

    public void setAnchorPane(AnchorPane anchorPane) {
        this.anchorPane = anchorPane;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public int getImageViewIndex() {
        return imageViewIndex;
    }

    public void setImageViewIndex(int imageViewIndex) {
        this.imageViewIndex = imageViewIndex;
    }
}
