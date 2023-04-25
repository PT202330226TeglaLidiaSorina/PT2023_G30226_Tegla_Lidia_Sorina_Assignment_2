package GUI;
 import Model.*;
 import Simulation.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class View extends JFrame {
    public JLabel timeLabel;
    public JScrollPane output;
    public JTextArea queues=new JTextArea(20,70);
    public JLabel getTimeLabel() {
        return this.timeLabel;
    }

    public View() {
        setTitle("Queue Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setResizable(true);

        // Create a panel to hold the input fields
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5, 5, 5, 5);

        // Add input fields
        JLabel clientsLabel = new JLabel("Number of Clients:");
        inputPanel.add(clientsLabel, c);
        c.gridx++;
        JTextField clientsField = new JTextField(10);
        inputPanel.add(clientsField, c);
        c.gridx = 0;
        c.gridy++;

        JLabel serversLabel = new JLabel("Number of Servers:");
        inputPanel.add(serversLabel, c);
        c.gridx++;
        JTextField serverField = new JTextField(10);
        inputPanel.add(serverField, c);
        c.gridx = 0;
        c.gridy++;

        JLabel simulationLabel = new JLabel("Simulation Interval:");
        inputPanel.add(simulationLabel, c);
        c.gridx++;
        JTextField simulationField = new JTextField(10);
        inputPanel.add(simulationField, c);
        c.gridx = 0;
        c.gridy++;

        JLabel minArrivalLabel = new JLabel("Minimum Arrival Time:");
        inputPanel.add(minArrivalLabel, c);
        c.gridx++;
        JTextField minArrivalField = new JTextField(10);
        inputPanel.add(minArrivalField, c);
        c.gridx = 0;
        c.gridy++;

        JLabel maxArrivalLabel = new JLabel("Maximum Arrival Time:");
        inputPanel.add(maxArrivalLabel, c);
        c.gridx++;
        JTextField maxArrivalField = new JTextField(10);
        inputPanel.add(maxArrivalField, c);
        c.gridx = 0;
        c.gridy++;

        JLabel minServiceLabel = new JLabel("Minimum Service Time:");
        inputPanel.add(minServiceLabel, c);
        c.gridx++;
        JTextField minServiceField = new JTextField(10);
        inputPanel.add(minServiceField, c);
        c.gridx = 0;
        c.gridy++;

        JLabel maxServiceLabel = new JLabel("Maximum Service Time:");
        inputPanel.add(maxServiceLabel, c);
        c.gridx++;
        JTextField maxServiceField = new JTextField(10);
        inputPanel.add(maxServiceField, c);
        c.gridx = 0;
        c.gridy++;

        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        c.gridy++;

        JRadioButton shortestQueueRadioButton = new JRadioButton("Shortest Queue");
        shortestQueueRadioButton.setSelected(true);
        JRadioButton shortestTimeRadioButton = new JRadioButton("Shortest Time");
        ButtonGroup strategyGroup = new ButtonGroup();
        strategyGroup.add(shortestQueueRadioButton);
        strategyGroup.add(shortestTimeRadioButton);
        final View g = this;
        JButton startButton = new JButton("Start");
        inputPanel.add(startButton, c);
        c.gridy++;

        JPanel outputPanel = new JPanel();
        inputPanel.add(outputPanel,c);
        c.gridy++;
        output = new JScrollPane(queues);
        outputPanel.add(output);
        inputPanel.add(outputPanel,c);
        c.gridy++;
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SimulationManager sm = new SimulationManager(Integer.valueOf(simulationField.getText()),
                        Integer.valueOf(maxServiceField.getText()), Integer.valueOf(minServiceField.getText()),
                        Integer.valueOf(minArrivalField.getText()), Integer.valueOf(maxArrivalField.getText()),
                        Integer.valueOf(serverField.getText()), Integer.valueOf(clientsField.getText()));
                sm.setGui(View.this);
                sm.start();

                if (shortestQueueRadioButton.isSelected())
                    sm.getScheduler().changeStrategy(SelectionPolicy.SHORTEST_QUEUE);
                else if (shortestTimeRadioButton.isSelected())
                    sm.getScheduler().changeStrategy(SelectionPolicy.SHORTEST_TIME);

                updateQueues(sm.toString());
            }
        });

        JPanel radioPanel = new JPanel(new FlowLayout());
        radioPanel.add(shortestQueueRadioButton);
        radioPanel.add(shortestTimeRadioButton);
        inputPanel.add(radioPanel);

        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        setContentPane(inputPanel);
        JLabel lblNewLabel_7 = new JLabel("Simulation Time: ");
        inputPanel.add(lblNewLabel_7,c);
        c.gridy++;
        timeLabel = new JLabel("0");
        inputPanel.add(timeLabel,c);

        setLocationRelativeTo(null);

        setVisible(true);
    }

    public void updateQueues(String queuesText) {
        synchronized (this) {
            queues.setText(queuesText);
        }
    }

    public static void main(String[] args) {
        View frame = new View();
        frame.setVisible(true);
    }
}