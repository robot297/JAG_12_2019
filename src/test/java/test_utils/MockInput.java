package test_utils;

import java.io.ByteArrayInputStream;


public class MockInput {
    
    
    //Helper methods to provide int input to the program
    public static void setInputs(int... inputs) {
        
        String input = "";
        for (Integer i : inputs) {
            input += i + "\n";
        }
    
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
    }
    
    //Helper method to provide String input to the program
    public static void setInputs(String... inputs) {
        
        String input = "";
        for (String i : inputs) {
            input += i + "\n";
        }
        
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
    }
}
