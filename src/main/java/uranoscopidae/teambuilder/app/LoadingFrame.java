package uranoscopidae.teambuilder.app;

import javax.swing.*;
import java.awt.*;

public class LoadingFrame
{

    private JDialog frame;

    public LoadingFrame()
    {
        frame = new JDialog((Window) null);
        frame.setModal(true);
        frame.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
    }

    public void waitFor(String operationName, Runnable action)
    {
        JPanel content = new JPanel();
        JProgressBar bar = new JProgressBar();
        bar.setStringPainted(true);
        bar.setString(operationName);
        bar.setIndeterminate(true); // TODO: True progress value?
        content.add(bar);
        frame.setContentPane(content);
        frame.pack();
        frame.setTitle("Teambuilder - Loading...");
        frame.setLocationRelativeTo(null);
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>()
        {
            @Override
            protected Void doInBackground() throws Exception
            {
                action.run();
                return null;
            }

            @Override
            protected void done()
            {
                super.done();
                frame.setVisible(false);
            }
        };
        worker.execute();
        frame.setVisible(true);
    }

    public void dispose()
    {
        frame.dispose();
    }
}
