import org.junit.Assert;
import org.junit.Test;
import uranoscopidae.teambuilder.Pokemon;
import uranoscopidae.teambuilder.TypeList;

import static org.junit.Assert.assertEquals;

/**
 * Created by philippine on 04/03/2016.
 */
public class TestAffinities
{

    @Test
    public void doubleType()
    {
        assertEquals(0f, new Pokemon("Ghastly", TypeList.ghost, TypeList.poison).calculateAttackedMultiplier(TypeList.normal), 0f);
    }

    @Test
    public void simpleType()
    {
        assertEquals(2f, new Pokemon("Bulbasaur", TypeList.grass, TypeList.poison).calculateAttackedMultiplier(TypeList.fire), 0f);
    }
}
