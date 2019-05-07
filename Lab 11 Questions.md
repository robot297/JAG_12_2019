## Lab 11: GUI and Database Programs: Rubik Cube Solver Database   

Write an GUI application to manage a database of things that can solve Rubik's cubes, and their fastest time.

A class should have a focused responsibility. GUI and database work are two separate tasks - so don't do database work in the RubikGUI class. Create a new class to handle talking to the database. The GUI will delegate database tasks to this new class. 

### Database setup:

Using the SQLite shell or DB Browser, create a database in a file called **rubik.db**. 

And, create a test database file called **rubik_test.db**.

The rubik database should have a table called **cube_records**.
The rubik_test database should also have a table called **cube_records**. Both tables should have the same structure.
 
The cubes table (in both databases) should have three columns, 

* **id** the primary key column, an integer, used as a rowid. 
* **solver_name** for the name of a thing that can solve Rubik’s cubes
* **time_seconds** a double number, for the time taken to solve the Rubik’s cube. 

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

An example SQL statement to create a table is as follows:

```
create table cube_records (id integer primary key, solver_name text, time_seconds number);

```

When inserting data using this form, the value for the id column will be assigned by the database automatically. 

```
insert into cube_records (solver_name, time_seconds) values ('Sub1', 0.637);

```


Verify the DB connection URL is correct in DBConfig.

### GUI setup

Create a GUI to view, edit, add to, and delete, this data in a JTable. 

Your GUI should have these components, configured in the following way,

**For displaying all current data:**

* A JTable called `solversTable` with two columns for solver_name and time_seconds, that displays all of the data from those columns in the database.  It should be configured to be **non-editable**, and **single row selection**.

When the program loads, it will show all the data from the database. As the user adds, deletes, or edits data, the table will update.

*Keep your data sorted in order of time, with fastest first*. Don't use the autosort feature. Use a database query that fetches and sorts (orders) the data with fastest time first.

You will need to create a table model for `solversTable`. This table model will provide data from the database to its JTable.

**For adding a new cube solver to the database:**

* A JTextField called `newCubeSolverNameText`
* A JTextField called `newCubeSolverTimeText`
* A JButton called `addNewSolverButton`

When the user clicks the `addNewSolverButton` button, validate that a name and a double time have been entered in the appropriate JTextField. The time must be positive, and not zero. The name must be at least one character. Empty names are not acceptable. If the name and time are both valid, add the data to the database and update `solversTable`. 

If not, display an JOptionPane alert dialog, and do not modify the database or JTable.

**For modifying a time**

* A JButton called `updateTimeButton`
* JTextField called `updateTimeText`
* JLabel called `updateSolverNameLabel`   

When the user selects a row in the table, the current name will be show in `updateSolverNameLabel` and that solver's time will be shown in `updateTimeText`. 
  
To update a time, the user will select the solver's row in the table, edit `updateTimeText` to the new time, and click `updateTimeButton`. The program will validate that the new time is also a non-zero double value. If so, the database table will be updated, and the GUI table will update. If the new text entered is not a double, show a JOptionPane alert dialog and do not modify the DB table or JTable. 

The current row should remain selected. 

If no user is selected in the table, show an alert dialog telling the user to select a row. 

**For deleting a cube solver**

* A JButton called `deleteSolverButton`

To delete a row, the user will select that row in the table and click  `deleteSolverButton`.

Show a JOptionPane dialog with 'Yes' and 'No' options to confirm the delete operation. If the user clicks 'Yes' then delete the row from the database and update the JTable. 

If the user clicks 'No' then do nothing.

For all parts of this lab, make sure you add appropriate error handling. 

When showing dialogs, use the methods provided in RubikGUI, or your tests will time out. 