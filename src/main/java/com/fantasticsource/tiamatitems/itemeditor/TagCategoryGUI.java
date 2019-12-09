package com.fantasticsource.tiamatitems.itemeditor;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUIMultilineTextInput;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNone;
import com.fantasticsource.mctools.gui.element.view.GUIMultilineTextInputView;
import com.fantasticsource.tiamatitems.TiamatItems;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class TagCategoryGUI extends GUIScreen
{
    public String category;

    public TagCategoryGUI(ItemStack stack, String category)
    {
        this(stack, category, 1);
    }

    public TagCategoryGUI(ItemStack stack, String category, double textScale)
    {
        super(textScale);


        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(this);
        else Minecraft.getMinecraft().displayGuiScreen(this);


        drawStack = false;


        this.category = category;


        //Background
        root.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.85f)));


        //Header
        GUINavbar navbar = new GUINavbar(this, Color.AQUA);
        GUITextButton save = new GUITextButton(this, "Save", Color.GREEN);
        GUITextButton cancel = new GUITextButton(this, "Cancel", Color.RED);
        root.addAll(navbar, save, cancel);


        //Tags
        StringBuilder tags;
        ArrayList<String> tagList = TiamatItems.getItemCategoryTags(stack, category);
        if (tagList.size() > 0)
        {
            tags = new StringBuilder(tagList.get(0));
            for (int i = 1; i < tagList.size(); i++) tags.append("\n").append(tagList.get(i));
        }
        else tags = new StringBuilder();
        GUIMultilineTextInputView tagsElement = new GUIMultilineTextInputView(this, 0.98, 1 - (cancel.y + cancel.height), new GUIMultilineTextInput(this, tags.toString(), FilterNone.INSTANCE));
        GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(this, 0.02, 1 - (cancel.y + cancel.height), Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, tagsElement);
        root.addAll(tagsElement, scrollbar);
        tagsElement.get(0).setActive(true);


        //Add actions
        cancel.addRecalcActions(() ->
        {
            tagsElement.height = 1 - (cancel.y + cancel.height);
            scrollbar.height = 1 - (cancel.y + cancel.height);
        });
        cancel.addClickActions(this::close);
        save.addClickActions(() ->
        {
            TiamatItems.removeItemCategory(stack, category);

            for (String tag : Tools.fixedSplit(tagsElement.getText(), "\n")) TiamatItems.addItemCategoryTag(stack, category, tag);
            close();
        });
    }

    @Override
    public String title()
    {
        return category;
    }
}
