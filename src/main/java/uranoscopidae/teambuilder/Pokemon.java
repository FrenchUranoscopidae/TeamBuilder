package uranoscopidae.teambuilder;

/**
 * Created by philippine on 04/03/2016.
 */
public class Pokemon
{
    private final String name;
    private final Type firstType;
    private final Type secondType;

    public Pokemon(String name, Type firstType)
    {
        this(name, firstType, TypeList.none);
    }

    public Pokemon(String name, Type firstType, Type secondType)
    {
        this.name = name;
        this.firstType = firstType;
        this.secondType = secondType;
    }

    public String getName()
    {
        return name;
    }

    public Type getFirstType()
    {
        return firstType;
    }

    public Type getSecondType()
    {
        return secondType;
    }

    public float calculateAttackedMultiplier(Type attackType)
    {
        return attackType.getAffinity(firstType) * attackType.getAffinity(secondType);
    }
}
