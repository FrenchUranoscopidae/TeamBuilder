package uranoscopidae.teambuilder.app;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel
{
    private final TeamBuilderApp app;
    private final JTabbedPane teamList;

    public MainPanel(TeamBuilderApp app)
    {
        this.app = app;
        setLayout(new BorderLayout());

        teamList = new JTabbedPane();
        teamList.add("Test Team", new TeamPanel(app));

        teamList.add("Test Team 2", new TeamPanel(app));

        add(teamList);
    }
}
