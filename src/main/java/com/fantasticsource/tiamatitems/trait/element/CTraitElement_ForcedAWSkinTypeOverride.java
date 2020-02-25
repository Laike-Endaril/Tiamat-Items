package com.fantasticsource.tiamatitems.trait.element;

import com.fantasticsource.mctools.aw.ForcedAWSkinOverrides;
import com.fantasticsource.tiamatitems.trait.CTraitElement;
import com.fantasticsource.tools.component.CStringUTF8;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CTraitElement_ForcedAWSkinTypeOverride extends CTraitElement
{
    public String skinType = "";


    @Override
    public int requiredArgumentCount()
    {
        return 0;
    }


    @Override
    public String getDescription(ArrayList<Integer> baseArgs, double[] multipliedArgs)
    {
        return "Forced AW Skin Type Override: " + skinType;
    }


    @Override
    public void applyToItem(ItemStack stack, ArrayList<Integer> baseArgs, double[] multipliedArgs)
    {
        ForcedAWSkinOverrides.setForcedAWSkinType(stack, skinType);
    }


    @Override
    public CTraitElement_ForcedAWSkinTypeOverride write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, skinType);

        return this;
    }

    @Override
    public CTraitElement_ForcedAWSkinTypeOverride read(ByteBuf buf)
    {
        skinType = ByteBufUtils.readUTF8String(buf);

        return this;
    }

    @Override
    public CTraitElement_ForcedAWSkinTypeOverride save(OutputStream stream)
    {
        new CStringUTF8().set(skinType).save(stream);

        return this;
    }

    @Override
    public CTraitElement_ForcedAWSkinTypeOverride load(InputStream stream)
    {
        skinType = new CStringUTF8().load(stream).value;

        return this;
    }
}
