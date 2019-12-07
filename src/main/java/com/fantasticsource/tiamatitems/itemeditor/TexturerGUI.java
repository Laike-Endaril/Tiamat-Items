package com.fantasticsource.tiamatitems.itemeditor;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.other.GUITab;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUIColor;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
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
                GUILabeledTextInput texture = new GUILabeledTextInput(gui, "File: ", "FILENAME", FilterNotEmpty.INSTANCE);
                GUILabeledTextInput subimage = new GUILabeledTextInput(gui, "Index: ", "0", FilterNotEmpty.INSTANCE);
                GUITab tab = new GUITab(screen, 0.4, 0);
                GUITab tab2 = new GUITab(screen, 0.7, 0);

                return new GUIElement[]
                        {
                                texture,
                                tab.addRecalcActions(() -> System.out.println("tab: " + tab.absolutePxX() + ", " + tab.absolutePxY() + ", " + tab.absolutePxWidth() + ", " + tab.absolutePxHeight())),
                                subimage,
                                tab2.addRecalcActions(() -> System.out.println("tab2: " + tab2.absolutePxX() + ", " + tab2.absolutePxY() + ", " + tab2.absolutePxWidth() + ", " + tab2.absolutePxHeight())),
                                new GUIColor(gui),
                                new GUIGradientBorder(gui, 1, 0.1, 0.3, Color.GRAY, Color.BLANK)
                        };
            }
        };
        GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(gui, 0.02, 1 - navbar.height, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, arrayList);

        gui.root.addAll(arrayList, scrollbar);


        //Add actions
        navbar.addRecalcActions(() ->
        {
            arrayList.height = 1 - navbar.height;
            scrollbar.height = 1 - navbar.height;
        });
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
