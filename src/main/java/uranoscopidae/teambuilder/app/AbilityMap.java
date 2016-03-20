package uranoscopidae.teambuilder.app;

import uranoscopidae.teambuilder.pkmn.Ability;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AbilityMap
{
    private static final Map<String, Ability> internalMap = new HashMap<>();

    public static void registerAbility(Ability def)
    {
        internalMap.put(def.getEnglishName(), def);
    }

    public static Ability getAbility(String englishName)
    {
        return internalMap.get(englishName);
    }

    public static Collection<Ability> getAllAbilities()
    {
        return internalMap.values();
    }

    public static int abilityCount()
    {
        return internalMap.size();
    }

    public static boolean has(String name)
    {
        return internalMap.containsKey(name);
    }
}