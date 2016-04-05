package uranoscopidae.teambuilder.init;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import uranoscopidae.teambuilder.utils.IOHelper;
import uranoscopidae.teambuilder.utils.mediawiki.MediaWikiPageExtractor;
import uranoscopidae.teambuilder.utils.mediawiki.WikiSourceElement;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;

public class BulbapediaExtractor extends MediaWikiPageExtractor
{
    public WikiSourceElement getPageSourceCode(String title) throws IOException
    {
        return getPageSourceCode(new URL("http://bulbapedia.bulbagarden.net/w/api.php" +
                "?action=query&prop=revisions&rvprop=content&format=json&titles="+ URLEncoder.encode(title, "UTF-8")));
    }

    public String fetchFromApi(String title) throws IOException
    {
        return fetchFromApi(new URL("http://bulbapedia.bulbagarden.net/w/api.php" +
                "?action=query&prop=revisions&rvprop=content&format=json&titles="+ URLEncoder.encode(title, "UTF-8")));
    }

    public BufferedImage getImageFromName(String fileName) throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        outputImageTo(out, fileName);
        out.flush();
        out.close();
        return ImageIO.read(new ByteArrayInputStream(out.toByteArray()));
    }

    public boolean outputImageTo(OutputStream out, String fileName) throws IOException
    {
        URL apiURL = new URL("http://bulbapedia.bulbagarden.net/w/api.php" +
                "?action=query&prop=imageinfo&format=json&&iiprop=url&titles="+ URLEncoder.encode(fileName, "UTF-8"));
        String result = fetchFromApi(apiURL);
        JsonObject object = getGson().fromJson(result, JsonObject.class);
        JsonArray infos = object.getAsJsonObject("query").getAsJsonObject("pages").getAsJsonObject("-1").getAsJsonArray("imageinfo");
        try
        {
            URL url = new URL(infos.get(0).getAsJsonObject().getAsJsonPrimitive("url").getAsString());
            InputStream in = url.openStream();
            IOHelper.copy(in, out);
            in.close();
            return true;
        }
        catch (NullPointerException e)
        {
            // File does not exist
            return false;
        }
    }
}
