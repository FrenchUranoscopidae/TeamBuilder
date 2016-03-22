package uranoscopidae.teambuilder.app.search;

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
        setBackgroundColor(panel, index);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(Box.createHorizontalStrut(10));
        String moveName = move.getEnglishName();
        boolean isStab = move.getType().equals(parent.getCurrentEntry().getPokemon().getFirstType()) || move.getType().equals(parent.getCurrentEntry().getPokemon().getSecondType());
        boolean canBeLearnt = parent.getCurrentEntry().getPokemon().canLearn(move);
        JLabel moveNameLabel = new JLabel();
        if(!canBeLearnt)
        {
            moveNameLabel.setForeground(Color.red);
        }
        else if(isStab)
        {
            moveName = "<b><u>"+moveName+"</u></b>";
        }
        moveNameLabel.setText("<html>"+moveName+"</html>");
        panel.add(moveNameLabel);
        panel.add(Box.createGlue());
        panel.add(parent.getBuilderPane().createImageLabel(move.getType().getIcon(), 32, 14));
        panel.add(Box.createHorizontalStrut(10));
        return panel;
    }

    @Override
    public int compareTo(SearchItem o)
    {
        if(o instanceof MoveSearchItem)
        {
            return move.getEnglishName().compareTo(((MoveSearchItem) o).move.getEnglishName());
        }
        return 0;
    }

    @Override
    public String toString()
    {
        return move.getEnglishName();
    }
}
