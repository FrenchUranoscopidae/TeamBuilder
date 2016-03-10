package uranoscopidae.teambuilder.app;

import uranoscopidae.teambuilder.app.refreshers.DexRefresher;
import uranoscopidae.teambuilder.app.refreshers.MovesRefresher;
import uranoscopidae.teambuilder.pkmn.moves.Move;
import uranoscopidae.teambuilder.pkmn.moves.MoveMap;
import uranoscopidae.teambuilder.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class TeamBuilderApp
{

    private final Settings settings;
    private final MovesRefresher movesRefresher;
    private final DexRefresher dexRefresher;
    private JFrame frame;

    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            e.printStackTrace();
        }
        new TeamBuilderApp().start();
    }

    public TeamBuilderApp()
    {
        settings = new Settings();
        try
        {
            settings.readFromFile();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        dexRefresher = new DexRefresher(settings, this);
        movesRefresher = new MovesRefresher(settings, this);
    }

    protected void start()
    {
        frame = new JFrame("Teambuilder - v"+ Constants.VERSION);
        frame.setJMenuBar(buildMenuBar());
        frame.add(buildProgressPanel(), "South");
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                TeamBuilderApp.this.shutdown();
            }
        });
        frame.setVisible(true);
    }

    private Component buildProgressPanel()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        JProgressBar dexBar = dexRefresher.getBar();
        JProgressBar moveBar = movesRefresher.getBar();
        dexBar.setStringPainted(true);
        moveBar.setStringPainted(true);

        dexBar.setString("Pokédex not updating");
        moveBar.setString("Moves not updating");
        panel.add(dexBar);
        panel.add(moveBar);
        return panel;
    }

    private JMenuBar buildMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenu database = new JMenu("Databases");
        JMenu settings = new JMenu("Settings");

        file.add("TODO");

        JMenuItem refreshMoves = new JMenuItem("Refresh move database");
        refreshMoves.addActionListener(e -> refreshMoves());
        database.add(refreshMoves);

        JMenuItem refreshDex = new JMenuItem("Refresh Pokémon database");
        refreshDex.addActionListener(e -> refreshDex());
        database.add(refreshDex);

        JMenu dbLocations = new JMenu("Set database location");
        JMenuItem dexLocation = new JMenuItem("Pokédex");
        JMenuItem movesLocation = new JMenuItem("Moves");

        dexLocation.addActionListener((e) -> {
            File newLocation = selectFolder(this.settings.getDexLocation());
            if(newLocation != null)
            {
                this.settings.setDexLocation(newLocation);
            }
        });

        movesLocation.addActionListener((e) -> {
            File newLocation = selectFolder(this.settings.getMovesLocation());
            if(newLocation != null)
            {
                this.settings.setMovesLocation(newLocation);
            }
        });
        dbLocations.add(dexLocation);
        dbLocations.add(movesLocation);

        settings.add(dbLocations);

        JMenu advancedMenu = new JMenu("Advanced");

        JMenuItem threadCount = new JMenuItem("Refreshers thread count");
        threadCount.addActionListener(e -> {
            SpinnerNumberModel sModel = new SpinnerNumberModel(this.settings.getThreadCount(), 1, 100, 1);
            JSpinner spinner = new JSpinner(sModel);
            JOptionPane.showMessageDialog(frame, spinner, "Enter thread count", JOptionPane.QUESTION_MESSAGE);
            int value = (int) spinner.getValue();
            this.settings.setThreadCount(value);
        });
        advancedMenu.add(threadCount);

        settings.add(advancedMenu);

        menuBar.add(file);
        menuBar.add(database);
        menuBar.add(settings);
        return menuBar;
    }

    private void refreshMoves()
    {
        movesRefresher.launch();
    }

    private void refreshDex()
    {
        dexRefresher.launch();
    }

    private File selectFolder(File current)
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if(current != null)
        {
            chooser.setCurrentDirectory(current.getParentFile());
            chooser.setSelectedFile(current);
        }
        chooser.showOpenDialog(frame);
        return chooser.getSelectedFile();
    }

    private void shutdown()
    {
        try
        {
            settings.saveToFile();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        frame.dispose();
    }

    public boolean hasMove(String name)
    {
        return new File(settings.getMovesLocation(), name+".movd").exists();
    }

    public void registerMove(Move definition) throws IOException
    {
        FileOutputStream out = new FileOutputStream(new File(settings.getMovesLocation(), definition.getEnglishName()+".movd"));
        definition.writeTo(out);
        out.flush();
        out.close();
    }

    public Move getMove(String name) throws IOException
    {
        if(!MoveMap.has(name))
        {
            FileInputStream in = new FileInputStream(settings.getMovesLocation().getAbsolutePath()+File.separatorChar+name+".movd");
            Move def = Move.readFrom(in);
            in.close();
            MoveMap.registerMove(def);
        }
        return MoveMap.getMove(name);
    }
}
