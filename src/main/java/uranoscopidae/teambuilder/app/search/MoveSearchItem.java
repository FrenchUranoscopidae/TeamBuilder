package uranoscopidae.teambuilder.app.search;

import uranoscopidae.teambuilder.pkmn.moves.MoveInfos;

import javax.swing.*;
import java.awt.*;

public class MoveSearchItem extends SearchItem
{

    public static final String[] MOVE_COLUMNS = {"Name", "Description", "Category", "Power", "Accuracy", "Type", "PP"};
    private final MoveInfos moveInfos;

    public MoveSearchItem(SearchZone searchZone, MoveInfos moveInfos)
    {
        super(searchZone);
        this.moveInfos = moveInfos;
    }

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
    public int compareTo(SearchItem o, int column)
    {
        if(o instanceof MoveSearchItem)
        {
            MoveSearchItem other = (MoveSearchItem) o;
            if(column == columnFromName(MOVE_COLUMNS, "Type")) {
                return moveInfos.getType().getName().compareTo(other.moveInfos.getType().getName());
            }
            if(column == columnFromName(MOVE_COLUMNS, "Name")) {
                return moveInfos.getEnglishName().compareTo(other.moveInfos.getEnglishName());
            }
            if(column == columnFromName(MOVE_COLUMNS, "Power")) {
                return Integer.compare(moveInfos.getPower(), other.moveInfos.getPower());
            }

            if(column == columnFromName(MOVE_COLUMNS, "Category")) {
                return moveInfos.getCategory().name().compareTo(other.moveInfos.getCategory().name());
            }

            if(column == columnFromName(MOVE_COLUMNS, "Accuracy")) {
                return Integer.compare(moveInfos.getAccuracy(), other.moveInfos.getAccuracy());
            }

            if(column == columnFromName(MOVE_COLUMNS, "PP")) {
                return Integer.compare(moveInfos.getPowerPoints(), other.moveInfos.getPowerPoints());
            }

        }
        return 0;
    }

    @Override
    public Object getValue(int column) {
        if(column == columnFromName(MOVE_COLUMNS, "Name")) {
            String name = moveInfos.getEnglishName();
            if(isStab())
                name = "<u><b>"+name+"</b></u>";
            if(isIllegal()) {
                name = "<font color=\"red\">"+name+"</font>";
            }
            return "<html>"+name+"</html";
        }
        if(column == columnFromName(MOVE_COLUMNS, "Description")) {
            return moveInfos.getDescription();
        }
        if(column == columnFromName(MOVE_COLUMNS, "Type")) {
            return moveInfos.getType();
        }
        if(column == columnFromName(MOVE_COLUMNS, "Power")) {
            return moveInfos.getPower();
        }
        if(column == columnFromName(MOVE_COLUMNS, "Accuracy")) {
            return moveInfos.getAccuracy();
        }
        if(column == columnFromName(MOVE_COLUMNS, "Category")) {
            return moveInfos.getCategory().name();
        }
        if(column == columnFromName(MOVE_COLUMNS, "PP")) {
            return moveInfos.getPowerPoints();
        }
        return "Unknown column: "+column;
    }

    @Override
    public String toStringID()
    {
        if(isIllegal())
            return "!"+moveInfos.getEnglishName();
        return moveInfos.getEnglishName();
    }

    public boolean isStab() {
        return moveInfos.getType().equals(parent.getCurrentEntry().getPokemon().getFirstType())
                || moveInfos.getType().equals(parent.getCurrentEntry().getPokemon().getSecondType());
    }

    public boolean isIllegal() {
        return !parent.getCurrentEntry().getPokemon().canLearn(moveInfos);
    }
}
