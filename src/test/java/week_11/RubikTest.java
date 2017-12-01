package week_11;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.lang.reflect.Field;
import java.sql.*;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

/**
 * Created by clara on 9/18/17.
 */
public class RubikTest {
    
    private static final String ANY = "ANY_VALUE_IS_OK";
    private JTable solversTable;
    
    private JTextField newCubeSolverNameText;
    private JTextField newCubeSolverTimeText;
    
    private JButton addNewSolverButton;
    
    private JButton updateTimeButton;
    
    private JTextField updateTimeText;
    private JLabel updateSolverNameLabel;
    
    private JButton deleteSolverButton;
    
    private String testDatabaseURL = "jdbc:sqlite://rubik_test";
    private String developmentDatabaseURK = "jdbc:sqlite://rubik.db";
    
    
    private Rubik rubikProgram;
    
    private RubikGUIMockDialog gui;
    
    private final int timeout = 3000;
    
    @Before
    public void setUp() throws Exception {
        
        rubikProgram = new Rubik();
        
        // Replace database with test DB
        DBConfig.db_url = testDatabaseURL;
        
        deleteTestData();   // remove all data from test DB
        addExampleData();
        
        // Find all the expected GUI components
        gui = new RubikGUIMockDialog(rubikProgram);
        rubikProgram.gui = gui;
        
        solversTable = (JTable) getField(gui, "solversTable");
        newCubeSolverNameText = (JTextField) getField(gui, "newCubeSolverNameText");
        newCubeSolverTimeText = (JTextField) getField(gui, "newCubeSolverTimeText");
        addNewSolverButton = (JButton) getField(gui, "addNewSolverButton");
        updateTimeButton = (JButton) getField(gui, "updateTimeButton");
        updateTimeText = (JTextField) getField(gui, "updateTimeText");
        updateSolverNameLabel = (JLabel) getField(gui, "updateSolverNameLabel");
        deleteSolverButton = (JButton) getField(gui, "deleteSolverButton");
        
    }
    
    @After
    public void deleteAll() {
        deleteTestData();
    }
    
    
    private void deleteTestData() {
        try ( Connection con = DriverManager.getConnection(testDatabaseURL) ) {
            String sql = "DELETE FROM cube_records";
            Statement statement = con.createStatement();
            statement.execute(sql);
            
            statement.close();  con.close();
            
        } catch (SQLException e) {
            fail("SQLException deleting data from test database." + e.getMessage());
        }
        
    }
    
    
    private void addExampleData() {
        try ( Connection con = DriverManager.getConnection(testDatabaseURL) ) {
            String sql = "INSERT INTO cube_records (solver_name, time_seconds) values (?, ?)";
            
            PreparedStatement statement = con.prepareStatement(sql);
            
            statement.setString(1, "Cubestormer II");
            statement.setDouble(2, 3.253);
            statement.execute();
            
            statement.setString(1, "Patrick Ponce");
            statement.setDouble(2, 4.69);
            statement.execute();
            
            statement.setString(1, "Sub1");
            statement.setDouble(2, 0.637);
            statement.execute();
            
            statement.close(); con.close();
            
        } catch (SQLException e) {
            fail("SQLException deleting data from test database." + e.getMessage());
        }
        
    }
    
    
    private Object getField(RubikGUI gui, String field) {
        
        try {
            Field f = RubikGUI.class.getDeclaredField(field);
            f.setAccessible(true);
            return f.get(gui);
        } catch (NoSuchFieldException ne) {
            fail("Expecting to find a component called " + field + " ." + ne.getMessage());
        } catch (IllegalAccessException ie) {
            fail(ie.getMessage());
        }
        
        return null;
        
    }
    
    
    
    @Test(timeout = timeout)
    public void testTableRowSelection() {
        assertEquals("Table should be single row selection", solversTable.getSelectionModel().getSelectionMode(), ListSelectionModel.SINGLE_SELECTION);
    }
    
    @Test(timeout = timeout)
    public void testTableSetupWithTestData() {
        
        // Expect data in table
        
        String[] names = { "Sub1", "Cubestormer II", "Patrick Ponce" };
        String[] times = { "0.637", "3.253", "4.69" };
        
        // Should be three rows and three columns
        assertEquals("If there are three rows of test data in the database, JTable should have three rows", 3, solversTable.getModel().getRowCount());
        assertEquals("For test data, table should have two columns for solver_name, and time_seconds", 2, solversTable.getModel().getColumnCount());
        
        for (int n = 0 ; n < names.length ; n++) {
            //Read first column
            String found = solversTable.getModel().getValueAt(n, 0).toString();
            assertEquals("Expected " + names[n] + " but found " + found,
                    names[n], found);
        }
        
        for (int n = 0 ; n < times.length ; n++) {
            // And second column
            String found = solversTable.getModel().getValueAt(n, 1).toString();
            assertEquals("Expected " + times[n] + " but found " + found,
                    times[n], found);
        }
    }
    
    
    @Test(timeout = timeout)
    public void testAddNewSolver() {
        
        deleteTestData();   // Start with empty database
        ((RubikModel)solversTable.getModel()).fireTableDataChanged();
        
        // Add new solver
        
        newCubeSolverNameText.setText("Cat");
        newCubeSolverTimeText.setText("12345.41");
        addNewSolverButton.doClick();
        
        // Expected data
        String[][] expected = {
                {ANY, "Cat", "12345.41"}
        };
        
        
        // Should be in table
        checkMatchesGUI(solversTable, expected);
        
        // Add another new solver
        
        newCubeSolverNameText.setText("Raccoon");
        newCubeSolverTimeText.setText("4000.23");
        addNewSolverButton.doClick();
        
        // Expected data
        String[][] expected_2_rows = {
                {ANY, "Raccoon", "4000.23"},
                {ANY, "Cat", "12345.41"},
        };
        
        // Should be in table
        checkMatchesGUI(solversTable, expected_2_rows);
        
        // Add 3 new rows
        
        newCubeSolverNameText.setText("Velociraptor");
        newCubeSolverTimeText.setText("9999999.99");
        addNewSolverButton.doClick();
        
        // Expected data
        String[][] expected_3_rows = {
                {ANY, "Raccoon", "4000.23"},
                {ANY, "Cat", "12345.41"},
                {ANY, "Velociraptor", "9999999.99"}
        };
        
        // Should be in table
        checkMatchesGUI(solversTable, expected_3_rows);
        
        // Various invalid data. Table should not change.
        
        useInvalidInput("", "1234", expected_3_rows);
        useInvalidInput("giraffe", "", expected_3_rows);
        useInvalidInput("", "", expected_3_rows);
        useInvalidInput("snake", "0", expected_3_rows);
        useInvalidInput("porcupine", "234.234.2345", expected_3_rows);
        useInvalidInput("fox", "-9", expected_3_rows);
        
    }
    
    
    
    @Test(timeout = timeout)
    public void testEditSolverTimeValid() {
        
        // Select 2nd row
        solversTable.setRowSelectionInterval(1, 1);
        // should be 2nd solver's data in GUI: "Cubestormer II", 3.253
        assertEquals(updateSolverNameLabel.getText(), "Cubestormer II");
        assertEquals(updateTimeText.getText(), "3.253");
        
        // Change time
        
        updateTimeText.setText("2.45");
        updateTimeButton.doClick();
        
        // Table should update
        
        String[][] expected = {
                {ANY, "Sub1", "0.637"},
                {ANY, "Cubestormer II", "2.45"},
                {ANY, "Patrick Ponce", "4.69"}
        };
        
        verifyData(solversTable, expected);
        
        // Select first row
        
        solversTable.setRowSelectionInterval(0, 0);
        assertEquals(updateSolverNameLabel.getText(), "Sub1");
        assertEquals(updateTimeText.getText(), "0.637");
        
        updateTimeText.setText("30.2");
        updateTimeButton.doClick();
        
        String[][] expected_move = {
                {ANY, "Cubestormer II", "2.45"},
                {ANY, "Patrick Ponce", "4.69"},
                {ANY, "Sub1", "30.2"},
        };
        
        verifyData(solversTable, expected_move);
        
    }
    
    
    @Test(timeout = timeout)
    public void testEditSolverTimeInvalid() {
        
        // Invalid data. Should see alert.
        
        solversTable.setRowSelectionInterval(0, 0);
        assertEquals(updateSolverNameLabel.getText(), "Sub1");
        assertEquals(updateTimeText.getText(), "0.637");
        
        updateTimeText.setText("pizza");
        updateTimeButton.doClick();
        assertTrue("Show an alert dialog if user enters an invalid time.", gui.checkAlertWasCalled());
        
        String[][] expected_no_change = {
                {ANY, "Sub1", "0.637"},
                {ANY, "Cubestormer II", "3.253"},
                {ANY, "Patrick Ponce", "4.69"}
        };
        
        verifyData(solversTable, expected_no_change);
        
        
        // Negative time. Should see alert.
        solversTable.setRowSelectionInterval(0, 0);
        
        updateTimeText.setText("-10");
        updateTimeButton.doClick();
        assertTrue("Show an alert dialog if user enters an invalid time.", gui.checkAlertWasCalled());
        
        verifyData(solversTable, expected_no_change);
        
    }
    
    
    
    @Test(timeout = timeout)
    public void testDeleteSolver() {
        
        // Select 2nd row, Cubestormer II
        solversTable.setRowSelectionInterval(1, 1);
        
        gui.mockYesNo = JOptionPane.YES_OPTION;
        
        deleteSolverButton.doClick();
        
        // Table should update
        
        String[][] expected = {
                {ANY, "Sub1", "0.637" },
                {ANY, "Patrick Ponce", "4.69" }
        } ;
        
        verifyData(solversTable, expected);
        
        
        // Select first row
        solversTable.setRowSelectionInterval(0, 0);
        
        gui.mockYesNo = JOptionPane.YES_OPTION;
        
        deleteSolverButton.doClick();
        
        // Table should update
        
        String[][] expected_2 = {
                {ANY, "Patrick Ponce", "4.69" }
        } ;
        
        verifyData(solversTable, expected_2);
        
        
        // Select first row
        solversTable.setRowSelectionInterval(0, 0);
        
        gui.mockYesNo = JOptionPane.YES_OPTION;
        
        deleteSolverButton.doClick();
        
        // Table should update
        
        String[][] expected_3 = {
        } ;
        
        verifyData(solversTable, expected_3);
        
        
    }
    
    
    
    private void useInvalidInput(String name, String time, String[][] expected) {
        
        newCubeSolverNameText.setText(name);
        newCubeSolverTimeText.setText(time);
        addNewSolverButton.doClick();
        
        // Should NOT be in table
        
        checkMatchesGUI(solversTable, expected);
        
        assertTrue("Show an alert dialog if invalid data is entered for a new cube solver", gui.checkAlertWasCalled());
    }
    
    
    private void verifyData(JTable table, String[][] expected) {
        checkMatchesDB(expected);
        checkMatchesGUI(table, expected);
    }
    
    
    private void checkMatchesDB(String[][] expected) {
        
        try (  Connection con = DriverManager.getConnection(testDatabaseURL) ) {
            String sql = "SELECT * FROM cube_records order by time_seconds";
            
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            
            int rowCounter = 0;
            
            while (rs.next()) {
                
                String name = rs.getString(2);
                String time = Double.toString(rs.getDouble(3));
                
                assertEquals(name, expected[rowCounter][1]);
                assertEquals(time, expected[rowCounter][2]);
                
                rowCounter++;
                
            }
            
            assertTrue("Database does not contain the expected number of rows", rowCounter == expected.length);  // more rows expected than DB returned?
            
            statement.close(); con.close();
            
        } catch (SQLException e) {
            fail("SQLException deleting data from test database." + e.getMessage());
        }
    }
    
    
    private void checkMatchesGUI(JTable solversTable, String[][] expected) {
        
        // Same number of rows?
        assertEquals("The number of rows in the database does not match the number of rows in the JTable", expected.length, solversTable.getRowCount());
        
        // Check data in database matches JTable.
        String msg = "Data at row %s %s was expected to be %s but found %s";
        
        for (int row = 0 ; row < expected.length ; row++) {
            
            String[] theRow = expected[row];
            
            // First column (id) is not shown in JTable.
            for (int col = 1 ; col < theRow.length ; col++) {
                
                String ex = expected[row][col];
                
                if (ex.equals(ANY)) continue;
                
                String ac = solversTable.getModel().getValueAt(row, col-1).toString();
                assertEquals( String.format(msg, row, col, ex, ac), ac, ex);
                
            }
        }
        
    }
    
    
    
    
    /* Override the dialog methods in this class, otherwise behave exactly as student code.
    */
    private class RubikGUIMockDialog extends RubikGUI {
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
    
    
    
    
}