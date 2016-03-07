package uranoscopidae.teambuilder.init;

import uranoscopidae.teambuilder.utils.MediaWikiPageExtractor;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;

public class BulbapediaExtractor extends MediaWikiPageExtractor
{
    public String getPageSourceCode(String title) throws IOException
    {
        return getPageSourceCode(new URL("http://bulbapedia.bulbagarden.net/w/api.php" +
                "?action=query&prop=revisions&rvprop=content&format=json&titles="+ URLEncoder.encode(title, "UTF-8")));
    }

    public String fetchFromApi(String title) throws IOException
    {
        return fetchFromApi(new URL("http://bulbapedia.bulbagarden.net/w/api.php" +
                "?action=query&prop=revisions&rvprop=content&format=json&titles="+ URLEncoder.encode(title, "UTF-8")));
    }
}
