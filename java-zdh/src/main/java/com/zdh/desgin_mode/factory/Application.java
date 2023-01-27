package com.zdh.desgin_mode.factory;

/**
 * @author zdh
 * @date 2022-07-05 17:26
 * @Version 1.0
 */
public class Application {
    private Button button;
    private Checkbox checkbox;

    public Application(GUIFactory factory) {
        button = factory.createButton();
        checkbox = factory.createCheckbox();
    }

    public void paint() {
        button.paint();
        checkbox.paint();
    }
}
