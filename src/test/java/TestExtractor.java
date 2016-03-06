import org.junit.Test;
import uranoscopidae.teambuilder.init.BulbapediaExtractor;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by philippine on 05/03/2016.
 */
public class TestExtractor
{

    @Test
    public void testReadList() throws IOException
    {
        String list = new BulbapediaExtractor().readListFromBulbapedia();
        System.out.println(list);
    }
}
