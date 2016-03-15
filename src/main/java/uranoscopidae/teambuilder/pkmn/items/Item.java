package uranoscopidae.teambuilder.pkmn.items;

import uranoscopidae.teambuilder.utils.Constants;
import uranoscopidae.teambuilder.utils.IOHelper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class Item
{
    private final String name;
    private final String type;
    private BufferedImage icon;

    public Item(String name, String type)
    {
        this.name = name;
        this.type = type;

        icon = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
    }

    public String getName()
    {
        return name;
    }

    public String getType()
    {
        return type;
    }

    public void writeTo(OutputStream out) throws IOException
    {
        DataOutputStream dataOut = new DataOutputStream(out);
        dataOut.writeInt(Constants.ITEM_FORMAT_VERSION_NUMBER);
        IOHelper.writeUTF(dataOut, name);
        IOHelper.writeUTF(dataOut, type);
        ImageIO.write(icon, "png", out);
        dataOut.flush();
    }

    public static Item readFrom(InputStream in) throws IOException
    {
        DataInputStream dataIn = new DataInputStream(in);
        int version = dataIn.readInt();
        if(version != Constants.ITEM_FORMAT_VERSION_NUMBER)
        {
            throw new UnsupportedOperationException("Format versions do not match (current: "+Constants.ITEM_FORMAT_VERSION_NUMBER+", found: "+version+")");
        }
        String name = IOHelper.readUTF(dataIn);
        String type = IOHelper.readUTF(dataIn);
        Item item = new Item(name, type);
        item.icon = ImageIO.read(in);
        return item;
    }

    public void setIcon(BufferedImage icon)
    {
        this.icon = icon;
    }

    public BufferedImage getIcon()
    {
        return icon;
    }
}
