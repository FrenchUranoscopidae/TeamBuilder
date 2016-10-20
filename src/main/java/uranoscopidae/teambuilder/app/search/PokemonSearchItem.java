package uranoscopidae.teambuilder.app.search;

import uranoscopidae.teambuilder.pkmn.PokemonInfos;
import uranoscopidae.teambuilder.pkmn.TypeList;

import javax.swing.*;
import java.awt.*;

public class PokemonSearchItem extends SearchItem
{
    private final PokemonInfos pokemon;
    public static final String[] COLUMNS = {"Icon", "DexID", "Name", "First Type", "Second Type", "HP", "Atk", "Def", "SpeA", "SpeD", "Speed"};

    public PokemonSearchItem(SearchZone searchZone, PokemonInfos pokemon)
    {
        super(searchZone);
        this.pokemon = pokemon;
    }

    @Override
    public Object getValue(int column) {
        if(column == columnFromName(COLUMNS, "Icon")) {
            return pokemon.getIcon();
        }
        if(column == columnFromName(COLUMNS, "Name")) {
            return pokemon.getEnglishName();
        }
        if(column == columnFromName(COLUMNS, "DexID")) {
            return pokemon.getDexID();
        }
        if(column == columnFromName(COLUMNS, "Atk")) {
            return pokemon.getStats().getAttack();
        }
        if(column == columnFromName(COLUMNS, "Def")) {
            return pokemon.getStats().getDefense();
        }
        if(column == columnFromName(COLUMNS, "SpeA")) {
            return pokemon.getStats().getSpecialAttack();
        }
        if(column == columnFromName(COLUMNS, "SpeD")) {
            return pokemon.getStats().getSpecialDefense();
        }
        if(column == columnFromName(COLUMNS, "Speed")) {
            return pokemon.getStats().getSpeed();
        }
        if(column == columnFromName(COLUMNS, "HP")) {
            return pokemon.getStats().getHP();
        }
        if(column == columnFromName(COLUMNS, "First Type")) {
            return pokemon.getFirstType();
        }
        if(column == columnFromName(COLUMNS, "Second Type")) {
            return pokemon.getSecondType();
        }
        return "TODO";
    }

    @Override
    public String toStringID()
    {
        return pokemon.getEnglishName();
    }

    public PokemonInfos getPokemon()
    {
        return pokemon;
    }
}
