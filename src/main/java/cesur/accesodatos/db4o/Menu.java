package cesur.accesodatos.db4o;

/**
 * User interaction interface. This interface is meant to manage all possible user
 * interactions for {@link IDAO} interface methods.
 *
 * All methods are called like IDAO's methods with the 'execute' prefix and call the corresponding one inside them.
 *
 * @author Carlos Sánchez Recio.
 */
public interface Menu {

    /**
     * Method to launch a menu and read user input.
     * Based on a series of possible options, the corresponding method will be called or
     * an error message will be displayed on terminal.
     */
    public void executeMenu();
    /**
     * Method to display a table with all {@link Employee}s stored.
     * This method displays all Employees information in a table view from a list that is returned by {@link IDAO}'s corresponding method, that will be called inside.
     * If the returned list is empty, a message notifying it will be shown.
     */
    public void executeFindAllEmployees();
    /**
     * Method to display an {@link Employee} found by its ID.
     * This method asks the user for a numeric ID and execute the corresponding {@link IDAO}'s method.
     * If an Employee is found, the toString() method is displayed, if not an error will be shown.
     */
    public void executeFindEmployeeByID();
    /**
     * Method to add new {@link Employee}.
     * This method asks the user for all required information to create a new Employee.
     * If any input from user is not good, an error will be displayed and the method execution stops.
     * If all inputs from user are good (all data is valid), the corresponding {@link IDAO}'s method will be executed.
     */
    public void executeAddEmployee();
    /**
     * Method to update an existing {@link Employee}.
     * This method asks the user for a numeric ID, checks if there is an existing Employee with that ID and if so,
     * executes the corresponding {@link IDAO}'s method. If there is no Employee with the ID, an error will be shown.
     */
    public void executeUpdateEmployee();
    /**
     * Method to delete an existing {@link Employee}.
     * This method asks for a numeric ID, checks if there is an existing Employee with that ID and if so,
     * executes the corresponding {@link IDAO}'s method. If there is no Employee with the ID, an error will be shown.
     */
    public void executeDeleteEmployee();
    /**
     * Method to display a table with all {@link Department}s stored.
     * This method displays all Departments information in a table view from a list that is returned by {@link IDAO}'s corresponding method, that will be called inside.
     * If the returned list is empty, a message notifying it will be shown.
     */
    public void executeFindAllDepartments();
    /**
     * Method to display a {@link Department} found by its ID.
     * This method asks the user for a numeric ID and execute the corresponding {@link IDAO}'s method.
     * If a Department is found, the toString() method is displayed, if not an error will be shown.
     */
    public void executeFindDepartmentByID();
    /**
     * Method to add new {@link Department}.
     * This method asks the user for all required information to create a new Department.
     * If any input from user is not good, an error will be displayed and the method execution stops.
     * If all inputs from user are good (all data is valid), the corresponding {@link IDAO}'s method will be executed.
     */
    public void executeAddDepartment();
    /**
     * Method to update an existing {@link Department}.
     * This method asks the user for a numeric ID, checks if there is an existing Department with that ID and if so,
     * executes the corresponding {@link IDAO}'s method. If there is no Department with the ID, an error will be shown.
     */
    public void executeUpdateDepartment();
    /**
     * Method to delete an existing {@link Department}.
     * This method asks for a numeric ID, checks if there is an existing Department with that ID and if so,
     * executes the corresponding {@link IDAO}'s method. If there is no Department with the ID, an error will be shown.
     */
    public void executeDeleteDepartment();
    /**
     * Method to display a table with all {@link Employee}s from a {@link Department} stored.
     * This method lists all Employees that belong to a Department in a table view from a list that is returned by {@link IDAO}'s corresponding method, that will be called inside.
     * If the returned list is empty, a message notifying it will be shown.
     */
    public void executeFindEmployeesByDept();
}
