package uranoscopidae.teambuilder.init;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import uranoscopidae.teambuilder.Pokemon;
import uranoscopidae.teambuilder.Type;
import uranoscopidae.teambuilder.TypeList;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by jglrxavpok on 05/03/2016.
 */
public class BulbapediaExtractor
{

    public static final String DEFAULT_LIST_LOCATION = "http://bulbapedia.bulbagarden.net/w/api.php?action=query&prop=revisions&rvprop=content&format=json&titles=List_of_Pok%C3%A9mon_by_National_Pok%C3%A9dex_number";
    private final URL location;

    public BulbapediaExtractor() throws MalformedURLException
    {
        this(DEFAULT_LIST_LOCATION);
    }

    public BulbapediaExtractor(String listLocation) throws MalformedURLException
    {
        this.location = new URL(listLocation);
    }

    public List<PokedexEntry> readPokedexEntries() throws IOException
    {
        List<PokedexEntry> entries = new LinkedList<>();
        String code = getPageSourceCode(fetchFromApi());
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

    public String getPageSourceCode(String apiResult)
    {
        Gson gson = new Gson();
        JsonObject result = gson.fromJson(apiResult, JsonObject.class);
        JsonObject queryObject = result.getAsJsonObject("query");
        JsonObject pages = queryObject.getAsJsonObject("pages");
        JsonObject pageObject = pages.getAsJsonObject("65356"); // TODO: Do not hardcode pageid?
        JsonArray revisions = pageObject.getAsJsonArray("revisions");
        JsonObject revisionContent = revisions.get(0).getAsJsonObject();
        return revisionContent.get("*").getAsString();
    }

    public String fetchFromApi() throws IOException
    {
        InputStream in = location.openStream();
        byte[] buffer = new byte[1024*8];
        int i;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while((i = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, i);
        }
        out.flush();
        out.close();
        in.close();
        return new String(out.toByteArray());
    }
}
