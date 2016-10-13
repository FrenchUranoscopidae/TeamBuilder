package uranoscopidae.teambuilder.app.search;

import uranoscopidae.teambuilder.pkmn.moves.MoveInfos;

import javax.swing.*;
import java.awt.*;

public class MoveSearchItem extends SearchItem
{
    private final MoveInfos moveInfos;

    public MoveSearchItem(SearchZone searchZone, MoveInfos moveInfos)
    {
        super(searchZone);
        this.moveInfos = moveInfos;
    }

    @Override
    public JComponent generateComponent(int index, int totalCount)
    {
        JPanel panel = new JPanel();
        setBackgroundColor(panel, index);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(Box.createHorizontalStrut(10));
        String moveName = moveInfos.getEnglishName();
        boolean isStab = moveInfos.getType().equals(parent.getCurrentEntry().getPokemon().getFirstType()) || moveInfos.getType().equals(parent.getCurrentEntry().getPokemon().getSecondType());
        boolean canBeLearnt = parent.getCurrentEntry().getPokemon().canLearn(moveInfos);
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
        panel.add(new JLabel(""+moveInfos.getPower()));
        panel.add(Box.createGlue());
        panel.add(new JLabel(""+moveInfos.getAccuracy()));
        panel.add(Box.createGlue());
        panel.add(new JLabel(moveInfos.getCategory().name()));
        panel.add(Box.createGlue());
        panel.add(parent.getBuilderPane().createImageLabel(moveInfos.getType().getIcon(), 32, 14));
        panel.add(Box.createHorizontalStrut(10));
        return panel;
    }

    @Override
    public int compareTo(SearchItem o)
    {
        if(o instanceof MoveSearchItem)
        {
            return moveInfos.getEnglishName().compareTo(((MoveSearchItem) o).moveInfos.getEnglishName());
        }
        return 0;
    }

    @Override
    public String toString()
    {
        return moveInfos.getEnglishName();
    }
}
