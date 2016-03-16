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
    public JComponent generateComponent()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel iconLabel = parent.getBuilderPane().createImageLabel(item.getIcon(), 24, 24);
        panel.add(iconLabel);
        panel.add(new JLabel(item.getName()+" ("+item.getType()+")"));
        // TODO: Add description
        return panel;
    }

    @Override
    public int compareTo(SearchItem o)
    {
        if(o instanceof ItemSearchItem)
        {
            return item.getName().compareTo(((ItemSearchItem) o).item.getName());
        }
        return 0;
    }
}
