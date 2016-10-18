package uranoscopidae.teambuilder.pkmn;

import uranoscopidae.teambuilder.app.TeamBuilderApp;
import uranoscopidae.teambuilder.pkmn.moves.MoveInfos;
import uranoscopidae.teambuilder.utils.Constants;
import uranoscopidae.teambuilder.utils.IOHelper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class PokemonInfos implements Cloneable, Comparable<PokemonInfos>
{
    private final String name;
    private final Type firstType;
    private final Type secondType;

    private final List<MoveInfos> moveInfoses;
    private final List<Ability> abilities;

    private final int pokeapiID;
    private String description;
    private BufferedImage sprite;
    private BufferedImage shinySprite;
    private BufferedImage icon;
    private PokemonStats stats;
    private int speciesID;
    private int dexID;

    public PokemonInfos(String name, Type firstType, int pokeapiID)
    {
        this(name, firstType, TypeList.none, pokeapiID);
    }

    public PokemonInfos(String name, Type firstType, Type secondType, int pokeapiID)
    {
        this(name, firstType, secondType, pokeapiID, new LinkedList<>());
    }

    public PokemonInfos(String name, Type firstType, Type secondType, int pokeapiID, List<MoveInfos> moveInfoses)
    {
        this.pokeapiID = pokeapiID;
        this.name = name;
        this.firstType = firstType;
        this.secondType = secondType;
        this.moveInfoses = moveInfoses;

        abilities = new LinkedList<>();

        description = "Not fetched yet";

        sprite = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
        sprite.setRGB(0,0,0xFF000000);

        shinySprite = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
        shinySprite.setRGB(0,0,0xFF000000);

        icon = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
        icon.setRGB(0,0,0xFF000000);

        stats = new PokemonStats(TeamBuilderApp.instance);
    }

    public int getPokeapiID()
    {
        return pokeapiID;
    }

    public String getEnglishName()
    {
        return name;
    }

    public Type getFirstType()
    {
        return firstType;
    }

    public Type getSecondType()
    {
        return secondType;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }

    public void echo()
    {
        System.out.println(toString());
        System.out.println("Description: "+description);
        System.out.println("===Moves===");
        for(MoveInfos def : getMoveInfoses())
        {
            System.out.println(def.getEnglishName()+" ("+def.getType().getName()+"/"+def.getCategory().name()+")");
        }
    }

    public void setDefaultSprite(BufferedImage sprite)
    {
        this.sprite = sprite;
    }

    public BufferedImage getSprite()
    {
        return sprite;
    }


    public float calculateAttackedMultiplier(Type attackType)
    {
        return attackType.getAffinity(firstType) * attackType.getAffinity(secondType);
    }

    @Override
    public String toString()
    {
        return "Pokémon["+name+", "+firstType.getName()+"/"+secondType.getName()+", #"+ pokeapiID +"]";
    }

    public void addMove(MoveInfos moveInfos)
    {
        moveInfoses.add(moveInfos);
    }

    public List<MoveInfos> getMoveInfoses()
    {
        return moveInfoses;
    }

    @Override
    public int compareTo(PokemonInfos o)
    {
        return Integer.compare(getPokeapiID(), o.getPokeapiID());
    }

    public void setIcon(BufferedImage icon)
    {
        this.icon = icon;
    }

    public BufferedImage getIcon()
    {
        return icon;
    }

    // I/O methods
    public void writeTo(ZipOutputStream out) throws IOException
    {
        DataOutputStream dataOut = new DataOutputStream(out);
        out.putNextEntry(new ZipEntry("meta"));
        writeMetadata(dataOut);

        out.putNextEntry(new ZipEntry("sprite.png"));
        ImageIO.write(sprite, "png", out);

        out.putNextEntry(new ZipEntry("shiny_sprite.png"));
        ImageIO.write(shinySprite, "png", out);

        out.putNextEntry(new ZipEntry("icon.png"));
        ImageIO.write(icon, "png", out);

        out.putNextEntry(new ZipEntry("moveInfoses"));
        dataOut.writeInt(getMoveInfoses().size());
        for(MoveInfos moveInfos : getMoveInfoses())
        {
            writeMove(dataOut, moveInfos);
        }

        out.putNextEntry(new ZipEntry("globalInfos"));
        writeGlobalInfo(dataOut);

        out.putNextEntry(new ZipEntry("abilities"));
        dataOut.writeInt(getAbilities().size());
        for(Ability ability : getAbilities())
        {
            writeAbility(dataOut, ability);
        }

        out.putNextEntry(new ZipEntry("stats"));
        stats.writeTo(out);

        dataOut.flush();
        out.flush();
    }

    private void writeAbility(DataOutputStream dataOut, Ability ability) throws IOException
    {
        IOHelper.writeUTF(dataOut, ability.getEnglishName());
    }

    private void writeMove(DataOutputStream out, MoveInfos moveInfos) throws IOException
    {
        // write definition name, will be fetched later
        IOHelper.writeUTF(out, moveInfos.getEnglishName());
    }

    private void writeMetadata(DataOutputStream out) throws IOException
    {
        out.writeInt(Constants.DEX_FORMAT_VERSION_NUMBER);
        out.flush();
    }

    private void writeGlobalInfo(DataOutputStream out) throws IOException
    {
        IOHelper.writeUTF(out, getEnglishName());
        IOHelper.writeUTF(out, getFirstType().getName());
        IOHelper.writeUTF(out, getSecondType().getName());
        out.writeInt(pokeapiID);
        IOHelper.writeUTF(out, description);
        out.flush();
    }

    private static MoveInfos readMove(TeamBuilderApp app, DataInputStream dataIn) throws IOException, ReflectiveOperationException
    {
        String definition = IOHelper.readUTF(dataIn);
        return app.getMove(definition);
    }

    private static Ability readAbility(TeamBuilderApp app, DataInputStream dataIn) throws IOException
    {
        String name = IOHelper.readUTF(dataIn);
        return app.getAbility(name);
    }

    public static PokemonInfos readPokemon(TeamBuilderApp app, ZipInputStream in) throws IOException, ReflectiveOperationException
    {
        ZipEntry entry;
        DataInputStream dataIn = new DataInputStream(in);
        BufferedImage sprite = null;
        BufferedImage shinySprite = null;
        BufferedImage icon = null;
        PokemonInfos pokemon = null;
        List<MoveInfos> moveInfoses = new LinkedList<>();
        List<Ability> abilities = new LinkedList<>();
        PokemonStats stats = new PokemonStats(app);
        while ((entry = in.getNextEntry()) != null)
        {
            switch (entry.getName())
            {
                case "meta":
                    int version = dataIn.readInt();
                    if (version != Constants.DEX_FORMAT_VERSION_NUMBER)
                    {
                        throw new IOException("Version numbers do not match: found: " + version + " and current: " + Constants.DEX_FORMAT_VERSION_NUMBER);
                    }
                    break;

                case "sprite.png":
                    sprite = ImageIO.read(in);
                    break;

                case "shiny_sprite.png":
                    shinySprite = ImageIO.read(in);
                    break;

                case "icon.png":
                    icon = ImageIO.read(in);
                    break;

                case "globalInfos":
                    String name = IOHelper.readUTF(dataIn);
                    String firstType = IOHelper.readUTF(dataIn);
                    String secondType = IOHelper.readUTF(dataIn);
                    int orderID = dataIn.readInt();
                    String desc = IOHelper.readUTF(dataIn);
                    pokemon = new PokemonInfos(name, TypeList.getFromID(firstType), TypeList.getFromID(secondType), orderID, moveInfoses);
                    pokemon.setDescription(desc);
                    break;

                case "moveInfoses":
                    int count = dataIn.readInt();
                    for (int i = 0; i < count; i++)
                    {
                        try
                        {
                            MoveInfos moveInfos = readMove(app, dataIn);
                            moveInfoses.add(moveInfos);
                        }
                        catch (UnsupportedOperationException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    break;

                case "abilities":
                    int abilityCount = dataIn.readInt();
                    for (int i = 0; i < abilityCount; i++)
                    {
                        try
                        {
                            Ability ability = readAbility(app, dataIn);
                            abilities.add(ability);
                        }
                        catch (UnsupportedOperationException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    break;

                case "stats":
                    stats.readFrom(dataIn);
                    break;

                default:
                    System.err.println("Unknown entry for Pokédex entry: " + entry.getName());
                    break;

            }
        }
        if (pokemon != null)
        {
            pokemon.getAbilities().addAll(abilities);
            if (sprite != null)
                pokemon.setDefaultSprite(sprite);
            if (icon != null)
                pokemon.setIcon(icon);
            if (shinySprite != null)
                pokemon.setShinySprite(shinySprite);
            pokemon.setStats(stats);
        }
        return pokemon;
    }

    public List<Ability> getAbilities()
    {
        return abilities;
    }

    public void setShinySprite(BufferedImage shinySprite)
    {
        this.shinySprite = shinySprite;
    }

    public BufferedImage getShinySprite()
    {
        return shinySprite;
    }

    public String getFullID()
    {
        DecimalFormat format = new DecimalFormat("000");
        return format.format(getPokeapiID())+getEnglishName();
    }

    public boolean canLearn(MoveInfos moveInfos)
    {
        return moveInfoses.contains(moveInfos);
    }

    public void setStats(PokemonStats stats)
    {
        this.stats = stats;
    }

    public PokemonStats getStats()
    {
        return stats;
    }

    public boolean canLearn(String s)
    {
        for (MoveInfos m : moveInfoses)
        {
            if(m.getEnglishName().equalsIgnoreCase(s))
                return true;
        }
        return false;
    }

    public void setSpeciesID(int speciesID) {
        this.speciesID = speciesID;
    }

    public int getSpeciesID() {
        return speciesID;
    }

    public void setDexID(int dexID) {
        this.dexID = dexID;
    }

    public int getDexID() {
        return dexID;
    }

    public boolean isMega() {
        return name.endsWith("Mega"); // TODO: Better check
    }
}
