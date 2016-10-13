package uranoscopidae.teambuilder.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CsvTable {

    private final int columnCount;
    private final int rowCount;
    private final String[][] table;

    public static CsvTable fromReader(Reader reader) {
        if(reader instanceof BufferedReader) {
            return fromReader0((BufferedReader) reader);
        }
        return fromReader0(new BufferedReader(reader));
    }

    private static CsvTable fromReader0(BufferedReader reader) {
        List<String> lines = reader.lines()
                .collect(Collectors.toList());
        String[] header = splitLine(lines.get(0));
        CsvTable table = new CsvTable(header.length, lines.size()-1);
        table.table[0] = header;
        for(int i = 1;i<lines.size();i++) {
            String[] rowElements = splitLine(lines.get(i));
            table.table[i] = rowElements;
        }
        return table;
    }

    private static String[] splitLine(String s) {
        return s.split(Pattern.quote(","));
    }

    public CsvTable(int columnCount, int rowCount) {
        this.columnCount = columnCount;
        this.rowCount = rowCount;
        table = new String[rowCount+1][columnCount]; // contains the header
    }

    public int getColumnCount() {
        return columnCount;
    }

    public int getRowCount() {
        return rowCount;
    }

    public String getElement(int column, int row) {
        return table[row+1][column];
    }

    public String getElement(String columnName, int row) {
        int column = getColumnWithName(columnName);
        if(column < -1)
            return null;
        return table[row+1][column];
    }

    public CsvTable setElement(int column, int row, String elem) {
        table[row+1][column] = elem;
        return this;
    }

    public String[] getColumnNames() {
        return table[0];
    }

    public int getColumnWithName(String name) {
        for(int i = 0;i<getColumnCount();i++) {
            String n = getColumnNames()[i];
            if(n.equals(name))
                return i;
        }
        return -1;
    }
}
