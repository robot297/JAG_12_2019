package week_11;

import org.junit.Test;

import javax.swing.*;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;

import static junit.framework.TestCase.*;

/**
 * Created by clara on 9/18/17.
 */


public class RubikTest {
    
    private JTable solversTable;
    
    private JTextField newCubeSolverNameText;
    private JTextField newCubeSolverTimeText;
    
    private JButton addNewSolverButton;
    
    private JButton updateTimeButton;
    
    private JTextField updateTimeText;
    private JLabel updateSolverNameLabel;
    
    private JButton deleteSolverButton;
    
    private String testDatabaseURL = "jdbc:sqlite:rubik_test.db";
    private String developmentDatabaseURL = "jdbc:sqlite:rubik.db";
    
    private Rubik rubikProgram;
    
    private RubikGUIMockDialog gui;
    
    
    
    public void setup(boolean testData) {
        
        // Replace database with test DB
        DBConfig.db_url = testDatabaseURL;
        
        resetTable();   // delete and recreate test db's table
        
        if (testData) { addExampleData(); }
        
        rubikProgram = new Rubik();
        
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
    
    
    private void resetTable() {
        
        try (Connection conn = DriverManager.getConnection(testDatabaseURL);
             Statement statement = conn.createStatement()) {
            
            String delete_sql = "drop table if exists cube_records";
            statement.executeUpdate(delete_sql);
            
            System.out.println("table deleted ");
            String sql = "create table cube_records (id integer primary key autoincrement, solver_name text unique, time_seconds number)";
            statement.executeUpdate(sql);
            
        } catch (SQLException e) {
            fail("SQLException creating table" + e);
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
            
            
        } catch (SQLException e) {
            fail("SQLException adding data to test database." + e.getMessage());
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
    
    
    @Test(timeout=3000)
    public void testTestDatabaseAndTableExists() throws Exception {
        testTableExists(testDatabaseURL);
    }
    
    
    @Test(timeout=3000)
    public void testTestDevelopmentDatabaseAndTableExists() throws Exception {
        testTableExists(developmentDatabaseURL);
    }
    
    
    public void testTableExists(String dbURL) throws Exception {
        
        try (Connection conn = DriverManager.getConnection(dbURL);
             Statement statement = conn.createStatement() ) {
            
            String tableInfo = "PRAGMA table_info(cube_records)";
            ResultSet rs = statement.executeQuery(tableInfo);
            
            
            rs.next();
            
            String idCol = rs.getString(2).toLowerCase();
            String idColType = rs.getString(3);
            int isPrimaryKey = rs.getInt(6);
            
            rs.next();
            
            String solverNameColumn = rs.getString(2).toLowerCase();
            String solverNameType = rs.getString(3);
            
            rs.next();
            
            String timeCol = rs.getString(2).toLowerCase();
            String timeType = rs.getString(3);
            
            boolean moreThanThreeRows = rs.next();
            
            
            //Is name unique? Get info from the sqlite_master table
            
            String sqliteMaster = "select sql from sqlite_master where name like 'cube_records'";
            ResultSet tableCreateInfo = statement.executeQuery(sqliteMaster);
            tableCreateInfo.next();
            String sqlToCreateTable = tableCreateInfo.getString(1);
            boolean nameUnique = sqlToCreateTable.toLowerCase().matches(".*solver_name +text +unique.*");
            
            
            assertFalse("The database should only contain three columns: id, solver_name, and time_seconds.", moreThanThreeRows);   // No more columns.
            
            assertEquals("The first column's name should be 'id'", "id", idCol);
            assertEquals("The first column's type should be 'integer'", "integer", idColType);
            assertEquals("The first column, id, should be a primary key", 1, isPrimaryKey);
            
            assertEquals("The second column's name should be 'solver_name'", "solver_name", solverNameColumn);
            assertEquals("The second column's type should be 'text'", "text", solverNameType);
            assertTrue("The solver_name column should contain unique values, should not permit duplicate names", nameUnique);
            
            assertEquals("The third column's name should be 'time_seconds'", "time_seconds", timeCol);
            assertEquals("The third column's type should be 'number'", "number", timeType);
            
        } catch (SQLException e) {
            throw e;
        }
        
    }
    
    
    @Test(timeout=3000)
    public void testTableRowSelection() {
        
        setup(true);
        
        assertEquals("Table should be single row selection",
                solversTable.getSelectionModel().getSelectionMode(), ListSelectionModel.SINGLE_SELECTION);
    }
    
    
    @Test(timeout=3000)
    public void testTableSetupWithTestData() {
        
        setup(true);     // Expect data in table
        
        String[] ids = { "3", "1", "2" };
        String[] names = { "Sub1", "Cubestormer II", "Patrick Ponce" };
        String[] times = { "0.637", "3.253", "4.69" };
        
        // Should be three rows and three columns
        assertEquals("If there are three rows of test data in the database, JTable should have three rows", 3, solversTable.getModel().getRowCount());
        assertEquals("For test data, table should have three columns for id, solver_name, and time_seconds", 3, solversTable.getModel().getColumnCount());
        
        
        for (int n = 0 ; n < ids.length ; n++) {
            //Read first column
            String found = solversTable.getModel().getValueAt(n, 0).toString();
            assertEquals("Expected " + ids[n] + " but found " + found,
                    ids[n], found);
        }
        
        for (int n = 0 ; n < names.length ; n++) {
            //Read 2nd column
            String found = solversTable.getModel().getValueAt(n, 1).toString();
            assertEquals("Expected " + names[n] + " but found " + found,
                    names[n], found);
        }
        
        for (int n = 0 ; n < times.length ; n++) {
            // And 3rd column
            String found = solversTable.getModel().getValueAt(n, 2).toString();
            assertEquals("Expected " + times[n] + " but found " + found,
                    times[n], found);
        }
        
        
    }
    
    
    
    @Test(timeout=3000)
    public void testAddNewSolver() {
        
        setup(false);   // no test data
        
        // Add new solver
        
        newCubeSolverNameText.setText("Cat");
        newCubeSolverTimeText.setText("12345.41");
        addNewSolverButton.doClick();
        
        // Expected data
        String[][] expected = {
                {"1", "Cat", "12345.41"}
        };
        
        
        // Should be in table
        checkMatchesGUI(solversTable, expected);
        
        // Add another new solver
        
        newCubeSolverNameText.setText("Raccoon");
        newCubeSolverTimeText.setText("4000.23");
        addNewSolverButton.doClick();
        
        // Expected data
        String[][] expected_2_rows = {
                {"2", "Raccoon", "4000.23"},
                {"1", "Cat", "12345.41"},
        };
        
        // Should be in table
        checkMatchesGUI(solversTable, expected_2_rows);
        
        // Add 3 new rows
        
        newCubeSolverNameText.setText("Velociraptor");
        newCubeSolverTimeText.setText("9999999.99");
        addNewSolverButton.doClick();
        
        // Expected data
        String[][] expected_3_rows = {
                {"2", "Raccoon", "4000.23"},
                {"1", "Cat", "12345.41"},
                {"3", "Velociraptor", "9999999.99"}
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
    
    
    @Test(timeout=3000)
    public void testEditSolverTimeValid() {
        
        setup(true);     // Expect data in table
        
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
                {"3", "Sub1", "0.637"},
                {"1", "Cubestormer II", "2.45"},
                {"2", "Patrick Ponce", "4.69"}
        };
        
        verifyData(solversTable, expected);
        
        // Select first row
        
        solversTable.setRowSelectionInterval(0, 0);
        assertEquals(updateSolverNameLabel.getText(), "Sub1");
        assertEquals(updateTimeText.getText(), "0.637");
        
        updateTimeText.setText("30.2");
        updateTimeButton.doClick();
        
        String[][] expected_move = {
                {"1", "Cubestormer II", "2.45"},
                {"2", "Patrick Ponce", "4.69"},
                {"3", "Sub1", "30.2"},
        };
        
        verifyData(solversTable, expected_move);
        
    }
    
    
    @Test(timeout=3000)
    public void testEditSolverTimeInvalid() {
        
        setup(true);     // Expect data in table
        
        
        // Invalid data. Should see alert.
        
        solversTable.setRowSelectionInterval(0, 0);
        assertEquals(updateSolverNameLabel.getText(), "Sub1");
        assertEquals(updateTimeText.getText(), "0.637");
        
        updateTimeText.setText("pizza");
        updateTimeButton.doClick();
        assertTrue("Show an alert dialog if user enters an invalid time.", gui.checkAlertWasCalled());
        
        String[][] expected_no_change = {
                {"3", "Sub1", "0.637"},
                {"1", "Cubestormer II", "3.253"},
                {"2", "Patrick Ponce", "4.69"}
        };
        
        verifyData(solversTable, expected_no_change);
        
        
        // Negative time. Should see alert.
        solversTable.setRowSelectionInterval(0, 0);
        
        updateTimeText.setText("-10");
        updateTimeButton.doClick();
        assertTrue("Show an alert dialog if user enters an invalid time.", gui.checkAlertWasCalled());
        
        verifyData(solversTable, expected_no_change);
        
    }
    
    
    
    @Test(timeout=3000)
    public void testDeleteSolver() {
        
        setup(true);     // Expect data in table
        
        
        // Select 2nd row, Cubestormer II
        solversTable.setRowSelectionInterval(1, 1);
        
        gui.mockYesNo = JOptionPane.YES_OPTION;
        
        deleteSolverButton.doClick();
        
        // Table should update
        
        String[][] expected = {
                {"3", "Sub1", "0.637" },
                {"2", "Patrick Ponce", "4.69" }
        } ;
        
        verifyData(solversTable, expected);
        
        
        // Select first row
        solversTable.setRowSelectionInterval(0, 0);
        
        gui.mockYesNo = JOptionPane.YES_OPTION;
        
        deleteSolverButton.doClick();
        
        // Table should update
        
        String[][] expected_2 = {
                {"2", "Patrick Ponce", "4.69" }
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
            
            ArrayList<String> ids = new ArrayList();
            ArrayList<String> names = new ArrayList();
            ArrayList<String> times = new ArrayList();
            
            while (rs.next()) {
                
                ids.add(Integer.toString(rs.getInt(1)));
                names.add(rs.getString(2));
                times.add(Double.toString(rs.getDouble(3)));
                
                rowCounter++;
                
            }
            
            
            rs.close(); statement.close(); con.close();
            
            assertTrue("Database does not contain the expected number of rows", rowCounter == expected.length);  // more rows expected than DB returned?
            
            
            // Check db against expected data
            
            for (int i = 0; i < ids.size() ; i++) {
                assertEquals("IDs in database don't match IDs in table", expected[i][0], ids.get(i));
            }
            
            for (int i = 0; i < names.size() ; i++) {
                assertEquals("Names in database don't match names in table", expected[i][1], names.get(i));
            }
            
            for (int i = 0; i < times.size() ; i++) {
                assertEquals("Times in database don't match times in table", expected[i][2], times.get(i));
            }
            
        } catch (SQLException e) {
            fail("SQLException deleting data from test database." + e.getMessage());
        }
    }
    
    
    private void checkMatchesGUI(JTable solversTable, String[][] expected) {
        
        // Same number of rows?
        assertEquals("The number of rows in the database does not match the number of rows in the JTable", expected.length, solversTable.getRowCount());
        
        // Check data in database matches JTable.
        String msg = "When comparing database table to GUI JTable, the data at JTable row:col %s:%s was expected \n" +
                "to be %s (from the database) but found %s (in the JTable)";
        
        for (int row = 0 ; row < expected.length ; row++) {
            
            String[] theRow = expected[row];
            
            for (int col = 0 ; col < theRow.length ; col++) {
                
                String ex = expected[row][col];
                String ac = solversTable.getModel().getValueAt(row, col).toString();
                
                assertEquals( String.format(msg, row, col, ex, ac), ac, ex);
                
            }
        }
    }
    
    
}