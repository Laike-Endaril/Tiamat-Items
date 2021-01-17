package com.fantasticsource.tiamatitems;

import com.evilnotch.iitemrender.handlers.IItemRenderer;
import com.evilnotch.iitemrender.handlers.IItemRendererHandler;
import com.fantasticsource.tiamatitems.nbt.AssemblyTags;
import com.fantasticsource.tiamatitems.nbt.MiscTags;
import com.fantasticsource.tiamatitems.nbt.TextureTags;
import com.fantasticsource.tiamatitems.settings.CRarity;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.GL_QUADS;

@SideOnly(Side.CLIENT)
public class TiamatItemRenderer implements IItemRenderer
{
    public static final VertexFormat VOXEL = new VertexFormat();
    private static final double Z1 = -0.5d / 16, Z2 = 0.5d / 16;
    private static double voxelSizeX, voxelSizeY;

    static
    {
        VOXEL.addElement(DefaultVertexFormats.POSITION_3F).addElement(DefaultVertexFormats.COLOR_4UB);
    }

    private static void voxel(BufferBuilder bufferBuilder, double x, double y, int r, int g, int b, int a, boolean renderLeft, boolean renderRight, boolean renderTop, boolean renderBottom)
    {
        //THE BUFFERBUILDER ONLY USES COLOR PER-QUAD, NOT PER-VERTEX!  IT WILL ALWAYS USE THE COLOR OF THE LAST VERTEX IN THE QUAD FOR THE ENTIRE QUAD!

        //All orientations are as seen from holder

        double x1 = -0.5 + voxelSizeX * x, x2 = -0.5 + voxelSizeX * (x + 1);
        double y1 = 0.5 - voxelSizeY * y, y2 = 0.5 - voxelSizeY * (y + 1);

        //Front
        bufferBuilder.pos(x1, y2, Z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x2, y2, Z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x2, y1, Z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x1, y1, Z2).color(r, g, b, a).endVertex();

        //Back
        bufferBuilder.pos(x2, y1, Z1).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x2, y2, Z1).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x1, y2, Z1).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x1, y1, Z1).color(r, g, b, a).endVertex();

        //Left
        if (renderLeft)
        {
            bufferBuilder.pos(x1, y2, Z2).color(r, g, b, a).endVertex();
            bufferBuilder.pos(x1, y1, Z2).color(r, g, b, a).endVertex();
            bufferBuilder.pos(x1, y1, Z1).color(r, g, b, a).endVertex();
            bufferBuilder.pos(x1, y2, Z1).color(r, g, b, a).endVertex();
        }

        //Right
        if (renderRight)
        {
            bufferBuilder.pos(x2, y2, Z1).color(r, g, b, a).endVertex();
            bufferBuilder.pos(x2, y1, Z1).color(r, g, b, a).endVertex();
            bufferBuilder.pos(x2, y1, Z2).color(r, g, b, a).endVertex();
            bufferBuilder.pos(x2, y2, Z2).color(r, g, b, a).endVertex();
        }

        //Top
        if (renderTop)
        {
            bufferBuilder.pos(x1, y1, Z1).color(r, g, b, a).endVertex();
            bufferBuilder.pos(x1, y1, Z2).color(r, g, b, a).endVertex();
            bufferBuilder.pos(x2, y1, Z2).color(r, g, b, a).endVertex();
            bufferBuilder.pos(x2, y1, Z1).color(r, g, b, a).endVertex();
        }

        //Bottom
        if (renderBottom)
        {
            bufferBuilder.pos(x2, y2, Z1).color(r, g, b, a).endVertex();
            bufferBuilder.pos(x2, y2, Z2).color(r, g, b, a).endVertex();
            bufferBuilder.pos(x1, y2, Z2).color(r, g, b, a).endVertex();
            bufferBuilder.pos(x1, y2, Z1).color(r, g, b, a).endVertex();
        }
    }

    @Override
    public void render(ItemStack stack, IBakedModel model, ItemCameraTransforms.TransformType transformType, float v)
    {
        int state = AssemblyTags.getState(stack);

        if (!TextureTags.itemHasMainLayerTag(stack, state))
        {
            IItemRendererHandler.renderItemStack(stack, model);
            return;
        }

        ArrayList<String> layerKeys = TextureTags.getItemLayers(stack, state);
        if (layerKeys.size() == 0) return;


        //Transform keys with color 00000000 to rarity color
        CRarity rarity = MiscTags.getItemRarity(stack);
        if (rarity == null && RarityData.rarities != null && RarityData.rarities.size() > 0) rarity = RarityData.rarities.values().iterator().next();
        if (rarity != null)
        {
            String rarityColor = rarity.color.hex8();
            for (int i = 0; i < layerKeys.size(); i++)
            {
                layerKeys.set(i, layerKeys.get(i).replaceAll("00000000", rarityColor));
            }
        }


        //Check and load/generate/cache texture
        StringBuilder combinedReference = new StringBuilder(layerKeys.get(0));
        for (int i = 1; i < layerKeys.size(); i++) combinedReference.append("|").append(layerKeys.get(i));

        int width = 0, height = 0;
        Texture texture = TextureCache.textures.get(combinedReference.toString());

        if (texture == null)
        {
            //Check and load/generate/cache layers
            ArrayList<Texture> layers = new ArrayList<>();
            for (String key : layerKeys)
            {
                Texture layer = TextureCache.textures.get(key);
                if (layer == null)
                {
                    //Get raw ("white") layer (may not actually be grayscale)
                    String[] tokens = Tools.fixedSplit(key, ":");
                    Texture whiteLayer = TextureCache.textures.get(tokens[0] + ":" + tokens[1]);
                    if (whiteLayer == null) return;


                    //Get blend color
                    Color blendColor;
                    if (tokens.length < 3) blendColor = Color.WHITE;
                    else blendColor = new Color(tokens[2].trim());


                    //Generate layer
                    layer = new Texture(whiteLayer.width, whiteLayer.height);
                    for (int x = 0; x < layer.width; x++)
                    {
                        for (int y = 0; y < layer.height; y++)
                        {
                            int[] c = whiteLayer.colors[x][y];
                            Color whiteColor = new Color(c[0], c[1], c[2], c[3]);

                            layer.colors[x][y][0] = (int) (blendColor.r() * whiteColor.rf());
                            layer.colors[x][y][1] = (int) (blendColor.g() * whiteColor.gf());
                            layer.colors[x][y][2] = (int) (blendColor.b() * whiteColor.bf());
                            layer.colors[x][y][3] = (int) (blendColor.a() * whiteColor.af());
                        }
                    }


                    //Cache
                    if (TextureTags.itemHasLayerCacheTag(stack))
                    {
                        TextureCache.textures.put(key, layer);
                    }
                }


                width = Tools.max(width, layer.width);
                height = Tools.max(height, layer.height);

                layers.add(layer);
            }


            //Generate final texture
            texture = new Texture(width, height);
            double r, g, b, t, lr, lg, lb, la, lt;
            for (Texture layer : layers)
            {
                layer.xScale = width / layer.width;
                layer.yScale = height / layer.height;
            }

            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    r = 0;
                    b = 0;
                    g = 0;
                    t = 1;

                    for (Texture layer : layers)
                    {
                        lr = layer.colors[x * layer.xScale][y * layer.yScale][0] / 255d;
                        lg = layer.colors[x * layer.xScale][y * layer.yScale][1] / 255d;
                        lb = layer.colors[x * layer.xScale][y * layer.yScale][2] / 255d;
                        la = layer.colors[x * layer.xScale][y * layer.yScale][3] / 255d;

                        if (la == 0) continue;

                        lt = 1 - la;

                        r = r * lt + lr * la;
                        g = g * lt + lg * la;
                        b = b * lt + lb * la;
                        t *= lt;
                    }

                    texture.colors[x][y][0] = (int) (Tools.min(Tools.max(r, 0), 1) * 255);
                    texture.colors[x][y][1] = (int) (Tools.min(Tools.max(g, 0), 1) * 255);
                    texture.colors[x][y][2] = (int) (Tools.min(Tools.max(b, 0), 1) * 255);
                    texture.colors[x][y][3] = (int) (Tools.min(Tools.max(1 - t, 0), 1) * 255);
                }
            }


            //Cache
            if (TextureTags.itemHasTextureCacheTag(stack))
            {
                TextureCache.textures.put(combinedReference.toString(), texture);
            }
        }


        //Render
        int[][][] colors = texture.colors;

        width = texture.width;
        height = texture.height;
        voxelSizeX = 1d / width;
        voxelSizeY = 1d / height;

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5, 0.5, 0.5);
        IItemRendererHandler.applyTransforms(model);

        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();
        GlStateManager.enableCull();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();


        bufferBuilder.begin(GL_QUADS, VOXEL);

        int color[], alpha, leftAlpha, rightAlpha, upAlpha, downAlpha;
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                color = colors[x][y];

                alpha = color[3];
                if (alpha == 0) continue;

                leftAlpha = x == 0 ? 0 : colors[x - 1][y][3];
                rightAlpha = x == width - 1 ? 0 : colors[x + 1][y][3];
                upAlpha = y == 0 ? 0 : colors[x][y - 1][3];
                downAlpha = y == height - 1 ? 0 : colors[x][y + 1][3];

                voxel(bufferBuilder, x, y, color[0], color[1], color[2], alpha, leftAlpha == 0, rightAlpha == 0, upAlpha == 0, downAlpha == 0);
            }
        }

        tessellator.draw();

        GlStateManager.disableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();

        GlStateManager.popMatrix();
    }

    @Override
    public TransformPreset getTransformPreset()
    {
        return TransformPreset.NONE;
    }
}
