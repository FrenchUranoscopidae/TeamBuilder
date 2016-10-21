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
            if(moveInfos.getPower() != 0)
                return moveInfos.getPower();
            return "/";
        }
        if(column == columnFromName(MOVE_COLUMNS, "Accuracy")) {
            if(moveInfos.getAccuracy() != 0)
                return moveInfos.getAccuracy();
            return "/";
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
        if(!parent.getCurrentEntry().hasPokemon())
            return false;
        return moveInfos.getType().equals(parent.getCurrentEntry().getPokemon().getFirstType())
                || moveInfos.getType().equals(parent.getCurrentEntry().getPokemon().getSecondType());
    }

    public boolean isIllegal() {
        if(!parent.getCurrentEntry().hasPokemon())
            return false;
        return !parent.getCurrentEntry().getPokemon().canLearn(moveInfos);
    }
}
