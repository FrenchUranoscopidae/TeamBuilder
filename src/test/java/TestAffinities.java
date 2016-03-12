import org.junit.Test;
import uranoscopidae.teambuilder.pkmn.Pokemon;
import uranoscopidae.teambuilder.pkmn.TypeList;

import static org.junit.Assert.assertEquals;

/**
 * Created by philippine on 04/03/2016.
 */
public class TestAffinities
{

    @Test
    public void doubleType()
    {
        assertEquals(0f, new Pokemon("Ghastly", TypeList.ghost, TypeList.poison, -1, 0).calculateAttackedMultiplier(TypeList.normal), 0f);
    }

    @Test
    public void simpleType()
    {
        assertEquals(2f, new Pokemon("Bulbasaur", TypeList.grass, TypeList.poison, -1, -1).calculateAttackedMultiplier(TypeList.fire), 0f);
    }
}
