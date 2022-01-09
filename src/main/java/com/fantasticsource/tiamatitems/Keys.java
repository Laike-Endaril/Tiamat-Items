package com.fantasticsource.tiamatitems;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import static com.fantasticsource.tiamatitems.TiamatItems.NAME;

@SideOnly(Side.CLIENT)
public class Keys
{
    public static final BetterKeyBinding
            MODIFY_ITEM = new BetterKeyBinding("Modify Item", Keyboard.KEY_HOME, NAME);
    public static final BetterKeyBinding[] KEY_BINDINGS = new BetterKeyBinding[]{MODIFY_ITEM};


    public static void init(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(Keys.class);
        for (KeyBinding keyBinding : new KeyBinding[]{MODIFY_ITEM}) ClientRegistry.registerKeyBinding(keyBinding);
    }

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            for (BetterKeyBinding keyBinding : KEY_BINDINGS) keyBinding.update();
        }
    }


    public static class BetterKeyBinding extends KeyBinding
    {
        protected boolean down = false, justPressed = false, justReleased = false;

        public BetterKeyBinding(String description, int keyCode, String category)
        {
            super(description, KeyConflictContext.UNIVERSAL, keyCode, category);
        }

        public BetterKeyBinding(String description, KeyModifier keyModifier, int keyCode, String category)
        {
            super(description, KeyConflictContext.UNIVERSAL, keyModifier, keyCode, category);
        }


        public void update()
        {
            justPressed = false;
            justReleased = false;

            if (isKeyDown())
            {
                if (!down)
                {
                    down = true;
                    justPressed = true;
                }
            }
            else
            {
                if (down)
                {
                    down = false;
                    justReleased = true;
                }
            }
        }

        @Override
        public boolean isKeyDown()
        {
            int code = getKeyCode();
            switch (getKeyModifier())
            {
                case CONTROL:
                    return GuiScreen.isCtrlKeyDown() && Keyboard.isKeyDown(code);

                case SHIFT:
                    return GuiScreen.isShiftKeyDown() && Keyboard.isKeyDown(code);

                case ALT:
                    return GuiScreen.isAltKeyDown() && Keyboard.isKeyDown(code);

                default:
                    return Keyboard.isKeyDown(code);
            }
        }

        @Override
        public boolean isPressed()
        {
            return wasJustPressed();
        }

        public boolean wasJustPressed()
        {
            boolean result = justPressed;
            justPressed = false;
            return result;
        }

        public boolean wasJustReleased()
        {
            boolean result = justReleased;
            justReleased = false;
            return result;
        }
    }
}
