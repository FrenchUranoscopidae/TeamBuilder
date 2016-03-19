package uranoscopidae.teambuilder.app;

import uranoscopidae.teambuilder.app.search.ItemSearchItem;
import uranoscopidae.teambuilder.app.search.MoveSearchItem;
import uranoscopidae.teambuilder.app.search.SearchItem;
import uranoscopidae.teambuilder.pkmn.items.Item;
import uranoscopidae.teambuilder.pkmn.moves.Move;
import uranoscopidae.teambuilder.pkmn.moves.MoveMap;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

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
            if(!matches(item.getName(), item.getDescription(), nameStart)) // filter out item names not starting with given text
                continue;
            data.add(new ItemSearchItem(this, item));
        }
        setData(data);
    }

    private boolean matches(String value, String desc, String expr)
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
            boolean isDescMatched = desc.toLowerCase().contains(descToMatch.toLowerCase());
            boolean isNameMatched = value.toLowerCase().startsWith(name.toLowerCase());
            return isDescMatched && isNameMatched;
        }
        return false;
    }

    public void searchMove(JTextField moveName)
    {
        data.clear();
        for(Move move : MoveMap.getAllMoves())
        {
            String nameStart = moveName.getText();
            if(!matches(move.getEnglishName(), "UNOWN", nameStart)) // filter out item names not starting with given text
                continue;
            data.add(new MoveSearchItem(this, move));
        }
        setData(data);
    }

    private void setData(List<SearchItem> data)
    {
        removeAll();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        Collections.sort(data);
        for (int i = 0; i < data.size(); i++)
        {
            add(data.get(i).generateComponent(i, data.size()));
        }
        add(Box.createVerticalGlue());
        updateUI();
    }

    public BuilderArea getBuilderPane()
    {
        return parent;
    }
}
