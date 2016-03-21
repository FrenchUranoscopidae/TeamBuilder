package uranoscopidae.teambuilder.app.search;

import uranoscopidae.teambuilder.app.SearchZone;

import javax.swing.*;
import java.awt.*;

public abstract class SearchItem implements Comparable<SearchItem>
{
    protected final SearchZone parent;
    private boolean hovered;

    public SearchItem(SearchZone searchZone)
    {
        this.parent = searchZone;
    }

    public abstract JComponent generateComponent(int index, int totalCount);

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

    public abstract String toString();

    public void setHovered(boolean hovered)
    {
        this.hovered = hovered;
    }

    public boolean isHovered()
    {
        return hovered;
    }
}
