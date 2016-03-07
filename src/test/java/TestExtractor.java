import org.junit.Test;
import uranoscopidae.teambuilder.init.PokedexExtractor;

import java.io.IOException;

/**
 * Created by philippine on 05/03/2016.
 */
public class TestExtractor
{

    @Test
    public void testReadList() throws IOException
    {
        PokedexExtractor extractor = new PokedexExtractor();
        System.out.println(extractor.getExtractor().getPageSourceCode("Bulbasaur (Pokémon)"));
        extractor.readPokedexEntries().forEach(System.out::println);
    }
}
