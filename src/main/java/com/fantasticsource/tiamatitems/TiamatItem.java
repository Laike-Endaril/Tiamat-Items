package com.fantasticsource.tiamatitems;

import com.fantasticsource.tiamatitems.nbt.MiscTags;
import com.fantasticsource.tiamatitems.nbt.TextureTags;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

import static com.fantasticsource.tiamatitems.TiamatItems.MODID;

public class TiamatItem extends Item
{
    public TiamatItem()
    {
        setCreativeTab(TiamatItems.creativeTab);

        setUnlocalizedName(MODID + ":tiamatitem");
        setRegistryName("tiamatitem");
    }

    public static ItemStack get(boolean cacheLayers, boolean cacheTexture, String... layers)
    {
        ItemStack stack = new ItemStack(TiamatItems.tiamatItem);
        for (String layer : layers)
        {
            if (layer != null) TextureTags.addItemLayer(stack, layer);
        }
        if (cacheLayers) TextureTags.addItemLayerCacheTag(stack);
        if (cacheTexture) TextureTags.addItemTextureCacheTag(stack);
        return stack;
    }

    @Override
    public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity)
    {
        String slotting = MiscTags.getItemSlotting(stack);
        if (slotting.equals("Any")) return true;

        switch (armorType)
        {
            case HEAD:
                return slotting.equals("Head") || slotting.equals("Armor");
            case CHEST:
                return slotting.equals("Chest") || slotting.equals("Armor");
            case LEGS:
                return slotting.equals("Legs") || slotting.equals("Armor");
            case FEET:
                return slotting.equals("Feet") || slotting.equals("Armor");

            case MAINHAND:
                return slotting.equals("Mainhand") || slotting.equals("Hand") || slotting.equals("Hotbar");
            case OFFHAND:
                return slotting.equals("Offhand") || slotting.equals("Hand");
        }

        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, net.minecraft.client.util.ITooltipFlag flagIn)
    {
        //TODO
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (this.isInCreativeTab(tab))
        {
            items.add(get(false, true));
        }
    }
}
