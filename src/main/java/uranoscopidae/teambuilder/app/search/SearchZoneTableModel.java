package uranoscopidae.teambuilder.app.search;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

public class SearchZoneTableModel extends AbstractTableModel {

    private final Vector<SearchItem> rows;
    private String[] header;

    public SearchZoneTableModel() {
        rows = new Vector<>();
        header = new String[0];
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return header.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return header[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return SearchItem.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return rows.get(rowIndex).getValue(columnIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    }

    public void clear(String[] header) {
        rows.clear();
        this.header = header;
        fireTableStructureChanged();
    }

    public void add(SearchItem item) {
        rows.add(item);
        fireTableStructureChanged();
    }

    public Vector<SearchItem> getItems() {
        return rows;
    }
}
