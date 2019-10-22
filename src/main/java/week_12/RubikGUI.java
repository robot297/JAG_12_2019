package week_12;

import javax.swing.*;

public class RubikGUI extends JFrame {
    
    private JPanel mainPanel;
    
    
    // TODO configure and implement functionality in your GUI.
    
    
    
    public RubikGUI(Rubik rubikProgram) {
    
        setContentPane(mainPanel);
        pack();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        // TODO configure and implement functionality in your GUI.
        
    }
    
    
    
    
    
    // Use these methods to show dialogs. Or your tests may time out.
    
    
    /* This will return
    *
    * JOptionPanel.YES_OPTION if the user clicks 'yes'
    * JOptionPanel.NO_OPTION if the user clicks 'no'.
    *
    *
    * You can call this method and check for the return type, e.g.
    *
    * if (showYesNoDialog("Delete this solver?") == JOptionPane.YES_OPTION) {
    *     // User clicked yes, so go ahead and delete
    * } else {
    *     // User did not click yes.
    * }
    * */
    protected int showYesNoDialog(String message) {
        return JOptionPane.showConfirmDialog(this, message, null, JOptionPane.YES_NO_OPTION);
    }
    
    
    
    /*
    *  Call this method to show an alert/message dialog with the message provided.
    * */
    protected void showAlertDialog(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
    
    
}
