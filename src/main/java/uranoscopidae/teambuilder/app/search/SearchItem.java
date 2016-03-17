package uranoscopidae.teambuilder.app.search;

import uranoscopidae.teambuilder.app.SearchZone;

import javax.swing.*;

public abstract class SearchItem implements Comparable<SearchItem>
{
    protected final SearchZone parent;

    public SearchItem(SearchZone searchZone)
    {
        this.parent = searchZone;
    }

    public abstract JComponent generateComponent(int index, int totalCount);
}
