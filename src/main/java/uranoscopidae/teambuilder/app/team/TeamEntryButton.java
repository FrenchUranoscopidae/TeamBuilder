package uranoscopidae.teambuilder.app.team;

import me.sargunvohra.lib.pokekotlin.model.Pokemon;
import uranoscopidae.teambuilder.app.TeamBuilderApp;
import uranoscopidae.teambuilder.pkmn.PokemonInfos;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TeamEntryButton extends JButton
{

    private final static Font levelFont = new Font(null, Font.PLAIN, 16);
    private final static Font nameFont = new Font(null, Font.PLAIN, 20);
    private final TeamBuilderApp app;
    private final TeamEntry entry;
    private static BufferedImage grayedPokeball;
    private static BufferedImage maleIcon;
    private static BufferedImage femaleIcon;
    private static BufferedImage shinyIcon;

    public TeamEntryButton(TeamBuilderApp app, TeamEntry entry)
    {
        this.app = app;
        this.entry = entry;

        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Dimension size = new Dimension(300, 64);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setSize(size);

        try
        {
            if(grayedPokeball == null)
            {
                grayedPokeball = ImageIO.read(getClass().getResourceAsStream("/grayedPokeball.png"));
                maleIcon = ImageIO.read(getClass().getResourceAsStream("/maleIcon.png"));
                femaleIcon = ImageIO.read(getClass().getResourceAsStream("/femaleIcon.png"));
                shinyIcon = ImageIO.read(getClass().getResourceAsStream("/shinyIcon.png"));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void paintComponent(Graphics gr)
    {
        Graphics2D g = (Graphics2D)gr;
        g.setColor(Color.black);

        BufferedImage pokeballIcon = entry.hasPokemon() ? grayedPokeball : grayedPokeball; // TODO: Use red Pokéball instead of grayed-out one when the entry actually has a Pokémon
        // Draw box
        float sizeFactor = (float)getHeight()/pokeballIcon.getHeight();
        final int pokeballWidth = (int) (pokeballIcon.getWidth()*sizeFactor);
        final int boxOffset = pokeballWidth/2;
        final int offset = 20;
        final int heightCompensation = 20;
        g.setStroke(new BasicStroke(4));
        g.drawLine(boxOffset+offset, heightCompensation/2, getWidth()-1, heightCompensation/2);
        g.drawLine(boxOffset, getHeight()-1-heightCompensation/2, getWidth()-offset-1, getHeight()-1-heightCompensation/2);

        g.drawLine(boxOffset, getHeight()-1-heightCompensation/2, offset+boxOffset, heightCompensation/2);
        g.drawLine(getWidth()-offset-1, getHeight()-1-heightCompensation/2, getWidth()-1, heightCompensation/2);

        g.setStroke(new BasicStroke(1));

        // draw background
        Shape s = new Polygon(new int[]{boxOffset+offset, getWidth()-1, getWidth()-offset-1, boxOffset},
                new int[]{heightCompensation/2,heightCompensation/2, getHeight()-heightCompensation/2-1, getHeight()-heightCompensation/2-1},
                4);
        Color startColor;
        Color endColor;
        if(entry.hasPokemon())
        {
            startColor = Color.cyan.darker();
            endColor = new Color(80, 80, 255);
        }
        else
        {
            startColor = Color.lightGray.darker();
            endColor = new Color(80, 80, 255/4);
        }
        GradientPaint gradient = new GradientPaint(0,heightCompensation/2, startColor, 0, getHeight()-heightCompensation/2-1, endColor);
        g.setPaint(gradient);
        g.fill(s);

        g.setPaint(Color.white);

        // Draw pokeball
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.drawImage(pokeballIcon, 0, 0, pokeballWidth, (int) (pokeballIcon.getHeight()*sizeFactor), null);

        // Draw pokemon infos
        if(entry.hasPokemon())
        {
            PokemonInfos pokemon = entry.getPokemon();
            BufferedImage icon = pokemon.getIcon();
            icon.setRGB(0,0,0xFF000000);
            float pokemonSizeFactor = Math.max(1f, Math.round(((float)getHeight()-heightCompensation) / (float)icon.getHeight()*2f))/2f; // magical formula that makes sure the size factor is a multiple of 0.5
            int iconWidth = (int) (pokemonSizeFactor*icon.getWidth());
            int iconHeight = (int) (pokemonSizeFactor*icon.getHeight());
            g.drawImage(icon, pokeballWidth, getHeight()/2-iconHeight/2, iconWidth, iconHeight, null);

            g.setFont(nameFont);
            String name;
            if(entry.hasNickname())
            {
                name = entry.getNickname();
            }
            else
            {
                name = entry.getPokemon().getEnglishName();
            }
            FontMetrics metrics = g.getFontMetrics();
            int nameWidth = metrics.stringWidth(name);
            int nameY = heightCompensation+metrics.getHeight()/2;
            int nameX = pokeballWidth+iconWidth;
            drawShadowedString(g, name, nameX, nameY);


            g.setFont(levelFont);
            metrics = g.getFontMetrics();
            int level = entry.getLevel();

            String levelText = "Lv."+level+(entry.hasNickname() ? (" ("+entry.getPokemon().getEnglishName()+")") : "");
            int levelX = pokeballWidth+iconWidth;
            int levelY = getHeight()-nameY+metrics.getHeight()-5;
            drawShadowedString(g, levelText, levelX, levelY);
            if(entry.isShiny())
            {
                g.drawImage(shinyIcon, levelX+1+metrics.stringWidth(levelText), levelY-shinyIcon.getHeight(), (int) (shinyIcon.getWidth()*pokemonSizeFactor), (int) (shinyIcon.getHeight()*pokemonSizeFactor), null);
            }

            if(entry.getGender() != PokemonGender.ASEXUAL)
            {
                BufferedImage genderIcon = entry.getGender() == PokemonGender.MALE ? maleIcon : femaleIcon;
                g.drawImage(genderIcon, nameX+nameWidth+1, nameY-genderIcon.getHeight(), (int) (genderIcon.getWidth()*pokemonSizeFactor), (int) (genderIcon.getHeight()*pokemonSizeFactor), null);
            }
        }
    }

    public void drawShadowedString(Graphics g, String text, int x, int y)
    {
        g.setColor(Color.black);
        g.drawString(text, x+1, y+1);
        g.setColor(Color.white);
        g.drawString(text, x, y);
    }

}
