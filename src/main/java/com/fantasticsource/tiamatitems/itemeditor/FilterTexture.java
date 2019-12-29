package com.fantasticsource.tiamatitems.itemeditor;

import com.fantasticsource.mctools.gui.element.text.filter.TextFilter;
import com.fantasticsource.tiamatitems.TextureCache;
import com.fantasticsource.tools.Tools;

public class FilterTexture extends TextFilter<String>
{
    public static final FilterTexture INSTANCE = new FilterTexture();

    private FilterTexture()
    {
    }

    @Override
    public String transformInput(String input)
    {
        String[] tokens = Tools.fixedSplit(input, ":");
        if (tokens.length == 2) return tokens[0].trim() + ":" + tokens[1].trim();
        if (tokens.length == 3) return tokens[0].trim() + ":" + tokens[1].trim() + ":" + tokens[2].trim();
        return null;
    }

    @Override
    public boolean acceptable(String input)
    {
        String result = transformInput(input);
        if (result == null) return false;

        String[] tokens = Tools.fixedSplit(result, ":");
        return TextureCache.textures.get(tokens[0] + ":" + tokens[1] + ":ffffffff") != null;
    }

    @Override
    public String parse(String input)
    {
        return !acceptable(input) ? null : transformInput(input);
    }
}
