package uranoscopidae.teambuilder.app.team;

import uranoscopidae.teambuilder.pkmn.Pokemon;
import uranoscopidae.teambuilder.pkmn.items.Item;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class TeamEntry
{

    private final Team team;
    private final int index;
    private PokemonGender gender;
    private Pokemon pokemon;
    private byte level;
    private Item ball;
    private int happiness;
    private boolean shiny;
    private Item item;

    public TeamEntry(Team team, int index)
    {
        try
        {
            this.ball = team.getApp().getItem("Poké Ball");
            List<String> items = team.getApp().getItemNames();
            int randIndex = new Random().nextInt(items.size());
            item = team.getApp().getItem(items.get(randIndex)); // TODO: Change
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        this.team = team;
        this.index = index;
        level = 50;
        gender = PokemonGender.ASEXUAL;
        happiness = 255;
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

    public int getHappiness()
    {
        return happiness;
    }

    public void setHappiness(int happiness)
    {
        this.happiness = happiness;
    }

    public boolean isShiny()
    {
        return shiny;
    }

    public void setShiny(boolean shiny)
    {
        this.shiny = shiny;
    }

    public Item getItem()
    {
        return item;
    }

    public void setItem(Item item)
    {
        this.item = item;
    }
}
