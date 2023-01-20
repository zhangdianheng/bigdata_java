package com.zdh.desgin_mode.factory;

/**
 * @author zdh
 * @date 2022-07-05 17:25
 * @Version 1.0
 */
public class MacOSFactory implements GUIFactory {
    @Override
    public Button createButton() {
        return new MacOSButton();
    }

    @Override
    public Checkbox createCheckbox() {
        return new MacOSCheckbox();
    }
}
