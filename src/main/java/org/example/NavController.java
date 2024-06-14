package org.example;

import javafx.scene.Scene;
import org.example.fragment.Fragment;

import java.util.HashMap;
import java.util.Map;

public class NavController {
    private static final Map<Class<? extends Fragment>, Fragment> fragments = new HashMap<>();
    private static Scene scene;

    public static void init(Scene scene, Fragment... fragments) {
        NavController.scene = scene;
        for (Fragment f : fragments) {
            NavController.fragments.put(f.getClass(), f);
        }
    }

    public static void navigate(Class<? extends Fragment> fragmentClass, Object... args) {
        fragments.get(fragmentClass).show(scene, args);
    }
}
