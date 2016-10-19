package uranoscopidae.teambuilder.pkmn.items;

import uranoscopidae.teambuilder.utils.Constants;
import uranoscopidae.teambuilder.utils.IOHelper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class Item
{
    private int apiID;
    private final String name;
    private final String type;
    private BufferedImage icon;
    private String description;

    public Item(int apiID, String name, String type)
    {
        this.apiID = apiID;
        this.name = name;
        this.type = type;

        icon = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
        description = "<NOT FETCHED>";
    }

    public String getEnglishName()
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
        dataOut.writeInt(apiID);
        IOHelper.writeUTF(dataOut, name);
        IOHelper.writeUTF(dataOut, type);
        IOHelper.writeUTF(dataOut, description);
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
        int apiID = dataIn.readInt();
        String name = IOHelper.readUTF(dataIn);
        String type = IOHelper.readUTF(dataIn);
        String desc = IOHelper.readUTF(dataIn);
        Item item = new Item(apiID, name, type);
        item.setDescription(desc);
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

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}
