package com.fantasticsource.tiamatitems.nbt;

import com.fantasticsource.tiamatitems.settings.CRarity;
import com.fantasticsource.tiamatitems.settings.CSettings;
import com.fantasticsource.tiamatitems.trait.CItemType;
import com.fantasticsource.tools.Tools;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.fantasticsource.tiamatitems.TiamatItems.DOMAIN;

public class DropTransformationTags
{
    public static void setDropTransformation(ItemStack stack, String rarity, int minLevel, int maxLevel, String... itemTypes)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());

        compound = compound.getCompoundTag(DOMAIN);
        compound.setTag("dropTransform", new NBTTagCompound());

        compound = compound.getCompoundTag("dropTransform");
        compound.setString("rarity", rarity);
        compound.setInteger("minLevel", minLevel);
        compound.setInteger("maxLevel", maxLevel);
        compound.setTag("itemTypes", new NBTTagList());

        NBTTagList list = compound.getTagList("itemTypes", Constants.NBT.TAG_STRING);
        for (String itemType : itemTypes) list.appendTag(new NBTTagString(itemType));
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void tryDropTransforms(LivingDropsEvent event)
    {
        if (event.getEntityLiving().world.isRemote) return;

        for (EntityItem entityItem : event.getDrops())
        {
            ItemStack stack = entityItem.getItem();

            NBTTagCompound compound = stack.getTagCompound();
            if (compound == null || !compound.hasKey(DOMAIN)) return;

            compound = compound.getCompoundTag(DOMAIN);
            if (!compound.hasKey("dropTransform")) return;

            compound = compound.getCompoundTag("dropTransform");

            int minLevel = compound.getInteger("minLevel");
            int level = minLevel + Tools.random(compound.getInteger("maxLevel") - minLevel);

            CRarity rarity = CSettings.SETTINGS.rarities.get(compound.getString("rarity"));
            if (rarity == null) return;


            NBTTagList list = compound.getTagList("itemTypes", Constants.NBT.TAG_STRING);
            CItemType itemType = CSettings.SETTINGS.itemTypes.get(list.getStringTagAt(Tools.random(list.tagCount())));
            if (itemType == null) return;


            ItemStack generated = itemType.generateItem(level, rarity);
            if (generated == null || generated.isEmpty()) return;


            stack.setTagCompound(generated.getTagCompound());
        }
    }
}
