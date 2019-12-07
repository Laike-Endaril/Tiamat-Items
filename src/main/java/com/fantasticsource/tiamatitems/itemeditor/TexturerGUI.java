package com.fantasticsource.tiamatitems.itemeditor;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUIColor;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.view.GUIArrayList;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

public class TexturerGUI extends GUIScreen
{
    public static void show()
    {
        TexturerGUI gui = new TexturerGUI();
        Minecraft.getMinecraft().displayGuiScreen(gui);


        //Root
        gui.root.add(new GUIGradient(gui, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.85f)));

        GUINavbar navbar = new GUINavbar(gui, Color.AQUA);
        gui.root.add(navbar);

        GUIScrollView arrayList = new GUIArrayList<GUIElement>(gui, 0.98, 1 - navbar.height)
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                GUILabeledTextInput texture = new GUILabeledTextInput(gui, "File: ", "aaaa", FilterNotEmpty.INSTANCE);
                GUILabeledTextInput subimage = new GUILabeledTextInput(gui, "Index: ", "", FilterNotEmpty.INSTANCE);

                return new GUIElement[]{texture, subimage, new GUIColor(gui), new GUIText(screen, "Test: "), new GUIText(screen, "aaaaaa"), new GUIText(screen, "IIIIIIII"), new GUIText(screen, "ccccc: ")};
            }

            @Override
            public GUIElement newLineBackgroundElement()
            {
                return new GUIGradientBorder(gui, 0, 0, 1, 1, 0.1, Color.WHITE, Color.BLANK);
            }
        };
        gui.root.add(arrayList);
        gui.root.add(new GUIVerticalScrollbar(gui, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, arrayList));


        //Add actions
        navbar.addRecalcActions(() -> arrayList.height = 1 - navbar.height);
    }

    @Override
    public String title()
    {
        return "Item Texturer";
    }

    @Override
    public void onClosed()
    {
        super.onClosed();
        //TODO send packet to create or edit item
    }
}
