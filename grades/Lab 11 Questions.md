## Lab 11: GUI and Database Programs: Rubik Cube Solver Database   

Write an GUI application to manage a database of things that can solve Rubik's cubes, and their fastest time.

### Database setup:

**Use a SQLite database.**

You will need to create a database called **rubik.db**. 

You will also need to create a test database file called **rubik_test.db**.

The rubik database should have a table called **cube_records**.
The rubik_test database should also have a table called **cube_records**.
 
The cubes table (in both databases) should have three columns, 

* **id** the primary key column, an auto-incrementing integer 
* **solver_name** for the name of a thing that can solve Rubik’s cubes
* **time_seconds** a double number, for the time taken to solve the Rubik’s cube. 

Verify the DB connection URL is correct in DBConfig.

Here’s some example data:

```
solver_name           time_seconds

Cubestormer II        5.27
Cubestormer III       3.253
Sub1                  0.637                      
Mats Valk             4.74
Feliks Zemdegs        4.73
Patrick Ponce         4.69
```


Source: [http://www.recordholders.org/en/list/rubik.html](http://www.recordholders.org/en/list/rubik.html)


### GUI setup

Create a GUI to view, edit, add to, and delete, this data. 

The solversTable should get data from a RubikModel AbstractTableModel. 


Your GUI should have these components, configured in the following way,


**For displaying all current data:**

* A JTable called `solversTable` with two columns for solver_name and time_seconds, that displays all of the data from those columns in the database.  It should be configured to be non-editable, and single row selection.

When the program loads, it will show all (solver_name and time_seconds) data from the database. As the user adds, deletes, or edits data, the table will update.

*Keep your data sorted in order of time, with fastest first*.

**For adding a new cube solver to the database:**

* A JTextField called `newCubeSolverNameText`
* A JTextField called `newCubeSolverTimeText`
* A JButton called `addNewSolverButton`

When the user clicks the `addNewSolverButton` button, validate that a name and a double time have been entered in the appropriate JTextBoxes. The time must be positive, and not zero. If so, add the data to the database and update `solversTable`. 

If not, display an JOptionPane alert dialog, and do not modify the database or JTable.

**For modifying a time**

* A JButton called `updateTimeButton`
* JTextField called `updateTimeText`
* JLabel called `updateSolverNameLabel`   

When the user selects a row in the table, the current name will be show in updateSolverNameLabel and that solver's time will be shown in updateTimeText. 
  
To update a time, the user will select the solver's row in the table, edit the updateTimeText to the new time, and click `updateTimeButton`. The program will validate that the new time is also a non-zero double value. If so, the database table will be updated, and the GUI table will update. If the new text entered is not a double, show a JOptionPane alert dialog and do not modify the DB table or JTable. 

The current row should remain selected. 

If no user is selected in the table, show an alert dialog telling the user to select a row. 

**For deleting a cube solver**

* A JButton called `deleteSolverButton`

To delete a row, the user will select that row in the table and click  `deleteSolverButton`.

Show a JOptionPane dialog with 'Yes' and 'No' options to confirm the delete operation. If the user clicks 'Yes' then delete the row from the database and update the JTable. 

If the user clicks 'No' then do nothing.



For all parts of this lab, make sure you add appropriate error handling, and close all resources (ResultSets, PreparedStatements, Statements, Connections) when your program is done with them. Use PreparedStatements for updates, add, delete operations. 

When showing dialogs, use the methods provided in RubikGUI, or your tests will time out. 