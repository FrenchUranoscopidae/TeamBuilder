package uranoscopidae.teambuilder.moves;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MoveMap
{
    private static final Map<String, MoveDefinition> internalMap = new HashMap<>();

    public static void registerMove(MoveDefinition def)
    {
        internalMap.put(def.getEnglishName(), def);
    }

    public static MoveDefinition getMove(String englishName)
    {
        return internalMap.get(englishName);
    }

    public static Collection<MoveDefinition> getAllMoves()
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
