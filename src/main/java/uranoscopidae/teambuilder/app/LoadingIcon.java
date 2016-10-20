package uranoscopidae.teambuilder.app;

import javax.swing.*;
import java.awt.*;

public class LoadingIcon extends JComponent {

    public LoadingIcon() {
        setMinimumSize(new Dimension(100,100));
        setPreferredSize(getMinimumSize());
        setMaximumSize(new Dimension(300,300));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.red);

        int size = Math.min(getWidth(), getHeight());
        g.fillArc(getWidth()/2-size/2, getHeight()/2-size/2, size, size, 0, 180);

        g.setColor(Color.white);
        g.fillArc(getWidth()/2-size/2, getHeight()/2-size/2, size, size, 0, -180);

        g.setColor(Color.black);
        g.drawLine(getWidth()/2-size/2, getHeight()/2, size+getWidth()/2-size/2, getHeight()/2);
    }
}
