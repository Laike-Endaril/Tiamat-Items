package com.fantasticsource.tiamatitems;

import net.minecraft.item.ItemStack;

import java.util.LinkedHashMap;

public class ClientData
{
    public static long serverItemGenConfigVersion = -1;

    public static int nextID = 0;
    public static LinkedHashMap<Integer, ItemStack> idToBadStack = new LinkedHashMap<>();
    public static LinkedHashMap<ItemStack, ItemStack> badStackToGoodStack = new LinkedHashMap<>();

    public static void clear()
    {
        serverItemGenConfigVersion = -1;
        idToBadStack.clear();
        badStackToGoodStack.clear();
    }
}
