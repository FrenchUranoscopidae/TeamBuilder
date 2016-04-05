package uranoscopidae.teambuilder.utils.mediawiki;

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
    private final ThreadLocal<ByteArrayOutputStream> localOut;
    private final ThreadLocal<byte[]> localBuffer;

    public MediaWikiPageExtractor()
    {
        gson = new Gson();
        localOut = new ThreadLocal<ByteArrayOutputStream>()
        {
            @Override
            protected ByteArrayOutputStream initialValue()
            {
                return new ByteArrayOutputStream();
            }
        };

        localBuffer = new ThreadLocal<byte[]>()
        {
            @Override
            protected byte[] initialValue()
            {
                return new byte[1024*8];
            }
        };
    }

    protected Gson getGson()
    {
        return gson;
    }

    public WikiSourceElement getPageSourceCode(URL location) throws IOException
    {
        return getPageSourceCode(location, true);
    }

    public WikiSourceElement getPageSourceCode(URL location, boolean redirect) throws IOException
    {
        String apiResult = fetchFromApi(location);
        JsonObject result = gson.fromJson(apiResult, JsonObject.class);
        JsonObject queryObject = result.getAsJsonObject("query");
        JsonObject pages = queryObject.getAsJsonObject("pages");
        JsonObject pageObject = pages.entrySet().iterator().next().getValue().getAsJsonObject();
        JsonArray revisions = pageObject.getAsJsonArray("revisions");
        JsonObject revisionContent = revisions.get(0).getAsJsonObject();
        String content = revisionContent.get("*").getAsString();
        if(content.contains("#REDIRECT") && redirect)
        {
            String redirection = content.substring(content.indexOf("[[")+2, content.lastIndexOf("]]"));
            String path = location.toExternalForm();
            String newLocation = path.substring(0, path.lastIndexOf("=")+1)+redirection;
            newLocation = newLocation.replace(" ", "_");
            return getPageSourceCode(new URL(newLocation));
        }
        return new WikiSourceElement(content);
    }

    public String fetchFromApi(URL location) throws IOException
    {
        InputStream in = location.openStream();
        byte[] buffer = localBuffer.get();
        int i;
        ByteArrayOutputStream out = localOut.get();
        out.reset();
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
