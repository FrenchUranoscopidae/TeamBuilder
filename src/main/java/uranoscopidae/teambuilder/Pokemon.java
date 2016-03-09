package uranoscopidae.teambuilder;

import uranoscopidae.teambuilder.moves.Move;

import java.util.LinkedList;

public class Pokemon implements Cloneable
{
    private final String name;
    private final Type firstType;
    private final Type secondType;
    private final LinkedList<Move> moves;

    public Pokemon(String name, Type firstType)
    {
        this(name, firstType, TypeList.none);
    }

    public Pokemon(String name, Type firstType, Type secondType)
    {
        this.name = name;
        this.firstType = firstType;
        this.secondType = secondType;
        moves = new LinkedList<>();
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
        clone.getMoves().addAll(getMoves());
        return clone;
    }

    public void addMove(Move move)
    {
        moves.add(move);
    }

    public LinkedList<Move> getMoves()
    {
        return moves;
    }
}
