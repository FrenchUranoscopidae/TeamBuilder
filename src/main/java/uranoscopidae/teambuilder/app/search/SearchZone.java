package uranoscopidae.teambuilder.app.search;

import uranoscopidae.teambuilder.app.BuilderArea;
import uranoscopidae.teambuilder.app.ConfirmableTextField;
import uranoscopidae.teambuilder.app.TeamBuilderApp;
import uranoscopidae.teambuilder.app.search.ItemSearchItem;
import uranoscopidae.teambuilder.app.search.MoveSearchItem;
import uranoscopidae.teambuilder.app.search.SearchItem;
import uranoscopidae.teambuilder.app.team.TeamEntry;
import uranoscopidae.teambuilder.pkmn.Pokemon;
import uranoscopidae.teambuilder.pkmn.PokemonMap;
import uranoscopidae.teambuilder.pkmn.items.Item;
import uranoscopidae.teambuilder.pkmn.items.ItemMap;
import uranoscopidae.teambuilder.pkmn.moves.Move;
import uranoscopidae.teambuilder.pkmn.moves.MoveMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SearchZone extends JPanel
{
    private final BuilderArea parent;
    private final List<SearchItem> data;
    private final TeamBuilderApp app;
    private TeamEntry entry;
    private ConfirmableTextField currentField;
    private List<SearchItem> currentItems;

    public SearchZone(BuilderArea parent)
    {
        setLayout(new BorderLayout());
        this.parent = parent;
        this.app = parent.getApp();
        data = new LinkedList<>();
    }

    public void setCurrentEntry(TeamEntry pokemon)
    {
        this.entry = pokemon;
    }

    public void searchPokemon(ConfirmableTextField pokemonName)
    {
        data.clear();
        for(Pokemon pokemon : PokemonMap.getAllPokemon())
        {
            String nameStart = pokemonName.getText();
            if(!matches(pokemon.getEnglishName(), pokemon.getFirstType()+" "+pokemon.getSecondType(), nameStart, false))
                continue;
            data.add(new PokemonSearchItem(this, pokemon));
        }
        setData(pokemonName, data);
    }

    public void searchItem(ConfirmableTextField itemName, JLabel itemIcon)
    {
        data.clear();
        for(Item item : ItemMap.getAllItems())
        {
            String nameStart = itemName.getText();
            if(!matches(item.getEnglishName(), item.getDescription(), nameStart, false)) // filter out item names not starting with given text
                continue;
            data.add(new ItemSearchItem(this, item));
        }
        setData(itemName, data);
    }

    private boolean matches(String value, String desc, String expr, boolean strictDescMatch)
    {
        if(expr.isEmpty())
        {
            return true;
        }
        if(value.toLowerCase().startsWith(expr.toLowerCase()))
        {
            return true;
        }

        if(expr.contains("[") && expr.contains("]") && desc != null)
        {
            if(expr.indexOf("[") > expr.lastIndexOf("]"))
                return false;
            String descToMatch = expr.substring(expr.indexOf("[")+1, expr.lastIndexOf("]"));
            String name = expr.substring(0, expr.indexOf("["));
            boolean isDescMatched;
            if(strictDescMatch)
            {
                isDescMatched = desc.toLowerCase().startsWith(descToMatch.toLowerCase());
            }
            else
            {
                isDescMatched = desc.toLowerCase().contains(descToMatch.toLowerCase());
            }
            boolean isNameMatched = value.toLowerCase().startsWith(name.toLowerCase());
            return isDescMatched && isNameMatched;
        }
        return false;
    }

    public void searchMove(ConfirmableTextField moveName)
    {
        data.clear();
        String nameStart = moveName.getText();
        final boolean allowIllegal = nameStart.startsWith("!");
        if(allowIllegal)
        {
            nameStart = nameStart.substring(1);
        }
        for(Move move : MoveMap.getAllMoves())
        {
            if(!allowIllegal && !getCurrentEntry().getPokemon().canLearn(move))
                continue;
            if(!matches(move.getEnglishName(), move.getType().getName(), nameStart, true)) // filter out item names not starting with given text
                continue;
            data.add(new MoveSearchItem(this, move));
        }
        setData(moveName, data);
    }

    private void setData(ConfirmableTextField toModify, List<SearchItem> data)
    {
        currentField = toModify;
        currentItems = data;
        removeAll();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        Collections.sort(data);
        for (int i = 0; i < data.size(); i++)
        {
            SearchItem item = data.get(i);
            JComponent component = item.generateComponent(i, data.size());
            add(component);
            final int index = i;
            component.addMouseListener(new MouseAdapter()
            {
                @Override
                public void mouseClicked(MouseEvent e)
                {
                    toModify.setText(item.toString());
                    confirm();
                }

                @Override
                public void mouseEntered(MouseEvent e)
                {
                    item.setHovered(true);
                    item.setBackgroundColor(component, index);
                }

                @Override
                public void mouseExited(MouseEvent e)
                {
                    item.setHovered(false);
                    item.setBackgroundColor(component, index);
                }
            });
            component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        add(Box.createVerticalGlue());
        updateUI();
    }

    public BuilderArea getBuilderPane()
    {
        return parent;
    }

    public TeamEntry getCurrentEntry()
    {
        return entry;
    }

    public void confirm()
    {
        if(currentField != null && currentItems != null)
        {
            if(currentItems.size() == 1)
            {
                currentField.setText(currentItems.get(0).toString());
                currentField.updateConfirmationState();
                clear();
            }
            else
            {
                for (SearchItem item : currentItems)
                {
                    String stringRepresentation = item.toString();
                    if(stringRepresentation.equals(currentField.getText()))
                    {
                        currentField.setText(stringRepresentation);
                        currentField.updateConfirmationState();
                        clear();
                        break;
                    }
                }
            }
        }
    }

    public void clear()
    {
        currentField = null;
        currentItems = null;
        removeAll();
        updateUI();
    }
}
