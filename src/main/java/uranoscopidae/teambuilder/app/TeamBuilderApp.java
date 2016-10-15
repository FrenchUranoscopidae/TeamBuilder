package uranoscopidae.teambuilder.app;

import uranoscopidae.teambuilder.app.team.Team;
import uranoscopidae.teambuilder.pkmn.Ability;
import uranoscopidae.teambuilder.pkmn.api.PokeApiInterface;
import uranoscopidae.teambuilder.pkmn.PokemonInfos;
import uranoscopidae.teambuilder.pkmn.items.Item;
import uranoscopidae.teambuilder.pkmn.items.ItemMap;
import uranoscopidae.teambuilder.pkmn.moves.MoveInfos;
import uranoscopidae.teambuilder.utils.Constants;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class TeamBuilderApp
{

    public static TeamBuilderApp instance;
    private final Settings settings;
    private final PokeApiInterface apiInterface;
    private JFrame frame;
    private MainPanel mainPanel;

    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            e.printStackTrace();
        }
        instance = new TeamBuilderApp();
        instance.start();
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
        apiInterface = new PokeApiInterface(this, settings);
    }

    protected void start()
    {
        loadData();
        frame = new JFrame("Teambuilder - v"+ Constants.VERSION);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setJMenuBar(buildMenuBar());
        mainPanel = new MainPanel(this);
        frame.add(mainPanel);
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

    private void loadData() {
        LoadingFrame loadingFrame = new LoadingFrame(settings);
        loadingFrame.setTitle("Loading data");
        loadingFrame.waitFor("Loading moves", apiInterface::loadMoveList);
        loadingFrame.waitFor("Loading Pokémons", apiInterface::loadPokemonList);
        loadingFrame.dispose();
    }

    private JMenuBar buildMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
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

        JMenu advancedMenu = new JMenu("Advanced");

        settings.add(advancedMenu);

        menuBar.add(file);
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

    public MoveInfos getMove(String name)
    {
        return apiInterface.getMoveFromName(name);
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

    public BufferedImage getBallIcon(String s) throws IOException
    {
        File file = new File(settings.getMainFolder(), s+".png");
        if(!file.exists())
            return null;
        return ImageIO.read(file);
    }

    public java.util.List<String> getNames(File folder, String extension)
    {
        File[] children = folder.listFiles((dir, name) -> name.endsWith(extension));
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
        return apiInterface.getMoveNames();
    }

    public java.util.List<String> getPokemonNames()
    {
        return apiInterface.getPokemonNames();
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

    public PokemonInfos getPokemonFromName(String name)
    {
        return apiInterface.getPokemonFromName(name);
    }

    public PokeApiInterface getApiInterface() {
        return apiInterface;
    }
}
