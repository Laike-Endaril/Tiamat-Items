package com.fantasticsource.tiamatitems.trait.recalculable.element;

import com.fantasticsource.mctools.aw.ForcedAWSkinOverrides;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitElement;
import com.fantasticsource.tools.component.CStringUTF8;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;

public class CRTraitElement_ForcedAWSkinTypeOverride extends CRecalculableTraitElement
{
    public String skinType = "";


    @Override
    public int requiredArgumentCount()
    {
        return 0;
    }


    @Override
    public String getDescription(ItemStack stack, int[] args, double itemTypeAndLevelMultiplier)
    {
        return "Forced AW Skin Type Override: " + (skinType.equals("") ? "No skin type selected" : skinType);
    }


    @Override
    public void applyToItem(ItemStack stack, int[] args, double itemTypeAndLevelMultiplier)
    {
        ForcedAWSkinOverrides.setForcedAWSkinType(stack, skinType);
    }


    @Override
    public CRTraitElement_ForcedAWSkinTypeOverride write(ByteBuf buf)
    {
        super.write(buf);

        ByteBufUtils.writeUTF8String(buf, skinType);

        return this;
    }

    @Override
    public CRTraitElement_ForcedAWSkinTypeOverride read(ByteBuf buf)
    {
        super.read(buf);

        skinType = ByteBufUtils.readUTF8String(buf);

        return this;
    }

    @Override
    public CRTraitElement_ForcedAWSkinTypeOverride save(OutputStream stream)
    {
        super.save(stream);

        new CStringUTF8().set(skinType).save(stream);

        return this;
    }

    @Override
    public CRTraitElement_ForcedAWSkinTypeOverride load(InputStream stream)
    {
        super.load(stream);

        skinType = new CStringUTF8().load(stream).value;

        return this;
    }
}
