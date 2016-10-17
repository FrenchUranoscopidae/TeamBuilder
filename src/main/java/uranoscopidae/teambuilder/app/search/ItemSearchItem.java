package uranoscopidae.teambuilder.app.search;

import uranoscopidae.teambuilder.pkmn.items.Item;

import javax.swing.*;

public class ItemSearchItem extends SearchItem
{
    private final Item item;
    public static final String[] COLUMNS = {"Icon", "Name", "Description", "Type"};

    public ItemSearchItem(SearchZone searchZone, Item item)
    {
        super(searchZone);
        this.item = item;
    }

    public JComponent generateComponent(int index, int totalCount)
    {
        JPanel panel = new JPanel();
        setBackgroundColor(panel, index);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        JLabel iconLabel = parent.getBuilderPane().createImageLabel(item.getIcon(), 24, 24);
        panel.add(iconLabel);
        panel.add(new JLabel(item.getEnglishName()+" ("+item.getType()+")"));
        panel.add(Box.createHorizontalGlue());
        JLabel descLabel = new JLabel(item.getDescription());
        panel.add(descLabel);
        panel.add(Box.createHorizontalStrut(15));
        return panel;
    }

    @Override
    public String toStringID()
    {
        return item.getEnglishName();
    }

    @Override
    public int compareTo(SearchItem o, int column)
    {
        if(o instanceof ItemSearchItem)
        {
            return item.getEnglishName().compareTo(((ItemSearchItem) o).item.getEnglishName());
        }
        return 0;
    }

    @Override
    public Object getValue(int column) {
        if(column == columnFromName(COLUMNS, "Icon"))
            return item.getIcon();
        if(column == columnFromName(COLUMNS, "Name"))
            return item.getEnglishName();
        if(column == columnFromName(COLUMNS, "Description"))
            return item.getDescription();
        if(column == columnFromName(COLUMNS, "Type"))
            return item.getType();
        return "TODO";
    }

}
