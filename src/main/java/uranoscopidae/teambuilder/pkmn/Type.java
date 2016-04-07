package uranoscopidae.teambuilder.pkmn;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by philippine on 04/03/2016.
 */
public class Type
{
    private final String name;
    private BufferedImage icon;
    private Type[] superEffectiveOn;
    private Type[] notEffectiveOn;
    private Type[] ineffectiveOn;

    public Type(String name)
    {
        this.name = name;
        try
        {
            InputStream in = getClass().getResourceAsStream("/typeicons/"+name+".png");
            this.icon = ImageIO.read(in);
            in.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
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

    public boolean isSuperEffectiveOn(Type type)
    {
        return arrayContains(superEffectiveOn, type);
    }

    public boolean isIneffectiveOn(Type type)
    {
        return arrayContains(ineffectiveOn, type);
    }

    public boolean isNotEffectiveOn(Type type)
    {
        return arrayContains(notEffectiveOn, type);
    }

    private boolean arrayContains(Type[] arr, Type elem)
    {
        for (Type arrayElem : arr)
        {
            if (arrayElem == elem)
            {
                return true;
            }
        }
        return false;
    }

    public float getAffinity(Type against)
    {
        if (isSuperEffectiveOn(against))
        {
            return 2f;
        }
        else if (isNotEffectiveOn(against))
        {
            return 0.5f;
        }
        else if (isIneffectiveOn(against))
        {
            return 0f;
        }
        else
        {
            return 1f;
        }
    }

    public BufferedImage getIcon()
    {
        return icon;
    }

    @Override
    public String toString()
    {
        return getName();
    }
}
