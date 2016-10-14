package uranoscopidae.teambuilder.app.search;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SearchZoneRowSorter extends RowSorter<SearchZoneTableModel> {

    private final SearchZoneTableModel model;
    private boolean descendingSort;
    private int sortingColumn;
    private List<SortKey> sortKeys;
    private int[] viewToModel;
    private int[] modelToView;

    public SearchZoneRowSorter(SearchZoneTableModel model) {
        modelToView = new int[0];
        viewToModel = new int[0];
        this.model = model;
        sortKeys = new LinkedList<>();
    }

    @Override
    public SearchZoneTableModel getModel() {
        return model;
    }

    @Override
    public void toggleSortOrder(int column) {
        sortKeys.clear();
        if(sortingColumn != column) {
            sortingColumn = column;
            descendingSort = false;
        } else {
            descendingSort = !descendingSort;
        }
        sortKeys.add(new SortKey(column, descendingSort ? SortOrder.DESCENDING : SortOrder.ASCENDING));
    }

    @Override
    public int convertRowIndexToModel(int index) {
        return viewToModel[index];
    }

    @Override
    public int convertRowIndexToView(int index) {
        return modelToView[index];
    }

    @Override
    public void setSortKeys(List<? extends SortKey> keys) {
        //sortKeys = keys;
    }

    @Override
    public List<? extends SortKey> getSortKeys() {
        return sortKeys;
    }

    @Override
    public int getViewRowCount() {
        return model.getRowCount();
    }

    @Override
    public int getModelRowCount() {
        return model.getRowCount();
    }

    @Override
    public void modelStructureChanged() {
        sort();
    }

    @Override
    public void allRowsChanged() {
        sort();
    }

    @Override
    public void rowsInserted(int firstRow, int endRow) {
        sort();
    }

    @Override
    public void rowsDeleted(int firstRow, int endRow) {
        sort();
    }

    @Override
    public void rowsUpdated(int firstRow, int endRow) {
        sort();
    }

    @Override
    public void rowsUpdated(int firstRow, int endRow, int column) {
        sort();
    }

    private void sort() {
        modelToView = new int[model.getRowCount()];
        viewToModel = new int[model.getRowCount()];

        List<SearchItem> items = new LinkedList<>();
        items.addAll(model.getItems());
        Collections.sort(items, (o1, o2) -> o1.compareTo(o2, sortingColumn) * (descendingSort ? -1 : 1));

        for (int newIndex = 0; newIndex < items.size(); newIndex++) {
            int oldIndex = model.getItems().indexOf(items.get(newIndex));
            modelToView[oldIndex] = newIndex;
            viewToModel[newIndex] = oldIndex;
        }
    }

}
