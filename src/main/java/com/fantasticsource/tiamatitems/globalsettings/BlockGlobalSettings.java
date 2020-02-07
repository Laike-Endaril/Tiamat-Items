package com.fantasticsource.tiamatitems.globalsettings;

import com.fantasticsource.tiamatitems.Network;
import com.fantasticsource.tiamatitems.TiamatItems;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static com.fantasticsource.tiamatitems.TiamatItems.MODID;

public class BlockGlobalSettings extends Block
{
    public BlockGlobalSettings()
    {
        super(Material.ROCK);
        setSoundType(SoundType.STONE);

        setBlockUnbreakable();
        setResistance(Float.MAX_VALUE);

        setCreativeTab(TiamatItems.creativeTab);

        setUnlocalizedName(MODID + ":globalsettings");
        setRegistryName("globalsettings");
    }


    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote) return true;

        if (player.isCreative()) Network.WRAPPER.sendTo(new Network.OpenGlobalSettingsPacket(), (EntityPlayerMP) player);
        return true;
    }
}
