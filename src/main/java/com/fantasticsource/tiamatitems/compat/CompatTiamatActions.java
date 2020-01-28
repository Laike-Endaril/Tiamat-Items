package com.fantasticsource.tiamatitems.compat;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.text.GUIText;

import java.lang.reflect.InvocationTargetException;

public class CompatTiamatActions
{
    public static GUIText getGUIAction(GUIScreen gui, String actionName)
    {
        try
        {
            return (GUIText) Class.forName("com.fantasticsource.tiamatactions.gui.GUIAction").getConstructor(GUIScreen.class, String.class).newInstance(gui, actionName);
        }
        catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
