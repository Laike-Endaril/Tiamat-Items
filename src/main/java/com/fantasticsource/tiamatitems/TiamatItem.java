package com.fantasticsource.tiamatitems;

import com.fantasticsource.tiamatitems.nbt.LayerTags;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

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
        for (String layer : layers) LayerTags.addItemLayer(stack, layer);
        if (cacheLayers) LayerTags.addItemLayerCacheTag(stack);
        if (cacheTexture) LayerTags.addItemTextureCacheTag(stack);
        return stack;
    }

    @Override
    public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity)
    {
        //Vanilla equipment slot support
        //Tiamat equipment slot support
        //Baubles equipment slot support?
        //TODO
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        //TODO
    }
}
