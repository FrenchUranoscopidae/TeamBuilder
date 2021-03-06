package uranoscopidae.teambuilder.app;

import com.alee.laf.WebLookAndFeel;
import com.alee.managers.style.StyleManager;
import com.alee.skin.dark.DarkSkin;
import com.alee.skin.flat.FlatSkin;
import com.alee.skin.modena.ModenaSkin;
import com.alee.skin.web.WebSkin;
import uranoscopidae.teambuilder.app.team.Team;
import uranoscopidae.teambuilder.pkmn.Ability;
import uranoscopidae.teambuilder.pkmn.api.PokeApiInterface;
import uranoscopidae.teambuilder.pkmn.PokemonInfos;
import uranoscopidae.teambuilder.pkmn.items.Item;
import uranoscopidae.teambuilder.pkmn.moves.MoveInfos;
import uranoscopidae.teambuilder.utils.Constants;

import javax.imageio.ImageIO;
import javax.swing.*;
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
        WebLookAndFeel.install();
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
        loadingFrame.newTask("Loading moves", apiInterface::loadMoveList);
        loadingFrame.newTask("Loading Pokémons", apiInterface::loadPokemonList);
        loadingFrame.newTask("Loading Abilities", apiInterface::loadAbilityList);
        loadingFrame.newTask("Loading Dex IDs", apiInterface::loadPkmnDexIDs);
        loadingFrame.newTask("Loading Icons", apiInterface::loadPkmnIcons);
        loadingFrame.newTask("Loading Items", apiInterface::loadItems);
        loadingFrame.initLoading();
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


        JMenu styleMenu = new JMenu("Interface style");
        // TODO: Save
        styleMenu.add("Dark skin").addActionListener(e -> {
            WebLookAndFeel.install();
            StyleManager.setSkin(DarkSkin.class);
        });
        styleMenu.add("Flat skin").addActionListener(e -> {
            WebLookAndFeel.install();
            StyleManager.setSkin(FlatSkin.class);
        });
        styleMenu.add("Modena skin").addActionListener(e -> {
            WebLookAndFeel.install();
            StyleManager.setSkin(ModenaSkin.class);
        });
        styleMenu.add("Web skin").addActionListener(e -> {
            WebLookAndFeel.install();
            StyleManager.setSkin(WebSkin.class);
        });
        styleMenu.add("OS skin").addActionListener(e -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e1) {
                e1.printStackTrace();
            }
        });
        styleMenu.add("Metal skin").addActionListener(e -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e1) {
                e1.printStackTrace();
            }
        });

        styleMenu.add("Nimubs skin").addActionListener(e -> {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e1) {
                e1.printStackTrace();
            }
        });
        settings.add(styleMenu);

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
        return apiInterface.getItemFromName(name);
    }

    public BufferedImage getBallIcon(String s) throws IOException
    {
        File file = new File(settings.getMainFolder(), s+".png");
        if(!file.exists())
            return null;
        return ImageIO.read(file);
    }

    public java.util.List<String> getItemNames()
    {
        return apiInterface.getItemNames();
    }

    public java.util.List<String> getMoveNames()
    {
        return apiInterface.getMoveNames();
    }

    public java.util.List<String> getPokemonNames()
    {
        return apiInterface.getPokemonNames();
    }

    public Ability getAbility(String name)
    {
        return apiInterface.getAbilityFromName(name);
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

    public Collection<Item> getItems() {
        return apiInterface.getItems();
    }

    public Collection<PokemonInfos> getPokemons() {
        return apiInterface.getPokemons();
    }

    public Collection<MoveInfos> getMoves() {
        return apiInterface.getMoves();
    }
}
