package com.fantasticsource.tiamatitems.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.mctools.gui.element.view.GUIList.Line;
import com.fantasticsource.tiamatitems.TextureCache;
import com.fantasticsource.tiamatitems.itemeditor.GUIItemLayer;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

public class LayerSelectionGUI extends GUIScreen
{
    public LayerSelectionGUI(GUIItemLayer clickedElement)
    {
        this(clickedElement, 1);
    }

    public LayerSelectionGUI(GUIItemLayer clickedElement, double textScale)
    {
        super(textScale);


        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(this);
        else Minecraft.getMinecraft().displayGuiScreen(this);


        drawStack = false;


        //Navigation bar
        GUINavbar navbar = new GUINavbar(this);


        GUIList list = new GUIList(this, false, 0.98, 1 - navbar.height)
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                GUIItemLayer layer = new GUIItemLayer(screen, 16, 16);
                GUIText text = new GUIText(screen, " " + clickedElement.getLayer());
                return new GUIElement[]
                        {
                                layer,
                                text
                        };
            }

            @Override
            public Line addLine(int index, GUIElement... lineElements)
            {
                Line line = super.addLine(index, lineElements);
                GUIItemLayer layer = (GUIItemLayer) line.getLineElement(0);
                line.linkMouseActivity(layer);
                GUIText text = (GUIText) line.getLineElement(1);
                line.linkMouseActivity(text);

                return (Line) line.addClickActions(() ->
                {
                    clickedElement.setLayer(layer.getLayer());
                    screen.close();
                });
            }

            @Override
            public GUIElement newLineBackgroundElement()
            {
                return new GUIGradient(this.screen, 1.0D, 1.0D, getIdleColor(AL_BLACK), getIdleColor(AL_BLACK), getIdleColor(AL_WHITE), getIdleColor(AL_WHITE), getHoverColor(AL_BLACK), getHoverColor(AL_BLACK), getHoverColor(AL_WHITE), getHoverColor(AL_WHITE), AL_BLACK, AL_BLACK, AL_WHITE, AL_WHITE);
            }
        };
        GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(this, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, list);

        //Add elements
        root.addAll
                (
                        new GUIDarkenedBackground(this),
                        navbar.addRecalcActions(() ->
                        {
                            list.height = 1 - navbar.height;
                            scrollbar.height = 1 - navbar.height;
                        }),
                        list,
                        scrollbar
                );

        //Add options
        for (String option : TextureCache.textures.keySet())
        {
            Line line = list.addLine();
            GUIItemLayer layer = (GUIItemLayer) line.getLineElement(0);
            layer.setLayer(option);
            GUIText text = (GUIText) line.getLineElement(1);
            text.setText(option);
            if (option.equals(clickedElement.getLayer())) text.setColor(getIdleColor(Color.PURPLE), getHoverColor(Color.PURPLE), Color.PURPLE);
            else text.setColor(getIdleColor(Color.WHITE), getHoverColor(Color.WHITE), Color.WHITE);
        }
    }

    @Override
    public String title()
    {
        return "Texture Layer Selection";
    }
}
