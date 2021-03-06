package uranoscopidae.teambuilder.pkmn.api;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.sargunvohra.lib.pokekotlin.client.PokeApiClient;
import me.sargunvohra.lib.pokekotlin.model.Move;
import me.sargunvohra.lib.pokekotlin.model.Pokemon;
import me.sargunvohra.lib.pokekotlin.model.PokemonMove;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import uranoscopidae.teambuilder.app.Settings;
import uranoscopidae.teambuilder.app.TeamBuilderApp;
import uranoscopidae.teambuilder.app.team.PokemonGender;
import uranoscopidae.teambuilder.pkmn.*;
import uranoscopidae.teambuilder.pkmn.Ability;
import uranoscopidae.teambuilder.pkmn.items.Item;
import uranoscopidae.teambuilder.pkmn.moves.*;
import uranoscopidae.teambuilder.pkmn.moves.MoveCategory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

public class PokeApiInterface {

    private final PokeApiClient apiClient;
    private final Map<Integer, PokemonInfos> pkmnCache;
    private final Map<Integer, MoveInfos> moveCache;
    private final Map<Integer, Ability> abilityCache;
    private final Map<Integer, Item> itemCache;
    private final List<String> pkmnNameList;
    private final List<String> moveNameList;
    private final List<String> itemNameList;
    private final TeamBuilderApp app;
    private final Settings settings;
    private SpriteCache spriteCache;

    public PokeApiInterface(TeamBuilderApp app, Settings settings) {
        this.app = app;
        this.settings = settings;
        apiClient = new PokeApiClient();
        pkmnCache = new HashMap<>();
        moveCache = new HashMap<>();
        abilityCache = new HashMap<>();
        itemCache = new HashMap<>();
        pkmnNameList = new LinkedList<>();
        itemNameList = new LinkedList<>();
        moveNameList = new LinkedList<>();
        spriteCache = new SpriteCache(this);

        // load 0th Pokémon (placeholder)
        PokemonInfos zeroth = new PokemonInfos("?", 0);
        zeroth.setDescription("No Pokémon");
        zeroth.setDefaultSprite(getSprite(0, PokemonGender.ASEXUAL, false));
        zeroth.setShinySprite(zeroth.getDefaultSprite());
        zeroth.setIcon(new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB));
        pkmnCache.put(0, zeroth);
        pkmnNameList.add(zeroth.getEnglishName());
    }

    public List<String> getItemNames() {
        return itemNameList;
    }

    public void loadItems(JProgressBar bar) {
        List<CSVRecord> items = csv("items");
        for (CSVRecord rec : items) {
            int apiID = parseInt(rec.get("id"), 0);
            String identifier = rec.get("identifier");
            String name = capitalizeWords(identifier.replace("-", " "));
            Item item = new Item(apiID, name, "<TODO>"); // TODO: Item type
            try {
                item.setIcon(ImageIO.read(getClass().getResourceAsStream("/building/pokeapi/data/v2/sprites/items/" + identifier + ".png")));
            } catch (IllegalArgumentException e) {
                // shhhh, ignore it
            } catch (IOException e) {
                e.printStackTrace();
            }

            itemCache.put(apiID, item);
            itemNameList.add(name);
        }
    }

    public void loadPkmnDexIDs(JProgressBar bar) {
        List<CSVRecord> dexNumbers = csv("pokemon_dex_numbers");

        bar.setIndeterminate(false);
        bar.setMinimum(0);
        bar.setMaximum(dexNumbers.size());
        for(CSVRecord dexID : dexNumbers) {
            int pokedexID = parseInt(dexID.get("pokedex_id"), 0);
            if(pokedexID == 1) { // National
                int number = parseInt(dexID.get("pokedex_number"), 0);
                int speciesID = parseInt(dexID.get("species_id"), 0);
                getPokemonListFromSpecies(speciesID).forEach(p -> p.setDexID(number));
            }

            bar.setValue(bar.getValue()+1);
        }
    }

    public void loadPkmnIcons(JProgressBar bar) {
        Gson gson = new Gson();
        JsonObject object = gson.fromJson(new InputStreamReader(getClass().getResourceAsStream("/pokemonicons/pkmn.json")), JsonObject.class);
        for (Map.Entry<String, JsonElement> entry: object.entrySet()) {
            JsonObject element = entry.getValue().getAsJsonObject();
            int dexID = element.get("idx").getAsInt();
            List<PokemonInfos> pokemons = getPokemonListFromDexID(dexID);
            String name = element.getAsJsonObject("slug").get("eng").getAsString();
            for(PokemonInfos infos : pokemons) {
                String iconName = name;
                if(infos.isMega()) {
                    iconName+="-mega";
                }
                // TODO: Shiny icon
                // TODO: Female icons
                try {
                    infos.setIcon(ImageIO.read(getClass().getResourceAsStream("/pokemonicons/regular/"+iconName+".png")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void loadPokemonList(JProgressBar bar) {
        List<CSVRecord> pokemons = csv("pokemon");
        List<CSVRecord> types = csv("types");
        List<CSVRecord> pkmnTypes = csv("pokemon_types");
        // pokemon_id,version_group_id,move_id,pokemon_move_method_id,level,order
        List<CSVRecord> pkmnMoves = csv("pokemon_moves");
        // [id, identifier, species_id, height, weight, base_experience, order, is_default]
        bar.setIndeterminate(false);
        bar.setMinimum(0);
        bar.setMaximum(pokemons.size());
        bar.setValue(0);
        for(int i = 0;i<pokemons.size();i++) {
            CSVRecord record = pokemons.get(i);
            int apiID = parseInt(record.get("id"), 0);
            String name = record.get("identifier");

            name = capitalizeWords(name.replace("-", " "));

            PokemonInfos pkmn = new PokemonInfos(name, apiID);
            pkmn.setSpeciesID(parseInt(record.get("species_id"), 0));
            // TODO: Female sprites
            pkmn.setDefaultSprite(getSprite(apiID, PokemonGender.MALE, false));
            pkmn.setShinySprite(getSprite(apiID, PokemonGender.MALE, true));

            pkmnCache.put(apiID, pkmn);
            pkmnNameList.add(pkmn.getEnglishName());

            bar.setValue(i);
            bar.setString("Loading Pokémons - Loaded "+name+" ("+apiID+")");
        }

        for(int row = 0;row<pkmnTypes.size();row++) {
            CSVRecord pkmnType = pkmnTypes.get(row);
            int pkmnID = parseInt(pkmnType.get("pokemon_id"), 0);
            int slot = parseInt(pkmnType.get("slot"), 1);
            uranoscopidae.teambuilder.pkmn.Type type = TypeList.getFromID(findInTable(types, "identifier", "id", pkmnType.get("type_id")));
            PokemonInfos infos = getPokemonFromID(pkmnID);
            switch (slot) {
                case 1:
                    infos.setFirstType(type);
                    break;

                case 2:
                    infos.setSecondType(type);
                    break;
            }
        }

        bar.setString("Assigning moves to Pokémons");

        for(CSVRecord pkmnMove : pkmnMoves) {
            int pkmnApiID = parseInt(pkmnMove.get("pokemon_id"), 0);

            int moveID = parseInt(pkmnMove.get("move_id"), 0);
            if(moveID > 0) {
                MoveInfos infos = getMoveFromID(moveID);
                getPokemonFromID(pkmnApiID).addMove(infos);
            }
        }

        bar.setString("Assigning stats to Pokémons");
        List<CSVRecord> pkmnStats = csv("pokemon_stats");
        for(CSVRecord statRec : pkmnStats) {
            int pokemonID = parseInt(statRec.get("pokemon_id"), 0);
            PokemonInfos infos = getPokemonFromID(pokemonID);
            int statID = parseInt(statRec.get("stat_id"), 0);
            int value = parseInt(statRec.get("base_stat"), 0);
            switch (statID) {
                case 1: // HP
                    infos.getStats().setHP(value);
                    break;

                case 2: // Attack
                    infos.getStats().setAttack(value);
                    break;

                case 3: // Defense
                    infos.getStats().setDefense(value);
                    break;

                case 4: // Special Attack
                    infos.getStats().setSpecialAttack(value);
                    break;

                case 5: // Special Defense
                    infos.getStats().setSpecialDefense(value);
                    break;

                case 6: // Speed
                    infos.getStats().setSpeed(value);
                    break;
            }
        }
    }

    public void loadAbilityList(JProgressBar bar) {
        List<CSVRecord> abilities = csv("abilities");
        List<CSVRecord> abilityDescriptions = csv("ability_flavor_text");

        bar.setIndeterminate(false);
        bar.setMinimum(0);
        bar.setMaximum(abilities.size());
        bar.setValue(0);
        for(CSVRecord ability : abilities) {
            String name = capitalizeWords(ability.get("identifier").replace("-", " "));
            int abilityID = parseInt(ability.get("id"), 0);
            String desc = loadFlavorText(abilityDescriptions, "ability_id", abilityID, 9);
            Ability infos = new Ability(abilityID, name, desc);
            abilityCache.put(abilityID, infos);

            bar.setValue(bar.getValue()+1);
        }

        bar.setString("Assigning abilities to Pokémons");
        List<CSVRecord> pokemonAbilities = csv("pokemon_abilities");
        for(CSVRecord rec : pokemonAbilities) {
            PokemonInfos pokemon = getPokemonFromID(parseInt(rec.get("pokemon_id"), 0));
            Ability ability = getAbilityFromID(parseInt(rec.get("ability_id"), 0));
            pokemon.getAbilities().add(ability);
        }
    }

    private static int parseInt(String s, int defaultValue) {
        if(s == null || s.isEmpty())
            return defaultValue;
        return Integer.parseInt(s);
    }

    private static List<CSVRecord> csv(String name) {
        final String root = "/building/pokeapi/data/v2/csv/";
        try {
            CSVParser parser = new CSVParser(new InputStreamReader(PokeApiInterface.class.getResourceAsStream(root+name+".csv")), CSVFormat.DEFAULT.withFirstRecordAsHeader().withRecordSeparator("\r"));
            try {
                List<CSVRecord> records = new LinkedList<>();
                for(CSVRecord rec : parser) {
                    records.add(rec);
                }
                parser.close();
                return records;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String findInTable(List<CSVRecord> table, String toFind, String knownColumn, String knownValue) {
        for(CSVRecord rec : table) {
            if(rec.get(knownColumn).equals(knownValue)) {
                return rec.get(toFind);
            }
        }
        return null;
    }

    public void loadMoveList(JProgressBar bar) {
        List<CSVRecord> moves = csv("moves");
        List<CSVRecord> types = csv("types");
        List<CSVRecord> damageClass = csv("move_damage_classes");
        List<CSVRecord> flavorText = csv("move_flavor_text");
        bar.setIndeterminate(false);
        bar.setValue(0);
        bar.setMinimum(0);
        bar.setMaximum(moves.size());
        for (int i = 0; i < moves.size(); i++) {
            CSVRecord moveCSV = moves.get(i);
            int apiID = parseInt(moveCSV.get("id"), 0);
            int typeID = parseInt(moveCSV.get("type_id"), 0);
            int categoryID = parseInt(moveCSV.get("damage_class_id"), 0);
            int power = parseInt(moveCSV.get("power"), 0);
            int accuracy = parseInt(moveCSV.get("accuracy"), 0);
            int pp = parseInt(moveCSV.get("pp"), 0);
            String name = moveCSV.get("identifier");

            String typeName = findInTable(types, "identifier", "id", Integer.toString(typeID));
            String categoryName = findInTable(damageClass, "identifier", "id", Integer.toString(categoryID));

            uranoscopidae.teambuilder.pkmn.Type type = TypeList.getFromID(typeName);
            MoveCategory category = MoveCategory.valueOf(categoryName.toUpperCase());

            name = capitalizeWords(name.replace("-", " "));
            MoveInfos move = new MoveInfos(apiID, type, category, name, power, accuracy, pp);
            try {
                // TODO: More efficient method (loading the descriptions and assigning each description to moves)
                move.setDescription(loadMoveDescription(flavorText, apiID, 9));
            } catch (Exception e) {
                e.printStackTrace();
            }
            moveCache.put(apiID, move);

            moveNameList.add(move.getEnglishName());
            bar.setValue(i);
            bar.setString("Loading moves - Loaded " + name + " (" + apiID + ")");
        }
    }

    /**
     *
     * @param table
     * @param moveID
     * @param languageID
     * 9 is English (only value supported for the moment)
     * @return
     */
    private String loadMoveDescription(List<CSVRecord> table, int moveID, int languageID) {
        return loadFlavorText(table, "move_id", moveID, languageID);
    }

    private String loadFlavorText(List<CSVRecord> table, String idColumn, int moveID, int languageID) {
        for(CSVRecord record : table) {
            int foundID = parseInt(record.get(idColumn), 0);
            if(foundID == moveID) {
                int foundLanguageID = parseInt(record.get("language_id"), 0);
                if(foundLanguageID == languageID) {
                    int versionID = parseInt(record.get("version_group_id"), 0);
                    if(versionID == 16) { // TODO: Don't hardcode this value
                        return record.get("flavor_text");
                    }
                }
            }
        }
        return "<NOT FOUND>";
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

    public List<PokemonInfos> getPokemonListFromDexID(int id) {
        return pkmnCache.values().stream()
                .filter(infos -> infos.getDexID() == id)
                .collect(Collectors.toList());
    }

    public List<PokemonInfos> getPokemonListFromSpecies(int id) {
        return pkmnCache.values().stream()
                .filter(infos -> infos.getSpeciesID() == id)
                .collect(Collectors.toList());
    }

    public PokemonInfos getPokemonFromID(int id) {
        if(pkmnCache.containsKey(id)) {
            return pkmnCache.get(id);
        }
        System.out.println("Loading info from PokeApi.co for Pokemon with resource ID "+id);
        Pokemon pokemon = apiClient.getPokemon(id);
        uranoscopidae.teambuilder.pkmn.Type firstType = TypeList.getFromID(pokemon.getTypes().get(0).getType().getName());
        uranoscopidae.teambuilder.pkmn.Type secondType = TypeList.none;
        if(pokemon.getTypes().size() > 1)
            secondType = TypeList.getFromID(pokemon.getTypes().get(1).getType().getName());
        PokemonInfos infos = new PokemonInfos(pokemon.getName(), id);
        infos.setFirstType(firstType);
        infos.setSecondType(secondType);
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

    public Ability getAbilityFromID(int id) {
        return abilityCache.get(id); // TODO: Does not try to load from the website
    }

    public Item getItemFromID(int id) {
        return itemCache.get(id); // TODO: Does not try to load from the website
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

    public Ability getAbilityFromName(String name) {
        Optional<Ability> ability = abilityCache.values().stream()
                .filter(infos -> infos.getEnglishName().equalsIgnoreCase(name))
                .findFirst();
        return ability.orElseGet(()-> null);
    }

    public Item getItemFromName(String name) {
        Optional<Item> ability = itemCache.values().stream()
                .filter(infos -> infos.getEnglishName().equalsIgnoreCase(name))
                .findFirst();
        return ability.orElseGet(()-> null);
    }

    public Collection<Item> getItems() {
        return itemCache.values();
    }

    public Collection<PokemonInfos> getPokemons() {
        return pkmnCache.values();
    }

    public Collection<MoveInfos> getMoves() {
        return moveCache.values();
    }
}
