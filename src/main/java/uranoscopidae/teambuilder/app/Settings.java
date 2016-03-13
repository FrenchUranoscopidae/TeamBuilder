package uranoscopidae.teambuilder.app;

import uranoscopidae.teambuilder.utils.Constants;

import java.io.*;
import java.util.Properties;

public class Settings
{
    private final File file;
    private File dexLocation;
    private File movesLocation;
    private File itemsLocation;
    private int nThreads;
    private File mainFolder;

    public Settings()
    {
        nThreads = 10;
        dexLocation = new File(".", "dexdata");
        movesLocation = new File(".", "movedata");
        itemsLocation= new File(".", "itemdata");
        mainFolder = new File(System.getProperty("user.home")+"/TeamBuilder");
        file = new File(mainFolder, "settings.txt");
    }

    public File getDexLocation()
    {
        return dexLocation;
    }

    public void setDexLocation(File dexLocation)
    {
        this.dexLocation = dexLocation;
    }

    public File getMovesLocation()
    {
        return movesLocation;
    }

    public void setMovesLocation(File movesLocation)
    {
        this.movesLocation = movesLocation;
    }

    public void readFromFile() throws IOException
    {
        if(!file.exists())
        {
            saveToFile();
        }
        Properties properties = new Properties();
        FileInputStream in = new FileInputStream(file);
        properties.load(in);
        in.close();
        dexLocation = new File(properties.getProperty("dexLocation", "./dexdata"));
        movesLocation = new File(properties.getProperty("movesLocation", "./movedata"));
        itemsLocation = new File(properties.getProperty("itemsLocation", "./itemdata"));
        try
        {
            nThreads = Integer.parseInt(properties.getProperty("nThreads", "10"));
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            nThreads = 10;
        }
    }

    public void saveToFile() throws IOException
    {
        if(!file.getParentFile().exists())
        {
            file.getParentFile().mkdirs();
        }
        Properties properties = new Properties();
        properties.setProperty("dexLocation", dexLocation.getAbsolutePath());
        properties.setProperty("movesLocation", movesLocation.getAbsolutePath());
        properties.setProperty("itemsLocation", itemsLocation.getAbsolutePath());
        properties.setProperty("nThreads", String.valueOf(nThreads));
        FileOutputStream out = new FileOutputStream(file);
        properties.store(out, "TeamBuilder settings");
        out.flush();
        out.close();
    }

    public int getThreadCount()
    {
        return nThreads;
    }

    public void setThreadCount(int nThreads)
    {
        this.nThreads = nThreads;
    }

    public File getItemsLocation()
    {
        return itemsLocation;
    }

    public void setItemsLocation(File itemsLocation)
    {
        this.itemsLocation = itemsLocation;
    }

    public File getMainFolder()
    {
        return mainFolder;
    }
}
