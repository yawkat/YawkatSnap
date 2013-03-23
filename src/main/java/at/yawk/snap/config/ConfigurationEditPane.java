package at.yawk.snap.config;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import at.yawk.snap.SnapConfig;

public class ConfigurationEditPane extends JPanel {
    public static void main(String[] args) throws IOException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        final JFrame jframe = new JFrame("Config");
        // jframe.setResizable(false);
        jframe.add(new ConfigurationEditPane(new SnapConfig(new File("config.properties"))));
        jframe.pack();
        jframe.setLocationRelativeTo(null);
        jframe.setVisible(true);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    private static final long serialVersionUID = 1L;
    
    public ConfigurationEditPane(final SnapConfig config) {
        final SpringLayout layout = new SpringLayout();
        setLayout(layout);
        
        // Target URL
        final JLabel targetUrlLabel = new JLabel("Target URL:");
        add(targetUrlLabel);
        final JTextField targetUrlField = new JTextField(config.getTargetUrl());
        add(targetUrlField);
        targetUrlField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent arg0) {
                final Document doc = arg0.getDocument();
                try {
                    config.setTargetUrl(doc.getText(0, doc.getLength()));
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
            
            @Override
            public void insertUpdate(DocumentEvent arg0) {
                final Document doc = arg0.getDocument();
                try {
                    config.setTargetUrl(doc.getText(0, doc.getLength()));
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
            
            @Override
            public void changedUpdate(DocumentEvent arg0) {
                final Document doc = arg0.getDocument();
                try {
                    config.setTargetUrl(doc.getText(0, doc.getLength()));
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        });
        targetUrlLabel.setLabelFor(targetUrlField);
        layout.putConstraint(SpringLayout.WEST, targetUrlField, 5, SpringLayout.EAST, targetUrlLabel);
        layout.putConstraint(SpringLayout.EAST, this, 5, SpringLayout.EAST, targetUrlField);
        layout.putConstraint(SpringLayout.WEST, targetUrlLabel, 5, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, targetUrlField, 5, SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.NORTH, targetUrlLabel, 3, SpringLayout.NORTH, targetUrlField);
        
        // Autorun
        final JLabel autoRunLabel = new JLabel("Start with Windows:");
        add(autoRunLabel);
        final JCheckBox autoRunBox = new JCheckBox();
        autoRunBox.setAction(new AbstractAction() {
            private static final long serialVersionUID = 1L;
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                config.setAutostart(autoRunBox.isSelected());
                autoRunBox.setSelected(config.isAutostart());
            }
        });
        add(autoRunBox);
        autoRunLabel.setLabelFor(autoRunBox);
        layout.putConstraint(SpringLayout.WEST, autoRunBox, 5, SpringLayout.EAST, autoRunLabel);
        layout.putConstraint(SpringLayout.WEST, targetUrlField, 5, SpringLayout.EAST, autoRunLabel);
        layout.putConstraint(SpringLayout.WEST, autoRunLabel, 5, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, autoRunBox, 5, SpringLayout.SOUTH, targetUrlField);
        layout.putConstraint(SpringLayout.NORTH, autoRunLabel, 4, SpringLayout.NORTH, autoRunBox);
        
        // Saving
        final SaveTargetConfigPane targetConfigPanel = new SaveTargetConfigPane(config);
        add(targetConfigPanel);
        layout.putConstraint(SpringLayout.NORTH, targetConfigPanel, 5, SpringLayout.SOUTH, autoRunLabel);
        layout.putConstraint(SpringLayout.WEST, targetConfigPanel, 5, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.EAST, targetConfigPanel, -5, SpringLayout.EAST, this);
        layout.putConstraint(SpringLayout.SOUTH, this, 5, SpringLayout.SOUTH, targetConfigPanel);
        validate();
    }
}
