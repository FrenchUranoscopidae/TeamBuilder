package uranoscopidae.teambuilder.app;

import uranoscopidae.teambuilder.app.refreshers.AbilitiesRefresher;
import uranoscopidae.teambuilder.app.refreshers.DexRefresher;
import uranoscopidae.teambuilder.app.refreshers.ItemsRefresher;
import uranoscopidae.teambuilder.app.refreshers.MovesRefresher;
import uranoscopidae.teambuilder.app.team.Team;
import uranoscopidae.teambuilder.pkmn.Ability;
import uranoscopidae.teambuilder.pkmn.Pokemon;
import uranoscopidae.teambuilder.pkmn.items.Item;
import uranoscopidae.teambuilder.pkmn.moves.Move;
import uranoscopidae.teambuilder.pkmn.moves.MoveMap;
import uranoscopidae.teambuilder.utils.Constants;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.zip.ZipInputStream;

public class TeamBuilderApp
{

    private final Settings settings;
    private final MovesRefresher movesRefresher;
    private final DexRefresher dexRefresher;
    private final ItemsRefresher itemsRefresher;
    private final AbilitiesRefresher abilitiesRefresher;
    private JFrame frame;
    private MainPanel mainPanel;

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
        itemsRefresher = new ItemsRefresher(settings, this);
        abilitiesRefresher = new AbilitiesRefresher(settings, this);
    }

    protected void start()
    {
        loadData();
        frame = new JFrame("Teambuilder - v"+ Constants.VERSION);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setJMenuBar(buildMenuBar());
        mainPanel = new MainPanel(this);
        frame.add(mainPanel, "Center");
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

    private void loadData()
    {
        LoadingFrame loadingFrame = new LoadingFrame();
        loadingFrame.waitForList("Loading items into memory...", this::getItemNames, s -> {
            try
            {
                getItem(s); // registers the item into the item map
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });

        loadingFrame.waitForList("Loading moves into memory...", this::getMoveNames, s -> {
            try
            {
                getMove(s); // registers the move into the move map
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });
        loadingFrame.dispose();
    }

    private Component buildProgressPanel()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        JProgressBar dexBar = dexRefresher.getBar();
        JProgressBar moveBar = movesRefresher.getBar();
        JProgressBar itemBar = itemsRefresher.getBar();
        JProgressBar abilityBar = abilitiesRefresher.getBar();
        dexBar.setStringPainted(true);
        moveBar.setStringPainted(true);
        itemBar.setStringPainted(true);
        abilityBar.setStringPainted(true);

        dexBar.setString("Pokédex not updating");
        moveBar.setString("Moves not updating");
        abilityBar.setString("Abilities not updating");
        itemBar.setString("Items not updating");

        panel.add(dexBar);
        panel.add(moveBar);
        panel.add(itemBar);
        panel.add(abilityBar);
        return panel;
    }

    private JMenuBar buildMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenu database = new JMenu("Databases");
        JMenu settings = new JMenu("Settings");

        JMenuItem importTeam = new JMenuItem("Import team");
        importTeam.addActionListener(e -> {
            File teamFile = selectFile(null);
            if(teamFile != null)
            {
                try
                {
                    FileInputStream in = new FileInputStream(teamFile);
                    Team team = Team.readFrom(this, in);
                    mainPanel.addTeam(team);
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        });
        file.add(importTeam);

        JMenuItem refreshMoves = new JMenuItem("Refresh move and abilities database");
        refreshMoves.addActionListener(e -> refreshMoves());
        refreshMoves.addActionListener(e -> refreshAbilities());
        database.add(refreshMoves);

        JMenuItem refreshDex = new JMenuItem("Refresh Pokémon database");
        refreshDex.addActionListener(e -> refreshDex());
        database.add(refreshDex);

        JMenuItem refreshItems = new JMenuItem("Refresh Items database");
        refreshItems.addActionListener(e -> refreshItems());
        database.add(refreshItems);

        JMenu dbLocations = new JMenu("Set database location");
        JMenuItem dexLocation = new JMenuItem("Pokédex");
        JMenuItem movesLocation = new JMenuItem("Moves");
        JMenuItem itemsLocation = new JMenuItem("Items");

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

        itemsLocation.addActionListener((e) -> {
            File newLocation = selectFolder(this.settings.getItemsLocation());
            if(newLocation != null)
            {
                this.settings.setItemsLocation(newLocation);
            }
        });
        dbLocations.add(dexLocation);
        dbLocations.add(movesLocation);
        dbLocations.add(itemsLocation);

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

    private File selectFile(File current)
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if(current != null)
        {
            chooser.setCurrentDirectory(current.getParentFile());
            chooser.setSelectedFile(current);
        }
        chooser.showOpenDialog(frame);
        return chooser.getSelectedFile();
    }

    private void refreshMoves()
    {
        movesRefresher.launch();
    }

    private void refreshAbilities()
    {
        abilitiesRefresher.launch();
    }

    private void refreshDex()
    {
        dexRefresher.launch();
    }

    private void refreshItems()
    {
        itemsRefresher.launch();
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
        if(!settings.getMovesLocation().exists())
        {
            settings.getMovesLocation().mkdirs();
        }
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
            try
            {
                Move def = Move.readFrom(in);
                in.close();
                MoveMap.registerMove(def);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                System.out.println("blame>>> "+name);
            }
        }
        return MoveMap.getMove(name);
    }

    public Pokemon getPokemon(String fullID) throws IOException, ReflectiveOperationException
    {
        File file = new File(settings.getDexLocation(), fullID+".dexd");
        ZipInputStream input = new ZipInputStream(new FileInputStream(file));
        Pokemon pkmn = Pokemon.readPokemon(this, input);
        return pkmn;
    }

    public boolean hasItem(String name)
    {
        return new File(settings.getItemsLocation(), name+".itemd").exists();
    }

    public void registerItem(Item definition) throws IOException
    {
        if(!settings.getItemsLocation().exists())
        {
            settings.getItemsLocation().mkdirs();
        }
        FileOutputStream out = new FileOutputStream(new File(settings.getItemsLocation(), definition.getEnglishName()+".itemd"));
        definition.writeTo(out);
        out.flush();
        out.close();
    }

    public Item getItem(String name) throws IOException
    {
        if(!ItemMap.has(name))
        {
            FileInputStream in = new FileInputStream(settings.getItemsLocation().getAbsolutePath()+File.separatorChar+name+".itemd");
            Item def = Item.readFrom(in);
            in.close();
            ItemMap.registerItem(def);
        }
        return ItemMap.getItem(name);
    }

    public ItemsRefresher getItemRefresher()
    {
        return itemsRefresher;
    }

    public BufferedImage getBallIcon(String s) throws IOException
    {
        File file = new File(settings.getMainFolder(), s+".png");
        if(!file.exists())
        {
            BufferedImage image = getItemRefresher().getExtractor().getExtractor().getImageFromName("File:Dream_"+s.replace(" ", "_")+"_Sprite.png");
            if(image.getWidth() == 80)
            {
                image = image.getSubimage(10,10,60,60);
            }
            ImageIO.write(image, "png", file);
        }
        return ImageIO.read(file);
    }

    public java.util.List<String> getNames(File folder, String extension)
    {
        File[] children = folder.listFiles((dir, name) -> {
            return name.endsWith(extension);
        });
        if(children == null)
        {
            return Collections.emptyList();
        }
        java.util.List<String> names = new LinkedList<>();
        for(File f : children)
        {
            names.add(f.getName().replace(extension, ""));
        }
        return names;
    }

    public java.util.List<String> getItemNames()
    {
        return getNames(settings.getItemsLocation(), ".itemd");
    }

    public java.util.List<String> getMoveNames()
    {
        return getNames(settings.getMovesLocation(), ".movd");
    }

    public void registerAbility(Ability part) throws IOException
    {
        if(!settings.getMovesLocation().exists())
        {
            settings.getMovesLocation().mkdirs();
        }
        FileOutputStream out = new FileOutputStream(new File(settings.getMovesLocation(), part.getEnglishName()+".abid"));
        part.writeTo(out);
        out.flush();
        out.close();
    }

    public Ability getAbility(String name) throws IOException
    {
        if(!AbilityMap.has(name))
        {
            FileInputStream in = new FileInputStream(settings.getMovesLocation().getAbsolutePath()+File.separatorChar+name+".abid");
            Ability def = Ability.readFrom(in);
            in.close();
            AbilityMap.registerAbility(def);
        }
        return AbilityMap.getAbility(name);
    }

    public void refreshFrame()
    {
        frame.repaint();
    }
}
