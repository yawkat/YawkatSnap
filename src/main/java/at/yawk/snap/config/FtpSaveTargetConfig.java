package at.yawk.snap.config;

import java.awt.Component;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import at.yawk.snap.FtpSaveTarget;
import at.yawk.snap.SnapConfig;

public class FtpSaveTargetConfig extends SaveTargetConfig<FtpSaveTarget> {
    private static final long serialVersionUID = 1L;
    
    private Component lastLabel;
    private Component lastEntry;
    
    public FtpSaveTargetConfig(SnapConfig config) {
        setLayout(new SpringLayout());
        addEntry(config, "save.ftp.host", false, "Host");
        addEntry(config, "save.ftp.port", true, "Port");
        ((SpringLayout) getLayout()).putConstraint(SpringLayout.SOUTH, this, 5, SpringLayout.SOUTH, lastEntry);
    }
    
    private void addEntry(final SnapConfig config, final String valueKey, boolean intOnly, String name) {
        final JLabel label = new JLabel(name);
        final JTextField textField = new JTextField(config.getProperties().getProperty(valueKey));
        if (intOnly) {
            textField.setInputVerifier(new InputVerifier() {
                @Override
                public boolean verify(JComponent arg0) {
                    return ((JTextField) arg0).getText().matches("[^0-9]{0,9}");
                }
            });
        }
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent arg0) {
                config.getProperties().setProperty(valueKey, textField.getText());
            }
            
            @Override
            public void insertUpdate(DocumentEvent arg0) {
                config.getProperties().setProperty(valueKey, textField.getText());
            }
            
            @Override
            public void changedUpdate(DocumentEvent arg0) {
                config.getProperties().setProperty(valueKey, textField.getText());
            }
        });
        add(label);
        add(textField);
        ((SpringLayout) getLayout()).putConstraint(SpringLayout.WEST, label, 5, SpringLayout.WEST, this);
        ((SpringLayout) getLayout()).putConstraint(SpringLayout.WEST, textField, 5, SpringLayout.EAST, label);
        ((SpringLayout) getLayout()).putConstraint(SpringLayout.NORTH, label, 2, SpringLayout.NORTH, textField);
        if (lastEntry != null) {
            ((SpringLayout) getLayout()).putConstraint(SpringLayout.NORTH, textField, 5, SpringLayout.SOUTH, lastEntry);
        } else {
            ((SpringLayout) getLayout()).putConstraint(SpringLayout.NORTH, textField, 5, SpringLayout.NORTH, this);
        }
        this.lastLabel = label;
        this.lastEntry = textField;
    }
}
