import org.junit.Test;
import uranoscopidae.teambuilder.init.PokedexEntry;
import uranoscopidae.teambuilder.init.PokedexExtractor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipOutputStream;

/**
 * Created by philippine on 05/03/2016.
 */
public class TestExtractor
{

    @Test
    public void testReadList() throws IOException, InterruptedException
    {
        PokedexExtractor extractor = new PokedexExtractor();
        List<PokedexEntry> entries = extractor.readPokedexEntries();
        entries.forEach(PokedexEntry::echo);
        DecimalFormat format = new DecimalFormat("000");
        //int[] ids = new int[] { 1, 151, 150, 250, 249, 719};

        ExecutorService pool = Executors.newFixedThreadPool(5);
        for(int id = 1;id <= 5;id++)
        {
            final int finalId = id;
            pool.execute(() -> {
                PokedexEntry entry = entries.get(finalId -1);
                try
                {
                    extractor.fillEntryFromWiki(entry);
                    ZipOutputStream out = new ZipOutputStream(new FileOutputStream(new File("./dexdata", format.format(entry.getNationalID())+entry.getPokemon().getEnglishName()+".dexd")));
                    entry.writeTo(out);
                    out.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            });
        }
        pool.shutdown();
        pool.awaitTermination(100, TimeUnit.HOURS);

    }
}
