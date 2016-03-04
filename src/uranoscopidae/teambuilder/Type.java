package uranoscopidae.teambuilder;

/**
 * Created by philippine on 04/03/2016.
 */
public class Type
{
    private final String name;
    private Type[] superEffectiveOn;
    private Type[] notEffectiveOn;
    private Type[] ineffectiveOn;

    public Type(String name)
    {
        this.name = name;
        superEffectiveOn = new Type[0];
        notEffectiveOn = new Type[0];
        ineffectiveOn = new Type[0];
    }

    public String getName()
    {
        return name;
    }

    public Type[] getSuperEffectiveOn()
    {
        return superEffectiveOn;
    }

    public void setSuperEffectiveOn(Type... superEffectiveOn)
    {
        this.superEffectiveOn = superEffectiveOn;
    }

    public Type[] getNotEffectiveOn()
    {
        return notEffectiveOn;
    }

    public void setNotEffectiveOn(Type... notEffectiveOn)
    {
        this.notEffectiveOn = notEffectiveOn;
    }

    public Type[] getIneffectiveOn()
    {
        return ineffectiveOn;
    }

    public void setIneffectiveOn(Type... ineffectiveOn)
    {
        this.ineffectiveOn = ineffectiveOn;
    }
}
