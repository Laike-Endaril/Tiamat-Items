package com.fantasticsource.tiamatitems.itemeditor;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.*;
import com.fantasticsource.mctools.gui.element.text.filter.FilterColor;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNone;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.view.GUIArrayList;
import com.fantasticsource.mctools.gui.element.view.GUIAutocroppedView;
import com.fantasticsource.mctools.gui.element.view.GUIMultilineTextInputView;
import com.fantasticsource.mctools.gui.element.view.GUITabView;
import com.fantasticsource.tiamatitems.Network;
import com.fantasticsource.tiamatitems.TiamatItems;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.fantasticsource.tiamatitems.TiamatItems.FILTER_POSITIVE;
import static com.fantasticsource.tiamatitems.TiamatItems.MODID;

public class ItemEditorGUI extends GUIScreen
{
    private GUIArrayList<GUIText> categories;

    public static void show()
    {
        ItemStack stack = Minecraft.getMinecraft().player.getHeldItemMainhand().copy();


        ItemEditorGUI gui = new ItemEditorGUI();
        Minecraft.getMinecraft().displayGuiScreen(gui);


        //Background
        gui.root.add(new GUIGradient(gui, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.85f)));


        //Header
        GUINavbar navbar = new GUINavbar(gui, Color.AQUA);
        GUITextButton save = new GUITextButton(gui, "Save", Color.GREEN);
        GUITextButton cancel = new GUITextButton(gui, "Cancel", Color.RED);
        gui.root.addAll(navbar, save, cancel);


        GUITabView tabView = new GUITabView(gui, 1, 1 - (cancel.y + cancel.height), "General", "Texture", "Category Tags");
        gui.root.add(tabView);


        //General tab
        GUILabeledTextInput name = new GUILabeledTextInput(gui, "Name: ", stack.getDisplayName(), FilterNotEmpty.INSTANCE);
        ArrayList<String> loreLines = MCTools.getLore(stack);
        StringBuilder loreString = new StringBuilder();
        if (loreLines != null && loreLines.size() > 0)
        {
            loreString.append(loreLines.get(0));
            for (int i = 1; i < loreLines.size(); i++) loreString.append("\n").append(loreLines.get(i));
        }
        GUIMultilineTextInputView lore = new GUIMultilineTextInputView(gui, 0.98, 0.5, new GUIMultilineTextInput(gui, loreString.toString(), FilterNone.INSTANCE));
        tabView.tabViews.get(0).addAll
                (
                        name,
                        new GUIText(gui, "Lore...\n").addClickActions(() -> lore.multilineTextInput.setActive(true)),
                        lore,
                        new GUIVerticalScrollbar(gui, 0.02, 0.5, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, lore)
                );


        //Texture tab
        GUIArrayList<GUIElement> layerArrayElement = new GUIArrayList<GUIElement>(gui, 0.98, 1)
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                return new GUIElement[]
                        {
                                new GUILabeledTextInput(gui, "File: ", "FILENAME", FilterNotEmpty.INSTANCE),
                                new GUILabeledTextInput(gui, 0.4, 0, "Index: ", "0", FILTER_POSITIVE),
                                new GUIColor(gui, 0.7, 0)
                        };
            }
        };
        GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(gui, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, layerArrayElement);
        tabView.tabViews.get(1).addAll(layerArrayElement, scrollbar);

        //Add existing layers
        if (stack.hasTagCompound())
        {
            NBTTagCompound compound = stack.getTagCompound();
            if (compound.hasKey(MODID))
            {
                compound = compound.getCompoundTag(MODID);
                if (compound.hasKey("layers"))
                {
                    NBTTagList layers = compound.getTagList("layers", Constants.NBT.TAG_STRING);
                    for (int i = 0; i < layers.tagCount(); i++)
                    {
                        String[] tokens = Tools.fixedSplit(layers.getStringTagAt(i), ":");
                        if (tokens.length != 3 || !FilterNotEmpty.INSTANCE.acceptable(tokens[0]) || !FILTER_POSITIVE.acceptable(tokens[1]) || !FilterColor.INSTANCE.acceptable(tokens[2])) continue;

                        layerArrayElement.addLine();
                        GUIAutocroppedView line = layerArrayElement.get(layerArrayElement.lineCount() - 1);
                        ((GUILabeledTextInput) line.get(3)).setInput(FilterNotEmpty.INSTANCE.parse(tokens[0]));
                        ((GUILabeledTextInput) line.get(4)).setInput("" + FILTER_POSITIVE.parse(tokens[1]));
                        ((GUIColor) line.get(5)).setValue(new Color(FilterColor.INSTANCE.parse(tokens[2])));
                    }
                }
            }
        }

        if (stack.getItem() != TiamatItems.tiamatItem)
        {
            layerArrayElement.clear();
            tabView.tabViews.get(1).remove(1);
            tabView.tabViews.get(1).add(0, new GUIText(gui, TextFormatting.RED + "This feature only works when right clicking the block with a " + new ItemStack(TiamatItems.tiamatItem).getDisplayName()));
        }


        //Category tags tab
        gui.categories = new GUIArrayList<GUIText>(gui, 0.98, 1)
        {
            @Override
            public GUIText[] newLineDefaultElements()
            {
                GUITextInput categoryInput = new GUITextInput(gui, "Type", FilterNotEmpty.INSTANCE);
                categoryInput.addRecalcActions(() ->
                {
                    if (categoryInput.valid()) TiamatItems.renameItemCategory(stack, categoryInput.oldText, categoryInput.getText());
                });

                GUIText tagsButton = new GUIText(gui, "<Edit Tags>", getIdleColor(Color.PURPLE), getHoverColor(Color.PURPLE), Color.PURPLE);
                tagsButton.addClickActions(() ->
                {
                    if (categoryInput.valid())
                    {
                        TagCategoryGUI categoryGUI = new TagCategoryGUI(stack, categoryInput.getText());
                        categoryGUI.addOnClosedActions(() ->
                        {
                            while (gui.categories.lineCount() > 0) gui.categories.remove(0);
                            gui.addCategories(stack);
                        });
                    }
                });

                return new GUIText[]{categoryInput, tagsButton};
            }
        };
        scrollbar = new GUIVerticalScrollbar(gui, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, gui.categories);
        tabView.tabViews.get(2).addAll(gui.categories, scrollbar);

        //Add existing categories
        gui.addCategories(stack);


        //Add actions
        cancel.addRecalcActions(() -> tabView.height = 1 - (cancel.y + cancel.height));
        cancel.addClickActions(gui::close);
        save.addClickActions(() ->
        {
            if (!name.valid()) return;

            LinkedHashMap<String, ArrayList<String>> map = new LinkedHashMap<>();
            for (String category : TiamatItems.getItemCategories(stack))
            {
                map.put(category, new ArrayList<>(TiamatItems.getItemCategoryTags(stack, category)));
            }

            if (stack.getItem() == TiamatItems.tiamatItem)
            {
                String[] layers = new String[layerArrayElement.lineCount()];
                for (int i = 0; i < layers.length; i++)
                {
                    GUIAutocroppedView line = layerArrayElement.get(i);

                    GUILabeledTextInput texture = (GUILabeledTextInput) line.get(3);
                    GUILabeledTextInput subimage = (GUILabeledTextInput) line.get(4);
                    if (!texture.valid() || !subimage.valid()) return;

                    GUIColor color = (GUIColor) line.get(5);

                    layers[i] = texture.getText().trim() + ':' + subimage.getText().trim() + ':' + color.getText();
                }

                Network.WRAPPER.sendToServer(new Network.EditItemPacket(name.getText(), lore.getText(), map, layers));
            }
            else
            {
                Network.WRAPPER.sendToServer(new Network.EditItemPacket(name.getText(), lore.getText(), map));
            }
            gui.close();
        });
    }

    @Override
    public String title()
    {
        return "Item Editor";
    }

    private void addCategories(ItemStack stack)
    {
        for (String category : TiamatItems.getItemCategories(stack))
        {
            categories.addLine();
            GUIAutocroppedView line = categories.get(categories.lineCount() - 1);
            GUITextInput categoryInput = (GUITextInput) line.get(3);
            categoryInput.setText(FilterNotEmpty.INSTANCE.parse(category));
            line.get(2).addClickActions(() -> TiamatItems.removeItemCategory(stack, categoryInput.getText()));
        }
    }
}
