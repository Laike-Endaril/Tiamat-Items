package com.fantasticsource.tiamatitems.traitgen;

import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CItemTypeGen extends Component
{
    public String name, slotting;
    public ArrayList<CTraitGen> staticTraits = new ArrayList<>();
    public ArrayList<CTraitGenPool> randomTraitPools = new ArrayList<>();


    @Override
    public CItemTypeGen write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, name);

        return this;
    }

    @Override
    public CItemTypeGen read(ByteBuf buf)
    {
        name = ByteBufUtils.readUTF8String(buf);

        return this;
    }

    @Override
    public CItemTypeGen save(OutputStream stream)
    {
        CStringUTF8 cs = new CStringUTF8().set(name).save(stream);

        return this;
    }

    @Override
    public CItemTypeGen load(InputStream stream)
    {
        CStringUTF8 cs = new CStringUTF8();
        name = cs.load(stream).value;

        return this;
    }
}
