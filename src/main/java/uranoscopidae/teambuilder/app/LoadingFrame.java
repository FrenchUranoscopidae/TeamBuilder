package uranoscopidae.teambuilder.app;


import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.ImageObserver;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LoadingFrame
{

    private final Settings settings;
    private final List<Task> tasks;
    private JDialog frame;

    public LoadingFrame(Settings settings)
    {
        tasks = new LinkedList<>();
        this.settings = settings;
        frame = new JDialog((Window) null);
        frame.setModal(true);
        frame.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
    }

    public void setTitle(String title) {
        frame.setTitle(title);
    }

    public <T> void waitForList(String operationName, Supplier<java.util.List<T>> initer, Consumer<T> action)
    {
        newTask(operationName, (bar) -> {
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

    public void newTask(String operationName, Runnable action)
    {
        newTask(operationName, (bar) -> action.run());
    }

    public void newTask(String operationName, Consumer<JProgressBar> action)
    {
        tasks.add(new Task(operationName, action));
    }

    public void initLoading() {

        JPanel content = new JPanel();
        JProgressBar bar = new JProgressBar();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        LoadingIcon icon = new
                LoadingIcon();
        content.add(icon);
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
                long start = System.currentTimeMillis();
                for(Task t : tasks) {
                    long taskStart = System.currentTimeMillis();
                    bar.setStringPainted(true);
                    bar.setString(t.getName());
                    try {
                        t.perform(bar);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    long taskEnd = System.currentTimeMillis();
                    long elapsedTime = taskEnd-taskStart;
                    System.out.println("Took "+elapsedTime+"ms for task "+t.getName());
                }
                long totalTime = System.currentTimeMillis()-start;
                System.out.println("Took "+totalTime+"ms in total");
                return null;
            }

            @Override
            protected void done()
            {
                super.done();
                frame.dispose();
            }
        };

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                worker.execute();
            }
        });

        frame.setVisible(true);
    }

    private class Task {
        private final String operationName;
        private final Consumer<JProgressBar> action;

        public Task(String operationName, Consumer<JProgressBar> action) {
            this.operationName = operationName;
            this.action = action;
        }

        public String getName() {
            return operationName;
        }

        public void perform(JProgressBar bar) {
            action.accept(bar);
        }
    }
}
