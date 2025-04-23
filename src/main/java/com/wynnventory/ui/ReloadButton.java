package com.wynnventory.ui;

import com.wynnventory.enums.Sprite;

public class ReloadButton extends WynnventoryImageButton {

    public ReloadButton(int x, int y, Runnable onClick) {
        super(x, y, 16, 16, Sprite.RELOAD_BUTTON, onClick, "Reload Lootpools");
    }
}