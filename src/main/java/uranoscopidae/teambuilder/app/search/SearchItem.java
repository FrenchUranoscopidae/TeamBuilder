package uranoscopidae.teambuilder.app.search;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public abstract class SearchItem
{
    protected final SearchZone parent;
    private boolean hovered;

    public SearchItem(SearchZone searchZone)
    {
        this.parent = searchZone;
    }

    public abstract Object getValue(int column);

    public void setBackgroundColor(JComponent comp, int index)
    {
        if(hovered)
        {
            comp.setBackground(index % 2 == 0 ? new Color(0x91C9F7) : new Color(0x91C9F7).darker());
        }
        else
        {
            comp.setBackground(index % 2 == 0 ? Color.lightGray : Color.gray);
        }
    }

    protected int columnFromName(String[] names, String n) {
        for (int i = 0; i < names.length; i++) {
            if(names[i].equalsIgnoreCase(n))
                return i;
        }
        return -1;
    }

    public abstract String toStringID();

    public void setHovered(boolean hovered)
    {
        this.hovered = hovered;
    }

    public boolean isHovered()
    {
        return hovered;
    }

    public abstract int compareTo(SearchItem item, int column);
}
