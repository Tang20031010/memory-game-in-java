import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class MemoryGame {
    private static final int APPLICATION_SIZE = 400;
    private static final Color BACKGROUND = new JLabel().getBackground();
    private int score = 0;
    private int timeElapsed = 0;

    private List<JButton> fieldCells; // Store the original game board state

    public static void main(String[] args) {
        new MemoryGame().runGame();
    }

    private void runGame() {
        JFrame application = new JFrame("color game");
        application.setTitle("Memory Game ");
        JLabel scoreLabel = new JLabel("score: " + score);
        JLabel timeLabel = new JLabel("time: " + formatTime(timeElapsed));
        List<Color> colors = Arrays.asList(Color.BLUE, Color.CYAN, Color.RED, Color.YELLOW, Color.GREEN, Color.BLACK);
        fieldCells = initializeGame(colors); // Store the original game board state
        JPanel gameFiled = initializeView(fieldCells);
        bindViewToModel(colors, fieldCells, scoreLabel);
        JPanel gameControl = setupController(colors, fieldCells, application, scoreLabel, timeLabel);
        application.getContentPane().add(gameFiled);
        application.getContentPane().add(gameControl, BorderLayout.SOUTH);
        application.setSize(APPLICATION_SIZE, 400);
        application.setVisible(true);

        Timer timer = new Timer(1000, e -> {
            timeElapsed++;
            timeLabel.setText("time: " + formatTime(timeElapsed));
        });
        timer.start();
    }

    private JPanel setupController(List<Color> colors,
            List<JButton> fieldCells,
            JFrame application,
            JLabel scoreLabel,
            JLabel timeLabel) {
        JPanel gameControl = new JPanel(new GridLayout(1, 0));
        gameControl.add(new JButton(new AbstractAction("restart") {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeElapsed = -1;
                score = 0;
                resetGame();
                bindViewToModel(colors, fieldCells, scoreLabel);
            }
        }));
        gameControl.add(scoreLabel);
        gameControl.add(timeLabel);
        gameControl.add(new JButton(new AbstractAction("quit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                application.dispose();
            }
        }));
        return gameControl;
    }

    private void resetGame() {
        for (JButton button : fieldCells) {
            button.setOpaque(true);
            button.setBackground(BACKGROUND);
            button.setEnabled(true);
        }
    }

    private void bindViewToModel(List<Color> colors, List<JButton> fieldCells, JLabel scoreLabel) {
        Collection<JComponent> clickedButtons = new HashSet<>(); // Model
        Collections.shuffle(fieldCells);
        Iterator<JButton> randomCells = fieldCells.iterator();
        for (Color color : colors) {
            AbstractAction buttonAction = createButtonAction(clickedButtons, color, scoreLabel);
            bindButton(buttonAction, randomCells.next());
            bindButton(buttonAction, randomCells.next());
        }
        clickedButtons.clear();
        score = 0;
    }

    private void bindButton(AbstractAction buttonAction, JButton jButton) {
        jButton.setAction(buttonAction);
        jButton.setOpaque(true);
        jButton.setBackground(BACKGROUND);
    }

    private JPanel initializeView(List<JButton> fieldCells) {
        JPanel gameFiled = new JPanel(new GridLayout(4, 0));
        for (JButton fieldCell : fieldCells) {
            fieldCell.setOpaque(true);
            fieldCell.setBackground(BACKGROUND);
            fieldCell.setEnabled(true);
            gameFiled.add(fieldCell);
        }
        return gameFiled;
    }

    private List<JButton> initializeGame(Collection<Color> colors) {
        List<JButton> fieldCells = new ArrayList<>();
        for (Color color : colors) {
            fieldCells.add(new JButton()); // two buttons per color
            fieldCells.add(new JButton());
        }
        return fieldCells;
    }

    private AbstractAction createButtonAction(Collection<JComponent> clickedButtons, Color color, JLabel scoreLabel) {
        @SuppressWarnings("serial")
        AbstractAction buttonAction = new AbstractAction() { // Controller
            Collection<JComponent> clickedPartners = new HashSet<>(); // also Model

            @Override
            public void actionPerformed(ActionEvent e) {
                JComponent thisButton = (JComponent) e.getSource();
                clickedPartners.add(thisButton);
                clickedButtons.add(thisButton);
                thisButton.setOpaque(true);
                thisButton.setBackground(color);
                thisButton.setEnabled(false);
                if (2 == clickedButtons.size()) { // is second clicked
                    if (2 == clickedPartners.size()) { // user found partner
                        score += 10;
                    } else {
                        JOptionPane.showMessageDialog(thisButton, "no match");
                        for (JComponent partner : clickedButtons) {
                            partner.setOpaque(true);
                            partner.setBackground(BACKGROUND);
                            partner.setEnabled(true);
                        }
                        score--;
                    }
                    clickedButtons.clear();
                    clickedPartners.clear();
                    scoreLabel.setText("score: " + score);
                }
            }
        };
        return buttonAction;
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }
}
