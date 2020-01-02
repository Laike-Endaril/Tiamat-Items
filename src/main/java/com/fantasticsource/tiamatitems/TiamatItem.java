package com.fantasticsource.tiamatitems;

import com.fantasticsource.tiamatitems.nbt.SlotTags;
import com.fantasticsource.tiamatitems.nbt.TextureTags;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
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
        ArrayList<String> slottings = SlotTags.getItemSlots(stack);
        if (slottings.contains("Any")) return true;

        switch (armorType)
        {
            case HEAD:
                return slottings.contains("Head") || slottings.contains("Armor");
            case CHEST:
                return slottings.contains("Chest") || slottings.contains("Armor");
            case LEGS:
                return slottings.contains("Legs") || slottings.contains("Armor");
            case FEET:
                return slottings.contains("Feet") || slottings.contains("Armor");

            case MAINHAND:
                return slottings.contains("Mainhand") || slottings.contains("Hand") || slottings.contains("Hotbar");
            case OFFHAND:
                return slottings.contains("Offhand") || slottings.contains("Hand");
        }

        return false;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
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
