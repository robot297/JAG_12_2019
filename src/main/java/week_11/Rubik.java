package week_11;

class Rubik {
    
    RubikGUI gui;
    
    public static void main(String[] args) {
        Rubik rubikProgram = new Rubik();
        rubikProgram.start();
    }
    
    
    public void start() {
        gui = new RubikGUI(this);
    }
    
    
}