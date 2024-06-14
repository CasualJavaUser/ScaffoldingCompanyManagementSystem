package org.example.fragment;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.example.App;

import java.io.IOException;
import java.net.URL;

public abstract class Fragment {
    protected Parent root;

    public Fragment(String resourcePath) throws IOException {
        URL viewResource = App.class.getClassLoader().getResource(resourcePath);
        if (viewResource == null)
            throw new IllegalStateException();
        root = FXMLLoader.load(viewResource);
    }

    public abstract void show(Scene scene, Object... args);
}
