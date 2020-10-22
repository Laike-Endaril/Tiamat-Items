package com.fantasticsource.tiamatitems.settings.gui;

import com.fantasticsource.mctools.Slottings;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.Namespace;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIButton;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.*;
import com.fantasticsource.mctools.gui.element.text.filter.FilterBlacklist;
import com.fantasticsource.mctools.gui.element.text.filter.FilterFloat;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.text.filter.FilterRangedInt;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.mctools.gui.element.view.GUITabView;
import com.fantasticsource.mctools.gui.screen.ColorSelectionGUI;
import com.fantasticsource.mctools.gui.screen.StringListGUI;
import com.fantasticsource.mctools.gui.screen.TextSelectionGUI;
import com.fantasticsource.tiamatitems.Network;
import com.fantasticsource.tiamatitems.settings.CRarity;
import com.fantasticsource.tiamatitems.settings.CSettings;
import com.fantasticsource.tiamatitems.trait.CItemType;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitPool;
import com.fantasticsource.tiamatitems.trait.unrecalculable.CUnrecalculableTraitPool;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.Predicate;

public class SettingsGUI extends GUIScreen
{
    public static final FilterRangedInt MAX_ITEM_LEVEL_FILTER = FilterRangedInt.get(1, Integer.MAX_VALUE);
    public static final String[] TEXT_COLOR_OPTIONS = new String[]
            {
                    TextFormatting.WHITE + TextFormatting.WHITE.name(),
                    TextFormatting.GRAY + TextFormatting.GRAY.name(),
                    TextFormatting.DARK_GRAY + TextFormatting.DARK_GRAY.name(),
                    TextFormatting.BLACK + TextFormatting.BLACK.name(),
                    TextFormatting.RED + TextFormatting.RED.name(),
                    TextFormatting.YELLOW + TextFormatting.YELLOW.name(),
                    TextFormatting.GREEN + TextFormatting.GREEN.name(),
                    TextFormatting.AQUA + TextFormatting.AQUA.name(),
                    TextFormatting.BLUE + TextFormatting.BLUE.name(),
                    TextFormatting.LIGHT_PURPLE + TextFormatting.LIGHT_PURPLE.name(),
                    TextFormatting.DARK_RED + TextFormatting.DARK_RED.name(),
                    TextFormatting.GOLD + TextFormatting.GOLD.name(),
                    TextFormatting.DARK_GREEN + TextFormatting.DARK_GREEN.name(),
                    TextFormatting.DARK_AQUA + TextFormatting.DARK_AQUA.name(),
                    TextFormatting.DARK_BLUE + TextFormatting.DARK_BLUE.name(),
                    TextFormatting.DARK_PURPLE + TextFormatting.DARK_PURPLE.name()
            };

    public CSettings settings;

    protected LinkedHashMap<GUILabeledTextInput, CRecalculableTraitPool> nameElementToRecalculableTraitPoolMap = new LinkedHashMap<>();
    protected LinkedHashMap<GUILabeledTextInput, CUnrecalculableTraitPool> nameElementToUnrecalculableTraitPoolMap = new LinkedHashMap<>();
    protected LinkedHashMap<GUILabeledTextInput, CRarity> nameElementToRarityMap = new LinkedHashMap<>();
    protected LinkedHashMap<GUILabeledTextInput, CItemType> nameElementToItemTypeMap = new LinkedHashMap<>();
    protected LinkedHashMap<GUILabeledTextInput, LinkedHashSet<String>> nameElementToSlotTypeMap = new LinkedHashMap<>();

    protected SettingsGUI(CSettings settings)
    {
        this.settings = settings;
    }

    public static void show(Network.OpenSettingsPacket packet)
    {
        SettingsGUI gui = new SettingsGUI(packet.settings);
        Minecraft.getMinecraft().displayGuiScreen(gui);


        //Background
        gui.root.add(new GUIDarkenedBackground(gui));


        //Header
        GUINavbar navbar = new GUINavbar(gui);
        GUITextButton saveAndClose = new GUITextButton(gui, "Save and Close", Color.GREEN);
        GUITextButton saveWithoutClosing = new GUITextButton(gui, "Save Without Closing", Color.GREEN);
        GUITextButton cancel = new GUITextButton(gui, "Close Without Saving", Color.RED);
        gui.root.addAll(navbar, saveAndClose, saveWithoutClosing, cancel);


        //Main
        GUITabView tabView = new GUITabView(gui, 1, 1 - (cancel.y + cancel.height), "General", "Trait Pools (Recalculable)", "Trait Pools (Unrecalculable)", "Rarities", "Item Types", "Attribute Balance Multipliers", "Slot Types");
        gui.root.add(tabView);


        //General tab
        GUILabeledTextInput maxItemLevel = new GUILabeledTextInput(gui, " Max Item Level: ", "" + gui.settings.maxItemLevel, MAX_ITEM_LEVEL_FILTER);
        GUILabeledTextInput baseMultiplier = new GUILabeledTextInput(gui, " Base Trait Multiplier: ", "" + gui.settings.baseMultiplier, FilterFloat.INSTANCE);
        GUILabeledTextInput multiplierBonusPerLevel = new GUILabeledTextInput(gui, " Trait Multiplier Bonus Per Item Level: ", "" + gui.settings.multiplierBonusPerLevel, FilterFloat.INSTANCE);
        tabView.tabViews.get(0).addAll
                (
                        new GUITextSpacer(gui),
                        new GUIText(gui, " Current Version: " + CSettings.SETTINGS.getVersion()),
                        new GUIElement(gui, 1, 0),
                        new GUIText(gui, " Pending Version: " + packet.settings.getVersion()),
                        new GUITextSpacer(gui),
                        maxItemLevel,
                        new GUITextSpacer(gui),
                        baseMultiplier,
                        new GUITextSpacer(gui),
                        multiplierBonusPerLevel
                );


        //Trait Pools (Recalculable) tab
        GUIList recalculableTraitPools = new GUIList(gui, true, 0.98, 1)
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                Namespace namespace = gui.namespaces.computeIfAbsent("Recalculable Trait Pools", o -> new Namespace());
                String nameString = namespace.getFirstAvailableNumberedName("RTraitPool");
                GUILabeledTextInput name = new GUILabeledTextInput(gui, " Pool Name: ", nameString, new FilterBlacklist("null")).setNamespace("Recalculable Trait Pools");

                gui.nameElementToRecalculableTraitPoolMap.put(name, new CRecalculableTraitPool());

                GUIButton duplicateButton = GUIButton.newDuplicateButton(screen);
                duplicateButton.addClickActions(() ->
                {
                    int index = getLineIndexContaining(name);
                    if (index == -1) index = lineCount() - 1;
                    index++;
                    GUIList.Line line = addLine(index);

                    CRecalculableTraitPool pool = (CRecalculableTraitPool) gui.nameElementToRecalculableTraitPoolMap.get(name).copy();
                    GUILabeledTextInput nameElement = (GUILabeledTextInput) line.getLineElement(3);

                    pool.name = namespace.getFirstAvailableNumberedName(name.getText() + "_Copy");
                    nameElement.setText(pool.name);

                    gui.nameElementToRecalculableTraitPoolMap.put(nameElement, pool);
                });

                return new GUIElement[]
                        {
                                duplicateButton,
                                GUIButton.newListButton(gui).addClickActions(() -> RecalculableTraitPoolGUI.show(name.getText(), gui.nameElementToRecalculableTraitPoolMap.get(name))),
                                new GUIElement(gui, 1, 0),
                                name
                        };
            }
        };
        recalculableTraitPools.addRemoveChildActions((Predicate<GUIElement>) element ->
        {
            if (element instanceof GUIList.Line)
            {
                GUIList.Line line = (GUIList.Line) element;
                GUILabeledTextInput labeledTextInput = (GUILabeledTextInput) line.getLineElement(3);
                gui.namespaces.get("Recalculable Trait Pools").inputs.remove(labeledTextInput.input);
            }
            return false;
        });
        GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(gui, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, recalculableTraitPools);
        tabView.tabViews.get(1).addAll
                (
                        recalculableTraitPools,
                        scrollbar
                );
        for (CRecalculableTraitPool pool : gui.settings.recalcTraitPools.values())
        {
            GUIList.Line line = recalculableTraitPools.addLine();
            GUILabeledTextInput nameElement = (GUILabeledTextInput) line.getLineElement(3);
            nameElement.setText(pool.name);
            gui.nameElementToRecalculableTraitPoolMap.put(nameElement, pool);
        }


        //Trait Pools (Unrecalculable) tab
        GUIList unrecalculableTraitPools = new GUIList(gui, true, 0.98, 1)
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                Namespace namespace = gui.namespaces.computeIfAbsent("Unrecalculable Trait Pools", o -> new Namespace());
                String nameString = namespace.getFirstAvailableNumberedName("UTraitPool");
                GUILabeledTextInput name = new GUILabeledTextInput(gui, " Pool Name: ", nameString, new FilterBlacklist("null")).setNamespace("Unrecalculable Trait Pools");

                gui.nameElementToUnrecalculableTraitPoolMap.put(name, new CUnrecalculableTraitPool());

                GUIButton duplicateButton = GUIButton.newDuplicateButton(screen);
                duplicateButton.addClickActions(() ->
                {
                    int index = getLineIndexContaining(name);
                    if (index == -1) index = lineCount() - 1;
                    index++;
                    GUIList.Line line = addLine(index);

                    CUnrecalculableTraitPool pool = (CUnrecalculableTraitPool) gui.nameElementToUnrecalculableTraitPoolMap.get(name).copy();
                    GUILabeledTextInput nameElement = (GUILabeledTextInput) line.getLineElement(3);

                    pool.name = namespace.getFirstAvailableNumberedName(name.getText() + "_Copy");
                    nameElement.setText(pool.name);

                    gui.nameElementToUnrecalculableTraitPoolMap.put(nameElement, pool);
                });

                return new GUIElement[]
                        {
                                duplicateButton,
                                GUIButton.newListButton(gui).addClickActions(() -> UnrecalculableTraitPoolGUI.show(name.getText(), gui.nameElementToUnrecalculableTraitPoolMap.get(name))),
                                new GUIElement(gui, 1, 0),
                                name
                        };
            }
        };
        unrecalculableTraitPools.addRemoveChildActions((Predicate<GUIElement>) element ->
        {
            if (element instanceof GUIList.Line)
            {
                GUIList.Line line = (GUIList.Line) element;
                GUILabeledTextInput labeledTextInput = (GUILabeledTextInput) line.getLineElement(3);
                gui.namespaces.get("Unrecalculable Trait Pools").inputs.remove(labeledTextInput.input);
            }
            return false;
        });
        GUIVerticalScrollbar scrollbar2 = new GUIVerticalScrollbar(gui, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, unrecalculableTraitPools);
        tabView.tabViews.get(2).addAll
                (
                        unrecalculableTraitPools,
                        scrollbar2
                );
        for (CUnrecalculableTraitPool pool : gui.settings.unrecalcTraitPools.values())
        {
            GUIList.Line line = unrecalculableTraitPools.addLine();
            GUILabeledTextInput nameElement = (GUILabeledTextInput) line.getLineElement(3);
            nameElement.setText(pool.name);
            gui.nameElementToUnrecalculableTraitPoolMap.put(nameElement, pool);
        }


        //Rarities tab
        GUIList rarities = new GUIList(gui, true, 0.98, 1)
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                Namespace namespace = gui.namespaces.computeIfAbsent("Rarities", o -> new Namespace());
                String nameString = namespace.getFirstAvailableNumberedName("Rarity");
                GUILabeledTextInput name = new GUILabeledTextInput(gui, " Rarity Name: ", nameString, FilterNotEmpty.INSTANCE).setNamespace("Rarities");

                gui.nameElementToRarityMap.put(name, new CRarity());

                GUIText colorLabel = new GUIText(gui, " Color: ");
                GUIColor color = new GUIColor(gui);
                GUIText textColorLabel = new GUIText(gui, " Text Color: ");
                GUIText textColor = new GUIText(gui, TEXT_COLOR_OPTIONS[0]);
                GUILabeledTextInput itemLevelModifier = new GUILabeledTextInput(gui, " Item Level Modifier: ", "" + new CRarity().itemLevelModifier, FilterFloat.INSTANCE);
                GUITextButton traitRollCounts = new GUITextButton(gui, "Trait Pool Set Roll Counts");

                GUIButton duplicateButton = GUIButton.newDuplicateButton(screen);
                duplicateButton.addClickActions(() ->
                {
                    int index = getLineIndexContaining(name);
                    if (index == -1) index = lineCount() - 1;
                    index++;
                    GUIList.Line line = addLine(index);

                    CRarity rarity = (CRarity) gui.nameElementToRarityMap.get(name).copy();
                    rarity.name = namespace.getFirstAvailableNumberedName(name.getText() + "_Copy");

                    GUILabeledTextInput nameElement = (GUILabeledTextInput) line.getLineElement(2);
                    nameElement.setText(rarity.name);

                    gui.nameElementToRarityMap.put(nameElement, rarity);

                    ((GUIColor) line.getLineElement(5)).setValue(rarity.color);
                    ((GUIText) line.getLineElement(8)).setText(rarity.textColor + rarity.textColor.name());
                    ((GUILabeledTextInput) line.getLineElement(10)).setText("" + rarity.itemLevelModifier);
                });

                return new GUIElement[]
                        {
                                duplicateButton,
                                new GUIElement(gui, 1, 0),
                                name,
                                new GUIElement(gui, 1, 0),
                                colorLabel.addClickActions(color::click),
                                color.addClickActions(() -> new ColorSelectionGUI(color)),
                                new GUIElement(gui, 1, 0),
                                textColorLabel.addClickActions(textColor::click),
                                textColor.addClickActions(() -> new TextSelectionGUI(textColor, "Rarity Text Color (" + name.getText() + ")", TEXT_COLOR_OPTIONS)),
                                new GUIElement(gui, 1, 0),
                                itemLevelModifier,
                                new GUIElement(gui, 1, 0),
                                traitRollCounts.addClickActions(() -> TraitRollCountsGUI.show(name.getText(), gui.nameElementToRarityMap.get(name)))
                        };
            }
        };
        rarities.addRemoveChildActions((Predicate<GUIElement>) element ->
        {
            if (element instanceof GUIList.Line)
            {
                GUIList.Line line = (GUIList.Line) element;
                GUILabeledTextInput labeledTextInput = (GUILabeledTextInput) line.getLineElement(2);
                gui.namespaces.get("Rarities").inputs.remove(labeledTextInput.input);
            }
            return false;
        });
        GUIVerticalScrollbar scrollbar3 = new GUIVerticalScrollbar(gui, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, rarities);
        tabView.tabViews.get(3).addAll
                (
                        rarities,
                        scrollbar3
                );
        for (CRarity rarity : gui.settings.rarities.values())
        {
            GUIList.Line line = rarities.addLine();
            GUILabeledTextInput name = (GUILabeledTextInput) line.getLineElement(2);
            name.setText(rarity.name);
            gui.nameElementToRarityMap.put(name, rarity);

            ((GUIColor) line.getLineElement(5)).setValue(rarity.color);
            ((GUIText) line.getLineElement(8)).setText(rarity.textColor + rarity.textColor.name());
            ((GUILabeledTextInput) line.getLineElement(10)).setText("" + rarity.itemLevelModifier);
        }


        //Item Types tab
        GUIList itemTypes = new GUIList(gui, true, 0.98, 1)
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                Namespace namespace = gui.namespaces.computeIfAbsent("Item Types", o -> new Namespace());
                GUILabeledTextInput name = new GUILabeledTextInput(gui, " Item Type Name: ", namespace.getFirstAvailableNumberedName("Item Type"), FilterNotEmpty.INSTANCE).setNamespace("Item Types");

                CItemType itemType = new CItemType();
                gui.nameElementToItemTypeMap.put(name, itemType);

                GUIText slottingLabel = new GUIText(gui, " Slotting: ");
                GUIText slotting = new GUIText(gui, itemType.slotting, getIdleColor(Color.WHITE), getHoverColor(Color.WHITE), Color.WHITE);
                GUILabeledTextInput traitLevelMultiplier = new GUILabeledTextInput(gui, " Trait Level Multiplier: ", "" + itemType.traitLevelMultiplier, FilterFloat.INSTANCE);
                GUILabeledTextInput value = new GUILabeledTextInput(gui, " Base Monetary Value: ", "" + itemType.value, FilterFloat.INSTANCE);

                GUIButton duplicateButton = GUIButton.newDuplicateButton(screen);
                duplicateButton.addClickActions(() ->
                {
                    int index = getLineIndexContaining(name);
                    if (index == -1) index = lineCount() - 1;
                    index++;
                    GUIList.Line line = addLine(index);

                    CItemType itemType2 = (CItemType) gui.nameElementToItemTypeMap.get(name).copy();
                    itemType2.name = namespace.getFirstAvailableNumberedName(name.getText() + "_Copy");

                    GUILabeledTextInput name2 = (GUILabeledTextInput) line.getLineElement(2);
                    name2.setText(itemType2.name);

                    gui.nameElementToItemTypeMap.put(name2, itemType2);

                    ((GUIText) line.getLineElement(5)).setText("" + itemType2.slotting);
                    ((GUILabeledTextInput) line.getLineElement(7)).setText("" + itemType2.traitLevelMultiplier);
                    ((GUILabeledTextInput) line.getLineElement(9)).setText("" + itemType2.value);
                });

                return new GUIElement[]
                        {
                                duplicateButton,
                                new GUIElement(gui, 1, 0),
                                name,
                                new GUIElement(gui, 1, 0),
                                slottingLabel, slotting.addClickActions(() -> new TextSelectionGUI(slotting, "Slotting Selection", Slottings.availableSlottings())),
                                new GUIElement(gui, 1, 0),
                                traitLevelMultiplier,
                                new GUIElement(gui, 1, 0),
                                value,
                                new GUIElement(gui, 1, 0),
                                new GUITextButton(gui, "Edit Static Recalculable Traits").addClickActions(() -> RecalculableTraitListGUI.show(name.getText(), gui.nameElementToItemTypeMap.get(name).staticRecalculableTraits)),
                                new GUITextButton(gui, "Edit Static Unrecalculable Traits").addClickActions(() -> UnrecalculableTraitListGUI.show(name.getText(), gui.nameElementToItemTypeMap.get(name).staticUnrecalculableTraits)),
                                new GUIElement(gui, 1, 0),
                                new GUITextButton(gui, "Edit Random Recalculable Trait Pool Sets").addClickActions(() -> RecalculableTraitPoolSetsGUI.show(name.getText(), gui.nameElementToItemTypeMap.get(name).randomRecalculableTraitPoolSets, gui.nameElementToItemTypeMap.get(name).randomUnrecalculableTraitPoolSets.keySet())),
                                new GUITextButton(gui, "Edit Random Unrecalculable Trait Pool Sets").addClickActions(() -> UnrecalculableTraitPoolSetsGUI.show(name.getText(), gui.nameElementToItemTypeMap.get(name).randomUnrecalculableTraitPoolSets, gui.nameElementToItemTypeMap.get(name).randomRecalculableTraitPoolSets.keySet()))
                        };
            }
        };
        itemTypes.addRemoveChildActions((Predicate<GUIElement>) element ->
        {
            if (element instanceof GUIList.Line)
            {
                GUIList.Line line = (GUIList.Line) element;
                GUILabeledTextInput labeledTextInput = (GUILabeledTextInput) line.getLineElement(2);
                gui.namespaces.get("Item Types").inputs.remove(labeledTextInput.input);
            }
            return false;
        });
        GUIVerticalScrollbar scrollbar4 = new GUIVerticalScrollbar(gui, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, itemTypes);
        tabView.tabViews.get(4).addAll
                (
                        itemTypes,
                        scrollbar4
                );
        for (CItemType itemType : gui.settings.itemTypes.values())
        {
            GUIList.Line line = itemTypes.addLine();
            GUILabeledTextInput name = (GUILabeledTextInput) line.getLineElement(2);
            name.setText(itemType.name);
            gui.nameElementToItemTypeMap.put(name, itemType);

            ((GUIText) line.getLineElement(5)).setText(itemType.slotting);
            ((GUILabeledTextInput) line.getLineElement(7)).setText("" + itemType.traitLevelMultiplier);
            ((GUILabeledTextInput) line.getLineElement(9)).setText("" + itemType.value);
        }


        //Attribute Multipliers tab
        GUIList attributeBalanceMultipliers = new GUIList(gui, true, 0.98, 1)
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                return new GUIElement[]{
                        new GUIElement(gui, 1, 0),
                        new GUILabeledTextInput(gui, " Attribute: ", "generic.attributeName", FilterNotEmpty.INSTANCE).setNamespace("Attribute Balance Multipliers"),
                        new GUIElement(gui, 1, 0),
                        new GUILabeledTextInput(gui, " Multiplier: ", "1", FilterFloat.INSTANCE)
                };
            }
        };
        attributeBalanceMultipliers.addRemoveChildActions((Predicate<GUIElement>) element ->
        {
            if (element instanceof GUIList.Line)
            {
                GUIList.Line line = (GUIList.Line) element;
                GUILabeledTextInput labeledTextInput = (GUILabeledTextInput) line.getLineElement(1);
                gui.namespaces.get("Attribute Balance Multipliers").inputs.remove(labeledTextInput.input);
            }
            return false;
        });
        GUIVerticalScrollbar scrollbar5 = new GUIVerticalScrollbar(gui, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, attributeBalanceMultipliers);
        tabView.tabViews.get(5).addAll
                (
                        attributeBalanceMultipliers,
                        scrollbar5
                );
        for (Map.Entry<String, Double> entry : CSettings.attributeBalanceMultipliers.entrySet())
        {
            GUIList.Line line = attributeBalanceMultipliers.addLine();
            ((GUILabeledTextInput) line.getLineElement(1)).setInput(entry.getKey());
            ((GUILabeledTextInput) line.getLineElement(3)).setInput("" + entry.getValue());
        }


        //Slot Types tab
        GUIList slotTypes = new GUIList(gui, true, 0.98, 1)
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                Namespace namespace = gui.namespaces.computeIfAbsent("Slot Types", o -> new Namespace());
                String nameString = namespace.getFirstAvailableNumberedName("SlotType");
                GUILabeledTextInput name = new GUILabeledTextInput(gui, " Slot Type Name: ", nameString, FilterNotEmpty.INSTANCE).setNamespace("Slot Types");

                gui.nameElementToSlotTypeMap.put(name, new LinkedHashSet<>());

                GUIButton duplicateButton = GUIButton.newDuplicateButton(screen);
                duplicateButton.addClickActions(() ->
                {
                    int index = getLineIndexContaining(name);
                    if (index == -1) index = lineCount() - 1;
                    index++;
                    GUIList.Line line = addLine(index);

                    String nameString2 = namespace.getFirstAvailableNumberedName(name.getText() + "_Copy");

                    GUILabeledTextInput nameElement = (GUILabeledTextInput) line.getLineElement(2);
                    nameElement.setText(nameString2);

                    gui.nameElementToSlotTypeMap.put(nameElement, (LinkedHashSet<String>) gui.nameElementToSlotTypeMap.get(name).clone());
                });

                return new GUIElement[]
                        {
                                duplicateButton,
                                GUIButton.newListButton(gui).addClickActions(() -> StringListGUI.show(name.getText() + " (Slot Type)", " Item Type: ", "ItemType", gui.nameElementToSlotTypeMap.get(name))),
                                name
                        };
            }
        };
        slotTypes.addRemoveChildActions((Predicate<GUIElement>) element ->
        {
            if (element instanceof GUIList.Line)
            {
                GUIList.Line line = (GUIList.Line) element;
                GUILabeledTextInput labeledTextInput = (GUILabeledTextInput) line.getLineElement(2);
                gui.namespaces.get("Slot Types").inputs.remove(labeledTextInput.input);
            }
            return false;
        });
        GUIVerticalScrollbar scrollbar6 = new GUIVerticalScrollbar(gui, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, slotTypes);
        tabView.tabViews.get(6).addAll
                (
                        slotTypes,
                        scrollbar6
                );
        for (Map.Entry<String, LinkedHashSet<String>> entry : gui.settings.slotTypes.entrySet())
        {
            GUIList.Line line = slotTypes.addLine();
            GUILabeledTextInput nameElement = (GUILabeledTextInput) line.getLineElement(2);
            nameElement.setText(entry.getKey());
            gui.nameElementToSlotTypeMap.put(nameElement, entry.getValue());
        }


        //Insert new tabs here


        //Add main header actions
        cancel.addRecalcActions(() -> tabView.height = 1 - (cancel.y + cancel.height));
        cancel.addClickActions(gui::close);
        saveWithoutClosing.addClickActions(() ->
        {
            //Validation

            //General
            if (!maxItemLevel.valid() || !baseMultiplier.valid() || !multiplierBonusPerLevel.valid()) return;

            //Recalculable Trait Pools
            for (GUIList.Line line : recalculableTraitPools.getLines())
            {
                if (!((GUILabeledTextInput) line.getLineElement(3)).valid()) return;
            }

            //Unrecalculable Trait Pools
            for (GUIList.Line line : unrecalculableTraitPools.getLines())
            {
                if (!((GUILabeledTextInput) line.getLineElement(3)).valid()) return;
            }

            //Rarities
            for (GUIList.Line line : rarities.getLines())
            {
                if (!((GUILabeledTextInput) line.getLineElement(2)).valid()) return;
                if (!((GUILabeledTextInput) line.getLineElement(10)).valid()) return;
            }

            //Item Types
            for (GUIList.Line line : itemTypes.getLines())
            {
                if (!((GUILabeledTextInput) line.getLineElement(2)).valid()) return;
                if (!((GUILabeledTextInput) line.getLineElement(7)).valid()) return;
                if (!((GUILabeledTextInput) line.getLineElement(9)).valid()) return;
            }

            //Attribute Balance Multipliers
            for (GUIList.Line line : attributeBalanceMultipliers.getLines())
            {
                if (!((GUILabeledTextInput) line.getLineElement(1)).valid()) return;
                if (!((GUILabeledTextInput) line.getLineElement(3)).valid()) return;
            }

            //Slot Types
            for (GUIList.Line line : slotTypes.getLines())
            {
                if (!((GUILabeledTextInput) line.getLineElement(2)).valid()) return;
            }


            //Processing

            //General
            gui.settings.maxItemLevel = MAX_ITEM_LEVEL_FILTER.parse(maxItemLevel.getText());
            gui.settings.baseMultiplier = FilterFloat.INSTANCE.parse(baseMultiplier.getText());
            gui.settings.multiplierBonusPerLevel = FilterFloat.INSTANCE.parse(multiplierBonusPerLevel.getText());

            //Recalculable Trait Pools
            gui.settings.recalcTraitPools.clear();
            for (GUIList.Line line : recalculableTraitPools.getLines())
            {
                GUILabeledTextInput name = (GUILabeledTextInput) line.getLineElement(3);
                CRecalculableTraitPool pool = gui.nameElementToRecalculableTraitPoolMap.get(name);
                pool.name = name.getText();
                gui.settings.recalcTraitPools.put(name.getText(), pool);
            }

            //Unrecalculable Trait Pools
            gui.settings.unrecalcTraitPools.clear();
            for (GUIList.Line line : unrecalculableTraitPools.getLines())
            {
                GUILabeledTextInput name = (GUILabeledTextInput) line.getLineElement(3);
                CUnrecalculableTraitPool pool = gui.nameElementToUnrecalculableTraitPoolMap.get(name);
                pool.name = name.getText();
                gui.settings.unrecalcTraitPools.put(name.getText(), pool);
            }

            //Rarities
            gui.settings.rarities.clear();
            for (GUIList.Line line : rarities.getLines())
            {
                GUILabeledTextInput name = (GUILabeledTextInput) line.getLineElement(2);
                CRarity rarity = gui.nameElementToRarityMap.get(name);
                rarity.name = name.getText();
                rarity.color = ((GUIColor) line.getLineElement(5)).getValue();
                rarity.textColor = TextFormatting.getValueByName(TextFormatting.getTextWithoutFormattingCodes(((GUIText) line.getLineElement(8)).getText()));
                rarity.itemLevelModifier = FilterFloat.INSTANCE.parse(((GUILabeledTextInput) line.getLineElement(10)).getText());

                gui.settings.rarities.put(rarity.name, rarity);
            }

            //Item Types
            gui.settings.itemTypes.clear();
            for (GUIList.Line line : itemTypes.getLines())
            {
                GUILabeledTextInput name = (GUILabeledTextInput) line.getLineElement(2);
                CItemType itemType = gui.nameElementToItemTypeMap.get(name);
                itemType.name = name.getText();
                itemType.slotting = ((GUIText) line.getLineElement(5)).getText();
                itemType.traitLevelMultiplier = FilterFloat.INSTANCE.parse(((GUILabeledTextInput) line.getLineElement(7)).getText());
                itemType.value = FilterFloat.INSTANCE.parse(((GUILabeledTextInput) line.getLineElement(9)).getText());

                gui.settings.itemTypes.put(itemType.name, itemType);
            }

            //Attribute Balance Multipliers
            CSettings.attributeBalanceMultipliers.clear();
            for (GUIList.Line line : attributeBalanceMultipliers.getLines())
            {
                CSettings.attributeBalanceMultipliers.put(((GUILabeledTextInput) line.getLineElement(1)).getText(), (double) FilterFloat.INSTANCE.parse(((GUILabeledTextInput) line.getLineElement(3)).getText()));
            }

            //Slot Types
            gui.settings.slotTypes.clear();
            for (GUIList.Line line : slotTypes.getLines())
            {
                GUILabeledTextInput nameElement = (GUILabeledTextInput) line.getLineElement(2);
                gui.settings.slotTypes.put(nameElement.getText(), gui.nameElementToSlotTypeMap.get(nameElement));
            }


            //Send to server
            Network.WRAPPER.sendToServer(new Network.SaveSettingsPacket(gui.settings));
        });
        saveAndClose.addClickActions(() ->
        {
            //Validation

            //General
            if (!maxItemLevel.valid() || !baseMultiplier.valid() || !multiplierBonusPerLevel.valid()) return;

            //Recalculable Trait Pools
            for (GUIList.Line line : recalculableTraitPools.getLines())
            {
                if (!((GUILabeledTextInput) line.getLineElement(3)).valid()) return;
            }

            //Unrecalculable Trait Pools
            for (GUIList.Line line : unrecalculableTraitPools.getLines())
            {
                if (!((GUILabeledTextInput) line.getLineElement(3)).valid()) return;
            }

            //Rarities
            for (GUIList.Line line : rarities.getLines())
            {
                if (!((GUILabeledTextInput) line.getLineElement(2)).valid()) return;
                if (!((GUILabeledTextInput) line.getLineElement(10)).valid()) return;
            }

            //Item Types
            for (GUIList.Line line : itemTypes.getLines())
            {
                if (!((GUILabeledTextInput) line.getLineElement(2)).valid()) return;
                if (!((GUILabeledTextInput) line.getLineElement(7)).valid()) return;
                if (!((GUILabeledTextInput) line.getLineElement(9)).valid()) return;
            }

            //Attribute Balance Multipliers
            for (GUIList.Line line : attributeBalanceMultipliers.getLines())
            {
                if (!((GUILabeledTextInput) line.getLineElement(1)).valid()) return;
                if (!((GUILabeledTextInput) line.getLineElement(3)).valid()) return;
            }

            //Slot Types
            for (GUIList.Line line : slotTypes.getLines())
            {
                if (!((GUILabeledTextInput) line.getLineElement(2)).valid()) return;
            }


            //Processing

            //General
            gui.settings.maxItemLevel = MAX_ITEM_LEVEL_FILTER.parse(maxItemLevel.getText());
            gui.settings.baseMultiplier = FilterFloat.INSTANCE.parse(baseMultiplier.getText());
            gui.settings.multiplierBonusPerLevel = FilterFloat.INSTANCE.parse(multiplierBonusPerLevel.getText());

            //Recalculable Trait Pools
            gui.settings.recalcTraitPools.clear();
            for (GUIList.Line line : recalculableTraitPools.getLines())
            {
                GUILabeledTextInput name = (GUILabeledTextInput) line.getLineElement(3);
                CRecalculableTraitPool pool = gui.nameElementToRecalculableTraitPoolMap.get(name);
                pool.name = name.getText();
                gui.settings.recalcTraitPools.put(name.getText(), pool);
            }

            //Unrecalculable Trait Pools
            gui.settings.unrecalcTraitPools.clear();
            for (GUIList.Line line : unrecalculableTraitPools.getLines())
            {
                GUILabeledTextInput name = (GUILabeledTextInput) line.getLineElement(3);
                CUnrecalculableTraitPool pool = gui.nameElementToUnrecalculableTraitPoolMap.get(name);
                pool.name = name.getText();
                gui.settings.unrecalcTraitPools.put(name.getText(), pool);
            }

            //Rarities
            gui.settings.rarities.clear();
            for (GUIList.Line line : rarities.getLines())
            {
                GUILabeledTextInput name = (GUILabeledTextInput) line.getLineElement(2);
                CRarity rarity = gui.nameElementToRarityMap.get(name);
                rarity.name = name.getText();
                rarity.color = ((GUIColor) line.getLineElement(5)).getValue();
                rarity.textColor = TextFormatting.getValueByName(TextFormatting.getTextWithoutFormattingCodes(((GUIText) line.getLineElement(8)).getText()));
                rarity.itemLevelModifier = FilterFloat.INSTANCE.parse(((GUILabeledTextInput) line.getLineElement(10)).getText());

                gui.settings.rarities.put(rarity.name, rarity);
            }

            //Item Types
            gui.settings.itemTypes.clear();
            for (GUIList.Line line : itemTypes.getLines())
            {
                GUILabeledTextInput name = (GUILabeledTextInput) line.getLineElement(2);
                CItemType itemType = gui.nameElementToItemTypeMap.get(name);
                itemType.name = name.getText();
                itemType.slotting = ((GUIText) line.getLineElement(5)).getText();
                itemType.traitLevelMultiplier = FilterFloat.INSTANCE.parse(((GUILabeledTextInput) line.getLineElement(7)).getText());
                itemType.value = FilterFloat.INSTANCE.parse(((GUILabeledTextInput) line.getLineElement(9)).getText());

                gui.settings.itemTypes.put(itemType.name, itemType);
            }

            //Attribute Balance Multipliers
            CSettings.attributeBalanceMultipliers.clear();
            for (GUIList.Line line : attributeBalanceMultipliers.getLines())
            {
                CSettings.attributeBalanceMultipliers.put(((GUILabeledTextInput) line.getLineElement(1)).getText(), (double) FilterFloat.INSTANCE.parse(((GUILabeledTextInput) line.getLineElement(3)).getText()));
            }

            //Slot Types
            gui.settings.slotTypes.clear();
            for (GUIList.Line line : slotTypes.getLines())
            {
                GUILabeledTextInput nameElement = (GUILabeledTextInput) line.getLineElement(2);
                gui.settings.slotTypes.put(nameElement.getText(), gui.nameElementToSlotTypeMap.get(nameElement));
            }


            //Send to server
            Network.WRAPPER.sendToServer(new Network.SaveSettingsPacket(gui.settings));


            //Close GUI
            gui.close();
        });
    }

    @Override
    public String title()
    {
        return "Settings";
    }
}
