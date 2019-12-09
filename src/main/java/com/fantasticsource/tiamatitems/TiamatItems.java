package com.fantasticsource.tiamatitems;

import com.evilnotch.iitemrender.handlers.IItemRendererHandler;
import com.fantasticsource.mctools.gui.element.text.filter.FilterRangedInt;
import com.fantasticsource.tiamatitems.itemeditor.BlockItemEditor;
import com.fantasticsource.tiamatitems.itemeditor.ItemItemEditor;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;

@Mod(modid = TiamatItems.MODID, name = TiamatItems.NAME, version = TiamatItems.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.029a,);required-after:iitemrenderer@[1.0,)")
public class TiamatItems
{
    public static final String MODID = "tiamatitems";
    public static final String NAME = "Tiamat Items";
    public static final String VERSION = "1.12.2.000";

    public static final FilterRangedInt FILTER_POSITIVE = FilterRangedInt.get(0, Integer.MAX_VALUE);

    @GameRegistry.ObjectHolder(MODID + ":itemeditor")
    public static BlockItemEditor blockItemEditor;
    @GameRegistry.ObjectHolder(MODID + ":itemeditor")
    public static ItemItemEditor itemItemEditor;
    @GameRegistry.ObjectHolder(MODID + ":tiamatitem")
    public static TiamatItem tiamatItem;

    public static CreativeTabs creativeTab = new CreativeTabs(MODID)
    {
        @Override
        public ItemStack getTabIconItem()
        {
            return new ItemStack(tiamatItem);
        }

        @Override
        public void displayAllRelevantItems(NonNullList<ItemStack> itemStacks)
        {
            super.displayAllRelevantItems(itemStacks);
        }
    };

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        Network.init();
        MinecraftForge.EVENT_BUS.register(TiamatItems.class);
    }

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            //Physical client

            TextureCache.addRawTextureLayers();
        }
    }

    @SubscribeEvent
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }


    @SubscribeEvent
    public static void blockRegistry(RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> registry = event.getRegistry();
        registry.register(new BlockItemEditor());
    }

    @SubscribeEvent
    public static void itemRegistry(RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.register(new ItemItemEditor());

        registry.register(new TiamatItem());
    }

    @SubscribeEvent
    public static void modelRegistry(ModelRegistryEvent event)
    {
        ModelLoader.setCustomModelResourceLocation(itemItemEditor, 0, new ModelResourceLocation(MODID + ":itemeditor", "inventory"));

        ModelLoader.setCustomModelResourceLocation(tiamatItem, 0, new ModelResourceLocation(MODID + ":tiamatitem", "inventory"));
        IItemRendererHandler.register(tiamatItem, new TiamatItemRenderer());
    }


    @SubscribeEvent
    public static void clientDisconnectFromServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        TextureCache.clear(event);
    }


    @SubscribeEvent
    public static void rightClickItem(PlayerInteractEvent.RightClickItem event)
    {
        ItemStack stack = event.getItemStack();

        if (stack.getItem() == tiamatItem)
        {
            NBTTagCompound compound = stack.getTagCompound();
            if (compound == null)
            {
                compound = new NBTTagCompound();
                stack.setTagCompound(compound);
            }

            compound.setTag(MODID, new NBTTagCompound());
            compound = compound.getCompoundTag(MODID);

            compound.setTag("layers", new NBTTagList());
            NBTTagList list = compound.getTagList("layers", Constants.NBT.TAG_STRING);

            list.appendTag(new NBTTagString("Test:0:ff000077"));
        }
    }


    public static boolean itemHasCategoryTag(ItemStack stack, String category, String tag)
    {
        if (!stack.hasTagCompound()) return false;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(MODID)) return false;

        compound = compound.getCompoundTag(MODID);
        if (!compound.hasKey("categories")) return false;

        compound = compound.getCompoundTag("categories");
        if (!compound.hasKey(category)) return false;

        NBTTagList list = compound.getTagList(category, Constants.NBT.TAG_STRING);
        for (int i = list.tagCount() - 1; i >= 0; i--) if (list.getStringTagAt(i).equals(tag)) return true;
        return false;
    }

    public static ArrayList<String> getItemCategoryTags(ItemStack stack, String category)
    {
        ArrayList<String> result = new ArrayList<>();

        if (!stack.hasTagCompound()) return result;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(MODID)) return result;

        compound = compound.getCompoundTag(MODID);
        if (!compound.hasKey("categories")) return result;

        compound = compound.getCompoundTag("categories");
        if (!compound.hasKey(category)) return result;

        NBTTagList list = compound.getTagList(category, Constants.NBT.TAG_STRING);
        for (int i = 0; i < list.tagCount(); i++) result.add(list.getStringTagAt(i));

        return result;
    }

    public static ArrayList<String> getItemCategories(ItemStack stack)
    {
        ArrayList<String> result = new ArrayList<>();

        if (!stack.hasTagCompound()) return result;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(MODID)) return result;

        compound = compound.getCompoundTag(MODID);
        if (!compound.hasKey("categories")) return result;

        compound = compound.getCompoundTag("categories");
        result.addAll(compound.getKeySet());

        return result;
    }

    public static void addItemCategoryTag(ItemStack stack, String category, String tag)
    {
        if (itemHasCategoryTag(stack, category, tag)) return;

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(MODID)) compound.setTag(MODID, new NBTTagCompound());
        compound = compound.getCompoundTag(MODID);

        if (!compound.hasKey("categories")) compound.setTag("categories", new NBTTagCompound());
        compound = compound.getCompoundTag("categories");

        if (!compound.hasKey(category)) compound.setTag(category, new NBTTagList());
        NBTTagList list = compound.getTagList(category, Constants.NBT.TAG_STRING);

        list.appendTag(new NBTTagString(tag));
    }


    public static void removeItemCategoryTag(ItemStack stack, String category, String tag)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(MODID)) return;

        compound = compound.getCompoundTag(MODID);
        if (!compound.hasKey("categories")) return;

        compound = compound.getCompoundTag("categories");
        if (!compound.hasKey(category)) return;

        NBTTagList list = compound.getTagList(category, Constants.NBT.TAG_STRING);
        for (int i = 0; i < list.tagCount(); i++)
        {
            if (list.getStringTagAt(i).equals(tag))
            {
                list.removeTag(i);
                if (list.tagCount() == 0) removeItemCategory(stack, category);
                break;
            }
        }
    }

    public static void clearItemCategories(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(MODID)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(MODID);
        if (!compound.hasKey("categories")) return;

        compound.removeTag("categories");
    }

    public static void removeItemCategory(ItemStack stack, String category)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(MODID)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(MODID);
        if (!compound.hasKey("categories")) return;

        NBTTagCompound categories = compound.getCompoundTag("categories");
        if (!categories.hasKey(category)) return;

        categories.removeTag(category);

        if (categories.getKeySet().size() == 0)
        {
            compound.removeTag("categories");

            if (compound.getKeySet().size() == 0) mainTag.removeTag(MODID);
        }
    }

    public static void renameItemCategory(ItemStack stack, String oldCategory, String newCategory)
    {
        if (oldCategory.equals(newCategory)) return;

        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(MODID)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(MODID);
        if (!compound.hasKey("categories")) return;

        NBTTagCompound categories = compound.getCompoundTag("categories");
        if (!categories.hasKey(oldCategory)) return;

        categories.setTag(newCategory, categories.getTag(oldCategory));
        categories.removeTag(oldCategory);
    }
}
