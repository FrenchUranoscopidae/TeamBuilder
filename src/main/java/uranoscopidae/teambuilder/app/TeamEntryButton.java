package uranoscopidae.teambuilder.app;

import uranoscopidae.teambuilder.app.team.PokemonGender;
import uranoscopidae.teambuilder.app.team.TeamEntry;
import uranoscopidae.teambuilder.pkmn.Pokemon;
import uranoscopidae.teambuilder.utils.IOHelper;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class TeamEntryButton extends JButton
{

    private final static Font levelFont = new Font(null, Font.PLAIN, 16);
    private final static Font nameFont = new Font(null, Font.PLAIN, 20);
    private final TeamEntry entry;
    private BufferedImage maleIcon;
    private BufferedImage femaleIcon;
    private BufferedImage pokeball;

    public TeamEntryButton(TeamBuilderApp app, TeamEntry entry)
    {
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
            pokeball = ImageIO.read(getClass().getResourceAsStream("/pokeball.png"));
            maleIcon = ImageIO.read(getClass().getResourceAsStream("/maleIcon.png"));
            femaleIcon = ImageIO.read(getClass().getResourceAsStream("/femaleIcon.png"));
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

        // Draw box
        float sizeFactor = (float)getHeight()/pokeball.getHeight();
        final int pokeballWidth = (int) (pokeball.getWidth()*sizeFactor);
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
        GradientPaint gradient = new GradientPaint(0,heightCompensation/2,Color.cyan.darker(), 0, getHeight()-heightCompensation/2-1, new Color(80,80,255));
        g.setPaint(gradient);
        g.fill(s);

        g.setPaint(Color.white);

        // Draw pokeball
        g.drawImage(pokeball, 0, 0, pokeballWidth, (int) (pokeball.getHeight()*sizeFactor), null);

        // Draw pokemon infos
        if(entry.hasPokemon())
        {
            Pokemon pokemon = entry.getPokemon();
            BufferedImage icon = pokemon.getIcon();
            float pokemonSizeFactor = (getHeight()-heightCompensation) / (float)icon.getHeight();
            int iconWidth = (int) (pokemonSizeFactor*icon.getWidth());
            int iconHeight = (int) (pokemonSizeFactor*icon.getHeight());
            g.drawImage(icon, pokeballWidth, getHeight()/2-iconHeight/2, iconWidth, iconHeight, null);

            g.setFont(nameFont);
            FontMetrics metrics = g.getFontMetrics();
            int nameWidth = metrics.stringWidth(pokemon.getEnglishName());
            int nameY = heightCompensation+metrics.getHeight()/2;
            int nameX = pokeballWidth+iconWidth;
            drawShadowedString(g, pokemon.getEnglishName(), nameX, nameY);


            g.setFont(levelFont);
            metrics = g.getFontMetrics();
            int level = entry.getLevel();

            drawShadowedString(g, "Lv."+level, pokeballWidth+iconWidth, getHeight()-nameY+metrics.getHeight()-5);

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
