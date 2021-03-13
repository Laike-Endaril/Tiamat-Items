package com.fantasticsource.tiamatitems;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tiamatitems.api.IPartSlot;
import com.fantasticsource.tiamatitems.nbt.AssemblyTags;
import com.fantasticsource.tiamatitems.nbt.MiscTags;
import com.fantasticsource.tiamatitems.nbt.PassiveAttributeModTags;
import com.fantasticsource.tiamatitems.nbt.TextureTags;
import com.fantasticsource.tiamatitems.settings.CRarity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.fantasticsource.tiamatitems.TiamatItems.DOMAIN;
import static com.fantasticsource.tiamatitems.TiamatItems.MODID;

public class TiamatItem extends Item
{
    public TiamatItem()
    {
        setCreativeTab(TiamatItems.creativeTab);

        setUnlocalizedName(MODID + ":tiamatitem");
        setRegistryName("tiamatitem");
    }

    public static ItemStack get(boolean cacheLayers, boolean cacheTexture, String... fullStateLayers)
    {
        ItemStack stack = new ItemStack(TiamatItems.tiamatItem);
        for (String layer : fullStateLayers)
        {
            if (layer != null) TextureTags.addItemLayer(stack, AssemblyTags.STATE_FULL, layer);
        }
        if (cacheLayers) TextureTags.addItemLayerCacheTag(stack);
        if (cacheTexture) TextureTags.addItemTextureCacheTag(stack);
        return stack;
    }

    @Override
    public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity)
    {
        if (AssemblyTags.getState(stack) < AssemblyTags.STATE_USABLE) return false;

        String slotting = "None";
        if (stack.hasTagCompound())
        {
            NBTTagCompound compound = MCTools.getSubCompoundIfExists(stack.getTagCompound(), DOMAIN);
            if (compound != null && compound.hasKey("slotting")) slotting = compound.getString("slotting");
        }

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
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, net.minecraft.client.util.ITooltipFlag flag)
    {
        addTooltipLines(tooltip, stack);

        if (GuiScreen.isShiftKeyDown())
        {
            for (ItemStack part : AssemblyTags.getNonEmptyParts(stack))
            {
                tooltip.add("+");

                for (String line : part.getTooltip(Minecraft.getMinecraft().player, ITooltipFlag.TooltipFlags.NORMAL))
                {
                    tooltip.add("+" + line);
                }
            }
        }
        else
        {
            for (IPartSlot partSlot : AssemblyTags.getPartSlots(stack))
            {
                if (!partSlot.getPart().isEmpty())
                {
                    tooltip.add("");
                    tooltip.add(TextFormatting.DARK_BLUE + "Press shift to see parts");
                    break;
                }
            }
        }

        if (flag.isAdvanced()) tooltip.add("");
    }

    protected void addTooltipLines(List<String> tooltip, ItemStack stack)
    {
        CRarity rarity = MiscTags.getItemRarity(stack);
        if (rarity != null) tooltip.add(rarity.textColor + "Level " + MiscTags.getItemLevel(stack) + " " + rarity.name);
        else tooltip.add("Level " + MiscTags.getItemLevel(stack));

        ArrayList<String> passiveModStrings = PassiveAttributeModTags.getPassiveMods(stack);
        for (String passiveModString : passiveModStrings)
        {
            tooltip.add(passiveModString);
        }

        tooltip.add(TextFormatting.GOLD + "Value: " + MiscTags.getItemValue(stack));
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (this.isInCreativeTab(tab))
        {
            items.add(get(false, true));
        }
    }

    @Override
    public int getItemStackLimit(ItemStack stack)
    {
        return MiscTags.getTiamatItemStackLimit(stack);
    }

    @Override
    public int getMaxDamage(ItemStack stack)
    {
        return MiscTags.getItemDurability(stack);
    }

    @Override
    public boolean isDamageable()
    {
        return true;
    }

    @Override
    public void setDamage(ItemStack stack, int damage)
    {
        //TODO
//        super.setDamage(stack, damage);
//        int max = getMaxDamage(stack);
//        if (damage >= max)
//        {
//            ItemStack copy = MCTools.cloneItemStack(stack);
//            ServerTickTimer.schedule(1, () -> setDamage(stack, max));
//        }
    }
}
