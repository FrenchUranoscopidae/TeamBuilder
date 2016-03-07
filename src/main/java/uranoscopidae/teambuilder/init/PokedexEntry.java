package uranoscopidae.teambuilder.init;

import uranoscopidae.teambuilder.Pokemon;

public class PokedexEntry implements Comparable<PokedexEntry>
{
    private final int regionalDexID;
    private final int nationalID;
    private final Pokemon pokemon;

    public PokedexEntry(int regionalDexID, int nationalID, Pokemon pokemon)
    {
        this.regionalDexID = regionalDexID;
        this.nationalID = nationalID;
        this.pokemon = pokemon;
    }

    public Pokemon getPokemon()
    {
        return pokemon;
    }

    public int getNationalID()
    {
        return nationalID;
    }

    public int getRegionalDexID()
    {
        return regionalDexID;
    }

    @Override
    public int compareTo(PokedexEntry o)
    {
        return Integer.compare(getNationalID(), o.getNationalID());
    }

    @Override
    public String toString()
    {
        return "PokédexEntry[nationalID:"+nationalID+", regionalID:"+regionalDexID+", pokémon:"+pokemon.toString()+"]";
    }
}
