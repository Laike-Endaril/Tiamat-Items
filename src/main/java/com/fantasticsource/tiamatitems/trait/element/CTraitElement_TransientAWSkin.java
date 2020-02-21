package com.fantasticsource.tiamatitems.trait.element;

import com.fantasticsource.mctools.TransientAWSkins;
import com.fantasticsource.tiamatitems.trait.CTraitElement;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.datastructures.Color;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CTraitElement_TransientAWSkin extends CTraitElement
{
    public String libraryFile = "", skinType = "";
    public ArrayList<Color> dyes = new ArrayList<>(); //The alpha of these colors is used for the AW paint type
    //TODO when editing, get paint types from PaintRegistry.REGISTERED_TYPES

    @Override
    public String getDescription()
    {
        return "AW Skin: " + libraryFile;
    }

    @Override
    public String getDescription(int wholeNumberPercentage)
    {
        return getDescription();
    }

    @Override
    public void applyToItem(ItemStack stack, int wholeNumberPercentage)
    {
        TransientAWSkins.addTransientAWSkin(stack, libraryFile, skinType, dyes.toArray(new Color[0]));
    }


    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof CTraitElement_TransientAWSkin)) return false;

        CTraitElement_TransientAWSkin other = (CTraitElement_TransientAWSkin) obj;
        if (other.dyes.size() != dyes.size()) return false;
        for (int i = dyes.size() - 1; i >= 0; i--)
        {
            if (!dyes.get(i).equals(other.dyes.get(i))) return false;
        }

        return other.libraryFile.equals(libraryFile) && other.skinType.equals(skinType);
    }


    @Override
    public CTraitElement_TransientAWSkin write(ByteBuf buf)
    {
        super.write(buf);

        ByteBufUtils.writeUTF8String(buf, libraryFile);
        ByteBufUtils.writeUTF8String(buf, skinType);

        buf.writeInt(dyes.size());
        for (Color color : dyes) buf.writeInt(color.color());

        return this;
    }

    @Override
    public CTraitElement_TransientAWSkin read(ByteBuf buf)
    {
        super.read(buf);

        libraryFile = ByteBufUtils.readUTF8String(buf);
        skinType = ByteBufUtils.readUTF8String(buf);

        dyes.clear();
        for (int i = buf.readInt(); i > 0; i--) dyes.add(new Color(buf.readInt()));

        return this;
    }

    @Override
    public CTraitElement_TransientAWSkin save(OutputStream stream)
    {
        super.save(stream);

        new CStringUTF8().set(libraryFile).save(stream).set(skinType).save(stream);

        CInt ci = new CInt().set(dyes.size()).save(stream);
        for (Color color : dyes) ci.set(color.color()).save(stream);

        return this;
    }

    @Override
    public CTraitElement_TransientAWSkin load(InputStream stream)
    {
        super.load(stream);

        CStringUTF8 cs = new CStringUTF8();
        libraryFile = cs.load(stream).value;
        skinType = cs.load(stream).value;

        CInt ci = new CInt();
        dyes.clear();
        for (int i = ci.load(stream).value; i > 0; i--) dyes.add(new Color(ci.load(stream).value));

        return this;
    }
}
