package uranoscopidae.teambuilder.app.team;

import uranoscopidae.teambuilder.app.TeamBuilderApp;
import uranoscopidae.teambuilder.pkmn.Ability;
import uranoscopidae.teambuilder.pkmn.Pokemon;
import uranoscopidae.teambuilder.pkmn.items.Item;
import uranoscopidae.teambuilder.pkmn.moves.Move;
import uranoscopidae.teambuilder.utils.SerializableField;
import uranoscopidae.teambuilder.utils.DataHolder;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TeamEntry extends DataHolder
{

    private final Team team;
    private final int index;

    @SerializableField
    private PokemonGender gender;

    @SerializableField("getFullID")
    private Pokemon pokemon;

    @SerializableField
    private byte level;

    @SerializableField("getEnglishName")
    private Item ball;

    @SerializableField
    private int happiness;

    @SerializableField
    private boolean shiny;

    @SerializableField("getEnglishName")
    private Item item;

    @SerializableField("getEnglishName")
    private Ability ability;

    @SerializableField("getEnglishName")
    private Move[] moves;

    @SerializableField
    private String nickname;

    public TeamEntry(Team team, int index)
    {
        super(team.getApp());
        try
        {
            this.ball = team.getApp().getItem("Poké Ball");
            List<String> items = team.getApp().getItemNames();
            int randIndex = new Random().nextInt(items.size());
            item = team.getApp().getItem(items.get(randIndex)); // TODO: Change
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        moves = new Move[4];
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
        if(pokemon.getAbilities().size() > 0)
        {
            ability = pokemon.getAbilities().get(0);
        }

        if(Arrays.equals(moves, new Move[4]))
        {
            for (int i = 0; i < moves.length && i < pokemon.getMoves().size(); i++)
            {
                moves[i] = pokemon.getMoves().get(i);
            }
        }
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

    public Ability getAbility()
    {
        return ability;
    }

    public void setAbility(Ability ability)
    {
        this.ability = ability;
    }

    public Move[] getMoves()
    {
        return moves;
    }

    public void setMove(Move move, int index)
    {
        this.moves[index] = move;
    }

    public String getNickname()
    {
        return nickname;
    }

    public boolean hasNickname()
    {
        return nickname != null && !nickname.isEmpty();
    }

    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }

}
