import org.junit.Test;
import uranoscopidae.teambuilder.init.BulbapediaExtractor;

import java.io.IOException;

/**
 * Created by philippine on 05/03/2016.
 */
public class TestExtractor
{

    @Test
    public void testReadList() throws IOException
    {
        BulbapediaExtractor extractor = new BulbapediaExtractor();
        extractor.readPokedexEntries().forEach(System.out::println);
    }
}
