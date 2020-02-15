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
import com.fantasticsource.tiamatactions.gui.GUIAction;
import com.fantasticsource.tiamatitems.Network;
import com.fantasticsource.tiamatitems.TextureCache;
import com.fantasticsource.tiamatitems.TiamatItems;
import com.fantasticsource.tiamatitems.nbt.*;
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
    protected static GUIList categories;
    protected static String[] actionList;

    public static void show(String[] actionList)
    {
        ItemStack stack = Minecraft.getMinecraft().player.getHeldItemMainhand().copy();


        ItemEditorGUI gui = new ItemEditorGUI();
        Minecraft.getMinecraft().displayGuiScreen(gui);


        ItemEditorGUI.actionList = actionList;


        //Background
        gui.root.add(new GUIGradient(gui, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.85f)));


        //Header
        GUINavbar navbar = new GUINavbar(gui);
        GUITextButton save = new GUITextButton(gui, "Save", Color.GREEN);
        GUITextButton cancel = new GUITextButton(gui, "Cancel", Color.RED);
        gui.root.addAll(navbar, save, cancel);


        GUITabView tabView = new GUITabView(gui, 1, 1 - (cancel.y + cancel.height), "General", "Texture", "Category Tags", "Attributes", "Part Slots");
        gui.root.add(tabView);


        //General tab
        GUILabeledTextInput name = new GUILabeledTextInput(gui, "Name: ", stack.getDisplayName(), FilterNotEmpty.INSTANCE);
        GUIText slotting = new GUIText(gui, MiscTags.getItemSlotting(stack)).setColor(getIdleColor(Color.WHITE), getHoverColor(Color.WHITE), Color.WHITE);
        GUILabeledTextInput level = new GUILabeledTextInput(gui, "Level: ", "" + MiscTags.getItemLevel(stack), FilterInt.INSTANCE);
        GUILabeledTextInput levelReq = new GUILabeledTextInput(gui, "Level Requirement: ", "" + MiscTags.getItemLevelReq(stack), FilterInt.INSTANCE);
        GUILabeledTextInput value = new GUILabeledTextInput(gui, "Value: ", "" + MiscTags.getItemValue(stack), FilterInt.INSTANCE);
        GUIAction action1 = new GUIAction(gui, ActionTags.getItemAction1(stack)), action2 = new GUIAction(gui, ActionTags.getItemAction2(stack));
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
                        new GUIText(gui, "Slotting: "),
                        slotting.addClickActions(() -> new TextSelectionGUI(slotting, "Slotting", Slottings.availableSlottings())),
                        new GUITextSpacer(gui),
                        level,
                        new GUIElement(gui, 1, 0),
                        levelReq,
                        new GUITextSpacer(gui),
                        value,
                        new GUITextSpacer(gui),
                        new GUIText(gui, "Action 1: "),
                        action1,
                        new GUIElement(gui, 1, 0),
                        new GUIText(gui, "Action 2: "),
                        action2,
                        new GUITextSpacer(gui),
                        new GUIText(gui, "Lore...\n").addClickActions(() -> lore.multilineTextInput.setActive(true)),
                        separator,
                        lore,
                        scrollbar
                );

        //Add GUI actions
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
        categories = new GUIList(gui, true, 0.98, 1)
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                GUITextInput categoryInput = new GUITextInput(gui, "Category", FilterNotEmpty.INSTANCE);
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
                            while (categories.lineCount() > 0) categories.remove(0);
                            gui.addCategories(stack);
                        });
                    }
                });

                return new GUIElement[]{tagsButton, categoryInput};
            }
        };
        GUIVerticalScrollbar scrollbar3 = new GUIVerticalScrollbar(gui, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, categories);
        tabView.tabViews.get(2).addAll(categories, scrollbar3);

        //Add existing categories
        gui.addCategories(stack);


        //Attributes tab
        GUIList passiveAttributeList = new GUIList(gui, true, 0.98, 1)
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                return new GUIElement[]
                        {
                                new GUIElement(screen, 1, 0),
                                new GUILabeledTextInput(screen, "Attribute: ", "", FilterNotEmpty.INSTANCE, 1),
                                new GUIElement(screen, 1, 0),
                                new GUILabeledTextInput(screen, "Amount: ", "0", FilterFloat.INSTANCE, 1),
                                new GUIElement(screen, 1, 0),
                                new GUILabeledTextInput(screen, "Operation: ", "0", FilterRangedInt.get(0, 2), 1)
                        };
            }
        };
        GUIVerticalScrollbar scrollbar5 = new GUIVerticalScrollbar(gui, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, passiveAttributeList);
        GUIList activeAttributeList = new GUIList(gui, true, 0.98, 1)
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                return new GUIElement[]
                        {
                                new GUIElement(screen, 1, 0),
                                new GUILabeledTextInput(screen, "Attribute: ", "", FilterNotEmpty.INSTANCE, 1),
                                new GUIElement(screen, 1, 0),
                                new GUILabeledTextInput(screen, "Amount: ", "0", FilterFloat.INSTANCE, 1),
                                new GUIElement(screen, 1, 0),
                                new GUILabeledTextInput(screen, "Operation: ", "0", FilterRangedInt.get(0, 2), 1)
                        };
            }
        };
        GUIVerticalScrollbar scrollbar6 = new GUIVerticalScrollbar(gui, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, activeAttributeList);
        GUIElement passiveLabel = new GUIText(gui, "PASSIVE ATTRIBUTE MODIFIERS...", Color.ORANGE).setTooltip("Attribute modifiers which are ALWAYS applied, so long as the equip is in a valid slot");
        GUIGradientBorder separator3 = new GUIGradientBorder(gui, 1, 0.02, 0.3, Color.WHITE, Color.BLANK);
        passiveLabel.addRecalcActions(() ->
        {
            double halfRemainingHeight = 0.5 - passiveLabel.height - separator3.height / 2;
            passiveAttributeList.height = halfRemainingHeight;
            scrollbar5.height = halfRemainingHeight;
            activeAttributeList.height = halfRemainingHeight;
            scrollbar6.height = halfRemainingHeight;
        });
        tabView.tabViews.get(3).addAll
                (
                        passiveLabel,
                        passiveAttributeList,
                        scrollbar5,
                        separator3,
                        new GUIText(gui, "ACTIVE ATTRIBUTE MODIFIERS...", Color.ORANGE).setTooltip("Attribute modifiers which only apply for the item being used.  Eg. weapon stats would normally go here; power, reach, and speed of the weapon"),
                        activeAttributeList,
                        scrollbar6
                );

        //Add existing passive attribute modifiers (these ones should only get applied when the item is in a tiamat tag slotting)
        for (String modString : PassiveAttributeModTags.getPassiveMods(stack))
        {
            String[] tokens = Tools.fixedSplit(modString, ";");
            if (tokens.length != 3) continue;

            passiveAttributeList.addLine();
            GUIList.Line line = passiveAttributeList.getLastFilledLine();
            ((GUILabeledTextInput) line.getLineElement(1)).setText(tokens[0]);
            ((GUILabeledTextInput) line.getLineElement(3)).setText(tokens[1]);
            ((GUILabeledTextInput) line.getLineElement(5)).setText(tokens[2]);
        }

        //Add existing active attribute modifiers (these ones should only get applied when the item is in a tiamat tag slotting *AND* is currently being used; eg. attacking with a weapon)
        for (String modString : ActiveAttributeModTags.getActiveMods(stack))
        {
            String[] tokens = Tools.fixedSplit(modString, ";");
            if (tokens.length != 3) continue;

            activeAttributeList.addLine();
            GUIList.Line line = activeAttributeList.getLastFilledLine();
            ((GUILabeledTextInput) line.getLineElement(1)).setText(tokens[0]);
            ((GUILabeledTextInput) line.getLineElement(3)).setText(tokens[1]);
            ((GUILabeledTextInput) line.getLineElement(5)).setText(tokens[2]);
        }


        //Part Slots tab
        //TODO or remove


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

            //Attribute Modifiers
            for (GUIList.Line line : passiveAttributeList.getLines())
            {
                if (!((GUILabeledTextInput) line.getLineElement(1)).valid() || !((GUILabeledTextInput) line.getLineElement(3)).valid() || !((GUILabeledTextInput) line.getLineElement(5)).valid()) return;
            }
            for (GUIList.Line line : activeAttributeList.getLines())
            {
                if (!((GUILabeledTextInput) line.getLineElement(1)).valid() || !((GUILabeledTextInput) line.getLineElement(3)).valid() || !((GUILabeledTextInput) line.getLineElement(5)).valid()) return;
            }


            //Set stack params

            //General
            stack.setStackDisplayName(name.getText());
            MiscTags.setItemSlotting(stack, slotting.getText());
            MiscTags.setItemLevel(stack, FilterInt.INSTANCE.parse(level.getText()));
            MiscTags.setItemLevelReq(stack, FilterInt.INSTANCE.parse(levelReq.getText()));
            MiscTags.setItemValue(stack, FilterInt.INSTANCE.parse(value.getText()));
            ActionTags.setItemAction1(stack, action1.getText());
            ActionTags.setItemAction2(stack, action2.getText());
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

            //Attribute modifiers
            PassiveAttributeModTags.clearPassiveMods(stack);
            for (GUIList.Line line : passiveAttributeList.getLines())
            {
                PassiveAttributeModTags.addPassiveMod(stack, ((GUILabeledTextInput) line.getLineElement(1)).getText() + ";" + ((GUILabeledTextInput) line.getLineElement(3)).getText() + ";" + ((GUILabeledTextInput) line.getLineElement(5)).getText());
            }
            ActiveAttributeModTags.clearActiveMods(stack);
            for (GUIList.Line line : activeAttributeList.getLines())
            {
                ActiveAttributeModTags.addActiveMod(stack, ((GUILabeledTextInput) line.getLineElement(1)).getText() + ";" + ((GUILabeledTextInput) line.getLineElement(3)).getText() + ";" + ((GUILabeledTextInput) line.getLineElement(5)).getText());
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
