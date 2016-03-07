package uranoscopidae.teambuilder.init;

import uranoscopidae.teambuilder.Pokemon;
import uranoscopidae.teambuilder.TypeList;
import uranoscopidae.teambuilder.utils.MediaWikiPageExtractor;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class PokedexExtractor
{

    public static final String DEFAULT_LIST_LOCATION = "http://bulbapedia.bulbagarden.net/w/api.php?action=query&prop=revisions&rvprop=content&format=json&titles=List_of_Pok%C3%A9mon_by_National_Pok%C3%A9dex_number";
    private final URL location;
    private final MediaWikiPageExtractor extractor;

    public PokedexExtractor() throws MalformedURLException
    {
        this(DEFAULT_LIST_LOCATION);
    }

    public PokedexExtractor(String listLocation) throws MalformedURLException
    {
        this.location = new URL(listLocation);
        extractor = new MediaWikiPageExtractor();
    }

    public MediaWikiPageExtractor getExtractor()
    {
        return extractor;
    }

    public void fillEntryFromWiki(PokedexEntry entry)
    {

    }

    public List<PokedexEntry> readPokedexEntries() throws IOException
    {
        List<PokedexEntry> entries = new LinkedList<>();
        String code = extractor.getPageSourceCode(location);
        String[] lines = code.split("\n");
        for(String l : lines)
        {
            if(l.startsWith("{{rdex|"))
            {
                String content = l.substring(2, l.indexOf("}}"));
                String[] parts = content.split(Pattern.quote("|"));
                String regionalID = parts[1];
                String nationalID = parts[2];
                if(nationalID.equals("???"))
                    continue;
                String name = parts[3];
                int typeCount = Integer.parseInt(parts[4]);
                String firstType = parts[5];
                String secondType = "None";
                if(typeCount == 2)
                {
                    secondType = parts[6];
                }

                System.out.println(content);

                Pokemon pokemon = new Pokemon(name, TypeList.getFromID(firstType), TypeList.getFromID(secondType));
                try
                {
                    PokedexEntry entry = new PokedexEntry(-1, Integer.parseInt(nationalID), pokemon);
                    entries.add(entry);
                }
                catch (NumberFormatException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return entries;
    }

}
