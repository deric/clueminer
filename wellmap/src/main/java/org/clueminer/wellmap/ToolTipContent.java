package org.clueminer.wellmap;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Tomas Barton
 */
public class ToolTipContent extends JPanel {

    private static final long serialVersionUID = 5130456589140462562L;
    
    public ToolTipContent(){
        initialize();
    }
    
    private void initialize(){
        add(new JLabel("no well selected"));
    }
}
