package uranoscopidae.builder;

import uranoscopidae.teambuilder.pkmn.PokemonInfos;
import uranoscopidae.teambuilder.pkmn.Type;
import uranoscopidae.teambuilder.pkmn.TypeList;
import uranoscopidae.teambuilder.pkmn.moves.MoveCategory;
import uranoscopidae.teambuilder.pkmn.moves.MoveInfos;
import uranoscopidae.teambuilder.utils.CsvTable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.zip.ZipOutputStream;

public class DataBuilderMain {

    // NOT MEANT TO BE RUN OUTSIDE A DEV ENVIRONMENT
    public static void main(String[] args) {
        buildMoves(new File(".", "tmp_movedata"));
        buildPokemons(new File(".", "tmp_dexdata"));
    }

    private static void buildPokemons(File outputFolder) {
        if(!outputFolder.exists())
            outputFolder.mkdirs();
        CsvTable pokemons = csv("pokemon");
        CsvTable types = csv("types");
        CsvTable pkmnTypes = csv("pokemon_types");
        System.out.println(Arrays.toString(pokemons.getColumnNames()));
        // [id, identifier, species_id, height, weight, base_experience, order, is_default]
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
            PokemonInfos pkmn = new PokemonInfos(name, firstType, secondType, apiID);

            // handle moves
            try {
                pkmn.writeTo(new ZipOutputStream(new FileOutputStream(new File(outputFolder, name+".dexd"))));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void buildMoves(File outputFolder) {
        if(!outputFolder.exists())
            outputFolder.mkdirs();
        CsvTable moves = csv("moves");
        CsvTable types = csv("types");
        CsvTable damageClass = csv("move_damage_classes");
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

            MoveInfos move = new MoveInfos(apiID, type, category, name, power, accuracy, pp);
            try {
                move.writeTo(new FileOutputStream(new File(outputFolder, name+".movd")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    private static int parseInt(String s, int defaultValue) {
        if(s == null || s.isEmpty())
            return defaultValue;
        return Integer.parseInt(s);
    }

    private static CsvTable csv(String name) {
        final String root = "/building/pokeapi/data/v2/";
        return CsvTable.fromReader(new InputStreamReader(DataBuilderMain.class.getResourceAsStream(root+name+".csv")));
    }
}
