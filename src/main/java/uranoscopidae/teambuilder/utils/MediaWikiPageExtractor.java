package uranoscopidae.teambuilder.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MediaWikiPageExtractor
{
    private final Gson gson;

    public MediaWikiPageExtractor()
    {
        gson = new Gson();
    }

    public String getPageSourceCode(URL location) throws IOException
    {
        String apiResult = fetchFromApi(location);
        JsonObject result = gson.fromJson(apiResult, JsonObject.class);
        JsonObject queryObject = result.getAsJsonObject("query");
        JsonObject pages = queryObject.getAsJsonObject("pages");
        JsonObject pageObject = pages.entrySet().iterator().next().getValue().getAsJsonObject();
        JsonArray revisions = pageObject.getAsJsonArray("revisions");
        JsonObject revisionContent = revisions.get(0).getAsJsonObject();
        return revisionContent.get("*").getAsString();
    }

    private String fetchFromApi(URL location) throws IOException
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
