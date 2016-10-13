package uranoscopidae.teambuilder.app;

import uranoscopidae.teambuilder.app.refreshers.Refresher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LoadingFrame
{

    private final Settings settings;
    private JDialog frame;

    public LoadingFrame(Settings settings)
    {
        this.settings = settings;
        frame = new JDialog((Window) null);
        frame.setModal(true);
        frame.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
    }

    public void setTitle(String title) {
        frame.setTitle(title);
    }

    public <T> void asyncWaitForList(String operationName, Supplier<java.util.List<T>> initer, Consumer<T> action)
    {
        waitFor(operationName, (bar) -> {
            Refresher<T> refresher = new Refresher<T>("Pokémon loading", settings)
            {
                @Override
                public List<T> init() throws IOException
                {
                    return initer.get();
                }

                @Override
                public void handle(T part) throws IOException
                {
                    action.accept(part);
                    bar.setString(this.getBar().getString());
                }

                @Override
                public String getText(T part) throws IOException
                {
                    return part.toString();
                }
            };
            refresher.launch();
            refresher.waitFor();
        });
    }

    public <T> void waitForList(String operationName, Supplier<java.util.List<T>> initer, Consumer<T> action)
    {
        waitFor(operationName, (bar) -> {
            bar.setIndeterminate(true);

            List<T> list = initer.get();
            if(list.isEmpty())
                return;
            int count = list.size();
            int index = 0;

            bar.setIndeterminate(false);
            bar.setMinimum(0);
            bar.setMaximum(count);
            for(T t : list)
            {
                index++;
                bar.setValue(index);
                bar.setString(operationName+": "+t.toString());
                frame.pack();
                frame.setLocationRelativeTo(null);
                action.accept(t);
            }
        });
    }

    public void waitFor(String operationName, Runnable action)
    {
        waitFor(operationName, (bar) -> action.run());
    }

    public void waitFor(String operationName, Consumer<JProgressBar> action)
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
                action.accept(bar);
                return null;
            }

            @Override
            protected void done()
            {
                super.done();
                frame.setVisible(false);
            }
        };
        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowActivated(WindowEvent e)
            {
                worker.execute();
            }
        });
        frame.setVisible(true);
    }

    public void dispose()
    {
        frame.dispose();
    }
}
