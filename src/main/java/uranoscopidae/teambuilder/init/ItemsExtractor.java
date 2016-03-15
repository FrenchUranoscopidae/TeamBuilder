package uranoscopidae.teambuilder.init;

import uranoscopidae.teambuilder.app.TeamBuilderApp;
import uranoscopidae.teambuilder.pkmn.items.Item;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class ItemsExtractor
{

    private final BulbapediaExtractor extractor;
    private final TeamBuilderApp app;
    private final DecimalFormat format;

    public ItemsExtractor(TeamBuilderApp app)
    {
        this.app = app;
        extractor = new BulbapediaExtractor();
        format = new DecimalFormat("000");
    }

    public List<Item> findAllItems(String generation) throws IOException
    {
        List<Item> list = new LinkedList<>();
        String source = extractor.getPageSourceCode("List_of_items_by_index_number_(Generation_"+generation+")");
        String[] lines = source.split("\n");
        for(String l : lines)
        {
            if(l.startsWith("{{hexlist|"))
            {
                String content = l.substring(2, l.lastIndexOf("}}"));
                String[] parts = content.split(Pattern.quote("|"));
                String name = parts[1];
                if(name.equalsIgnoreCase("unknown"))
                {
                    continue;
                }
                if(parts.length <= 4)
                {
                    if(name.equalsIgnoreCase("none"))
                    {
                        list.add(new Item(name, "None"));
                    }
                }
                else
                {
                    String location = parts[4];
                    if(location.equals("yes"))
                    {
                        if(parts.length > 6)
                        {
                            location = parts[6];
                        }
                        else
                        {
                            location = parts[5];
                        }
                    }
                    if(!location.contains("Key") && !location.startsWith("6=X ") && !location.startsWith("6=Data Card") && !location.contains("nknown"))
                    {
                        String type;
                        if(location.contains("="))
                        {
                            type = location.split("=")[1];
                        }
                        else
                        {
                            type = location;
                        }
                        if(name.contains("#"))
                        {
                            name = name.split("#")[1];
                        }
                        if(name.contains("{{"))
                        {
                            name = name.substring(0, name.indexOf("{{"));
                        }
                        Item item = new Item(name, type);
                        list.add(item);
                    }
                }
            }
        }
        return list;
    }

    public BulbapediaExtractor getExtractor()
    {
        return extractor;
    }
}
