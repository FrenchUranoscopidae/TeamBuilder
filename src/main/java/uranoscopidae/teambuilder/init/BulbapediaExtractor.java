package uranoscopidae.teambuilder.init;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by xavier on 05/03/2016.
 */
public class BulbapediaExtractor
{

    public static final String DEFAULT_LIST_LOCATION = "http://bulbapedia.bulbagarden.net/w/index.php?title=List_of_Pokémon_by_National_Pokédex_number&action=edit";
    private final URL location;

    public BulbapediaExtractor() throws MalformedURLException
    {
        this(DEFAULT_LIST_LOCATION);
    }

    public BulbapediaExtractor(String listLocation) throws MalformedURLException
    {
        this.location = new URL(listLocation);
    }

    public String readListFromBulbapedia() throws IOException
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
        String sourceCode = new String(out.toByteArray());
        String beginning = "<textarea readonly=\"\" accesskey=\",\" id=\"wpTextbox1\" cols=\"80\" rows=\"25\" style=\"\" lang=\"en\" dir=\"ltr\" name=\"wpTextbox1\">";
        int start = sourceCode.indexOf(beginning);
        int end = sourceCode.indexOf("</textarea>", start);
        System.out.println("c:"+sourceCode);
        System.out.println("s:"+start);
        System.out.println("e:"+end);
        return sourceCode.substring(start+beginning.length(), end);
    }
}
