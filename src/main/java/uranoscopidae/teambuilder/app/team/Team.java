package uranoscopidae.teambuilder.app.team;

import uranoscopidae.teambuilder.app.TeamBuilderApp;

public class Team
{

    private final TeamEntry[] party;
    private final TeamBuilderApp app;
    private String name;

    public Team(TeamBuilderApp app, String name)
    {
        this.app = app;
        this.name = name;
        party = new TeamEntry[6];
        for (int i = 0; i < party.length; i++)
        {
            party[i] = new TeamEntry(this, i);
        }
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public TeamEntry[] getParty()
    {
        return party;
    }

    public TeamBuilderApp getApp()
    {
        return app;
    }
}
