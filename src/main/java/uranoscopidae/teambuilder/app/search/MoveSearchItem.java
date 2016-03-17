package uranoscopidae.teambuilder.app.search;

import uranoscopidae.teambuilder.app.SearchZone;
import uranoscopidae.teambuilder.pkmn.moves.Move;

import javax.swing.*;
import java.awt.*;

public class MoveSearchItem extends SearchItem
{
    private final Move move;

    public MoveSearchItem(SearchZone searchZone, Move move)
    {
        super(searchZone);
        this.move = move;
    }

    @Override
    public JComponent generateComponent(int index, int totalCount)
    {
        JPanel panel = new JPanel();
        panel.setBackground(index % 2 == 0 ? Color.gray : Color.darkGray);
        panel.add(new JLabel(move.getEnglishName()));
        return panel;
    }

    @Override
    public int compareTo(SearchItem o)
    {

        return 0;
    }
}
