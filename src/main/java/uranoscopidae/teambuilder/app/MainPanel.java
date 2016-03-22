package uranoscopidae.teambuilder.app;

import uranoscopidae.teambuilder.app.team.PokemonGender;
import uranoscopidae.teambuilder.app.team.Team;
import uranoscopidae.teambuilder.app.team.TeamPanel;
import uranoscopidae.teambuilder.pkmn.items.Pokeballs;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Random;

public class MainPanel extends JPanel
{
    private final TeamBuilderApp app;
    private final JTabbedPane teamList;

    public MainPanel(TeamBuilderApp app)
    {
        this.app = app;
        setLayout(new BorderLayout());

        teamList = new JTabbedPane();

        Team testTeam = new Team(app, "Test team");
        try
        {
            testTeam.getParty()[0].setPokemon(app.getPokemon("025Pikachu"));
            testTeam.getParty()[1].setPokemon(app.getPokemon("208Steelix"));
            testTeam.getParty()[2].setPokemon(app.getPokemon("151Mew"));
            testTeam.getParty()[3].setPokemon(app.getPokemon("384Rayquaza"));
            testTeam.getParty()[4].setPokemon(app.getPokemon("065Alakazam"));
            testTeam.getParty()[5].setPokemon(app.getPokemon("306Aggron"));

            testTeam.getParty()[0].setGender(PokemonGender.FEMALE);
            testTeam.getParty()[1].setGender(PokemonGender.ASEXUAL);
            testTeam.getParty()[2].setGender(PokemonGender.FEMALE);
            testTeam.getParty()[4].setGender(PokemonGender.FEMALE);
            testTeam.getParty()[3].setGender(PokemonGender.MALE);
            testTeam.getParty()[5].setGender(PokemonGender.MALE);

            Random rand = new Random();
            for (int i = 0; i < testTeam.getParty().length; i++)
            {
                testTeam.getParty()[i].setLevel((byte) rand.nextInt(100+1));
                int randIndex = rand.nextInt(Pokeballs.values().length);
                try
                {
                    testTeam.getParty()[i].setBall(app.getItem(Pokeballs.values()[randIndex].getItemName()));
                }
                catch (UnsupportedOperationException e)
                {
                    e.printStackTrace();
                }
            }
        }
        catch (IOException | ReflectiveOperationException e)
        {
            e.printStackTrace();
        }
        teamList.add("Test Team", new TeamPanel(app, testTeam));

        teamList.add("Test Team 2", new TeamPanel(app, testTeam));

        add(teamList);
    }

    public void addTeam(Team team)
    {
        teamList.add(team.getName(), new TeamPanel(app, team));
    }
}
