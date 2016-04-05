package uranoscopidae.teambuilder.app;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class ConfirmableTextField extends JTextField
{
    private LinkedList<Predicate<String>> confirmationPredicates;
    private Border confirmedBorder;
    private Border unconfirmedBorder;
    private LinkedList<ConfirmationListener> confirmationListeners;

    public ConfirmableTextField(int size, List<String> allowedNames)
    {
        super(size);
        init(allowedNames);
    }

    private void init(List<String> allowedNames)
    {
        confirmationPredicates = new LinkedList<>();
        confirmationListeners = new LinkedList<>();
        addConfirmationPredicate(allowedNames::contains);

        confirmedBorder = BorderFactory.createLineBorder(Color.green);
        unconfirmedBorder = BorderFactory.createLineBorder(Color.red);

        updateConfirmationState();
        addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyReleased(KeyEvent e)
            {
                updateConfirmationState();
            }
        });
    }

    public ConfirmableTextField(String text, List<String> allowedNames)
    {
        super(text, 20);
        init(allowedNames);
    }

    public void addConfirmationListener(ConfirmationListener listener)
    {
        confirmationListeners.add(listener);
    }

    public void addConfirmationPredicate(Predicate<String> predicate)
    {
        confirmationPredicates.add(predicate);
    }

    public void updateConfirmationState()
    {
        String text = getText();
        boolean valid = true;
        for (Predicate<String> p : confirmationPredicates)
        {
            if(!p.test(text))
            {
                valid = false;
                break;
            }
        }
        setBorder(valid ? confirmedBorder : unconfirmedBorder);

        if(valid)
        {
            for (ConfirmationListener l : confirmationListeners)
            {
                l.onConfirmation(text);
            }
        }
    }

    @Override
    public void setText(String t)
    {
        super.setText(t);
    }
}
