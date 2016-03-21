package uranoscopidae.teambuilder.app.search;

import uranoscopidae.teambuilder.app.SearchZone;
import uranoscopidae.teambuilder.pkmn.items.Item;

import javax.swing.*;
import java.awt.*;

public class ItemSearchItem extends SearchItem
{
    private final Item item;

    public ItemSearchItem(SearchZone searchZone, Item item)
    {
        super(searchZone);
        this.item = item;
    }

    @Override
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
    public String toString()
    {
        return item.getEnglishName();
    }

    @Override
    public int compareTo(SearchItem o)
    {
        if(o instanceof ItemSearchItem)
        {
            return item.getEnglishName().compareTo(((ItemSearchItem) o).item.getEnglishName());
        }
        return 0;
    }
}
