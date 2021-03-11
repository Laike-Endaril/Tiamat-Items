package com.fantasticsource.tiamatitems;

import com.fantasticsource.tiamatitems.settings.CRarity;
import com.fantasticsource.tiamatitems.settings.CSettings;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class EffectiveData
{
    public static long serverItemGenConfigVersion = -1;

    public static void clearVersion()
    {
        serverItemGenConfigVersion = -1;
    }

    public static LinkedHashMap<String, CRarity> rarities = CSettings.LOCAL_SETTINGS.rarities;
    public static LinkedHashMap<String, LinkedHashSet<String>> slotTypes = CSettings.LOCAL_SETTINGS.slotTypes;
}
