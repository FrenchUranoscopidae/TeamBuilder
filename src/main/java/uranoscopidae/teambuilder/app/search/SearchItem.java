package uranoscopidae.teambuilder.app.search;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public abstract class SearchItem
{
    protected final SearchZone parent;

    public SearchItem(SearchZone searchZone)
    {
        this.parent = searchZone;
    }

    public abstract Object getValue(int column);

    protected int columnFromName(String[] names, String n) {
        for (int i = 0; i < names.length; i++) {
            if(names[i].equalsIgnoreCase(n))
                return i;
        }
        return -1;
    }

    public abstract String toStringID();

}
