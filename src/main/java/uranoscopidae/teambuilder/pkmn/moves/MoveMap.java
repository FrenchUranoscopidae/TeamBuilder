package uranoscopidae.teambuilder.pkmn.moves;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MoveMap
{
    private static final Map<String, Move> internalMap = new HashMap<>();

    public static void registerMove(Move def)
    {
        internalMap.put(def.getEnglishName(), def);
    }

    public static Move getMove(String englishName)
    {
        return internalMap.get(englishName);
    }

    public static Collection<Move> getAllMoves()
    {
        return internalMap.values();
    }

    public static int moveCount()
    {
        return internalMap.size();
    }

    public static boolean has(String name)
    {
        return internalMap.containsKey(name);
    }
}