package uranoscopidae.teambuilder.app.refreshers;

import uranoscopidae.teambuilder.app.Settings;
import uranoscopidae.teambuilder.app.TeamBuilderApp;
import uranoscopidae.teambuilder.init.PokedexExtractor;
import uranoscopidae.teambuilder.pkmn.Pokemon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.zip.ZipOutputStream;

public class DexRefresher extends Refresher<Pokemon>
{

    private final PokedexExtractor extractor;
    private final DecimalFormat format;

    public DexRefresher(Settings settings, TeamBuilderApp app)
    {
        super("Pokédex", settings);
        extractor = new PokedexExtractor(app);
        format = new DecimalFormat("000");
    }

    @Override
    public List<Pokemon> init() throws IOException
    {
        return extractor.readPokedexEntries();
    }

    @Override
    public void handle(Pokemon part) throws IOException
    {
        if(!settings.getDexLocation().exists())
        {
            settings.getDexLocation().mkdirs();
        }
        extractor.fillEntryFromWiki(part);
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(new File(settings.getDexLocation(), format.format(part.getNationalDexID())+part.getEnglishName()+".dexd")));
        part.writeTo(extractor, out);
        out.close();
    }

    @Override
    public String getText(Pokemon part) throws IOException
    {
        return "Retrieved "+part.getEnglishName()+" #"+format.format(part.getNationalDexID());
    }

}
