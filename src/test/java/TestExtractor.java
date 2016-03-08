import org.junit.Before;
import org.junit.Test;
import uranoscopidae.teambuilder.Pokemon;
import uranoscopidae.teambuilder.TypeList;
import uranoscopidae.teambuilder.init.MoveExtractor;
import uranoscopidae.teambuilder.init.PokedexEntry;
import uranoscopidae.teambuilder.init.PokedexExtractor;
import uranoscopidae.teambuilder.moves.MoveCategory;
import uranoscopidae.teambuilder.moves.MoveDefinition;
import uranoscopidae.teambuilder.moves.MoveMap;

import java.io.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static org.junit.Assert.assertEquals;

/**
 * Created by philippine on 05/03/2016.
 */
public class TestExtractor
{

    @Test
    public void extractFullList() throws IOException, InterruptedException
    {
        extractMoves();
        PokedexExtractor extractor = new PokedexExtractor();
        List<PokedexEntry> entries = extractor.readPokedexEntries();
        entries.forEach(PokedexEntry::echo);
        DecimalFormat format = new DecimalFormat("000");
        //int[] ids = new int[] { 1, 151, 150, 250, 249, 719};

        ExecutorService pool = Executors.newFixedThreadPool(25);
        for(int id = 1;id <= entries.size();id++)
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
                    System.gc();
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

    @Test
    public void writeEntryFromBulbapediaToFile() throws IOException
    {
        PokedexEntry entry = new PokedexEntry(-1, 25, new Pokemon("Pikachu", TypeList.electric));
        new PokedexExtractor().fillEntryFromWiki(entry);
        entry.echo();
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(new File("./dexdata", "025Pikachu.dexd")));
        entry.writeTo(out);
        out.flush();
        out.close();
    }

    @Test
    public void readEntryFromFile() throws IOException, ReflectiveOperationException
    {
        ZipInputStream in = new ZipInputStream(new FileInputStream(new File("./dexdata", "025Pikachu.dexd")));
        PokedexEntry entry = PokedexEntry.readEntry(in);
        in.close();
        entry.echo();
    }

    @Test
    public void writeMoveEntry() throws IOException
    {
        MoveDefinition entry = new MoveDefinition(TypeList.normal, MoveCategory.PHYSICAL, "Tackle", 50, 100, 35);
        FileOutputStream out = new FileOutputStream(new File("./movedata", "Tackle.movd"));
        entry.writeTo(out);
        out.flush();
        out.close();
    }

    @Test
    public void readMoveEntry() throws IOException
    {
        FileInputStream in = new FileInputStream(new File("./movedata", "Tackle.movd"));
        MoveDefinition def = MoveDefinition.readFrom(in);
        assertEquals("Tackle", def.getEnglishName());
        assertEquals(100, def.getAccuracy());
        assertEquals(50, def.getPower());
        assertEquals(35, def.getPowerPoints());
        assertEquals(MoveCategory.PHYSICAL, def.getCategory());
        assertEquals(TypeList.normal, def.getType());
        in.close();
    }

    @Test
    public void extractMoves() throws IOException
    {
        MoveExtractor extractor = new MoveExtractor();
        List<MoveDefinition> list = extractor.findAllMoves();
        for(int i = 0;i<list.size();i++)
        {
            try
            {
                MoveDefinition def = list.get(i);
                FileOutputStream out = new FileOutputStream(new File("./movedata", def.getEnglishName()+".movd"));
                def.writeTo(out);
                out.flush();
                out.close();
                MoveMap.registerMove(def);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
