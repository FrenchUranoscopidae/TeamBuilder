package uranoscopidae.teambuilder.pkmn;

import uranoscopidae.teambuilder.pkmn.items.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PokemonMap
{
    private static final Map<String, Pokemon> internalMap = new HashMap<>();

    public static void registerPokemon(Pokemon def)
    {
        internalMap.put(def.getEnglishName(), def);
    }

    public static Pokemon getPokemon(String englishName)
    {
        return internalMap.get(englishName);
    }

    public static Collection<Pokemon> getAllPokemon()
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