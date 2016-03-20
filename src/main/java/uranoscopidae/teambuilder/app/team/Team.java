package uranoscopidae.teambuilder.app.team;

import uranoscopidae.teambuilder.app.TeamBuilderApp;
import uranoscopidae.teambuilder.utils.IOHelper;

import java.io.*;

public class Team
{

    private final TeamEntry[] party;
    private final TeamBuilderApp app;
    private String name;
    private File file;

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

    public File getFile()
    {
        return file;
    }

    public void setFile(File file)
    {
        this.file = file;
    }

    public void writeTo(OutputStream out) throws IOException
    {
        DataOutputStream dataOut = new DataOutputStream(out);
        dataOut.writeUTF(name);
        for (int i = 0; i < 6; i++)
        {
            party[i].writeTo(out);
        }
        dataOut.flush();
    }


    public static Team readFrom(TeamBuilderApp app, InputStream in) throws IOException
    {
        DataInputStream dataIn = new DataInputStream(in);
        String name = dataIn.readUTF();
        Team team = new Team(app, name);
        for (int i = 0; i < 6; i++)
        {
            team.getParty()[i].readFrom(in);
        }
        return team;
    }
}
