package uranoscopidae.teambuilder.app;

import uranoscopidae.teambuilder.app.team.PokemonGender;
import uranoscopidae.teambuilder.app.team.TeamEntry;
import uranoscopidae.teambuilder.pkmn.Ability;
import uranoscopidae.teambuilder.utils.YesNoEnum;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
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
            JPanel infosPanel = new JPanel();
            buildInfosPanel(infosPanel);
            add(infosPanel,"North");
            searchZone = new SearchZone(this);
            add(new JScrollPane(searchZone));
        }
        repaint();
    }

    private void buildInfosPanel(JPanel infosPanel)
    {
        if(entry == null)
            return;
        JLabel spriteLabel = createImageLabel(entry.isShiny() ? entry.getPokemon().getShinySprite() : entry.getPokemon().getSprite());
        infosPanel.setLayout(new BoxLayout(infosPanel, BoxLayout.X_AXIS));

        JPanel globalInfos = new JPanel();
        globalInfos.setLayout(new BoxLayout(globalInfos, BoxLayout.Y_AXIS));
        JTextField nicknameField = createNicknameField(app, entry);
        JTextField nameField = new JTextField(entry.getPokemon().getEnglishName(), 15);

        nameField.setOpaque(false);
        nicknameField.setOpaque(false);
        nicknameField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Nickname"), BorderFactory.createLoweredBevelBorder()));
        nameField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Pokémon"), BorderFactory.createLoweredBevelBorder()));
        globalInfos.add(nicknameField);
        globalInfos.add(spriteLabel);
        globalInfos.add(nameField);

        infosPanel.add(globalInfos);

        JPanel characteristicsPanel = new JPanel();
        characteristicsPanel.setLayout(new BoxLayout(characteristicsPanel, BoxLayout.Y_AXIS));

        JPanel details = new JPanel();
        details.setBorder(BorderFactory.createTitledBorder("Details"));
        details.setLayout(new FlowLayout());

        JSpinner levelField = new JSpinner(new SpinnerNumberModel(entry.getLevel(), 1, 100, 1));
        levelField.setValue(entry.getLevel());
        addPart("Level", levelField, details);

        addPart("Gender", createGenderPanel(app, entry), details);

        JSpinner happiness = new JSpinner(new SpinnerNumberModel(entry.getLevel(), 0, 255, 1));
        happiness.setValue(entry.getHappiness());
        addPart("Happiness", happiness, details);

        addPart("Shiny", createShinyPanel(spriteLabel, entry), details);

        characteristicsPanel.add(details);

        JPanel itemPanel = new JPanel();
        itemPanel.add(Box.createVerticalGlue());
        JLabel itemIcon = createImageLabel(entry.getItem().getIcon(), 24, 24);
        itemPanel.add(itemIcon);
        JTextField itemName = new JTextField(entry.getItem().getName(), 20);
        itemPanel.add(itemName);

        itemName.addActionListener((e) -> searchZone.searchItem(itemName, itemIcon));
        itemName.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                searchZone.searchItem(itemName, itemIcon);
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                searchZone.searchItem(itemName, itemIcon);
            }
        });
        itemName.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                searchZone.searchItem(itemName, itemIcon);
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
                searchZone.searchItem(itemName, itemIcon);
            }

            @Override
            public void keyTyped(KeyEvent e)
            {
                searchZone.searchItem(itemName, itemIcon);
            }
        });

        itemPanel.setBorder(BorderFactory.createTitledBorder("Item"));

        characteristicsPanel.add(itemPanel);

        addPart("Ability", createAbilityPanel(entry), characteristicsPanel, new FlowLayout());


        JPanel movePanel = new JPanel();
        for (int i = 0; i < 4; i++)
        {
            JTextField moveField = new JTextField(30);
            if(entry.getMoves()[i] != null)
                moveField.setText(entry.getMoves()[i].getEnglishName());
            movePanel.add(moveField);
        }
        addPart("Moves", movePanel, characteristicsPanel);

        infosPanel.add(characteristicsPanel);
    }

    private JComponent createAbilityPanel(TeamEntry entry)
    {
        JComboBox<Ability> abilities = new JComboBox<>(entry.getPokemon().getAbilities().toArray(new Ability[0]));
        abilities.setSelectedItem(entry.getAbility());
        abilities.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JPanel panel = new JPanel();
            JLabel label = new JLabel(value.getName());
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

    private JComponent createShinyPanel(JLabel spriteLabel, TeamEntry entry)
    {
        JComboBox<YesNoEnum> shiny = new JComboBox<>(YesNoEnum.values());
        shiny.setSelectedItem(entry.isShiny() ? YesNoEnum.YES : YesNoEnum.NO);
        shiny.addItemListener(e1 -> {
            if(e1.getStateChange() == ItemEvent.SELECTED)
            {
                boolean isShiny = e1.getItem() == YesNoEnum.YES;
                entry.setShiny(isShiny);
                loadImage(isShiny ? entry.getPokemon().getShinySprite() : entry.getPokemon().getSprite(), spriteLabel);
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
