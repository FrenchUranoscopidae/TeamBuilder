package uranoscopidae.teambuilder;

/**
 * Created by philippine on 04/03/2016.
 */
public class TypeList
{
    public static final Type none = new Type("None");
    public static final Type normal = new Type("Normal");
    public static final Type fighting = new Type("Fighting");
    public static final Type flying = new Type("Flying");
    public static final Type poison = new Type("Poison");
    public static final Type ground = new Type("Ground");
    public static final Type rock = new Type("Rock");
    public static final Type bug = new Type("Bug");
    public static final Type ghost = new Type("Ghost");
    public static final Type steel = new Type("Steel");
    public static final Type fire = new Type("Fire");
    public static final Type water = new Type("Water");
    public static final Type grass = new Type("Grass");
    public static final Type electric = new Type("Electric");
    public static final Type psychic = new Type("Psychic");
    public static final Type ice = new Type("Ice");
    public static final Type dragon = new Type("Dragon");
    public static final Type dark = new Type("Dark");
    public static final Type fairy = new Type("Fairy");

    static
    {
        init();
    }

    private static void init()
    {
        fighting.setSuperEffectiveOn(normal, steel, dark, ice, rock);
        flying.setSuperEffectiveOn(fighting, bug, grass);
        poison.setSuperEffectiveOn(grass, fairy);
        ground.setSuperEffectiveOn(poison, rock, steel, fire, electric);
        rock.setSuperEffectiveOn(flying, bug, fire, ice);
        bug.setSuperEffectiveOn(grass, psychic, dark);
        ghost.setSuperEffectiveOn(ghost, psychic);
        steel.setSuperEffectiveOn(rock, ice, fairy);
        fire.setSuperEffectiveOn(bug, steel, grass, ice);
        water.setSuperEffectiveOn(ground, rock, fire);
        grass.setSuperEffectiveOn(ground, rock, water);
        electric.setSuperEffectiveOn(flying, water);
        psychic.setSuperEffectiveOn(fighting, poison);
        ice.setSuperEffectiveOn(ground, flying, grass, dragon);
        dragon.setSuperEffectiveOn(dragon);
        dark.setSuperEffectiveOn(psychic, ghost);
        fairy.setSuperEffectiveOn(fighting, dragon, dark);

        normal.setNotEffectiveOn(rock, steel);
        fighting.setNotEffectiveOn(flying, poison, bug, psychic, fairy);
        flying.setNotEffectiveOn(rock, steel, electric);
        poison.setNotEffectiveOn(poison, rock, ground, ghost);
        ground.setNotEffectiveOn(bug, grass);
        rock.setNotEffectiveOn(fighting, ground, steel);
        bug.setNotEffectiveOn(fighting, flying, poison, ghost, steel, fire, fairy);
        ghost.setNotEffectiveOn(dark);
        steel.setNotEffectiveOn(steel, fire, water, electric);
        fire.setNotEffectiveOn(rock, fire, water, dragon);
        water.setNotEffectiveOn(water, grass, dragon);
        grass.setNotEffectiveOn(flying, poison, bug, steel, fire, grass, dragon);
        electric.setNotEffectiveOn(grass, electric, dragon);
        psychic.setNotEffectiveOn(steel, psychic);
        ice.setNotEffectiveOn(steel, fire, water, ice);
        dragon.setNotEffectiveOn(steel);
        dark.setNotEffectiveOn(fighting, dark, fairy);
        fairy.setNotEffectiveOn(poison, steel, fire);

        normal.setIneffectiveOn(ghost);
        fighting.setIneffectiveOn(ghost);
        poison.setIneffectiveOn(steel);
        ground.setIneffectiveOn(flying);
        ghost.setIneffectiveOn(normal);
        electric.setIneffectiveOn(ground);
        psychic.setIneffectiveOn(dark);
        dragon.setIneffectiveOn(fairy);
    }
}

