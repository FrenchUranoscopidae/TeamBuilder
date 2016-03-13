package uranoscopidae.teambuilder.app.team;

import uranoscopidae.teambuilder.pkmn.Pokemon;
import uranoscopidae.teambuilder.pkmn.items.Item;

import java.io.IOException;

public class TeamEntry
{

    private final Team team;
    private final int index;
    private PokemonGender gender;
    private Pokemon pokemon;
    private byte level;
    private Item ball;

    public TeamEntry(Team team, int index)
    {
        try
        {
            this.ball = team.getApp().getItem("Poké Ball");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        this.team = team;
        this.index = index;
        level = 50;
        gender = PokemonGender.ASEXUAL;
    }

    public int getIndex()
    {
        return index;
    }

    public Team getTeam()
    {
        return team;
    }

    public Pokemon getPokemon()
    {
        return pokemon;
    }

    public void setPokemon(Pokemon pokemon)
    {
        this.pokemon = pokemon;
    }

    public boolean hasPokemon()
    {
        return pokemon != null;
    }

    public PokemonGender getGender()
    {
        return gender;
    }

    public void setGender(PokemonGender gender)
    {
        this.gender = gender;
    }

    public byte getLevel()
    {
        return level;
    }

    public void setLevel(byte level)
    {
        this.level = level;
    }

    public Item getBall()
    {
        return ball;
    }

    public void setBall(Item ball)
    {
        this.ball = ball;
    }
}
