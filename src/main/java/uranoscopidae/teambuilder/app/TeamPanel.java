package uranoscopidae.teambuilder.app;

import javax.swing.*;

public class TeamPanel extends JPanel
{

    public TeamPanel(TeamBuilderApp app)
    {
        add(new PokemonEntryPanel(app));
    }
}
