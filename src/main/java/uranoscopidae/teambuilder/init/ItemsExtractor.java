package uranoscopidae.teambuilder.init;

import uranoscopidae.teambuilder.app.TeamBuilderApp;
import uranoscopidae.teambuilder.pkmn.items.Item;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

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

    public void addDescription(Item part)
    {
        try
        {
            String sourceCode = extractor.getPageSourceCode(part.getName().replace(" ", "_"));
            int endFirstLine = sourceCode.indexOf("\n");
            if(endFirstLine < 0)
                endFirstLine = sourceCode.length();
            String firstLine = sourceCode.substring(0, endFirstLine);
            if(firstLine.contains("several referrals"))
            {
                sourceCode = extractor.getPageSourceCode((part.getName()+"_(Item)").replace(" ", "_"));
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

                        if(!name.equalsIgnoreCase(part.getName()))
                        {
                            name = null;
                        }
                    }
                }

                if(name != null && effect != null)
                {
                    part.setDescription(effect);
                    System.out.println("No prob "+name);
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

    private List<String> properSplit(String content, char splitter)
    {
        List<String> list = new LinkedList<>();
        char[] chars = content.toCharArray();
        int unclosed = 0;
        StringBuilder builder = new StringBuilder();
        for (char c : chars)
        {
            if (c == splitter && unclosed == 0)
            {
                list.add(builder.toString());
                builder.delete(0, builder.length());
                continue;
            }
            else if (c == '{')
            {
                unclosed++;
            }
            else if (c == '}')
            {
                unclosed--;
            }
            builder.append(c);
        }
        if(builder.length() > 0)
            list.add(builder.toString());
        return list;
    }
}
