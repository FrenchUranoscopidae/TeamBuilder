package uranoscopidae.teambuilder.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MediaWikiPageExtractor
{
    private final Gson gson;
    private final ThreadLocal<ByteArrayOutputStream> localOut;
    private final ThreadLocal<byte[]> localBuffer;
    private final ThreadLocal<byte[]> stringBuffer;
    private final ThreadLocal<DirectByteArrayOutputSteam> directArrayOuts;

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

        stringBuffer = new ThreadLocal<>();

        directArrayOuts = new ThreadLocal<DirectByteArrayOutputSteam>()
        {
            @Override
            protected DirectByteArrayOutputSteam initialValue()
            {
                return new DirectByteArrayOutputSteam(new byte[8*1024]);
            }
        };


    }

    protected Gson getGson()
    {
        return gson;
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
       // System.out.println(">> "+location+" ("+out.toByteArray().length+")");
        int size = out.size();
        byte[] charBuf = stringBuffer.get();
        if(charBuf != null)
        {
            if(charBuf.length < size)
            {
                charBuf = new byte[size];
                System.out.println("NEW CHAR");
                stringBuffer.remove();
                stringBuffer.set(charBuf);
            }
        }
        else
        {
            charBuf = new byte[size];
            stringBuffer.remove();
            stringBuffer.set(charBuf);
        }
        DirectByteArrayOutputSteam directOut = directArrayOuts.get();
        directOut.setBuf(charBuf);
        out.writeTo(directOut);
        return new String(charBuf, 0, size);
    }
}
