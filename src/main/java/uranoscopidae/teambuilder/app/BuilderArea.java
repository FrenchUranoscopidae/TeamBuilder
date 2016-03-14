package uranoscopidae.teambuilder.app;

import uranoscopidae.teambuilder.app.team.PokemonGender;
import uranoscopidae.teambuilder.app.team.TeamEntry;
import uranoscopidae.teambuilder.utils.YesNoEnum;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;

public class BuilderArea extends JPanel
{

    private boolean general;
    private TeamEntry entry;

    public BuilderArea()
    {
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
            JPanel searchZone = new JPanel();
            searchZone.add(new JButton("TEST"));
            add(new JScrollPane(searchZone));
        }
        repaint();
    }

    private void buildInfosPanel(JPanel infosPanel)
    {
        if(entry == null)
            return;
        JLabel spriteLabel = createImageLabel(entry.getPokemon().getSprite());
        infosPanel.setLayout(new BoxLayout(infosPanel, BoxLayout.X_AXIS));

        JPanel globalInfos = new JPanel();
        globalInfos.setLayout(new BoxLayout(globalInfos, BoxLayout.Y_AXIS));
        JTextField nicknameField = new JTextField(15);
        nicknameField.setText(entry.getPokemon().getEnglishName());
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

        JComboBox<PokemonGender> genders = new JComboBox<>(PokemonGender.values());
        genders.setSelectedItem(entry.getGender());
        addPart("Gender", genders, details);

        JSpinner happiness = new JSpinner(new SpinnerNumberModel(entry.getLevel(), 0, 255, 1));
        happiness.setValue(entry.getHappiness());
        addPart("Happiness", happiness, details);

        JComboBox<YesNoEnum> shiny = new JComboBox<>(YesNoEnum.values());
        shiny.setSelectedItem(entry.isShiny() ? YesNoEnum.YES : YesNoEnum.NO);
        addPart("Shiny", shiny, details);

        characteristicsPanel.add(details);

        JPanel itemPanel = new JPanel();
        itemPanel.add(Box.createVerticalGlue());
        itemPanel.add(createImageLabel(entry.getItem().getIcon(), 24, 24));
        itemPanel.add(new JTextField(entry.getItem().getName(), 20));
        itemPanel.setBorder(BorderFactory.createTitledBorder("Item"));

        characteristicsPanel.add(itemPanel);

        JPanel abilityPanel = new JPanel();
        abilityPanel.add(new JTextField("ABILITY", 20));
        abilityPanel.setBorder(BorderFactory.createTitledBorder("Ability"));
        characteristicsPanel.add(abilityPanel);

        infosPanel.add(characteristicsPanel);
    }

    private JLabel createImageLabel(BufferedImage image)
    {
        return createImageLabel(image, image.getWidth(), image.getHeight());
    }

    private JLabel createImageLabel(BufferedImage image, int minW, int minH)
    {
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(minW, minH));
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
        return label;
    }

    public void addPart(String title, JComponent part, JPanel to)
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
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
}
