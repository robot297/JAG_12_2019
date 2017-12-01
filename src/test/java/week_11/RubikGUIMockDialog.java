package week_11;


/*

Override the dialog methods in RubikGUI class, otherwise behave exactly as student code.
 */


class RubikGUIMockDialog extends RubikGUI {
    
    RubikGUIMockDialog(Rubik r){ super(r);}
    
    private boolean wasAlertCalled = false;
    
    protected boolean checkAlertWasCalled() {
        boolean copyToReturn = wasAlertCalled;
        wasAlertCalled = false;
        return copyToReturn;
    }
    
    @Override
    protected void showAlertDialog(String msg){
        // do nothing so the program does not show an alert dialog
        wasAlertCalled = true;
    }
    
    // Set this to force showInputDialog to return a particular value. Do this before the inputDialog is expected to be called.
    int mockYesNo;
    
    
    @Override
    protected int showYesNoDialog(String msg) {
        return mockYesNo;
    }
    
}

