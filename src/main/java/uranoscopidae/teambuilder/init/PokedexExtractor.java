package uranoscopidae.teambuilder.init;

import uranoscopidae.teambuilder.pkmn.Ability;
import uranoscopidae.teambuilder.pkmn.Pokemon;
import uranoscopidae.teambuilder.pkmn.Type;
import uranoscopidae.teambuilder.pkmn.TypeList;
import uranoscopidae.teambuilder.app.TeamBuilderApp;
import uranoscopidae.teambuilder.pkmn.moves.*;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class PokedexExtractor extends Extractor
{

    private final BulbapediaExtractor extractor;
    private final TeamBuilderApp app;
    private final DecimalFormat format;

    public PokedexExtractor(TeamBuilderApp app)
    {
        this.app = app;
        extractor = new BulbapediaExtractor();
        format = new DecimalFormat("000");
    }

    public BulbapediaExtractor getExtractor()
    {
        return extractor;
    }

    public void fillEntryFromWiki(Pokemon entry) throws IOException
    {
        String name = entry.getEnglishName();
        String source = extractor.getPageSourceCode(name+" (Pokémon)");
        String startString = "==Game data==";
        String gameData = source.substring(source.indexOf(startString)+startString.length());

        //System.out.println(gameData);

        // Fetch last Pokédex entry
        String dexEntry = fetchLastDexEntry(gameData);
        entry.setDescription(dexEntry);

        addLearningMoves(entry, gameData);
        addBreedingMoves(entry, gameData);
        addTMMoves(entry, gameData);
        addTutorMoves(entry, gameData);

        addAbilities(entry, source);

        entry.setIcon(extractor.getImageFromName("File:"+format.format(entry.getNationalDexID())+"MS.png"));
    }

    private void addAbilities(Pokemon entry, String source)
    {
        int start = source.indexOf("{{Pokémon Infobox");
        int end = findCorrespondingBrace(source, start);
        String infobox = source.substring(start, end);
        String[] parts = infobox.split(Pattern.quote("|"));
        String lastAbility = "1";
        for(String s : parts)
        {
            if(s.contains("abilityn="))
            {
                lastAbility = s.split("=")[1];
            }
            else if(s.contains("ability"))
            {
                String abilityData = s.substring(s.indexOf("ability")+"ability".length());
                if(abilityData.contains("="))
                {
                    String[] data = abilityData.split("=");
                    String number = data[0];
                    String name = data[1];
                    while(name.endsWith(" "))
                        name = name.substring(0, name.length()-1);
                    while(name.startsWith(" "))
                        name = name.substring(1);
                    try
                    {
                        Ability ability = app.getAbility(name.replace("\n", ""));
                        entry.getAbilities().add(ability);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    if(number.equals(lastAbility))
                    {
                        break;
                    }
                }
            }
        }
    }

    public void readArtwork(Pokemon entry, OutputStream out) throws IOException
    {
        entry.setSprite(extractor.getImageFromName(getArtworkFullID(entry)+".png"));
    }

    public String getArtworkFullID(Pokemon entry)
    {
        return "File:"+format.format(entry.getNationalDexID())+entry.getEnglishName();
    }

    public String getSpriteFullID(Pokemon entry, String gen)
    {
        return getSpriteFullID(entry, gen, null);
    }

    public String getSpriteFullID(Pokemon entry, String gen, String form)
    {
        String formPart = "";
        if(form != null)
        {
            formPart = "_"+form;
        }
        return "File:Spr_"+gen+"_"+format.format(entry.getNationalDexID())+formPart;
    }

    private void addTMMoves(Pokemon dexEntry, String gameData) throws IOException
    {
        String start = "{{learnlist/tmh";
        String end = "{{learnlist/tmf";
        if(!gameData.contains(end))
        {
            end = "{{Learnlist/tmf";
        }
        addMoves(dexEntry, gameData, start, end, "{{learnlist/tm", 0);
    }

    private void addTutorMoves(Pokemon dexEntry, String gameData) throws IOException
    {
        String start = "{{learnlist/tutorh";
        String end = "{{learnlist/tutorf";
        if(!gameData.contains(end))
        {
            end = "{{Learnlist/tutorf";
        }
        addMoves(dexEntry, gameData, start, end, "{{learnlist/tutor", -1);
    }

    private void addBreedingMoves(Pokemon dexEntry, String gameData) throws IOException
    {
        String start = "{{learnlist/breedh";
        String end = "{{learnlist/breedf";
        if(!gameData.contains(end))
        {
            end = "{{Learnlist/breedf";
        }
        addMoves(dexEntry, gameData, start, end, "{{learnlist/breed", 0);
    }

    private void addLearningMoves(Pokemon dexEntry, String gameData) throws IOException
    {
        String start = "{{learnlist/levelh";
        String end = "{{learnlist/levelf";
        if(!gameData.contains(end))
        {
            end = "{{Learnlist/levelf";
        }
        addMoves(dexEntry, gameData, start, end, "{{learnlist/level", 0);
    }

    private void addMoves(Pokemon dexEntry, String gameData, String header, String footer, String lineStart, int globalOffset) throws IOException
    {
        try
        {
            String learnlist = gameData.substring(gameData.indexOf(header), gameData.indexOf(footer));
            String[] lines = learnlist.split("\n");
            for(String l : lines)
            {
                if(l.startsWith(lineStart) && !l.startsWith(header) && !l.startsWith(footer))
                {
                    String content = l.substring(2, l.lastIndexOf("}}"));
                    List<String> parts = properSplit(content, '|');
                    if(parts.size() <= 2)
                    {
                        continue;
                    }
                    int off = (l.startsWith(lineStart+"VI") ? 1 : 0) + globalOffset;
                    String name = parts.get(2+off);

                    if(!app.hasMove(name))
                    {
                        System.out.println("NOT FOUND: "+name);
                        Type type = TypeList.getFromID(parts.get(3+off));
                        MoveCategory category = MoveCategory.valueOf(parts.get(4+off).toUpperCase());
                        int power = MoveExtractor.readInt(parts.get(5+off));
                        int accuracy = MoveExtractor.readInt(parts.get(6+off));
                        int pp = MoveExtractor.readInt(parts.get(7+off));
                        Move definition = new Move(type, category, name, power, accuracy, pp);
                        app.registerMove(definition);
                    }

                    Move def = app.getMove(name);
                    dexEntry.addMove(def);
                }
            }

        }
        catch (StringIndexOutOfBoundsException e)
        {
            System.err.println(">><< "+gameData);
        }
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

    public List<Pokemon> readPokedexEntries() throws IOException
    {
        List<Pokemon> entries = new LinkedList<>();
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

                try
                {
                    Pokemon pokemon = new Pokemon(name, TypeList.getFromID(firstType), TypeList.getFromID(secondType), -1, Integer.parseInt(nationalID));
                    entries.add(pokemon);
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
