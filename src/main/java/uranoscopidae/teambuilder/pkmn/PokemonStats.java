package uranoscopidae.teambuilder.pkmn;

import uranoscopidae.teambuilder.app.TeamBuilderApp;
import uranoscopidae.teambuilder.utils.DataHolder;
import uranoscopidae.teambuilder.utils.SerializableField;

public class PokemonStats extends DataHolder
{

    @SerializableField
    private int hp;

    @SerializableField
    private int attack;

    @SerializableField
    private int defense;

    @SerializableField
    private int specialAttack;

    @SerializableField
    private int specialDefense;

    @SerializableField
    private int speed;

    public PokemonStats(TeamBuilderApp app) {
        super(app);
    }

    public int getAttack()
    {
        return attack;
    }

    public void setAttack(int attack)
    {
        this.attack = attack;
    }

    public int getDefense()
    {
        return defense;
    }

    public void setDefense(int defense)
    {
        this.defense = defense;
    }

    public int getHp()
    {
        return hp;
    }

    public void setHp(int hp)
    {
        this.hp = hp;
    }

    public int getSpecialAttack()
    {
        return specialAttack;
    }

    public void setSpecialAttack(int specialAttack)
    {
        this.specialAttack = specialAttack;
    }

    public int getSpecialDefense()
    {
        return specialDefense;
    }

    public void setSpecialDefense(int specialDefense)
    {
        this.specialDefense = specialDefense;
    }

    public int getSpeed()
    {
        return speed;
    }

    public void setSpeed(int speed)
    {
        this.speed = speed;
    }
}