package uranoscopidae.teambuilder.init;

import uranoscopidae.teambuilder.pkmn.Ability;
import uranoscopidae.teambuilder.utils.mediawiki.WikiSourceElement;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class AbilityExtractor
{

    private final BulbapediaExtractor extractor;

    public AbilityExtractor()
    {
        extractor = new BulbapediaExtractor();
    }

    public BulbapediaExtractor getExtractor()
    {
        return extractor;
    }

    public List<Ability> findAllAbilities() throws IOException
    {
        List<Ability> abilities = new LinkedList<>();
        WikiSourceElement source = extractor.getPageSourceCode("Ability");
        String start = "|-";
        String end = "|}\n" +
                "|}";
        String list = source.getRaw().substring(source.indexOf(start)+start.length(), source.indexOf(end, source.indexOf(start)));
        String[] movesList = list.split(Pattern.quote("|-"));

        // skip first 2 because starts with table definition
        for(int i = 2;i<movesList.length;i++)
        {
            String m = movesList[i];
            String[] parts = m.split(Pattern.quote("\n|"));
            String infos = parts[2];

            int extractStart = infos.indexOf("{{a|");
            String name = infos.substring(extractStart+"{{a|".length(), infos.indexOf("}}", extractStart));

            String desc = parts[4];

            abilities.add(new Ability(name, desc));
        }
        return abilities;
    }

}
