package com.fantasticsource.tiamatitems;

import com.fantasticsource.tiamatitems.settings.CRarity;
import com.fantasticsource.tiamatitems.settings.CSettings;

import java.util.LinkedHashMap;

public class RarityData
{
    public static LinkedHashMap<String, CRarity> rarities = CSettings.LOCAL_SETTINGS.rarities;
}
