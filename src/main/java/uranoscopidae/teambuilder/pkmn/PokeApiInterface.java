package uranoscopidae.teambuilder.pkmn;

import me.sargunvohra.lib.pokekotlin.client.PokeApiClient;
import me.sargunvohra.lib.pokekotlin.model.*;
import uranoscopidae.builder.DataBuilderMain;
import uranoscopidae.teambuilder.app.Settings;
import uranoscopidae.teambuilder.app.TeamBuilderApp;
import uranoscopidae.teambuilder.app.team.PokemonGender;
import uranoscopidae.teambuilder.pkmn.api.SpriteCache;
import uranoscopidae.teambuilder.pkmn.moves.*;
import uranoscopidae.teambuilder.pkmn.moves.MoveCategory;
import uranoscopidae.teambuilder.utils.CsvTable;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.zip.ZipOutputStream;

public class PokeApiInterface {

    private final PokeApiClient apiClient;
    private final Map<Integer, PokemonInfos> pkmnCache;
    private final Map<Integer, MoveInfos> moveCache;
    private final List<String> pkmnNameList;
    private final List<String> moveNameList;
    private final TeamBuilderApp app;
    private final Settings settings;
    private SpriteCache spriteCache;

    public PokeApiInterface(TeamBuilderApp app, Settings settings) {
        this.app = app;
        this.settings = settings;
        apiClient = new PokeApiClient();
        pkmnCache = new HashMap<>();
        moveCache = new HashMap<>();
        pkmnNameList = new LinkedList<>();
        moveNameList = new LinkedList<>();
        spriteCache = new SpriteCache(this);
    }

    public void loadPokemonList(JProgressBar bar) {
        CsvTable pokemons = csv("pokemon");
        CsvTable types = csv("types");
        CsvTable pkmnTypes = csv("pokemon_types");
        // pokemon_id,version_group_id,move_id,pokemon_move_method_id,level,order
        CsvTable pkmnMoves = csv("pokemon_moves");
        // System.out.println(Arrays.toString(pokemons.getColumnNames()));
        // [id, identifier, species_id, height, weight, base_experience, order, is_default]
        bar.setIndeterminate(false);
        bar.setMinimum(0);
        bar.setMaximum(pokemons.getRowCount());
        bar.setValue(0);
        for(int i = 0;i<pokemons.getRowCount();i++) {
            int apiID = parseInt(pokemons.getElement("id", i), 0);
            String name = pokemons.getElement("identifier", i);

            Type firstType = TypeList.none;
            Type secondType = TypeList.none;
            for(int row = 0;row<pkmnTypes.getRowCount();row++) {
                if(String.valueOf(apiID).equals(pkmnTypes.getElement("pokemon_id", row))) { // correct pokemon
                    int slot = parseInt(pkmnTypes.getElement("slot", row), 1);
                    Type type = TypeList.getFromID(findInTable(types, "identifier", "id", pkmnTypes.getElement("type_id", row)));
                    switch (slot) {
                        case 1:
                            firstType = type;
                            break;

                        case 2:
                            secondType = type;
                            break;
                    }
                }
            }

            name = capitalizeWords(name.replace("-", " "));

            PokemonInfos pkmn = new PokemonInfos(name, firstType, secondType, apiID);
            pkmn.setDefaultSprite(getSprite(apiID, PokemonGender.MALE, false));
            pkmn.setShinySprite(getSprite(apiID, PokemonGender.MALE, true));
            pkmn.setIcon(getSprite(apiID, PokemonGender.MALE, false));

            pkmnCache.put(apiID, pkmn);
            pkmnNameList.add(pkmn.getEnglishName());

            bar.setValue(i);
            bar.setString("Loading Pokémons - Loaded "+name+" ("+apiID+")");
        }

        for(int row = 0;row<pkmnMoves.getRowCount();row++) {
            int pkmnApiID = parseInt(pkmnMoves.getElement("pokemon_id", row), 0);

            int moveID = parseInt(pkmnMoves.getElement("move_id", row), 0);
            if(moveID > 0) {
                MoveInfos infos = getMoveFromID(moveID);
                getPokemonFromID(pkmnApiID).addMove(infos);
            }
        }
    }

    private static int parseInt(String s, int defaultValue) {
        if(s == null || s.isEmpty())
            return defaultValue;
        return Integer.parseInt(s);
    }

    private static CsvTable csv(String name) {
        final String root = "/building/pokeapi/data/v2/csv/";
        return CsvTable.fromReader(new InputStreamReader(DataBuilderMain.class.getResourceAsStream(root+name+".csv")));
    }

    private static String findInTable(CsvTable table, String toFind, String knownColumn, String knownValue) {
        int toFindColumnID = table.getColumnWithName(toFind);
        if(toFindColumnID < -1)
            return null;
        for(int row = 0;row<table.getRowCount();row++) {
            if(table.getElement(knownColumn, row).equals(knownValue)) {
                return table.getElement(toFindColumnID, row);
            }
        }
        return null;
    }

    public void loadMoveList(JProgressBar bar) {
        CsvTable moves = csv("moves");
        CsvTable types = csv("types");
        CsvTable damageClass = csv("move_damage_classes");
        bar.setIndeterminate(false);
        bar.setValue(0);
        bar.setMinimum(0);
        bar.setMaximum(moves.getRowCount());
        for(int i = 0;i<moves.getRowCount();i++) {
            int apiID = parseInt(moves.getElement("id", i), 0);
            int typeID = parseInt(moves.getElement("type_id", i), 0);
            int categoryID = parseInt(moves.getElement("damage_class_id", i), 0);
            int power = parseInt(moves.getElement("power", i), 0);
            int accuracy = parseInt(moves.getElement("accuracy", i), 0);
            int pp = parseInt(moves.getElement("pp", i), 0);
            String name = moves.getElement("identifier", i);

            String typeName = findInTable(types, "identifier", "id", Integer.toString(typeID));
            String categoryName = findInTable(damageClass, "identifier", "id", Integer.toString(categoryID));

            Type type = TypeList.getFromID(typeName);
            MoveCategory category = MoveCategory.valueOf(categoryName.toUpperCase());

            name = capitalizeWords(name.replace("-", " "));
            MoveInfos move = new MoveInfos(apiID, type, category, name, power, accuracy, pp);
            moveCache.put(apiID, move);

            moveNameList.add(move.getEnglishName());
            bar.setValue(i);
            bar.setString("Loading moves - Loaded "+name+" ("+apiID+")");
        }
    }

    public PokemonInfos getPokemonFromName(String name) {
        Optional<PokemonInfos> pkmnInfos = pkmnCache.values().stream()
                .filter(infos -> infos.getEnglishName().equalsIgnoreCase(name))
                .findFirst();
        return pkmnInfos.orElseGet(()-> null);
    }

    public MoveInfos getMoveFromName(String name) {
        Optional<MoveInfos> moveInfos = moveCache.values().stream()
                .filter(infos -> infos.getEnglishName().equalsIgnoreCase(name))
                .findFirst();
        return moveInfos.orElseGet(()-> null);
    }

    public PokemonInfos getPokemonFromID(int id) {
        if(pkmnCache.containsKey(id)) {
            return pkmnCache.get(id);
        }
        System.out.println("Loading info from PokeApi.co for Pokemon with resource ID "+id);
        Pokemon pokemon = apiClient.getPokemon(id);
        Type firstType = TypeList.getFromID(pokemon.getTypes().get(0).getType().getName());
        Type secondType = TypeList.none;
        if(pokemon.getTypes().size() > 1)
            secondType = TypeList.getFromID(pokemon.getTypes().get(1).getType().getName());
        PokemonInfos infos = new PokemonInfos(pokemon.getName(), firstType, secondType, id);
        infos.setDefaultSprite(getSprite(pokemon.getId(), PokemonGender.MALE, false));
        infos.setShinySprite(getSprite(pokemon.getId(), PokemonGender.MALE, true));
        infos.setIcon(getSprite(pokemon.getId(), PokemonGender.MALE, true));

        for(PokemonMove move : pokemon.getMoves()) {
            infos.addMove(getMoveFromID(move.getMove().getId()));
        }
        try {
            if(!settings.getDexLocation().exists()) {
                settings.getDexLocation().mkdirs();
            }
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(new File(settings.getDexLocation(), infos.getFullID()+".dexd")));
            infos.writeTo(out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pkmnCache.put(id, infos);
        return infos;
    }

    public MoveInfos getMoveFromID(int id) {
        if(moveCache.containsKey(id)) {
            return moveCache.get(id);
        }
        System.out.println("Loading info from PokeApi.co for Move with resource ID "+id);
        Move move = apiClient.getMove(id);
        int power = move.getPower() != null ? move.getPower() : 0;
        int accuracy = move.getAccuracy() != null ? move.getAccuracy() : 0;
        int pp = move.getPp() != null ? move.getPp() : 0;
        String name = move.getName();
        name = capitalizeWords(name.replace("-", " "));
        MoveInfos infos = new MoveInfos(id, TypeList.getFromID(move.getType().getName()), MoveCategory.valueOf(move.getDamageClass().getName().toUpperCase()), name, power, accuracy, pp);
        try {
            if(!settings.getMovesLocation().exists()) {
                settings.getMovesLocation().mkdirs();
            }
            FileOutputStream out = new FileOutputStream(new File(settings.getMovesLocation(), infos.getEnglishName()+".movd"));
            infos.writeTo(out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        moveCache.put(id, infos);
        return infos;
    }

    private String capitalizeWords(String s) {
        String[] words = s.split(" ");
        for (int i = 0; i < words.length; i++) {
            String w = words[i];
            words[i] = Character.toUpperCase(w.charAt(0)) + w.substring(1);
        }
        return String.join(" ", (CharSequence[]) words);
    }

    public List<String> getPokemonNames() {
        return pkmnNameList;
    }

    public List<String> getMoveNames() {
        return moveNameList;
    }

    public BufferedImage getSprite(int apiID, PokemonGender gender, boolean isShiny) {
        return spriteCache.get(apiID, gender, isShiny);
    }
}
