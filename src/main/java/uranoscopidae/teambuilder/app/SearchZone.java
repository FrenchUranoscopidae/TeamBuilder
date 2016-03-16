package uranoscopidae.teambuilder.app;

import uranoscopidae.teambuilder.app.search.ItemSearchItem;
import uranoscopidae.teambuilder.app.search.SearchItem;
import uranoscopidae.teambuilder.pkmn.items.Item;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SearchZone extends JPanel
{
    private final BuilderArea parent;
    private final List<SearchItem> data;
    private final TeamBuilderApp app;

    public SearchZone(BuilderArea parent)
    {
        setLayout(new BorderLayout());
        this.parent = parent;
        this.app = parent.getApp();
        data = new LinkedList<>();
    }

    public void searchItem(JTextField itemName, JLabel itemIcon)
    {
        data.clear();
        for(Item item : ItemMap.getAllItems())
        {
            String nameStart = itemName.getText();
            if(!item.getName().toLowerCase().startsWith(nameStart.toLowerCase())) // filter out item names not starting with given text
                continue;
            data.add(new ItemSearchItem(this, item));
        }
        setData(data);
    }

    private void setData(List<SearchItem> data)
    {
        removeAll();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        Collections.sort(data);
        for(SearchItem item : data)
        {
            add(item.generateComponent());
        }
        repaint();
    }

    public BuilderArea getBuilderPane()
    {
        return parent;
    }
}
