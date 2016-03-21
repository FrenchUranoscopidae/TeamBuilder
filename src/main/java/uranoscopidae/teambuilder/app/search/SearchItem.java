package uranoscopidae.teambuilder.app.search;

import uranoscopidae.teambuilder.app.SearchZone;

import javax.swing.*;
import java.awt.*;

public abstract class SearchItem implements Comparable<SearchItem>
{
    protected final SearchZone parent;

    public SearchItem(SearchZone searchZone)
    {
        this.parent = searchZone;
    }

    public abstract JComponent generateComponent(int index, int totalCount);

    protected void setBackgroundColor(JComponent comp, int index)
    {
        comp.setBackground(index % 2 == 0 ? Color.lightGray : Color.gray);
    }
}
