package uranoscopidae.teambuilder.app.search;

import me.sargunvohra.lib.pokekotlin.model.Pokemon;
import uranoscopidae.teambuilder.pkmn.PokemonInfos;
import uranoscopidae.teambuilder.pkmn.TypeList;

import javax.swing.*;
import java.awt.*;

public class PokemonSearchItem extends SearchItem
{
    private final PokemonInfos pokemon;

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
        if(column == 0) {
            return pokemon.getEnglishName();
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
