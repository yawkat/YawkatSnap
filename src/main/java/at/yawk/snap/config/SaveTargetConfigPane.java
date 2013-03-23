package at.yawk.snap.config;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SpringLayout;
import javax.swing.border.EtchedBorder;

import at.yawk.snap.FtpSaveTarget;
import at.yawk.snap.SaveTarget;
import at.yawk.snap.SaveTargets;
import at.yawk.snap.SnapConfig;

public class SaveTargetConfigPane extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private static final Map<Class<? extends SaveTarget>, Class<? extends SaveTargetConfig<?>>> configTypes = new HashMap<Class<? extends SaveTarget>, Class<? extends SaveTargetConfig<?>>>(4);
    
    static {
        configTypes.put(FtpSaveTarget.class, FtpSaveTargetConfig.class);
    }
    
    private JPanel specificConfigPanel;
    
    public SaveTargetConfigPane(SnapConfig snapConfig) {
        final SpringLayout layout = new SpringLayout();
        setLayout(layout);
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Save target"));
        final JComboBox<Class<? extends SaveTarget>> saveTargetTypes = new JComboBox<Class<? extends SaveTarget>>();
        saveTargetTypes.setSelectedItem(snapConfig.getSaveTarget().getClass());
        add(saveTargetTypes);
        saveTargetTypes.addItem(FtpSaveTarget.class);
        saveTargetTypes.setRenderer(new ListCellRenderer<Class<? extends SaveTarget>>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends Class<? extends SaveTarget>> list, Class<? extends SaveTarget> value, int index, boolean isSelected, boolean cellHasFocus) {
                return new JLabel(SaveTargets.getSaveTargetType(value));
            }
        });
        layout.putConstraint(SpringLayout.WEST, saveTargetTypes, 5, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.EAST, saveTargetTypes, -5, SpringLayout.EAST, this);
        
        updateSpecificConfig(snapConfig);
    }
    
    private void updateSpecificConfig(SnapConfig config) {
        if (specificConfigPanel != null) {
            remove(specificConfigPanel);
        }
        try {
            specificConfigPanel = configTypes.get(config.getSaveTarget().getClass()).getConstructor(config.getClass()).newInstance(config);
            add(specificConfigPanel);
            ((SpringLayout) getLayout()).putConstraint(SpringLayout.SOUTH, this, 5, SpringLayout.SOUTH, specificConfigPanel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
