package ru.krlvm.powertunnel.frames;

import ru.krlvm.powertunnel.PowerTunnel;
import ru.krlvm.powertunnel.data.DataStoreException;
import ru.krlvm.powertunnel.utilities.UIUtility;
import ru.krlvm.powertunnel.utilities.Utility;
import ru.krlvm.swingdpi.SwingDPI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;

public class MainFrame extends ControlFrame {

    private JLabel header;
    private JButton stateButton;
    private JTextField[] inputs;

    public MainFrame() {
        super(PowerTunnel.NAME + " v" + PowerTunnel.VERSION);
        setSize(500, 250);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        header = new JLabel(getHeaderText());

        final JTextField ipInput = new JTextField();
        ipInput.setPreferredSize(SwingDPI.scale(200, 22));
        ipInput.setToolTipText("IP Address");
        ipInput.setText(String.valueOf(PowerTunnel.SERVER_IP_ADDRESS));

        final JTextField portInput = new JTextField();
        portInput.setPreferredSize(SwingDPI.scale(75, 22));
        portInput.setToolTipText("Port");
        portInput.setText(String.valueOf(PowerTunnel.SERVER_PORT));

        inputs = new JTextField[] { ipInput, portInput };

        stateButton = new JButton("Start server");
        stateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(PowerTunnel.isRunning()) {
                    PowerTunnel.stopServer();
                } else {
                    try {
                        PowerTunnel.SERVER_IP_ADDRESS = ipInput.getText();
                        PowerTunnel.SERVER_PORT = Integer.parseInt(portInput.getText());
                        try {
                            PowerTunnel.bootstrap();
                        } catch (UnknownHostException ex) {
                            JOptionPane.showMessageDialog(MainFrame.this, "Cannot use IP Address '" + PowerTunnel.SERVER_IP_ADDRESS,
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            Utility.print("[?] Cannot use IP-Address '%s': %s", PowerTunnel.SERVER_IP_ADDRESS, ex.getMessage());
                            ex.printStackTrace();
                            System.out.println();
                            Utility.print("[!] Program halted");
                        } catch (DataStoreException ex) {
                            JOptionPane.showMessageDialog(MainFrame.this, "Failed to load data store: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                            Utility.print("[x] Failed to load data store: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(MainFrame.this, "Invalid port",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        JButton logButton = new JButton("Logs");
        logButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PowerTunnel.logFrame.setVisible(true);
            }
        });

        JButton journalButton = new JButton("Journal");
        journalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PowerTunnel.journalFrame.setVisible(true);
            }
        });

        JButton userBlacklist = new JButton("Blacklist");
        userBlacklist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PowerTunnel.USER_FRAMES[0].setVisible(true);
            }
        });

        JButton userWhitelist = new JButton("Whitelist");
        userWhitelist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PowerTunnel.USER_FRAMES[1].setVisible(true);
            }
        });

        JPanel mainPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        setContentPane(mainPanel);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.add(header);
        mainPanel.add(panel, "First");

        panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.add(ipInput);
        panel.add(portInput);
        mainPanel.add(panel, "Center");

        panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.add(stateButton);
        mainPanel.add(panel, "Center");

        panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.add(logButton);
        panel.add(journalButton);
        panel.add(userBlacklist);
        panel.add(userWhitelist);
        mainPanel.add(panel, "Last");

        panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.add(UIUtility.getLabelWithHyperlinkSupport("<b><a href=\"" + PowerTunnel.REPOSITORY_URL + "\">" + PowerTunnel.REPOSITORY_URL + "</a></b><br>(c) krlvm, 2019", "text-align: center"));
        //panel.add(new JLabel(getCenteredLabel("<b>https://github.com/krlvm/PowerTunnel</b><br>(c) krlvm, 2019")));
        mainPanel.add(panel, "Last");

        getRootPane().setDefaultButton(stateButton);
        setResizable(false);
        controlFrameInitialized();
        setVisible(true);
    }

    public void update() {
        boolean running = PowerTunnel.isRunning();
        stateButton.setText((running ? "Stop" : "Start") + " server");
        for (JTextField input : inputs) {
            input.setEnabled(!running);
        }
        header.setText(getHeaderText());
    }

    private String getHeaderText() {
        return getCenteredLabel("<b>" + PowerTunnel.NAME + " v" + PowerTunnel.VERSION + "</b><br>Server is" + (PowerTunnel.isRunning() ? " running" : "n't running") + "</div></html>");
    }

    private String getCenteredLabel(String text) {
        return "<html><div style='text-align: center;'>" + text + "</div></html>";
    }
}
