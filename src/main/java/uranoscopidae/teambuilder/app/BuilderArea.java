package uranoscopidae.teambuilder.app;

import uranoscopidae.teambuilder.app.search.SearchZone;
import uranoscopidae.teambuilder.app.search.SearchZoneSearchListener;
import uranoscopidae.teambuilder.app.team.PokemonGender;
import uranoscopidae.teambuilder.app.team.TeamEntry;
import uranoscopidae.teambuilder.pkmn.Ability;
import uranoscopidae.teambuilder.pkmn.PokemonStats;
import uranoscopidae.teambuilder.utils.YesNoEnum;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class BuilderArea extends JPanel
{

    private final TeamBuilderApp app;
    private boolean general;
    private TeamEntry entry;
    private SearchZone searchZone;

    public BuilderArea(TeamBuilderApp app)
    {
        this.app = app;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setGeneralView();
    }

    public void setGeneralView()
    {
        this.general = true;
        updateContents();
    }

    private void updateContents()
    {
        removeAll();
        if(general)
        {

        }
        else
        {
            setBorder(new TitledBorder("Your Pokémon"));
            setLayout(new BorderLayout());
            searchZone = new SearchZone(this);
            searchZone.setCurrentEntry(entry);
            JPanel infosPanel = new JPanel();
            ConfirmableTextField textField = buildInfosPanel(infosPanel);

            JScrollPane pane = new JScrollPane(searchZone);
            pane.getVerticalScrollBar().setUnitIncrement(8);
            add(infosPanel,"North");
            add(pane, "Center");

            if(textField != null)
                textField.requestFocusInWindow();
        }
        repaint();
    }

    private ConfirmableTextField buildInfosPanel(JPanel infosPanel)
    {
        if(entry == null)
            return null;
        JLabel spriteLabel = createImageLabel(entry.isShiny() ? entry.getPokemon().getShinySprite() : entry.getPokemon().getSprite());
        infosPanel.setLayout(new BoxLayout(infosPanel, BoxLayout.X_AXIS));

        JPanel globalInfos = new JPanel();
        globalInfos.setLayout(new BoxLayout(globalInfos, BoxLayout.Y_AXIS));
        JTextField nicknameField = createNicknameField(app, entry);
        ConfirmableTextField nameField = createNameField(app, entry);

        nameField.setOpaque(false);
        nicknameField.setOpaque(false);
        nicknameField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Nickname"), BorderFactory.createLoweredBevelBorder()));
        nameField.setSurroundingBorder(BorderFactory.createTitledBorder("Pokémon"));
        globalInfos.add(nicknameField);
        globalInfos.add(spriteLabel);
        globalInfos.add(nameField);

        infosPanel.add(globalInfos);

        JPanel characteristicsPanel = new JPanel();
        characteristicsPanel.setLayout(new BoxLayout(characteristicsPanel, BoxLayout.Y_AXIS));

        JPanel details = new JPanel();
        details.setBorder(BorderFactory.createTitledBorder("Details"));
        details.setLayout(new FlowLayout());

        addPart("Level", createLevelField(app, entry), details);

        addPart("Gender", createGenderPanel(app, entry), details);

        addPart("Happiness", createHappinessField(app, entry), details);

        addPart("Shiny", createShinyPanel(app, spriteLabel, entry), details);

        characteristicsPanel.add(details);

        JPanel itemPanel = new JPanel();
        itemPanel.add(Box.createVerticalGlue());
        JLabel itemIcon = createImageLabel(entry.getItem().getIcon(), 24, 24);
        itemPanel.add(itemIcon);
        ConfirmableTextField itemName = new ConfirmableTextField(entry.getItem() == null ? "" : entry.getItem().getEnglishName(), app.getItemNames());
        itemPanel.add(itemName);
        itemName.addConfirmationListener(s -> {
            try
            {
                loadImage(app.getItem(s).getIcon(), itemIcon);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        });

        itemName.addActionListener((e) -> searchZone.searchItem(itemName, itemIcon));
        SearchZoneSearchListener itemSearchListener = new SearchZoneSearchListener(() -> searchZone.searchItem(itemName, itemIcon), searchZone::confirm);
        itemName.addMouseListener(itemSearchListener);
        itemName.addKeyListener(itemSearchListener);

        itemPanel.setBorder(BorderFactory.createTitledBorder("Item"));

        characteristicsPanel.add(itemPanel);

        addPart("Ability", createAbilityPanel(entry), characteristicsPanel, new FlowLayout());


        JPanel movePanel = new JPanel();
        for (int i = 0; i < 4; i++)
        {
            ConfirmableTextField moveField = new ConfirmableTextField(30, app.getMoveNames());
            if(entry.getMoveInfoses()[i] != null)
                moveField.setText(entry.getMoveInfoses()[i].getEnglishName());
            moveField.updateConfirmationState();
            SearchZoneSearchListener moveSearchListener = new SearchZoneSearchListener(() -> searchZone.searchMove(moveField), searchZone::confirm);
            moveField.addMouseListener(moveSearchListener);
            moveField.addKeyListener(moveSearchListener);
            moveField.addConfirmationPredicate(s -> entry.getPokemon().canLearn(s) || s.startsWith("!"));
            moveField.updateConfirmationState();
            movePanel.add(moveField);
        }
        addPart("Moves", movePanel, characteristicsPanel);

        infosPanel.add(characteristicsPanel);

        infosPanel.add(createStatsPanel(app, entry));

        return nameField;
    }

    private ConfirmableTextField createNameField(TeamBuilderApp app, TeamEntry entry)
    {
        List<String> list = app.getPokemonNames();
        /*for (int i = 0; i < list.size(); i++)
        {
            list.set(i, list.get(i).substring(3));
        }*/
        ConfirmableTextField nameField = new ConfirmableTextField(entry.getPokemon().getEnglishName(), list);
        nameField.updateConfirmationState();
        nameField.addConfirmationListener((s) -> {
            entry.setPokemon(app.getPokemonFromName(s));
            setSpecificView(entry);
        });
        SearchZoneSearchListener moveSearchListener = new SearchZoneSearchListener(() -> searchZone.searchPokemon(nameField), searchZone::confirm);
        nameField.addMouseListener(moveSearchListener);
        nameField.addKeyListener(moveSearchListener);
        return nameField;
    }

    private JComponent createStatsPanel(TeamBuilderApp app, TeamEntry entry)
    {
        JPanel panel = new JPanel();
        PokemonStats stats = entry.getPokemon().getStats();
        panel.add(new JLabel("HP: "+stats.getHP()));
        panel.add(new JLabel("Attack: "+stats.getAttack()));
        panel.add(new JLabel("Defense: "+stats.getDefense()));
        panel.add(new JLabel("Special Attack: "+stats.getSpecialAttack()));
        panel.add(new JLabel("Special Defense: "+stats.getSpecialDefense()));
        panel.add(new JLabel("Speed: "+stats.getSpeed()));
        return panel;
    }

    private JComponent createHappinessField(TeamBuilderApp app, TeamEntry entry)
    {
        JSpinner happiness = new JSpinner(new SpinnerNumberModel(entry.getLevel(), 0, 255, 1));
        happiness.setValue(entry.getHappiness());
        happiness.addChangeListener(e -> entry.setHappiness((Integer) happiness.getValue()));
        return happiness;
    }

    private JComponent createLevelField(TeamBuilderApp app, TeamEntry entry)
    {
        JSpinner levelField = new JSpinner(new SpinnerNumberModel(entry.getLevel(), 1, 100, 1));
        levelField.setValue(entry.getLevel());
        levelField.addChangeListener(e -> {
            int intLevel = ((Integer) levelField.getValue());
            entry.setLevel((byte)intLevel);
            app.refreshFrame();
        });
        return levelField;
    }

    private JComponent createAbilityPanel(TeamEntry entry)
    {
        JComboBox<Ability> abilities = new JComboBox<>(entry.getPokemon().getAbilities().toArray(new Ability[0]));
        abilities.setSelectedItem(entry.getAbility());
        abilities.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JPanel panel = new JPanel();
            JLabel label = new JLabel(value == null ? "???" : value.getEnglishName());
            if(isSelected)
            {
                panel.setBackground(Color.gray);
            }
            panel.add(label);
            return panel;
        });
        abilities.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED)
            {
                Ability ability = (Ability) e.getItem();
                entry.setAbility(ability);
            }
        });
        return abilities;
    }

    private JComponent createGenderPanel(TeamBuilderApp app, TeamEntry entry)
    {
        JComboBox<PokemonGender> genders = new JComboBox<>(PokemonGender.values());
        genders.setSelectedItem(entry.getGender());
        genders.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED)
            {
                entry.setGender((PokemonGender) e.getItem());
                app.refreshFrame();
            }
        });
        return genders;
    }

    private JTextField createNicknameField(TeamBuilderApp app, TeamEntry entry)
    {
        JTextField field = new JTextField(15);
        field.setText(entry.getNickname());
        field.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if(field.getText() != null)
                    entry.setNickname(field.getText());
                app.refreshFrame();
                super.keyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
                if(field.getText() != null)
                    entry.setNickname(field.getText());
                app.refreshFrame();
                super.keyReleased(e);
            }

            @Override
            public void keyTyped(KeyEvent e)
            {
                if(field.getText() != null)
                    entry.setNickname(field.getText());
                app.refreshFrame();
                super.keyTyped(e);
            }
        });
        return field;
    }

    private JComponent createShinyPanel(TeamBuilderApp app, JLabel spriteLabel, TeamEntry entry)
    {
        JComboBox<YesNoEnum> shiny = new JComboBox<>(YesNoEnum.values());
        shiny.setSelectedItem(entry.isShiny() ? YesNoEnum.YES : YesNoEnum.NO);
        shiny.addItemListener(e1 -> {
            if(e1.getStateChange() == ItemEvent.SELECTED)
            {
                boolean isShiny = e1.getItem() == YesNoEnum.YES;
                entry.setShiny(isShiny);
                loadImage(isShiny ? entry.getPokemon().getShinySprite() : entry.getPokemon().getSprite(), spriteLabel);
                app.refreshFrame();
            }
        });
        return shiny;
    }

    private JLabel createImageLabel(BufferedImage image)
    {
        return createImageLabel(image, image.getWidth(), image.getHeight());
    }

    public JLabel createImageLabel(BufferedImage image, int minW, int minH)
    {
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(minW, minH));
        loadImage(image, label);
        return label;
    }

    public void loadImage(BufferedImage image, JLabel label)
    {
        SwingWorker<ImageIcon, BufferedImage> worker = new SwingWorker<ImageIcon, BufferedImage>()
        {
            @Override
            protected ImageIcon doInBackground() throws Exception
            {
                return new ImageIcon(image);
            }

            @Override
            protected void done()
            {
                super.done();
                try
                {
                    label.setIcon(this.get());
                }
                catch (InterruptedException | ExecutionException e)
                {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    public void addPart(String title, JComponent part, JPanel to)
    {
        addPart(title, part, to, new BorderLayout());
    }

    public void addPart(String title, JComponent part, JPanel to, LayoutManager layout)
    {
        JPanel panel = new JPanel();
        panel.setLayout(layout);
        panel.add(part);
        panel.setBorder(BorderFactory.createTitledBorder(title));

        to.add(panel);
    }

    public void setSpecificView(TeamEntry entry)
    {
        this.entry = entry;
        this.general = false;
        updateContents();
    }

    public TeamBuilderApp getApp()
    {
        return app;
    }
}
