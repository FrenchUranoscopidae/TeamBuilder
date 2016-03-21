package uranoscopidae.teambuilder.app.search;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class SearchZoneSearchListener implements KeyListener, MouseListener
{

    private final Runnable runnable;

    public SearchZoneSearchListener(Runnable runnable)
    {
        this.runnable = runnable;
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
        runnable.run();
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        runnable.run();
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        runnable.run();
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        runnable.run();
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        runnable.run();
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        runnable.run();
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
        // nop
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        // nop
    }
}
