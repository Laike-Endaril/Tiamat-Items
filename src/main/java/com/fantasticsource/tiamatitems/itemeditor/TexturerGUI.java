package com.fantasticsource.tiamatitems.itemeditor;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.text.GUIColor;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

public class TexturerGUI extends GUIScreen
{
    public static final Color T_BLACK = Color.BLACK.copy().setAF(0.85f);

    public static final Color[]
            RED = new Color[]{Color.RED.copy().setVF(0.5f), Color.RED.copy().setVF(0.75f), Color.WHITE},
            YELLOW = new Color[]{Color.YELLOW.copy().setVF(0.5f), Color.YELLOW.copy().setVF(0.75f), Color.WHITE},
            GREEN = new Color[]{Color.GREEN.copy().setVF(0.5f), Color.GREEN.copy().setVF(0.75f), Color.WHITE},
            BLUE = new Color[]{Color.BLUE.copy().setVF(0.5f), Color.BLUE.copy().setVF(0.75f), Color.WHITE},
            PURPLE = new Color[]{Color.PURPLE.copy().setVF(0.5f), Color.PURPLE.copy().setVF(0.75f), Color.WHITE},
            WHITE = new Color[]{Color.WHITE.copy().setVF(0.5f), Color.WHITE.copy().setVF(0.75f), Color.WHITE};


    public static void show()
    {
        TexturerGUI gui = new TexturerGUI();

        //Make sure GUI exists
        Minecraft.getMinecraft().displayGuiScreen(gui);
    }

    @Override
    public String title()
    {
        return "Item Texturer";
    }

    @Override
    protected void init()
    {
        root.add(new GUIGradient(this, 0, 0, 1, 1, T_BLACK));

        GUINavbar navbar = new GUINavbar(this, Color.AQUA);
        root.add(navbar);

        root.add(new GUIColor(this, Color.WHITE.copy(), textScale));
        root.add(new GUIColor(this, Color.RED.copy(), textScale));
    }

    @Override
    public void onClosed()
    {
        super.onClosed();
        //TODO send packet to create or edit item
    }
}
