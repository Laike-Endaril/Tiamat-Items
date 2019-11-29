package com.fantasticsource.tiamatitems;

import com.evilnotch.iitemrender.handlers.IItemRenderer;
import com.evilnotch.iitemrender.handlers.IItemRendererHandler;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;

public class TiamatItemRenderer implements IItemRenderer
{
    @Override
    public void render(ItemStack itemStack, IBakedModel iBakedModel, ItemCameraTransforms.TransformType transformType, float v)
    {
        IItemRendererHandler.renderItemStack(itemStack, iBakedModel);
    }

    @Override
    public TransformPreset getTransformPreset()
    {
        return TransformPreset.NONE;
    }
}
