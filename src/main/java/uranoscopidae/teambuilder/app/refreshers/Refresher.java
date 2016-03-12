package uranoscopidae.teambuilder.app.refreshers;

import uranoscopidae.teambuilder.app.Settings;

import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public abstract class Refresher<T> implements Runnable
{

    private final JProgressBar bar;
    private final String name;
    protected final Settings settings;
    private boolean running;
    private final ReentrantLock lock;
    private int counter;
    private int nThreads;

    public Refresher(String name, Settings settings)
    {
        this.settings = settings;
        nThreads = 10;
        counter = 0;
        lock = new ReentrantLock(true);
        this.name = name;
        bar = new JProgressBar();
    }

    public abstract List<T> init() throws IOException;

    public abstract void handle(T part) throws IOException;

    public abstract String getText(T part) throws IOException;

    public int getThreadCount()
    {
        return nThreads;
    }

    public void setThreadCount(int count)
    {
        nThreads = count;
    }

    public JProgressBar getBar()
    {
        return bar;
    }

    public boolean launch()
    {
        if(!running)
        {
            new Thread(this, "Refresher Thread").start();
            return true;
        }
        return false;
    }

    @Override
    public void run()
    {
        synchronized (lock)
        {
            running = true;
            final int nThreads = settings.getThreadCount();
            bar.setIndeterminate(true);
            List<T> content = null;
            counter = 0;
            try
            {
                content = init();
                bar.setIndeterminate(false);
                bar.setValue(0);
                ExecutorService executor = Executors.newFixedThreadPool(nThreads);
                Future[] futures = new Future[content.size()];
                for(int i = 0;i<futures.length;i++)
                {
                    final T part = content.get(i);

                    final int index = i;
                    Callable<Void> callable = () -> {
                        handle(part);
                        System.gc();
                        onDone(part, index, futures.length);
                        return null;
                    };
                    Future<Void> future = executor.submit(callable);
                    futures[i] = future;
                }
                executor.shutdown();
                try
                {
                    if(executor.awaitTermination(2, TimeUnit.HOURS))
                    {
                        bar.setString(name+": done!");
                    }
                    else
                    {
                        bar.setString(name+": timeout :c");
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                    bar.setString("Error: "+e.getLocalizedMessage());
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                bar.setString("Error: "+e.getLocalizedMessage());
            }
            running = false;
        }
    }

    public void onDone(T part, int index, int size) throws IOException
    {
        lock.lock();
        final int currentCount;
        try
        {
            counter++;
            currentCount = counter;
        }
        finally
        {
            lock.unlock();
        }
        int percent = (int) ((float)currentCount/(float)size * 100);
        bar.setString(name+": "+getText(part)+" ("+percent+"%)");
        bar.setValue(counter);
        bar.setMinimum(0);
        bar.setMaximum(size);
        SwingUtilities.invokeLater(bar::repaint);
    }
}
