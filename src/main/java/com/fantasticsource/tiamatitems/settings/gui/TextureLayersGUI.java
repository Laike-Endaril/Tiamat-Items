package com.fantasticsource.tiamatitems.settings.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIButton;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUIColor;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.filter.FilterColor;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.mctools.gui.element.view.GUIView;
import com.fantasticsource.mctools.gui.screen.ColorSelectionGUI;
import com.fantasticsource.tiamatitems.TextureCache;
import com.fantasticsource.tiamatitems.gui.LayerSelectionGUI;
import com.fantasticsource.tiamatitems.itemeditor.GUIItemLayer;
import com.fantasticsource.tiamatitems.trait.recalculable.element.CRTraitElement_TextureLayers;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;

import java.util.ArrayList;

import static com.fantasticsource.tiamatitems.TiamatItems.FILTER_POSITIVE;

public class TextureLayersGUI extends GUIScreen
{
    protected int state;

    protected TextureLayersGUI(int state)
    {
        this.state = state;
    }

    public static TextureLayersGUI show(CRTraitElement_TextureLayers traitElement, int state)
    {
        TextureLayersGUI gui = new TextureLayersGUI(state);
        showStacked(gui);
        gui.drawStack = false;


        //Background
        gui.root.add(new GUIDarkenedBackground(gui));


        //Header
        GUINavbar navbar = new GUINavbar(gui);
        GUITextButton done = new GUITextButton(gui, "Done", Color.GREEN);
        GUITextButton cancel = new GUITextButton(gui, "Cancel", Color.RED);
        gui.root.addAll(navbar, done, cancel);


        //Main
        GUIList layers = new GUIList(gui, true, 0.98, 1 - (cancel.y + cancel.height))
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
                                new GUIText(screen, "Texture: ").addClickActions(texture::click),
                                texture.addClickActions(layer::click),
                                new GUIElement(screen, 1, 0),
                                new GUIText(screen, "Color: ").addClickActions(color::click),
                                color.addClickActions(() -> new ColorSelectionGUI(color).addOnClosedActions(() ->
                                {
                                    String text = texture.getText() + ":" + color.getText();
                                    if (!layer.getLayer().equals(text))
                                    {
                                        layer.setLayer(text);
                                    }
                                }))
                        );

                GUIButton duplicateButton = GUIButton.newDuplicateButton(screen);
                duplicateButton.addClickActions(() ->
                {
                    int index = getLineIndexContaining(layer);
                    if (index == -1) index = lineCount() - 1;
                    index++;
                    GUIList.Line line = addLine(index);

                    ((GUIItemLayer) line.getLineElement(1)).setLayer(layer.getLayer());
                    GUIView view2 = (GUIView) line.getLineElement(2);
                    GUIText texture2 = (GUIText) view2.get(1);
                    texture2.setText(texture.getText());
                    GUIColor color2 = (GUIColor) view2.get(4);
                    color2.setValue(color.getValue().copy());
                });

                return new GUIElement[]
                        {
                                duplicateButton,
                                layer.addClickActions(() -> new LayerSelectionGUI(layer).addOnClosedActions(() ->
                                {
                                    String[] tokens2 = Tools.fixedSplit(layer.getLayer(), ":");
                                    texture.setText(tokens2[0] + ":" + tokens2[1]);
                                    layer.setLayer(texture.getText() + ":" + color.getText());
                                })),
                                view
                        };
            }
        };
        GUIVerticalScrollbar scrollbar2 = new GUIVerticalScrollbar(gui, 0.02, 1 - (cancel.y + cancel.height), Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, layers);

        gui.root.addAll
                (
                        layers,
                        scrollbar2
                );

        //Add existing layers
        ArrayList<String> layerStrings = traitElement.layerGroups.get(state);
        if (layerStrings != null)
        {
            for (String layerString : layerStrings)
            {
                String[] tokens = Tools.fixedSplit(layerString, ":");
                if (tokens.length != 3 || !FilterNotEmpty.INSTANCE.acceptable(tokens[0]) || !FILTER_POSITIVE.acceptable(tokens[1]) || !FilterColor.INSTANCE.acceptable(tokens[2])) continue;

                GUIList.Line line = layers.addLine();
                ((GUIItemLayer) line.getLineElement(1)).setLayer(layerString);
                GUIView view = (GUIView) line.getLineElement(2);
                GUIText texture = (GUIText) view.get(1);
                texture.setText(tokens[0] + ":" + tokens[1]);
                GUIColor color = (GUIColor) view.get(4);
                color.setValue(new Color(FilterColor.INSTANCE.parse(tokens[2])));
            }
        }


        //Add main header actions
        cancel.addRecalcActions(() ->
        {
            layers.height = 1 - (cancel.y + cancel.height);
            scrollbar2.height = 1 - (cancel.y + cancel.height);
        });
        cancel.addClickActions(gui::close);
        done.addClickActions(() ->
        {
            //Processing
            ArrayList<String> layerGroup = new ArrayList<>();
            for (GUIList.Line line : layers.getLines())
            {
                layerGroup.add(((GUIItemLayer) line.getLineElement(1)).getLayer());
            }
            traitElement.layerGroups.put(state, layerGroup);


            //Close GUI
            gui.close();
        });


        //Recalc once to fix texture colors
        gui.recalc();


        //Return gui reference
        return gui;
    }

    @Override
    public String title()
    {
        switch (state)
        {
            case 0:
                return "'Empty Item' Layers";

            case 1:
                return "'Unusable Item' Layers";

            case 2:
                return "'Usable Item' Layers";

            case 3:
                return "'Full Item' Layers";

            default:
                return "UNKNOWN STATE!";
        }
    }
}
