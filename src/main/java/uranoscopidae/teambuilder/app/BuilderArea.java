package uranoscopidae.teambuilder.app;

import uranoscopidae.teambuilder.app.team.TeamEntry;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;

public class BuilderArea extends JPanel
{

    private boolean general;
    private TeamEntry entry;

    public BuilderArea()
    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setGeneralView();
    }

    public void setGeneralView()
    {
        this.general = true;
        updateContents();
    }

    private void updateContents()
    {
        removeAll();
        if(general)
        {

        }
        else
        {
            add(new JLabel(entry.getPokemon().getEnglishName()));
            JLabel spriteLabel = new JLabel();
            SwingWorker<ImageIcon, BufferedImage> worker = new SwingWorker<ImageIcon, BufferedImage>()
            {
                @Override
                protected ImageIcon doInBackground() throws Exception
                {
                    return new ImageIcon(entry.getPokemon().getSprite());
                }

                @Override
                protected void done()
                {
                    super.done();
                    try
                    {
                        spriteLabel.setIcon(this.get());
                    }
                    catch (InterruptedException | ExecutionException e)
                    {
                        e.printStackTrace();
                    }
                }
            };
            worker.execute();
            add(spriteLabel);
        }
        repaint();
    }

    public void setSpecificView(TeamEntry entry)
    {
        this.entry = entry;
        this.general = false;
        updateContents();
    }
}
