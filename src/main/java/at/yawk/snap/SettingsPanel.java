package at.yawk.snap;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;

public class SettingsPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTextField ftpHost;
    private JTextField ftpUsername;
    private JTextField ftpDirectory;
    private JTextField ftpFilename;
    private JTextField idRegex;
    private JTextField clipboard;
    private JLabel lblClipboardLink;
    private JPasswordField ftpPassword;
    private JSpinner ftpPort;
    private JComboBox<String> imageFormat;
    
    private final Runnable cancelCallback;
    private final Runnable saveCallback;
    private final SnapConfig config;
    
    /**
     * Create the panel.
     */
    public SettingsPanel(Runnable cancelCallback, Runnable saveCallback, SnapConfig config) {
        this.cancelCallback = cancelCallback;
        this.saveCallback = saveCallback;
        this.config = config;
        
        setMinimumSize(new Dimension(250, 278));
        setPreferredSize(getMinimumSize());
        SpringLayout springLayout = new SpringLayout();
        setLayout(springLayout);
        
        ftpHost = new JTextField();
        ftpHost.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        ftpHost.setText(config.getProperties().getProperty("save.ftp.host"));
        springLayout.putConstraint(SpringLayout.NORTH, ftpHost, 37, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.WEST, ftpHost, 119, SpringLayout.WEST, this);
        springLayout.putConstraint(SpringLayout.EAST, ftpHost, -10, SpringLayout.EAST, this);
        add(ftpHost);
        ftpHost.setColumns(64);
        
        JLabel lblFtpHost = new JLabel("FTP Host");
        springLayout.putConstraint(SpringLayout.NORTH, lblFtpHost, 3, SpringLayout.NORTH, ftpHost);
        springLayout.putConstraint(SpringLayout.WEST, lblFtpHost, 10, SpringLayout.WEST, this);
        add(lblFtpHost);
        
        JLabel lblFtpPort = new JLabel("FTP Port");
        springLayout.putConstraint(SpringLayout.NORTH, lblFtpPort, 66, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.WEST, lblFtpPort, 10, SpringLayout.WEST, this);
        add(lblFtpPort);
        
        JLabel lblFtpUsername = new JLabel("FTP Username");
        springLayout.putConstraint(SpringLayout.WEST, lblFtpUsername, 10, SpringLayout.WEST, this);
        add(lblFtpUsername);
        
        JLabel lblFtpPassword = new JLabel("FTP Password");
        springLayout.putConstraint(SpringLayout.WEST, lblFtpPassword, 10, SpringLayout.WEST, this);
        add(lblFtpPassword);
        
        JLabel lblFtpDirectory = new JLabel("FTP Directory");
        springLayout.putConstraint(SpringLayout.WEST, lblFtpDirectory, 10, SpringLayout.WEST, this);
        springLayout.putConstraint(SpringLayout.SOUTH, lblFtpPassword, -11, SpringLayout.NORTH, lblFtpDirectory);
        add(lblFtpDirectory);
        
        JLabel lblFtpFileName = new JLabel("FTP File name");
        springLayout.putConstraint(SpringLayout.WEST, lblFtpFileName, 10, SpringLayout.WEST, this);
        add(lblFtpFileName);
        
        ftpUsername = new JTextField();
        ftpUsername.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        ftpUsername.setText(config.getProperties().getProperty("save.ftp.username"));
        springLayout.putConstraint(SpringLayout.NORTH, ftpUsername, 32, SpringLayout.SOUTH, ftpHost);
        springLayout.putConstraint(SpringLayout.NORTH, lblFtpUsername, 3, SpringLayout.NORTH, ftpUsername);
        springLayout.putConstraint(SpringLayout.WEST, ftpUsername, 0, SpringLayout.WEST, ftpHost);
        springLayout.putConstraint(SpringLayout.EAST, ftpUsername, 0, SpringLayout.EAST, ftpHost);
        ftpUsername.setColumns(64);
        add(ftpUsername);
        
        ftpDirectory = new JTextField();
        ftpDirectory.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        ftpDirectory.setToolTipText("should end with /");
        ftpDirectory.setText(config.getProperties().getProperty("save.ftp.directory"));
        springLayout.putConstraint(SpringLayout.NORTH, lblFtpDirectory, 3, SpringLayout.NORTH, ftpDirectory);
        springLayout.putConstraint(SpringLayout.WEST, ftpDirectory, 0, SpringLayout.WEST, ftpHost);
        springLayout.putConstraint(SpringLayout.EAST, ftpDirectory, 0, SpringLayout.EAST, ftpHost);
        ftpDirectory.setColumns(128);
        add(ftpDirectory);
        
        ftpFilename = new JTextField();
        ftpFilename.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        ftpFilename.setToolTipText("can contain %id placeholder for generated ID");
        ftpFilename.setText(config.getProperties().getProperty("save.ftp.filename"));
        springLayout.putConstraint(SpringLayout.NORTH, ftpFilename, 166, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.SOUTH, ftpDirectory, -6, SpringLayout.NORTH, ftpFilename);
        springLayout.putConstraint(SpringLayout.NORTH, lblFtpFileName, 3, SpringLayout.NORTH, ftpFilename);
        springLayout.putConstraint(SpringLayout.WEST, ftpFilename, 0, SpringLayout.WEST, ftpHost);
        springLayout.putConstraint(SpringLayout.EAST, ftpFilename, 0, SpringLayout.EAST, ftpHost);
        ftpFilename.setColumns(10);
        add(ftpFilename);
        
        idRegex = new JTextField();
        springLayout.putConstraint(SpringLayout.WEST, idRegex, 119, SpringLayout.WEST, this);
        idRegex.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        idRegex.setText(config.getProperties().getProperty("idgen.regex.regex"));
        springLayout.putConstraint(SpringLayout.SOUTH, idRegex, -7, SpringLayout.NORTH, ftpHost);
        springLayout.putConstraint(SpringLayout.EAST, idRegex, -10, SpringLayout.EAST, this);
        idRegex.setColumns(30);
        add(idRegex);
        
        JLabel lblIdRegex = new JLabel("ID Regex");
        springLayout.putConstraint(SpringLayout.NORTH, lblIdRegex, 3, SpringLayout.NORTH, idRegex);
        springLayout.putConstraint(SpringLayout.WEST, lblIdRegex, 10, SpringLayout.WEST, this);
        add(lblIdRegex);
        
        clipboard = new JTextField();
        springLayout.putConstraint(SpringLayout.NORTH, clipboard, 31, SpringLayout.SOUTH, ftpFilename);
        springLayout.putConstraint(SpringLayout.WEST, clipboard, 109, SpringLayout.WEST, lblFtpHost);
        springLayout.putConstraint(SpringLayout.EAST, clipboard, 0, SpringLayout.EAST, ftpHost);
        clipboard.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        clipboard.setText(config.getProperties().getProperty("clipboard"));
        clipboard.setToolTipText("can contain %id placeholder for generated ID");
        add(clipboard);
        clipboard.setColumns(128);
        
        lblClipboardLink = new JLabel("Clipboard Link");
        springLayout.putConstraint(SpringLayout.NORTH, lblClipboardLink, 37, SpringLayout.SOUTH, lblFtpFileName);
        springLayout.putConstraint(SpringLayout.WEST, lblClipboardLink, 0, SpringLayout.WEST, lblFtpHost);
        add(lblClipboardLink);
        
        ftpPort = new JSpinner();
        ftpPort.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        ftpPort.setModel(new SpinnerNumberModel(Integer.parseInt(config.getProperties().getProperty("save.ftp.port")), 1, 65535, 1));
        springLayout.putConstraint(SpringLayout.NORTH, ftpPort, 6, SpringLayout.SOUTH, ftpHost);
        springLayout.putConstraint(SpringLayout.WEST, ftpPort, 0, SpringLayout.WEST, ftpHost);
        springLayout.putConstraint(SpringLayout.EAST, ftpPort, 0, SpringLayout.EAST, ftpHost);
        add(ftpPort);
        
        ftpPassword = new JPasswordField();
        ftpPassword.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        springLayout.putConstraint(SpringLayout.NORTH, ftpPassword, -3, SpringLayout.NORTH, lblFtpPassword);
        springLayout.putConstraint(SpringLayout.WEST, ftpPassword, 0, SpringLayout.WEST, ftpHost);
        springLayout.putConstraint(SpringLayout.EAST, ftpPassword, 0, SpringLayout.EAST, ftpHost);
        ftpPassword.setText(config.getProperties().getProperty("save.ftp.password"));
        add(ftpPassword);
        
        JButton btnCancel = new JButton("Cancel");
        springLayout.putConstraint(SpringLayout.NORTH, btnCancel, 6, SpringLayout.SOUTH, clipboard);
        springLayout.putConstraint(SpringLayout.EAST, btnCancel, 0, SpringLayout.EAST, ftpHost);
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancel();
            }
        });
        add(btnCancel);
        
        JButton btnSave = new JButton("Save");
        springLayout.putConstraint(SpringLayout.WEST, btnSave, 0, SpringLayout.WEST, lblFtpHost);
        springLayout.putConstraint(SpringLayout.NORTH, btnSave, 243, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.EAST, btnSave, -6, SpringLayout.WEST, btnCancel);
        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });
        add(btnSave);
        
        imageFormat = new JComboBox<String>();
        imageFormat.setModel(new DefaultComboBoxModel<String>(new String[] { "PNG", "JPEG", "GIF", "BMP" }));
        imageFormat.setSelectedItem(config.getProperties().getProperty("save.ftp.filetype"));
        springLayout.putConstraint(SpringLayout.WEST, imageFormat, 0, SpringLayout.WEST, ftpHost);
        springLayout.putConstraint(SpringLayout.SOUTH, imageFormat, -5, SpringLayout.NORTH, clipboard);
        springLayout.putConstraint(SpringLayout.EAST, imageFormat, 0, SpringLayout.EAST, ftpHost);
        add(imageFormat);
        
        JLabel lblImageFormat = new JLabel("Image format");
        springLayout.putConstraint(SpringLayout.WEST, lblImageFormat, 0, SpringLayout.WEST, lblFtpHost);
        springLayout.putConstraint(SpringLayout.SOUTH, lblImageFormat, -11, SpringLayout.NORTH, lblClipboardLink);
        add(lblImageFormat);
    }
    
    private void save() {
        config.setIdGenerator(new RegexIdGenerator(idRegex.getText()));
        Properties ftpProperties = config.getProperties();
        ftpProperties.setProperty("save.ftp.host", ftpHost.getText());
        ftpProperties.setProperty("save.ftp.port", ftpPort.getValue().toString());
        ftpProperties.setProperty("save.ftp.username", ftpUsername.getText());
        ftpProperties.setProperty("save.ftp.password", new String(ftpPassword.getPassword()));
        ftpProperties.setProperty("save.ftp.directory", ftpDirectory.getText());
        ftpProperties.setProperty("save.ftp.filename", ftpFilename.getText());
        ftpProperties.setProperty("save.ftp.filetype", imageFormat.getSelectedItem().toString());
        config.setSaveTarget(new FtpSaveTarget(ftpProperties));
        config.setTargetUrl(clipboard.getText());
        saveCallback.run();
    }
    
    private void cancel() {
        cancelCallback.run();
    }
}
