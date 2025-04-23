package com.wynnventory.ui;

import com.wynnventory.enums.Sprite;

public class SettingsButton extends WynnventoryImageButton {

    public SettingsButton(int x, int y, Runnable onClick) {
        super(x, y, 16, 16, Sprite.SETTINGS_BUTTON, onClick, "Open mod settings");
    }
}