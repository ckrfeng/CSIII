import javax.swing.*;
import java.awt.*;
import java.util.function.IntConsumer;

public class MenuPanel extends JPanel {
    public MenuPanel(IntConsumer onStart) {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10,10,10,10);
        c.gridx = 0; c.gridy = 0;
        add(new JLabel("Sushi Go! Pass & Play"), c);
        c.gridy++;
        add(new JLabel("Number of Players (3-5)"), c);
        Integer[] opts = {3,4,5};
        JComboBox<Integer> sel = new JComboBox<>(opts);
        c.gridy++;
        add(sel, c);
        JButton start = new JButton("Start Game");
        c.gridy++;
        add(start, c);
        start.addActionListener(e -> onStart.accept((Integer)sel.getSelectedItem()));
    }
}
