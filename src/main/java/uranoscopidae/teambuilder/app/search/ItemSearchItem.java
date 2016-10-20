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

    @Override
    public String toStringID()
    {
        return item.getEnglishName();
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
