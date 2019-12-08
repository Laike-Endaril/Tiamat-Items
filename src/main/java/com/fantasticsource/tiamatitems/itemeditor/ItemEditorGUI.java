package com.fantasticsource.tiamatitems.itemeditor;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUIColor;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.filter.FilterColor;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.view.GUIArrayList;
import com.fantasticsource.mctools.gui.element.view.GUIAutocroppedView;
import com.fantasticsource.mctools.gui.element.view.GUITabView;
import com.fantasticsource.tiamatitems.Network;
import com.fantasticsource.tiamatitems.TiamatItems;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;

import static com.fantasticsource.tiamatitems.TiamatItems.FILTER_POSITIVE;
import static com.fantasticsource.tiamatitems.TiamatItems.MODID;

public class ItemEditorGUI extends GUIScreen
{
    public static final Color
            AL_WHITE = Color.WHITE.copy().setAF(0.3f),
            AL_BLACK = Color.BLACK.copy().setAF(0.3f);

    public static void show()
    {
        ItemStack stack = Minecraft.getMinecraft().player.getHeldItemMainhand();
        if (stack.getItem() != TiamatItems.tiamatItem)
        {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "Right click this block with a " + new ItemStack(TiamatItems.tiamatItem).getDisplayName() + " to start"));
            return;
        }


        ItemEditorGUI gui = new ItemEditorGUI();
        Minecraft.getMinecraft().displayGuiScreen(gui);


        //Background
        gui.root.add(new GUIGradient(gui, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.85f)));


        //Header
        GUINavbar navbar = new GUINavbar(gui, Color.AQUA);
        GUITextButton save = new GUITextButton(gui, "Save", Color.GREEN);
        GUITextButton cancel = new GUITextButton(gui, "Cancel", Color.RED);
        gui.root.addAll(navbar, save, cancel);


        GUITabView tabView = new GUITabView(gui, 1, 1 - (cancel.y + cancel.height), "General", "Texture");
        gui.root.add(tabView);


        //General tab
        GUILabeledTextInput name = new GUILabeledTextInput(gui, "Name: ", stack.getDisplayName(), FilterNotEmpty.INSTANCE);
        tabView.tabViews.get(0).addAll
                (
                        name
                );


        //Texture tab
        GUIArrayList<GUIElement> arrayList = new GUIArrayList<GUIElement>(gui, 0.98, 1)
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

            @Override
            public GUIElement newLineBackgroundElement()
            {
                return new GUIGradient(gui, 1, 1, AL_WHITE, AL_WHITE, AL_BLACK, AL_BLACK);
            }
        };
        GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(gui, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, arrayList);
        tabView.tabViews.get(1).addAll(arrayList, scrollbar);


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

                        arrayList.addLine();
                        GUIAutocroppedView line = arrayList.get(arrayList.lineCount() - 1);
                        ((GUILabeledTextInput) line.get(3)).setInput(FilterNotEmpty.INSTANCE.parse(tokens[0]));
                        ((GUILabeledTextInput) line.get(4)).setInput("" + FILTER_POSITIVE.parse(tokens[1]));
                        ((GUIColor) line.get(5)).setValue(new Color(FilterColor.INSTANCE.parse(tokens[2])));
                    }
                }
            }
        }


        //Add actions
        cancel.addRecalcActions(() -> tabView.height = 1 - (cancel.y + cancel.height));
        cancel.addClickActions(gui::close);
        save.addClickActions(() ->
        {
            if (!name.valid()) return;

            String[] layers = new String[arrayList.lineCount()];
            for (int i = 0; i < layers.length; i++)
            {
                GUIAutocroppedView line = arrayList.get(i);

                GUILabeledTextInput texture = (GUILabeledTextInput) line.get(3);
                GUILabeledTextInput subimage = (GUILabeledTextInput) line.get(4);
                if (!texture.valid() || !subimage.valid()) return;

                GUIColor color = (GUIColor) line.get(5);

                layers[i] = texture.getText().trim() + ':' + subimage.getText().trim() + ':' + color.getText();
            }

            Network.WRAPPER.sendToServer(new Network.EditItemPacket(name.getText(), layers));
            gui.close();
        });
    }

    @Override
    public String title()
    {
        return "Item Editor";
    }
}
