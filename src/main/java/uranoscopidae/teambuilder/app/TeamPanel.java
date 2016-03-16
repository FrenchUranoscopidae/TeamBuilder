package uranoscopidae.teambuilder.app;

import uranoscopidae.teambuilder.app.team.Team;
import uranoscopidae.teambuilder.app.team.TeamEntry;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class TeamPanel extends JPanel
{

    public TeamPanel(TeamBuilderApp app, Team team)
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new TitledBorder("Your team"));
        setLayout(new BorderLayout());

        BuilderArea area = new BuilderArea(app);
        for (int i = 0; i < team.getParty().length; i++)
        {
            TeamEntry entry = team.getParty()[i];
            TeamEntryButton pokemonEntry = new TeamEntryButton(app, entry);
            panel.add(pokemonEntry);
            pokemonEntry.addActionListener(e -> area.setSpecificView(entry));
        }

        add(area, "Center");
        add(panel, "West");
    }

}
