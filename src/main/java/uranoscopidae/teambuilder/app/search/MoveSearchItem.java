package uranoscopidae.teambuilder.app.search;

import uranoscopidae.teambuilder.app.SearchZone;
import uranoscopidae.teambuilder.pkmn.moves.Move;

import javax.swing.*;

public class MoveSearchItem extends SearchItem
{
    private final Move move;

    public MoveSearchItem(SearchZone searchZone, Move move)
    {
        super(searchZone);
        this.move = move;
    }

    @Override
    public JComponent generateComponent()
    {
        return new JLabel(move.getEnglishName());
    }

    @Override
    public int compareTo(SearchItem o)
    {

        return 0;
    }
}
