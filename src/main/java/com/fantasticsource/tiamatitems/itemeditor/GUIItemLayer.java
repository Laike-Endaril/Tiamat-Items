package com.fantasticsource.tiamatitems.itemeditor;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.textured.GUIItemStack;
import com.fantasticsource.tiamatitems.TiamatItem;

public class GUIItemLayer extends GUIItemStack
{
    public GUIItemLayer(GUIScreen screen, double unscaledWidth, double unscaledHeight, String layer)
    {
        super(screen, unscaledWidth, unscaledHeight, TiamatItem.get(false, false, layer));
    }

    public GUIItemLayer(GUIScreen screen, double x, double y, double unscaledWidth, double unscaledHeight, String layer)
    {
        super(screen, x, y, unscaledWidth, unscaledHeight, TiamatItem.get(false, false, layer));
    }
}
