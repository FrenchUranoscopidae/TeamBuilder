package uranoscopidae.teambuilder.init;

import uranoscopidae.teambuilder.Pokemon;
import uranoscopidae.teambuilder.Type;
import uranoscopidae.teambuilder.TypeList;
import uranoscopidae.teambuilder.moves.*;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class PokedexExtractor
{

    private final BulbapediaExtractor extractor;

    public PokedexExtractor()
    {
        extractor = new BulbapediaExtractor();
    }

    public BulbapediaExtractor getExtractor()
    {
        return extractor;
    }

    public void fillEntryFromWiki(PokedexEntry entry) throws IOException
    {
        String name = entry.getPokemon().getEnglishName();
        String source = extractor.getPageSourceCode(name+" (Pokémon)");
        String startString = "==Game data==";
        String gameData = source.substring(source.indexOf(startString)+startString.length());

        //System.out.println(gameData);

        // Fetch last Pokédex entry
        String dexEntry = fetchLastDexEntry(gameData);
        entry.setDescription(dexEntry);

        addLearningMoves(entry, gameData);

        DecimalFormat format = new DecimalFormat("000");
        entry.setArtwork(extractor.getImageFromName("File:"+format.format(entry.getNationalID())+entry.getPokemon().getEnglishName()+".png"));
    }

    private void addLearningMoves(PokedexEntry dexEntry, String gameData)
    {
        String start = "===Learnset===";
        String end = "{{learnlist/levelf";
        String learnlist = gameData.substring(gameData.indexOf(start)+start.length(), gameData.indexOf(end));
        String[] lines = learnlist.split("\n");
        for(String l : lines)
        {
            if(l.startsWith("{{learnlist/level") && !l.startsWith("{{learnlist/levelh") && !l.startsWith("{{learnlist/levelf"))
            {
                String content = l.substring(0, l.lastIndexOf("}}"));
                String[] parts = content.split(Pattern.quote("|"));
                int level = -1;
                int off = l.startsWith("{{learnlist/levelVI") ? 1 : 0;
                try
                {
                    level = Integer.parseInt(parts[1+off]);
                }
                catch (NumberFormatException e)
                {
                    e.printStackTrace();
                }
                String name = parts[2+off];

                if(!MoveMap.has(name))
                {
                    System.out.println("NOT FOUND: "+name);
                    Type type = TypeList.getFromID(parts[3+off]);
                    MoveCategory category = MoveCategory.valueOf(parts[4+off].toUpperCase());
                    int power = MoveExtractor.readInt(parts[5+off]);
                    int accuracy = MoveExtractor.readInt(parts[6+off]);
                    int pp = MoveExtractor.readInt(parts[7+off]);
                    MoveDefinition definition = new MoveDefinition(type, category, name, power, accuracy, pp);
                    MoveMap.registerMove(definition);
                }

                MoveDefinition def = MoveMap.getMove(name);
                Move learntMove = new Move(def, new LevelingLearning(level));
                dexEntry.getPokemon().addMove(learntMove);
            }
        }
        //System.out.println(learnlist);
    }

    private String fetchLastDexEntry(String gameData)
    {
        int end = gameData.indexOf("{{Dex/Footer}}");
        String entries = gameData.substring(0, end);
        String[] lines = entries.split("\n");
        for(String l : lines)
        {
            if(l.startsWith("{{Dex/Entry"))
            {
                String content = l.substring(l.indexOf("|"), l.lastIndexOf("}}"));
                String[] parts = content.split(Pattern.quote("|"));
                for(String p : parts)
                {
                    if(p.startsWith("entry="))
                    {
                        return p.substring("entry=".length());
                    }
                }
            }
        }
        return "Could not find";
    }

    public List<PokedexEntry> readPokedexEntries() throws IOException
    {
        List<PokedexEntry> entries = new LinkedList<>();
        String code = extractor.getPageSourceCode("List_of_Pokémon_by_National_Pokédex_number");
        String[] lines = code.split("\n");
        for(String l : lines)
        {
            if(l.startsWith("{{rdex|"))
            {
                String content = l.substring(2, l.indexOf("}}"));
                String[] parts = content.split(Pattern.quote("|"));
                String regionalID = parts[1];
                String nationalID = parts[2];
                if(nationalID.equals("???"))
                    continue;
                String name = parts[3];
                int typeCount = Integer.parseInt(parts[4]);
                String firstType = parts[5];
                String secondType = "None";
                if(typeCount == 2)
                {
                    secondType = parts[6];
                }

                // System.out.println(content);

                Pokemon pokemon = new Pokemon(name, TypeList.getFromID(firstType), TypeList.getFromID(secondType));
                try
                {
                    PokedexEntry entry = new PokedexEntry(-1, Integer.parseInt(nationalID), pokemon);
                    entries.add(entry);
                }
                catch (NumberFormatException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return entries;
    }

}
