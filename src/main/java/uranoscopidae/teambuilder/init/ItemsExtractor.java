package uranoscopidae.teambuilder.init;

import uranoscopidae.teambuilder.app.TeamBuilderApp;
import uranoscopidae.teambuilder.pkmn.items.Item;
import uranoscopidae.teambuilder.utils.mediawiki.WikiSourceElement;
import uranoscopidae.teambuilder.utils.mediawiki.WikiTemplate;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

public class ItemsExtractor extends Extractor
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
        WikiSourceElement source = extractor.getPageSourceCode("List_of_items_by_index_number_(Generation_"+generation+")");
        String[] lines = source.getRaw().split("\n");
        WikiTemplate template = new WikiTemplate();
        for(String l : lines)
        {
            try
            {
                template.setRaw(l);
            }
            catch (IllegalArgumentException e)
            {
                // malformed template or just not a template yet
                e.printStackTrace();
                continue;
            }
            if(template.getName().equals("hexlist"))
            {
                String name = template.getElement(0).getRaw();
                if(name.equalsIgnoreCase("unknown"))
                {
                    continue;
                }
                if(template.getElementCount() <= 3)
                {
                    if(name.equalsIgnoreCase("none"))
                    {
                        list.add(new Item(name, "None"));
                    }
                }
                else
                {
                    String location = template.getElement(3).getRaw();
                    if(location.equals("yes"))
                    {
                        if(template.getElementCount() > 5)
                        {
                            location = template.getElement(5).getRaw();
                        }
                        else
                        {
                            location = template.getElement(4).getRaw();
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

    public void addDescription(Item part)
    {
        try
        {
            String sourceCode = extractor.getPageSourceCode(part.getEnglishName().replace(" ", "_")).getRaw();
            int endFirstLine = sourceCode.indexOf("\n");
            if(endFirstLine < 0)
                endFirstLine = sourceCode.length();
            String firstLine = sourceCode.substring(0, endFirstLine);
            if(firstLine.contains("several referrals"))
            {
                sourceCode = extractor.getPageSourceCode((part.getEnglishName()+"_(Item)").replace(" ", "_")).getRaw();
            }
            int start = sourceCode.indexOf("{{Item");
            while(start >= 0)
            {
                int end = findCorrespondingBrace(sourceCode, start);
                String content = sourceCode.substring(start+2, end);
                List<String> parts = properSplit(content, '|');
                String name = null;
                String effect = null;
                for(String s : parts)
                {
                    if((s.contains("desc") && s.contains("=")) && name != null)
                    {
                        String[] data = s.split("=");
                        effect = data[1].replace("\n", "");
                        while(effect.endsWith(" "))
                            effect = effect.substring(0, effect.length()-1);
                        while(effect.startsWith(" "))
                            effect = effect.substring(1);
                    }
                    else if(s.contains("name=") && name == null)
                    {
                        String[] data = s.split("=");
                        name = data[1].replace("\n", "");
                        while(name.endsWith(" "))
                        {
                            name = name.substring(0, name.length()-1);
                        }
                        while(name.startsWith(" "))
                        {
                            name = name.substring(1);
                        }

                        if(!name.equalsIgnoreCase(part.getEnglishName()))
                        {
                            name = null;
                        }
                    }
                }

                if(name != null && effect != null)
                {
                    part.setDescription(effect);
                    break;
                }
                start = sourceCode.indexOf("{{Item", end);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
