package uranoscopidae.teambuilder;

public class Pokemon implements Cloneable
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

    public String getEnglishName()
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

    @Override
    public String toString()
    {
        return "Pokémon["+name+", "+firstType.getName()+"/"+secondType.getName()+"]";
    }

    @Override
    protected Object clone()
    {
        Pokemon clone = new Pokemon(name, firstType, secondType);

        return clone;
    }
}
