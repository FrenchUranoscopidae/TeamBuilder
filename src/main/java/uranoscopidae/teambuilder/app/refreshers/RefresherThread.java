package uranoscopidae.teambuilder.app.refreshers;

public class RefresherThread extends Thread
{
    private final Runnable action;
    private boolean done;

    public RefresherThread(Runnable action, String name)
    {
        super(name);
        this.action = action;
    }

    @Override
    public synchronized void start()
    {
        done = false;
        super.start();
    }

    public void run()
    {
        done = false;
        action.run();
        done = true;
    }

    public boolean isDone()
    {
        return done;
    }
}
