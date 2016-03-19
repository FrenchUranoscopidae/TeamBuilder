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
        JPanel teamPanel = new JPanel();
        teamPanel.setLayout(new BoxLayout(teamPanel, BoxLayout.Y_AXIS));
        teamPanel.setBorder(new TitledBorder("Your team"));
        setLayout(new BorderLayout());

        BuilderArea area = new BuilderArea(app);
        for (int i = 0; i < team.getParty().length; i++)
        {
            TeamEntry entry = team.getParty()[i];
            TeamEntryButton pokemonEntry = new TeamEntryButton(app, entry);
            teamPanel.add(pokemonEntry);
            pokemonEntry.addActionListener(e -> area.setSpecificView(entry));
        }

        JButton saveButton = new JButton("Save");
        JButton saveToButton = new JButton("Save to...");
        teamPanel.add(saveButton);
        teamPanel.add(saveToButton);

        add(area, "Center");
        add(teamPanel, "West");
    }

}
