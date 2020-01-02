package com.fantasticsource.tiamatitems.itemeditor;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.Slottings;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIButton;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.*;
import com.fantasticsource.mctools.gui.element.text.filter.*;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.mctools.gui.element.view.GUIMultilineTextInputView;
import com.fantasticsource.mctools.gui.element.view.GUITabView;
import com.fantasticsource.mctools.gui.element.view.GUIView;
import com.fantasticsource.mctools.gui.screen.TextSelectionGUI;
import com.fantasticsource.tiamatitems.Network;
import com.fantasticsource.tiamatitems.TextureCache;
import com.fantasticsource.tiamatitems.TiamatItems;
import com.fantasticsource.tiamatitems.nbt.CategoryTags;
import com.fantasticsource.tiamatitems.nbt.MiscTags;
import com.fantasticsource.tiamatitems.nbt.SlottingTags;
import com.fantasticsource.tiamatitems.nbt.TextureTags;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;

import static com.fantasticsource.tiamatitems.TiamatItems.FILTER_POSITIVE;
import static com.fantasticsource.tiamatitems.TiamatItems.MODID;

public class ItemEditorGUI extends GUIScreen
{
    private GUIList categories;

    public static void show()
    {
        ItemStack stack = Minecraft.getMinecraft().player.getHeldItemMainhand().copy();


        ItemEditorGUI gui = new ItemEditorGUI();
        Minecraft.getMinecraft().displayGuiScreen(gui);


        //Background
        gui.root.add(new GUIGradient(gui, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.85f)));


        //Header
        GUINavbar navbar = new GUINavbar(gui);
        GUITextButton save = new GUITextButton(gui, "Save", Color.GREEN);
        GUITextButton cancel = new GUITextButton(gui, "Cancel", Color.RED);
        gui.root.addAll(navbar, save, cancel);


        GUITabView tabView = new GUITabView(gui, 1, 1 - (cancel.y + cancel.height), "General", "Texture", "Category Tags", "Slottings");
        gui.root.add(tabView);


        //General tab
        GUILabeledTextInput name = new GUILabeledTextInput(gui, "Name: ", stack.getDisplayName(), FilterNotEmpty.INSTANCE);
        GUILabeledTextInput level = new GUILabeledTextInput(gui, "Level: ", "" + MiscTags.getItemLevel(stack), FilterInt.INSTANCE);
        GUILabeledTextInput levelReq = new GUILabeledTextInput(gui, "Level Requirement: ", "" + MiscTags.getItemLevelReq(stack), FilterInt.INSTANCE);
        GUILabeledTextInput value = new GUILabeledTextInput(gui, "Value: ", "" + MiscTags.getItemValue(stack), FilterInt.INSTANCE);
        GUIGradientBorder separator = new GUIGradientBorder(gui, 1, 0.02, 0.3, Color.WHITE, Color.BLANK);

        //Lore
        ArrayList<String> loreLines = MCTools.getLore(stack);
        StringBuilder loreString = new StringBuilder();
        if (loreLines != null && loreLines.size() > 0)
        {
            loreString.append(loreLines.get(0));
            for (int i = 1; i < loreLines.size(); i++) loreString.append("\n").append(loreLines.get(i));
        }
        GUIMultilineTextInputView lore = new GUIMultilineTextInputView(gui, 0.98, 1 - (separator.y + separator.height), new GUIMultilineTextInput(gui, loreString.toString(), FilterNone.INSTANCE));
        GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(gui, 0.02, 1 - (separator.y + separator.height), Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, lore);

        tabView.tabViews.get(0).addAll
                (
                        new GUITextSpacer(gui),
                        name,
                        new GUITextSpacer(gui),
                        level,
                        new GUITextSpacer(gui),
                        levelReq,
                        new GUITextSpacer(gui),
                        value,
                        new GUITextSpacer(gui),
                        new GUIText(gui, "Lore...\n").addClickActions(() -> lore.multilineTextInput.setActive(true)),
                        separator,
                        lore,
                        scrollbar
                );

        //Add actions
        scrollbar.addRecalcActions(() ->
        {
            lore.height = 1 - (separator.y + separator.height);
            scrollbar.height = 1 - (separator.y + separator.height);
        });


        //Texture tab

        //Caching options
        GUILabeledTextInput cacheLayers = new GUILabeledTextInput(gui, "Cache Layers: ", TextureTags.itemHasLayerCacheTag(stack) ? "true" : "false", FilterBoolean.INSTANCE);
        GUILabeledTextInput cacheTexture = new GUILabeledTextInput(gui, "Cache Texture: ", TextureTags.itemHasTextureCacheTag(stack) ? "true" : "false", FilterBoolean.INSTANCE);
        GUIGradientBorder separator2 = new GUIGradientBorder(gui, 1, 0.02, 0.3, Color.WHITE, Color.BLANK);

        //Layer list
        String[] uncoloredTextures = Tools.sort(TextureCache.getUncoloredTextureNames());
        GUIList layerArrayElement = new GUIList(gui, true, 0.98, 1 - (separator2.y + separator2.height))
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                GUIItemLayer layer = new GUIItemLayer(screen, 16, 16, TextureCache.textures.keySet().toArray(new String[0])[0]);
                GUIView view = new GUIView(screen, 1 - (layer.x + layer.width), 1);
                layer.addRecalcActions(() ->
                {
                    view.width = 1 - (layer.x + layer.width);
                    view.height = layer.height;
                });
                view.addRecalcActions(() ->
                {
                    view.width = 1 - (layer.x + layer.width);
                    view.height = layer.height;
                });
                String[] tokens = Tools.fixedSplit(layer.getLayer(), ":");
                GUIText texture = new GUIText(screen, tokens[0] + ":" + tokens[1]).setColor(getIdleColor(Color.WHITE), getHoverColor(Color.WHITE), Color.WHITE);
                GUIColor color = new GUIColor(screen);
                view.addAll
                        (
                                new GUIText(screen, "Texture: "),
                                texture.addClickActions(() -> new TextSelectionGUI(texture, "Texture", uncoloredTextures)),
                                new GUIElement(screen, 1, 0),
                                new GUIText(screen, "Color: ").addClickActions(color::click),
                                color
                        );
                texture.addRecalcActions(() ->
                {
                    String text = texture.getText() + ":" + color.getText();
                    if (!layer.getLayer().equals(text))
                    {
                        layer.setLayer(text);
                    }
                });
                color.addRecalcActions(() ->
                {
                    String text = texture.getText() + ":" + color.getText();
                    if (!layer.getLayer().equals(text))
                    {
                        layer.setLayer(text);
                    }
                });
                return new GUIElement[]
                        {
                                layer,
                                view
                        };
            }
        };
        GUIVerticalScrollbar scrollbar2 = new GUIVerticalScrollbar(gui, 0.02, 1 - (separator2.y + separator2.height), Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, layerArrayElement);

        tabView.tabViews.get(1).addAll
                (
                        new GUITextSpacer(gui),
                        cacheLayers,
                        new GUITextSpacer(gui),
                        cacheTexture,
                        new GUITextSpacer(gui),
                        separator2,
                        layerArrayElement,
                        scrollbar2
                );

        //Remove and replace with explanation if item is not compatible with texture layers, or if no texture files exist
        if (stack.getItem() != TiamatItems.tiamatItem)
        {
            layerArrayElement.clear();
            tabView.tabViews.get(1).clear();
            tabView.tabViews.get(1).add(0, new GUIText(gui, TextFormatting.RED + "This feature only works when right clicking the block with a " + new ItemStack(TiamatItems.tiamatItem).getDisplayName()));
        }
        else if (TextureCache.textures.size() == 0)
        {
            layerArrayElement.clear();
            tabView.tabViews.get(1).clear();
            tabView.tabViews.get(1).add(0, new GUIText(gui, TextFormatting.RED + "No texture files were found! You can add them to...\n" + TextFormatting.RED + MCTools.getConfigDir().replaceAll("\\\\", "/") + MODID));
        }
        else
        {
            //If no errors occurred...

            //Add existing layers
            for (String layerString : TextureTags.getItemLayers(stack))
            {
                String[] tokens = Tools.fixedSplit(layerString, ":");
                if (tokens.length != 3 || !FilterNotEmpty.INSTANCE.acceptable(tokens[0]) || !FILTER_POSITIVE.acceptable(tokens[1]) || !FilterColor.INSTANCE.acceptable(tokens[2])) continue;

                layerArrayElement.addLine();
                GUIList.Line line = layerArrayElement.get(layerArrayElement.lineCount() - 1);
                ((GUIItemLayer) line.getLineElement(0)).setLayer(layerString);
                GUIView view = (GUIView) line.getLineElement(1);
                GUIText texture = (GUIText) view.get(1);
                texture.setText(tokens[0] + ":" + tokens[1]);
                GUIColor color = (GUIColor) view.get(4);
                color.setValue(new Color(FilterColor.INSTANCE.parse(tokens[2])));
            }

            //Add recalc actions
            separator2.addRecalcActions(() ->
            {
                layerArrayElement.height = 1 - (separator2.y + separator2.height);
                scrollbar2.height = 1 - (separator2.y + separator2.height);
            });
        }


        //Category tags tab
        gui.categories = new GUIList(gui, true, 0.98, 1)
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                GUITextInput categoryInput = new GUITextInput(gui, "Type", FilterNotEmpty.INSTANCE);
                categoryInput.addRecalcActions(() ->
                {
                    if (categoryInput.valid()) CategoryTags.renameItemCategory(stack, categoryInput.oldText, categoryInput.getText());
                });

                GUIButton tagsButton = GUIButton.newListButton(screen);
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

                return new GUIElement[]{tagsButton, categoryInput};
            }
        };
        GUIVerticalScrollbar scrollbar3 = new GUIVerticalScrollbar(gui, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, gui.categories);
        tabView.tabViews.get(2).addAll(gui.categories, scrollbar3);

        //Add existing categories
        gui.addCategories(stack);


        //Slottings tab
        GUIList slottings = new GUIList(gui, true, 0.98, 1)
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                GUIText text = new GUIText(screen, "Hand").setColor(getIdleColor(Color.WHITE), getHoverColor(Color.WHITE), Color.WHITE);
                return new GUIElement[]{text.addClickActions(() -> new TextSelectionGUI(text, "Slot", Slottings.availableSlottings()))};
            }
        };
        GUIVerticalScrollbar scrollbar4 = new GUIVerticalScrollbar(gui, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, slottings);
        tabView.tabViews.get(3).addAll(slottings, scrollbar4);

        //Add existing slottings
        for (String slotname : SlottingTags.getItemSlottings(stack))
        {
            slottings.addLine();
            GUIList.Line line = slottings.get(slottings.lineCount() - 1);
            ((GUIText) line.getLineElement(0)).setText(slotname);
        }
        //TODO detect existing slots from sources other than tiamat tags


        //Add main header actions
        cancel.addRecalcActions(() -> tabView.height = 1 - (cancel.y + cancel.height));
        cancel.addClickActions(gui::close);
        save.addClickActions(() ->
        {
            //Validation

            //General
            if (!name.valid() || !level.valid() || !levelReq.valid() || !value.valid()) return;

            //Texture
            if (stack.getItem() == TiamatItems.tiamatItem && TextureCache.textures.size() > 0)
            {
                if (!cacheLayers.valid() || !cacheTexture.valid()) return;
            }


            //Set stack params

            //General
            stack.setStackDisplayName(name.getText());
            MiscTags.setItemLevel(stack, FilterInt.INSTANCE.parse(level.getText()));
            MiscTags.setItemLevelReq(stack, FilterInt.INSTANCE.parse(levelReq.getText()));
            MiscTags.setItemValue(stack, FilterInt.INSTANCE.parse(value.getText()));
            MCTools.setLore(stack, lore.getText());

            //Texture
            if (stack.getItem() == TiamatItems.tiamatItem && TextureCache.textures.size() > 0)
            {
                if (FilterBoolean.INSTANCE.parse(cacheLayers.getText())) TextureTags.addItemLayerCacheTag(stack);
                else TextureTags.removeItemLayerCacheTag(stack);
                if (FilterBoolean.INSTANCE.parse(cacheTexture.getText())) TextureTags.addItemTextureCacheTag(stack);
                else TextureTags.removeItemTextureCacheTag(stack);

                String[] layers = new String[layerArrayElement.lineCount()];
                for (int i = 0; i < layers.length; i++)
                {
                    GUIList.Line line = layerArrayElement.get(i);
                    layers[i] = ((GUIItemLayer) line.getLineElement(0)).getLayer();
                }

                TextureTags.clearItemLayers(stack);
                for (String layer : layers) TextureTags.addItemLayer(stack, layer);
            }

            //Category tags are already stored in the stack via GUI logic

            //Slottings
            SlottingTags.clearItemSlottings(stack);
            for (GUIList.Line line : slottings.getLines())
            {
                SlottingTags.addItemSlotting(stack, ((GUIText) line.getLineElement(0)).getText());
            }

            //Send to server
            Network.WRAPPER.sendToServer(new Network.EditItemPacket(stack));
            gui.close();
        });


        //Recalc all twice to fix lore scrollbar...
        gui.root.recalc(0);
        gui.root.recalc(0);
    }

    @Override
    public String title()
    {
        return "Item Editor";
    }

    private void addCategories(ItemStack stack)
    {
        for (String category : CategoryTags.getItemCategories(stack))
        {
            categories.addLine();
            GUIList.Line line = categories.get(categories.lineCount() - 1);
            GUITextInput categoryInput = (GUITextInput) line.getLineElement(1);
            categoryInput.setText(FilterNotEmpty.INSTANCE.parse(category));
            line.get(3).addClickActions(() -> CategoryTags.removeItemCategory(stack, categoryInput.getText()));
        }
    }
}
