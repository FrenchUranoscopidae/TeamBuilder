package uranoscopidae.teambuilder.app.refreshers;

import uranoscopidae.teambuilder.app.Settings;
import uranoscopidae.teambuilder.app.TeamBuilderApp;
import uranoscopidae.teambuilder.init.ItemsExtractor;
import uranoscopidae.teambuilder.pkmn.items.Item;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipOutputStream;

public class ItemsRefresher extends Refresher<Item>
{
    private final TeamBuilderApp app;
    private final ItemsExtractor extractor;

    public ItemsRefresher(Settings settings, TeamBuilderApp app)
    {
        super("Items", settings);
        this.app = app;
        extractor = new ItemsExtractor(app);
    }

    @Override
    public List<Item> init() throws IOException
    {
        return extractor.findAllItems("VI");
    }

    @Override
    public void handle(Item part) throws IOException
    {
        if(!settings.getItemsLocation().exists())
        {
            settings.getItemsLocation().mkdirs();
        }
        extractor.addDescription(part);
        String itemName = part.getName().replace(" "," _");
        if(part.getType().startsWith("TM ") || part.getType().startsWith("HM "))
        {
            itemName = part.getType().replace(" ", "_");
        }
        BufferedImage icon = extractor.getExtractor().getImageFromName("File:Bag_"+itemName+"_Sprite.png");
        if(icon == null)
        {
            icon = extractor.getExtractor().getImageFromName("File:Bag_unknown_Sprite.png"); // Handle tms
        }
        part.setIcon(icon);


        FileOutputStream out = new FileOutputStream(new File(settings.getItemsLocation(), part.getName()+".itemd"));
        part.writeTo(out);
        out.close();
    }

    @Override
    public String getText(Item part) throws IOException
    {
        return "Retrieved "+part.getName();
    }

    public ItemsExtractor getExtractor()
    {
        return extractor;
    }
}
