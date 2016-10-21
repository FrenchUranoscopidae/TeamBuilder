package uranoscopidae.teambuilder.app.search;

import uranoscopidae.teambuilder.app.BuilderArea;
import uranoscopidae.teambuilder.app.ConfirmableTextField;
import uranoscopidae.teambuilder.app.TeamBuilderApp;
import uranoscopidae.teambuilder.app.team.TeamEntry;
import uranoscopidae.teambuilder.pkmn.PokemonInfos;
import uranoscopidae.teambuilder.pkmn.Type;
import uranoscopidae.teambuilder.pkmn.items.Item;
import uranoscopidae.teambuilder.pkmn.moves.MoveInfos;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;

public class SearchZone extends JPanel {
    private final BuilderArea parent;
    private final TeamBuilderApp app;
    private final JTable table;
    private final SearchZoneTableModel model;
    private TeamEntry entry;
    private ConfirmableTextField currentField;
    private List<SearchItem> currentItems;

    public SearchZone(BuilderArea parent) {
        setLayout(new BorderLayout());
        this.parent = parent;
        this.app = parent.getApp();
        model = new SearchZoneTableModel();
        table = new JTable(model) ;
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table));

        DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();
        table.setDefaultRenderer(SearchItem.class, new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = createRenderer(table, value, isSelected, hasFocus, row, column);
                if(!isSelected)
                    comp.setBackground(row % 2 == 0 ? Color.lightGray.brighter() : Color.lightGray);
                else
                    comp.setBackground(table.getSelectionBackground());
                return comp;
            }

            private Component createRenderer(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if(value instanceof Type) {
                    JLabel label = new JLabel(new ImageIcon(((Type)value).getIcon()));
                    label.setOpaque(true);
                    return label;
                }
                if(value instanceof BufferedImage) {
                    JLabel label = new JLabel(new ImageIcon((BufferedImage)value));
                    label.setOpaque(true);
                    return label;
                }
                return defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                SearchItem item = model.getItems().get(table.getRowSorter().convertRowIndexToModel(table.getSelectedRow()));
                currentField.setText(item.toStringID());
                currentField.updateConfirmationState();
            }
        });

    }

    public void setCurrentEntry(TeamEntry pokemon)
    {
        this.entry = pokemon;
    }

    public void searchPokemon(ConfirmableTextField pokemonName)
    {
        currentField = pokemonName;
        model.clear(PokemonSearchItem.COLUMNS);
        for(PokemonInfos pokemon : app.getPokemons())
        {
            if(pokemon.getPokeapiID() == 0)
                continue;
            String nameStart = pokemonName.getText();
            if(!matches(pokemon.getEnglishName(), pokemon.getFirstType()+" "+pokemon.getSecondType(), nameStart, false))
                continue;

            model.add(new PokemonSearchItem(this, pokemon));
        }
        setData(pokemonName, null);
    }

    public void searchItem(ConfirmableTextField itemName, JLabel itemIcon)
    {
        currentField = itemName;
        model.clear(ItemSearchItem.COLUMNS);
        for(Item item : app.getItems())
        {
            String nameStart = itemName.getText();
            if(!matches(item.getEnglishName(), item.getDescription(), nameStart, false)) // filter out item names not starting with given text
                continue;
            model.add(new ItemSearchItem(this, item));
        }
        setData(itemName, null);
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
        currentField = moveName;
        model.clear(MoveSearchItem.MOVE_COLUMNS);
        String nameStart = moveName.getText();
        final boolean allowIllegal = nameStart.startsWith("!");
        if(allowIllegal)
        {
            nameStart = nameStart.substring(1);
        }
        for(MoveInfos moveInfos : app.getMoves())
        {
            if(entry.hasPokemon() && (!allowIllegal && !getCurrentEntry().getPokemon().canLearn(moveInfos)))
                continue;
            if(!matches(moveInfos.getEnglishName(), moveInfos.getType().getName(), nameStart, true)) // filter out item names not starting with given text
                continue;
            model.add(new MoveSearchItem(this, moveInfos));
        }
        setData(moveName, null);
    }

    private void setData(ConfirmableTextField toModify, List<SearchItem> data)
    {
        TableRowSorter<SearchZoneTableModel> sorter = new TableRowSorter<>(model);
        for (int i = 0; i < model.getColumnCount(); i++) {
            sorter.setComparator(i, (o1, o2) -> {
                if(o1.equals("/"))
                    o1 = 0;
                if(o2.equals("/"))
                    o2 = 0;
                if(o1 instanceof Double) {
                    return Double.compare((Double)o1, (Double)o2);
                }
                if(o1 instanceof Integer) {
                    return Integer.compare((Integer)o1, (Integer)o2);
                }
                if(o1 instanceof Type) {
                    return ((Type) o1).getName().compareTo(((Type)o2).getName());
                }
                if(o1 instanceof String) {
                    return ((String) o1).compareTo((String) o2);
                }
                return 0;
            });
        }
        table.setRowSorter(sorter);
        sorter.toggleSortOrder(0);
        sorter.sort();

        table.invalidate();
        table.repaint();
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
                    String stringRepresentation = item.toStringID();
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
