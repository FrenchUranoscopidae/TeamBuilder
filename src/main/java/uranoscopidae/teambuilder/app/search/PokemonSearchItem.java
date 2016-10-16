package uranoscopidae.teambuilder.app.search;

import uranoscopidae.teambuilder.pkmn.PokemonInfos;
import uranoscopidae.teambuilder.pkmn.TypeList;

import javax.swing.*;
import java.awt.*;

public class PokemonSearchItem extends SearchItem
{
    private final PokemonInfos pokemon;
    public static final String[] COLUMNS = {"DexID", "Name", "First Type", "Second Type", "HP", "Atk", "Def", "SpeA", "SpeD", "Speed"};

    public PokemonSearchItem(SearchZone searchZone, PokemonInfos pokemon)
    {
        super(searchZone);
        this.pokemon = pokemon;
    }

    public JComponent generateComponent(int index, int totalCount)
    {
        JPanel panel = new JPanel();
        setBackgroundColor(panel, index);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(Box.createHorizontalStrut(10));
        String pokemonName = pokemon.getEnglishName();
        panel.add(parent.getBuilderPane().createImageLabel(pokemon.getIcon(), 38, 38));
        JLabel nameLabel = new JLabel(pokemonName);
        panel.add(nameLabel);
        nameLabel.setFont(new Font(null, Font.PLAIN, 14));
        panel.add(Box.createGlue());
        panel.add(parent.getBuilderPane().createImageLabel(pokemon.getFirstType().getIcon(), 32, 14));
        if(pokemon.getSecondType() != TypeList.none)
            panel.add(parent.getBuilderPane().createImageLabel(pokemon.getSecondType().getIcon(), 32, 14));
        panel.add(Box.createHorizontalStrut(10));
        return panel;
    }

    @Override
    public Object getValue(int column) {
        if(column == columnFromName(COLUMNS, "Name")) {
            return pokemon.getEnglishName();
        }
        if(column == columnFromName(COLUMNS, "DexID")) {
            return pokemon.getPokeapiID(); // TODO: use the real dex id
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

    @Override
    public int compareTo(SearchItem o, int column)
    {
        if(o == null)
            return 0;
        if(((PokemonSearchItem)o).getPokemon() == null)
            return 0;
        return pokemon.getEnglishName().compareTo(((PokemonSearchItem)o).getPokemon().getEnglishName());
    }

    public PokemonInfos getPokemon()
    {
        return pokemon;
    }
}
