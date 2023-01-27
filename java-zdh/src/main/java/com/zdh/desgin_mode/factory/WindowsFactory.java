package com.zdh.desgin_mode.factory;

/**
 * @author zdh
 * @date 2022-07-05 17:26
 * @Version 1.0
 */
public class WindowsFactory implements GUIFactory {
    @Override
    public Button createButton() {
        return new WindowsButton();
    }

    @Override
    public Checkbox createCheckbox() {
        return new WindowsCheckbox();
    }
}
