package uranoscopidae.teambuilder.app.refreshers;

import uranoscopidae.teambuilder.app.Settings;
import uranoscopidae.teambuilder.app.TeamBuilderApp;
import uranoscopidae.teambuilder.init.PokedexEntry;
import uranoscopidae.teambuilder.init.PokedexExtractor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.zip.ZipOutputStream;

public class DexRefresher extends Refresher<PokedexEntry>
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
    public List<PokedexEntry> init() throws IOException
    {
        return extractor.readPokedexEntries();
    }

    @Override
    public void handle(PokedexEntry part) throws IOException
    {
        extractor.fillEntryFromWiki(part);
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(new File(settings.getDexLocation(), format.format(part.getNationalID())+part.getPokemon().getEnglishName()+".dexd")));
        part.writeTo(out);
        out.close();
    }

    @Override
    public String getText(PokedexEntry part) throws IOException
    {
        return "Retrieved "+part.getPokemon().getEnglishName()+" #"+format.format(part.getNationalID());
    }
}
