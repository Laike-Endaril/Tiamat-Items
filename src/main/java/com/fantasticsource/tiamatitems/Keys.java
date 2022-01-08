package com.fantasticsource.tiamatitems;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import static com.fantasticsource.tiamatitems.TiamatItems.NAME;

@SideOnly(Side.CLIENT)
public class Keys
{
    public static final KeyBinding
            MODIFY_ITEM = new KeyBinding("Modify Item", KeyConflictContext.IN_GAME, Keyboard.KEY_HOME, NAME);


    public static void init(FMLPreInitializationEvent event)
    {
        for (KeyBinding keyBinding : new KeyBinding[]{MODIFY_ITEM}) ClientRegistry.registerKeyBinding(keyBinding);
    }
}
