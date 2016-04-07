package uranoscopidae.teambuilder.pkmn.items;

import uranoscopidae.teambuilder.pkmn.items.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ItemMap
{
    private static final Map<String, Item> internalMap = new HashMap<>();

    public static void registerItem(Item def)
    {
        internalMap.put(def.getEnglishName(), def);
    }

    public static Item getItem(String englishName)
    {
        return internalMap.get(englishName);
    }

    public static Collection<Item> getAllItems()
    {
        return internalMap.values();
    }

    public static int itemCount()
    {
        return internalMap.size();
    }

    public static boolean has(String name)
    {
        return internalMap.containsKey(name);
    }
}