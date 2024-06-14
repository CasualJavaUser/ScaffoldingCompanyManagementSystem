package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.fragment.*;
import org.example.util.HibernateUtil;

import java.net.URL;

public class App extends Application {
    public static void show() {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        URL styleResource = getClass().getClassLoader().getResource("style/style.css");
        if (styleResource == null)
            throw new IllegalStateException();

        Scene scene = new Scene(new HBox());
        scene.getStylesheets().add(styleResource.toExternalForm());
        stage.setScene(scene);
        ClientListFragment initialFragment = new ClientListFragment();

        NavController.init(scene,
                initialFragment,
                new OfferListFragment(),
                new OfferDetailsFragment(),
                new NewOfferFragment(),
                new OfferEditFragment()
        );

        initialFragment.show(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        HibernateUtil.shutdown();
    }
}
