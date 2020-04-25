package com.fantasticsource.tiamatitems.itemeditor;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.textured.GUIItemStack;
import com.fantasticsource.tiamatitems.TextureCache;
import com.fantasticsource.tiamatitems.TiamatItem;
import com.fantasticsource.tiamatitems.nbt.TextureTags;

import static com.fantasticsource.tiamatitems.nbt.AssemblyTags.STATE_FULL;

public class GUIItemLayer extends GUIItemStack
{
    public GUIItemLayer(GUIScreen screen, double unscaledWidth, double unscaledHeight)
    {
        super(screen, unscaledWidth, unscaledHeight, TiamatItem.get(false, false, TextureCache.textures.size() == 0 ? "" : TextureCache.textures.keySet().iterator().next()));
    }

    public GUIItemLayer(GUIScreen screen, double unscaledWidth, double unscaledHeight, String layer)
    {
        super(screen, unscaledWidth, unscaledHeight, TiamatItem.get(false, false, layer));
    }

    public GUIItemLayer(GUIScreen screen, double x, double y, double unscaledWidth, double unscaledHeight)
    {
        super(screen, x, y, unscaledWidth, unscaledHeight, TiamatItem.get(false, false, TextureCache.textures.size() == 0 ? "" : TextureCache.textures.keySet().iterator().next()));
    }

    public GUIItemLayer(GUIScreen screen, double x, double y, double unscaledWidth, double unscaledHeight, String layer)
    {
        super(screen, x, y, unscaledWidth, unscaledHeight, TiamatItem.get(false, false, layer));
    }

    public String getLayer()
    {
        return TextureTags.getItemLayers(getItemStack(), STATE_FULL).get(0);
    }

    public void setLayer(String layer)
    {
        setItemStack(TiamatItem.get(false, false, layer));
    }
}
