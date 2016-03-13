package uranoscopidae.teambuilder.app;

import uranoscopidae.teambuilder.app.team.Team;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class MainPanel extends JPanel
{
    private final TeamBuilderApp app;
    private final JTabbedPane teamList;

    public MainPanel(TeamBuilderApp app)
    {
        this.app = app;
        setLayout(new BorderLayout());

        teamList = new JTabbedPane();

        Team testTeam = new Team("Test team");
        try
        {
            testTeam.getParty()[0].setPokemon(app.getPokemon("025Pikachu"));
            testTeam.getParty()[1].setPokemon(app.getPokemon("003Venusaur"));
            testTeam.getParty()[2].setPokemon(app.getPokemon("151Mew"));
            testTeam.getParty()[3].setPokemon(app.getPokemon("384Rayquaza"));
            testTeam.getParty()[4].setPokemon(app.getPokemon("065Alakazam"));
            testTeam.getParty()[5].setPokemon(app.getPokemon("306Aggron"));
        }
        catch (IOException | ReflectiveOperationException e)
        {
            e.printStackTrace();
        }
        teamList.add("Test Team", new TeamPanel(app, testTeam));

        teamList.add("Test Team 2", new TeamPanel(app, testTeam));

        add(teamList);
    }
}
