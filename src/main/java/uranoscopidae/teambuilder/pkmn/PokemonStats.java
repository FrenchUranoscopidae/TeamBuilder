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

    public int getHP()
    {
        return hp;
    }

    public void setHP(int hp)
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

    public void set(String key, int actualValue)
    {
        key = key.toLowerCase().replace("\n", "");
        switch (key) {
            case "hp":
                hp = actualValue;
                break;

            case "attack":
                attack = actualValue;
                break;

            case "defense":
                defense = actualValue;
                break;

            case "spatk":
                specialAttack = actualValue;
                break;

            case "spdef":
                specialDefense = actualValue;
                break;

            case "speed":
                speed = actualValue;
                break;

            case "special": // ignore, old value from Gen I (or II, don't remember)
                break;

            default:
                System.err.println("Invalid stat: "+key);
                break;
        }
    }
}
