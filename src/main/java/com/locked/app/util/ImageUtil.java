package com.locked.app.util;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Circle;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;

import java.awt.image.BufferedImage;

import com.locked.app.models.UserSession;

public class ImageUtil {

    private ImageUtil() {
        // static utility class
    }

    public static Image convertToFXImage(BufferedImage bfImage) {
        return SwingFXUtils.toFXImage(bfImage, null);
    }

    public static Button setupProfilePicture(UserSession user) {
        Image pfpImage = convertToFXImage(user.getPfp());
        ImageView pfpBox = new ImageView(pfpImage);

        double size = 32;
        pfpBox.setFitWidth(size);
        pfpBox.setFitHeight(size);
        pfpBox.setPreserveRatio(true);
        pfpBox.setSmooth(true);

        Circle clip = new Circle(size / 2, size / 2, size / 2);
        pfpBox.setClip(clip);

        Button clickablePfp = new Button(user.getUsername(), pfpBox);
        clickablePfp.setPadding(new Insets(8, 0, 0, 8));
        clickablePfp.getStyleClass().add("sidebar-button");

        return clickablePfp;
    }

    public static ImageView displayCover(BufferedImage cover) {
        WritableImage fxImage = SwingFXUtils.toFXImage(cover, null);
        ImageView imageView = new ImageView(fxImage);
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        return imageView;
    }
}