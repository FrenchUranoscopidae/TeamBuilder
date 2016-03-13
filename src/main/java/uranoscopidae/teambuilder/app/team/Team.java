package uranoscopidae.teambuilder.app.team;

public class Team
{

    private final TeamEntry[] party;
    private String name;

    public Team(String name)
    {
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
}
