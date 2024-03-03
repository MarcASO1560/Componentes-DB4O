package cesur.accesodatos.db4o;


import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * db4o-based Data Access Object (DAO) component for managing data persistence.
 * This class utilizes db4o, an object database, to perform CRUD (Create, Read, Update, Delete) operations
 * on entities such as Employees and Departments. Designed to be efficient and user-friendly, it abstracts
 * the complexities of direct database manipulation, providing straightforward methods for data management.
 * Ensure the db4o database path is correctly configured before initiating operations.
 *
 * {@link IDAO} for data operations.
 * {@link FileHandlerInterface} for file database management.
 * {@link Menu} for user related interactions.
 *
 * @author Marc Albert Seguí Olmos
 */
public class db4oDAO  implements IDAO, Menu, FileHandlerInterface{

    // Terminal outputs and colors
    /**
     * BLACK_FONT -> Static and final {@link String} variable that stores ASCII code for black font color.
     */
    static final String BLACK_FONT = "\u001B[30m";
    /**
     * GREEN_FONT -> Static and final {@link String} variable that stores ASCII code for green font color.
     */
    static final String GREEN_FONT = "\u001B[32m";
    /**
     * WHITE_BG -> Static and final {@link String} variable that stores ASCII code for white background color.
     */
    static final String WHITE_BG = "\u001B[47m";
    /**
     * RESET -> Static and final {@link String} variable that stores ASCII code to reset terminal colors.
     */
    static final String RESET = "\u001B[0m";
    /**
     * USER_INPUT -> Static and final {@link String} variable that stores a simple prompt for the user when he has to introduce any data.
     */
    static final String USER_INPUT = String.format("%s%s>%s ", BLACK_FONT, WHITE_BG, RESET);
    /**
     * Flag indicating if a file connection has been established.
     * Used to ensure operations don't proceed without proper file access setup.
     */
    private boolean connectionFlag = false;

    /**
     * Flag to control the execution flow of the application, typically used to keep the application running or initiate a graceful shutdown.
     */
    private boolean executionFlag = true;
    /**
     * Scanner used for capturing user input from the terminal.
     */
    private final Scanner scanner = new Scanner(System.in);
    /**
     * isr -> {@link InputStreamReader} variable that will allow the user to insert data through terminal.
     *
     */
    private final InputStreamReader isr = new InputStreamReader(System.in);
    /**
     * Path to the "empresa.txt" file within the project's resources directory, used as the data storage for the application.
     */
    static String path = "src/main/resources/empresa.yap"; // Path of the file

    static ObjectContainer db = Db4oEmbedded.openFile("src/main/resources/empresa.yap");

    /**
     * Checks if the db4o database file exists at the specified path.
     * This method attempts to verify the presence of a db4o database file by checking the filesystem.
     * It sets the connectionFlag based on the file's existence to indicate the availability of the database for further operations.
     *
     * @return true if the database file exists, false otherwise. It also returns false if an error occurs during the check.
     */
    @Override
    public boolean checkDBExists() {
        try {
            File dbFile = new File(path);
            connectionFlag = dbFile.exists();

            if (connectionFlag) {
                System.out.println("The db4o database file exists.");
            } else {
                System.out.println("The db4o database file does not exist.");
            }
            return connectionFlag; // Return the state of connectionFlag
        } catch (Exception e) {
            System.err.println("ERROR: An error occurred checking the db4o database file existence: " + e.getMessage());
            return false; // Return false if an exception is caught
        }
    }

    /**
     * Closes the db4o database connection if it's currently open.
     * This method attempts to close the db4o database connection gracefully, ensuring all resources are freed properly.
     * If the connection is successfully closed, a confirmation message is printed to the console.
     * If an error occurs during the closure process, an error message is displayed, detailing the issue encountered.
     */
    @Override
    public void closeConnection() {
        if (db != null) {
            try {
                db.close(); // Intenta cerrar la conexión db4o
                System.out.println("Database connection successfully closed.");
            } catch (Exception e) {
                System.err.println("ERROR: An error occurred while closing the database connection: " + e.getMessage());
            }
        }
    }

    /**
     * Retrieves all Employee objects from the db4o database.
     * This method queries the db4o database for all instances of the Employee class, adding each to a list of Employee objects.
     * If an error occurs during the retrieval process, an error message is printed with the exception's details.
     *
     * @return A list of all Employee objects found in the database. Returns an empty list if no employees are found or if an error occurs.
     */
    @Override
    public List<Employee> findAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        try {
            // Consulta para obtener todos los objetos Employee
            ObjectSet<Employee> result = db.query(Employee.class);
            for (Employee emp : result) {
                employees.add(emp);
            }
        } catch (Exception e) {
            System.err.println("ERROR: An error occurred while retrieving employees: " + e.getMessage());
        }
        return employees;
    }

    /**
     * Searches for an Employee in the db4o database by their ID.
     * This method accepts an Object as an ID, which it expects to be an Integer. It validates the ID's data type before proceeding with the search.
     * It utilizes a db4o query with a Predicate to find the employee whose employee number matches the given ID.
     * If found, it returns the first matching Employee object, assuming ID uniqueness. Otherwise, it notifies the user of the absence of such an employee.
     *
     * @param id The unique identifier for the employee, expected to be an Integer.
     * @return The Employee object matching the given ID, or null if no matching employee is found or an invalid ID is provided.
     */
    @Override
    public Employee findEmployeeById(Object id) {
        if (!(id instanceof Integer)) {
            System.out.println("Invalid ID provided. ID must be an integer.");
            return null;
        }

        int searchId = (Integer) id;
        Employee foundEmployee = null;

        try {
            List<Employee> employees = db.query(new Predicate<Employee>() {
                @Override
                public boolean match(Employee employee) {
                    return employee.getEmpno() == searchId;
                }
            });

            if (!employees.isEmpty()) {
                foundEmployee = employees.get(0); // Assuming IDs are unique, there should be at most one match.
                System.out.println("Employee found: " + foundEmployee);
            } else {
                System.out.println("No employee found with ID: " + searchId);
            }
        } catch (Exception e) {
            System.err.println("ERROR: An error occurred while searching for the employee: " + e.getMessage());
        }

        return foundEmployee;
    }

    /**
     * Adds a given Employee object to the db4o database.
     * This method stores the provided Employee object in the db4o database and commits the transaction to ensure the data is saved.
     * If an error occurs during the process, the transaction is rolled back, and an error message is printed.
     * It checks if the database connection is established before attempting to add the employee.
     *
     * @param employee The Employee object to be added to the database. It should have all its attributes set.
     */
    @Override
    public void addEmployee(Employee employee) {
        if (db != null) {
            try {
                db.store(employee); // Almacena el objeto Employee en la base de datos db4o
                db.commit(); // Confirma la transacción
                System.out.println("Employee added successfully.");
            } catch (Exception e) {
                db.rollback(); // Revierte la transacción en caso de error
                System.err.println("ERROR: Unable to add employee - " + e.getMessage());
            }
        } else {
            System.err.println("Database connection is not established.");
        }
    }

    /**
     * Updates the information of an existing employee identified by the provided ID.
     * Validates the ID to ensure it's an integer and searches for the corresponding employee in the database.
     * If the employee is found, prompts for new values for the employee's last name, job, and department ID.
     * Updates the employee's information in the db4o database and commits the changes.
     * If any provided values are empty or if the department ID is invalid, an error message is displayed and the update is not performed.
     * If an error occurs during the database operation, the transaction is rolled back.
     *
     * @param id The unique identifier of the employee to update, must be an Integer.
     * @return The updated Employee object, or null if the ID is invalid, the employee is not found, or an error occurs.
     */
    @Override
    public Employee updateEmployee(Object id) {
        if (!(id instanceof Integer)) {
            System.out.println("Invalid ID");
            return null;
        }

        int empId = (Integer) id;
        // Find the employee
        Employee employee = findEmployeeById(empId);
        if (employee == null) {
            System.out.println("Employee not found.");
            return null;
        }

        System.out.println("Updating employee with ID: " + empId);
        System.out.print("Last name (current: " + employee.getName() + "): ");
        String surname = scanner.nextLine();
        if (surname.isEmpty()) throw new IllegalArgumentException("The last name cannot be empty");

        System.out.print("Job (current: " + employee.getPosition() + "): ");
        String job = scanner.nextLine();
        if (job.isEmpty()) throw new IllegalArgumentException("The job cannot be empty");

        System.out.print("Department ID (current: " + employee.getDepno() + "): ");
        String departmentId = scanner.nextLine();
        if (departmentId.isEmpty()) throw new IllegalArgumentException("The department ID cannot be empty");

        // Set the new values to the employee
        employee.setName(surname);
        employee.setPosition(job);
        try {
            employee.setDepno(Integer.parseInt(departmentId));
        } catch (NumberFormatException e) {
            System.err.println("Invalid department ID format");
            return null;
        }

        try {
            // Store the updated object
            db.store(employee);
            db.commit(); // Confirm the transaction
            System.out.println("Employee has been successfully updated.");
        } catch (Exception e) {
            db.rollback(); // Revert in case of error
            System.err.println("An error occurred: " + e.getMessage());
            return null;
        }

        return employee;
    }

    /**
     * Deletes an employee from the db4o database based on the provided ID.
     * Validates the provided ID to ensure it's an integer and checks if the database connection is established.
     * Queries the database for the employee matching the given ID. If found, the employee is deleted from the database,
     * and a confirmation message is printed. If no matching employee is found, a message indicating this is printed.
     *
     * @param id The unique identifier of the employee to be deleted, must be an Integer.
     * @return The Employee object that was deleted, or null if the ID is invalid, the employee is not found, or the database connection is not established.
     */
    @Override
    public Employee deleteEmployee(Object id) {
        if (!(id instanceof Integer)) {
            System.out.println("Invalid ID format.");
            return null;
        }

        if (db == null) {
            System.out.println("Database connection is not established.");
            return null;
        }

        int empId = (Integer) id;
        Employee employeeToDelete = null;

        // Query for the employee by ID
        List<Employee> employees = db.query(new Predicate<Employee>() {
            public boolean match(Employee employee) {
                return employee.getEmpno() == empId;
            }
        });

        // Check if the employee exists
        if (!employees.isEmpty()) {
            employeeToDelete = employees.get(0);
            // Delete the employee
            db.delete(employeeToDelete);
            System.out.println("Employee deleted successfully.");
            db.commit();
        } else {
            System.out.println("Employee not found.");
        }

        return employeeToDelete;
    }

    /**
     * Retrieves all Department objects from the db4o database.
     * This method queries the db4o database for all instances of the Department class, adding each to a list of Department objects.
     * It prints the total number of departments found to the console. If an error occurs during the retrieval process, an error message is printed with the exception's details.
     * Checks if the database connection is established before attempting to retrieve departments.
     *
     * @return A list of all Department objects found in the database. If the database connection is not established or an error occurs, the list may be empty.
     */
    @Override
    public List<Department> findAllDepartments() {
        List<Department> departments = new ArrayList<>();

        if (db != null) {
            try {
                // Realiza una consulta para obtener todos los objetos Department
                List<Department> result = db.query(Department.class);
                departments.addAll(result);
                System.out.println("Found " + departments.size() + " departments.");
            } catch (Exception e) {
                System.err.println("ERROR: Unable to retrieve departments - " + e.getMessage());
            }
        } else {
            System.err.println("Database connection is not established.");
        }
        return departments;
    }

    /**
     * Retrieves a Department object from the db4o database by its ID.
     * Validates the ID to ensure it's an integer before attempting a search. Utilizes db4o's queryByExample method to find the Department
     * matching the given ID. If a matching department is found, it's returned; otherwise, a message indicating no such department
     * is printed. Checks if the database connection is established before attempting the search.
     *
     * @param id The unique identifier of the department to find, expected to be an Integer.
     * @return The found Department object if present, or null if not found or in case of an invalid ID.
     */
    @Override
    public Department findDepartmentById(Object id) {
        if (!(id instanceof Integer)) {
            System.out.println("Invalid ID");
            return null;
        }

        int deptId = (Integer) id;
        Department foundDepartment = null;

        if (db != null) {
            try {
                Department proto = new Department(deptId, null, null);
                ObjectSet<Department> result = db.queryByExample(proto);

                if (result.hasNext()) {
                    foundDepartment = result.next();
                    System.out.println("Department found.");
                } else {
                    System.out.println("No department found with ID: " + deptId);
                }
            } catch (Exception e) {
                System.err.println("ERROR: Searching department failed - " + e.getMessage());
            }
        } else {
            System.err.println("Database connection is not established.");
        }
        return foundDepartment;
    }

    /**
     * Adds a specified Department object to the db4o database.
     * This method attempts to store the provided Department object in the db4o database, ensuring data persistence by committing the transaction.
     * If an exception occurs during the process, the transaction is rolled back to maintain database integrity, and an error message is printed.
     * Before attempting to add the department, it verifies that the database connection is properly established.
     *
     * @param department The Department object to be added to the database, with all necessary attributes set.
     */
    @Override
    public void addDepartment(Department department) {
        if (db != null) {
            try {
                db.store(department); // Almacena el objeto Department en la base de datos db4o
                db.commit(); // Confirma la transacción
                System.out.println("Department added successfully.");
            } catch (Exception e) {
                db.rollback(); // Revierte la transacción en caso de error
                System.err.println("ERROR: Unable to add department - " + e.getMessage());
            }
        } else {
            System.err.println("Database connection is not established.");
        }
    }

    /**
     * Updates the information of an existing department identified by the provided ID.
     * Validates the ID to ensure it's an integer and searches for the corresponding department in the database.
     * If the department is found, prompts for new values for the department's name and city.
     * Updates the department's information in the db4o database and commits the changes.
     * If any provided values are empty, an error message is displayed and the update is not performed.
     * If an error occurs during the database operation, the transaction is rolled back.
     *
     * @param id The unique identifier of the department to update, must be an Integer.
     * @return The updated Department object, or null if the ID is invalid, the department is not found, or an error occurs.
     */
    @Override
    public Department updateDepartment(Object id) {
        if (!(id instanceof Integer)) {
            System.out.println("Invalid ID");
            return null;
        }

        int deptId = (Integer) id;
        Department department = findDepartmentById(deptId);
        if (department == null) {
            System.out.println("Department not found.");
            return null;
        }

        System.out.println("Updating department with ID: " + deptId);
        System.out.print("Name (current: " + department.getName() + "): ");
        String name = scanner.nextLine();
        if (name.isEmpty()) throw new IllegalArgumentException("The name cannot be empty");

        System.out.print("City (current: " + department.getLocation() + "): ");
        String city = scanner.nextLine();
        if (city.isEmpty()) throw new IllegalArgumentException("The city cannot be empty");

        department.setName(name);
        department.setLocation(city);

        try {
            db.store(department);
            db.commit();
            System.out.println("Department has been successfully updated.");
        } catch (Exception e) {
            db.rollback();
            System.err.println("An error occurred while updating the department: " + e.getMessage());
            return null;
        }

        return department;
    }

    /**
     * Deletes a department identified by the provided ID from the db4o database.
     * Validates the ID to ensure it's an integer and searches for the department to delete.
     * If the department is found, it is removed from the database, and the transaction is committed to confirm the deletion.
     * An error message is printed, and the transaction is rolled back if an error occurs during the deletion process.
     *
     * @param id The unique identifier of the department to delete, must be an Integer.
     * @return The Department object that was deleted, or null if the ID is invalid, the department is not found, or an error occurs.
     */
    @Override
    public Department deleteDepartment(Object id) {
        if (!(id instanceof Integer)) {
            System.out.println("Invalid ID");
            return null;
        }

        int deptId = (Integer) id;
        Department departmentToDelete = findDepartmentById(deptId);
        if (departmentToDelete == null) {
            System.out.println("Department not found.");
            return null;
        }

        try {
            db.delete(departmentToDelete); // Deletes the department from the db4o database
            db.commit(); // Confirms the transaction
            System.out.println("Department has been successfully deleted.");
        } catch (Exception e) {
            db.rollback(); // Reverts the transaction in case of error
            System.err.println("An error occurred while deleting the department: " + e.getMessage());
            return null;
        }

        return departmentToDelete; // Returns the deleted department
    }

    /**
     * Retrieves a list of employees who belong to a specified department.
     * Validates the department ID to ensure it's an integer before querying the db4o database for employees in that department.
     * Iterates over all employees, adding those whose department number matches the provided ID to the list.
     *
     * @param idDept The unique identifier of the department whose employees are to be found. Expected to be of type {@link Integer}.
     * @return A list of {@link Employee} objects associated with the specified department ID. Returns an empty list if the department ID is invalid or if no employees are found in the specified department. Returns null if an error occurs during retrieval.
     */
    @Override
    public List<Employee> findEmployeesByDept(Object idDept) {
        if (!(idDept instanceof Integer)) {
            System.out.println("Invalid department ID.");
            return new ArrayList<>();
        }

        List<Employee> employeesInDept = new ArrayList<>();
        int deptId = (Integer) idDept;

        try {
            List<Employee> allEmployees = db.query(Employee.class); // Assuming db is your ObjectContainer
            for (Employee emp : allEmployees) {
                if (emp.getDepno() == deptId) {
                    employeesInDept.add(emp);
                }
            }
            return employeesInDept;
        } catch (Exception e) {
            System.err.println("Error retrieving employees: " + e.getMessage());
            return null;
        }
    }

    // Implementation from Menu interface
    @Override
    public void executeMenu() {
        BufferedReader reader = new BufferedReader(this.isr); // At this point the Stream is still opened -> At finally block I'll close it
        try {
            while (this.executionFlag) {
                System.out.printf("%s%s- WELCOME TO THE COMPANY -%s\n", "\u001B[46m", BLACK_FONT, RESET);
                System.out.println("Select an option:" + "\n\t1) List all Employees" + "\n\t2) Find Employee by its ID" + "\n\t3) Add new Employee" + "\n\t4) Update Employee" + "\n\t5) Delete Employee" + "\n\t6) List all Departments" + "\n\t7) Find Department by its ID" + "\n\t8) Add new Department" + "\n\t9) Update Department" + "\n\t10) Delete Department" + "\n\t11) Find Employees by Department" + "\n\t0) Exit program");
                System.out.print(USER_INPUT);
                String optStr = reader.readLine(); // Read user input and check its value for bad inputs
                if (optStr.isEmpty()) {
                    System.err.println("ERROR: Please indicate the option number");
                    continue;
                } else if (!optStr.matches("\\d{1,2}")) {
                    System.err.println("ERROR: Please provide a valid input for option! The input must be an Integer value");
                    continue;
                }
                int opt = Integer.parseInt(optStr);
                switch (opt) { // Execute corresponding method for user input
                    case 1 -> executeFindAllEmployees();
                    case 2 -> executeFindEmployeeByID();
                    case 3 -> executeAddEmployee();
                    case 4 -> executeUpdateEmployee();
                    case 5 -> executeDeleteEmployee();
                    case 6 -> executeFindAllDepartments();
                    case 7 -> executeFindDepartmentByID();
                    case 8 -> executeAddDepartment();
                    case 9 -> executeUpdateDepartment();
                    case 10 -> executeDeleteDepartment();
                    case 11 -> executeFindEmployeesByDept();
                    case 0 -> this.executionFlag = false;
                    default -> System.err.println("Please provide a valid option");
                }
            }
        } catch (IOException ioe) {
            System.err.println("ERROR: IOException error reported: " + ioe.getMessage());
        } finally {
            try {
                reader.close(); // Close reader
            } catch (IOException ioe) {
                System.err.println("ERROR: IOException error on reader close reported: " + ioe.getMessage());
            }
            closeConnection(); // Close connection method
        }
        System.out.printf("%s%s- SEE YOU SOON -%s\n", "\u001B[46m", BLACK_FONT, RESET); // Program execution end
    }

    // Implementation from Menu interface
    @Override
    public void executeFindAllEmployees() {
        if (this.connectionFlag) {
            String row = "+" + "-".repeat(7) + "+" + "-".repeat(16) + "+" + "-".repeat(16) + "+" + "-".repeat(7) + "+";
            List<Employee> employees = this.findAllEmployees(); // Get the Employees list
            if (employees != null) { // Check if the returned list is not null
                System.out.println(row);
                System.out.printf("| %-5s | %-14s | %-14s | %-5s |\n", "EMPNO", "NOMBRE", "PUESTO", "DEPNO");
                System.out.println(row);
                for (Employee e : employees) {
                    System.out.printf("| %-5s | %-14s | %-14s | %-5s |\n", e.getEmpno(), e.getName(), e.getPosition(), e.getDepno());
                }
                System.out.println(row);
            } else {
                System.out.println("There are currently no Employees stored");
            }
        } else {
            System.err.println("ERROR: You must first try to connect to the database with the method .connectDB()");
        }
    }

    // Implementation from Menu interface
    @Override
    public void executeFindEmployeeByID() {
        if (this.connectionFlag) {
            BufferedReader reader = new BufferedReader(this.isr); // To read user input
            try {
                System.out.println("Insert Employee's ID:");
                System.out.print(USER_INPUT);
                String input = reader.readLine();
                if (!input.matches("\\d+")) { // Check if the output is not numeric
                    System.err.println("ERROR: Please provide a valid Employee ID. Employee's ID are Integer values");
                    return;
                }
                Employee returnEmp = this.findEmployeeById(Integer.parseInt(input)); // Get the Employee object by querying it by the ID
                if (returnEmp != null) {
                    System.out.println("Employee's information:");
                    System.out.println(returnEmp.toString());
                } else { // There is no Employee with the indicated ID
                    System.out.println("There is no Employee with EMPNO " + input);
                }
            } catch (IOException ioe) {
                System.err.println("ERROR: IOException error reported: " + ioe.getMessage());
            }
        } else {
            System.err.println("ERROR: You must first try to connect to the database with the method .connectDB()");
        }
    }

    // Implementation from Menu interface
    @Override
    public void executeAddEmployee() {
        if (this.connectionFlag) {
            BufferedReader reader = new BufferedReader(this.isr); // To read user input
            try { // Ask for all required information to create a new Employee
                System.out.println("Insert new Employee's ID:");
                System.out.print(USER_INPUT);
                String id = reader.readLine();
                if (!id.matches("\\d+")) { // Check if the output is not numeric
                    System.err.println("ERROR: Please provide a valid Employee ID. Employee's ID are Integer values");
                    return;
                } else if (findEmployeeById(Integer.parseInt(id)) != null) { // There is already an Employee with that ID
                    System.err.println("ERROR: There is already an Employee with the same ID");
                    return;
                }
                System.out.println("Insert new Employee's NAME:");
                System.out.print(USER_INPUT);
                String name = reader.readLine();
                if (name.isEmpty()) { // Check for empty input
                    System.err.println("ERROR: You can't leave the information empty");
                    return;
                }
                System.out.println("Insert new Employee's ROLE:");
                System.out.print(USER_INPUT);
                String role = reader.readLine();
                if (role.isEmpty()) { // Check for empty input
                    System.err.println("ERROR: You can't leave the information empty");
                    return;
                }
                System.out.println("Insert new Employee's DEPNO:");
                System.out.print(USER_INPUT);
                String depno = reader.readLine();
                if (!depno.matches("\\d+")) { // Check if the output is not numeric
                    System.err.println("ERROR: Please provide a valid Department ID. Departments' ID are Integer values");
                    return;
                } else if (findDepartmentById(Integer.parseInt(depno)) == null) { // There is no Department with introduced DEPNO
                    System.err.println("ERROR: There is no Department with DEPNO " + depno);
                    return;
                }
                // Everything is good to execute the method
                Employee newEmployee = new Employee(Integer.parseInt(id), name, role, Integer.parseInt(depno)); // Create Employee object
                this.addEmployee(newEmployee);
                System.out.printf("%sNew Employee added successfully!%s\n", GREEN_FONT, RESET);
            } catch (IOException ioe) {
                System.err.println("ERROR: IOException error reported: " + ioe.getMessage());
            }
        } else {
            System.err.println("ERROR: You must first try to connect to the database with the method .connectDB()");
        }
    }

    // Implementation from Menu interface
    @Override
    public void executeUpdateEmployee() {
        if (this.connectionFlag) {
            BufferedReader reader = new BufferedReader(this.isr); // To read user input
            try {
                System.out.println("Insert Employee's ID:");
                System.out.print(USER_INPUT);
                String input = reader.readLine();
                if (!input.matches("\\d+")) { // Check if the output is not numeric
                    System.err.println("ERROR: Please provide a valid Employee ID. Employee's ID are Integer values");
                    return;
                }
                Employee returnEmp = this.findEmployeeById(Integer.parseInt(input));
                if (returnEmp == null) { // Check if there is an Employee with the indicated ID
                    System.out.println("There is no Employee with EMPNO " + input);
                    return;
                }
                // Execute IDAO method
                Employee updated = updateEmployee(Integer.parseInt(input));
                System.out.println(updated.toString());
            } catch (IOException ioe) {
                System.err.println("ERROR: IOException error reported: " + ioe.getMessage());
            }
        } else {
            System.err.println("ERROR: You must first try to connect to the database with the method .connectDB()");
        }
    }

    // Implementation from Menu interface
    @Override
    public void executeDeleteEmployee() {
        if (this.connectionFlag) {
            BufferedReader reader = new BufferedReader(this.isr); // To read user input
            try {
                System.out.println("Insert Employee's ID:");
                System.out.print(USER_INPUT);
                String input = reader.readLine();
                if (!input.matches("\\d+")) { // Check if the output is not numeric
                    System.err.println("ERROR: Please provide a valid Employee ID. Employee's ID are Integer values");
                    return;
                }
                Employee returnEmp = this.findEmployeeById(Integer.parseInt(input));
                if (returnEmp == null) { // Check if there is an Employee with the indicated ID
                    System.out.println("There is no Employee with EMPNO " + input);
                    return;
                }
                // Execute IDAO method
                Employee deleted = deleteEmployee(Integer.parseInt(input));
                System.out.println(deleted.toString());
            } catch (IOException ioe) {
                System.err.println("ERROR: IOException error reported: " + ioe.getMessage());
            }
        } else {
            System.err.println("ERROR: You must first try to connect to the database with the method .connectDB()");
        }
    }

    // Implementation from Menu interface
    @Override
    public void executeFindAllDepartments() {
        if (this.connectionFlag) {
            String row = "+" + "-".repeat(7) + "+" + "-".repeat(20) + "+" + "-".repeat(16) + "+";
            List<Department> departments = this.findAllDepartments();
            if (departments != null) { // Check if the returned list is null or empty
                System.out.println(row);
                System.out.printf("| %-5s | %-18s | %-14s |\n", "DEPNO", "NOMBRE", "UBICACION");
                System.out.println(row);
                for (Department d : departments) {
                    System.out.printf("| %-5s | %-18s | %-14s |\n", d.getDepno(), d.getName(), d.getLocation());
                }
                System.out.println(row);
            } else {
                System.out.println("There are currently no Department stored");
            }
        } else {
            System.err.println("ERROR: You must first try to connect to the database with the method .connectDB()");
        }
    }

    // Implementation from Menu interface
    @Override
    public void executeFindDepartmentByID() {
        if (this.connectionFlag) {
            BufferedReader reader = new BufferedReader(this.isr); // To read user input
            try {
                System.out.println("Insert Department's ID:");
                System.out.print(USER_INPUT);
                String input = reader.readLine();
                if (!input.matches("\\d+")) { // Check if the output is not numeric
                    System.err.println("ERROR: Please provide a valid Department ID. Department's ID are Integer values");
                    return;
                }
                Department returnDept = this.findDepartmentById(Integer.parseInt(input));
                if (returnDept != null) { // Check if the returning Department is null
                    System.out.println("Department's information:");
                    System.out.println(returnDept.toString());
                } else { // There is no Employee with the indicated ID
                    System.out.println("There is no Department with DEPNO " + input);
                }
            } catch (IOException ioe) {
                System.err.println("ERROR: IOException error reported: " + ioe.getMessage());
            }
        } else {
            System.err.println("ERROR: You must first try to connect to the database with the method .connectDB()");
        }
    }

    // Implementation from Menu interface
    @Override
    public void executeAddDepartment() {
        if (this.connectionFlag) {
            BufferedReader reader = new BufferedReader(this.isr); // To read user input
            try {
                System.out.println("Insert new Department's ID:");
                System.out.print(USER_INPUT);
                String depno = reader.readLine();
                if (!depno.matches("\\d+")) { // Check if the output is not numeric
                    System.err.println("ERROR: Please provide a valid Department ID. Department's ID are Integer values");
                    return;
                } else if (findDepartmentById(Integer.parseInt(depno)) != null) { // There is already an Employee with that ID
                    System.err.println("ERROR: There is already an Department with the same ID");
                    return;
                }
                System.out.println("Insert new Department's NAME:");
                System.out.print(USER_INPUT);
                String name = reader.readLine();
                if (name.isEmpty()) { // Check for empty input
                    System.err.println("ERROR: You can't leave the information empty");
                    return;
                }
                System.out.println("Insert new Department's LOCATION:");
                System.out.print(USER_INPUT);
                String location = reader.readLine();
                if (location.isEmpty()) { // Check for empty input
                    System.err.println("ERROR: You can't leave the information empty");
                    return;
                }
                // Everything is good to execute the method
                Department newDepartment = new Department(Integer.parseInt(depno), name, location); // Create Employee object
                this.addDepartment(newDepartment);
                System.out.printf("%sNew Department added successfully!%s\n", GREEN_FONT, RESET);
            } catch (IOException ioe) {
                System.err.println("ERROR: IOException error reported: " + ioe.getMessage());
            }
        } else {
            System.err.println("ERROR: You must first try to connect to the database with the method .connectDB()");
        }
    }

    // Implementation from Menu interface
    @Override
    public void executeUpdateDepartment() {
        if (this.connectionFlag) {
            BufferedReader reader = new BufferedReader(this.isr); // To read user input
            try {
                System.out.println("Insert Department's ID:");
                System.out.print(USER_INPUT);
                String input = reader.readLine();
                if (!input.matches("\\d+")) { // Check if the output is not numeric
                    System.err.println("ERROR: Please provide a valid Department ID. Department's ID are Integer values");
                    return;
                }
                Department returnDept = this.findDepartmentById(Integer.parseInt(input));
                if (returnDept == null) { // Check if there is an Employee with the indicated ID
                    System.out.println("There is no Department with DEPNO " + input);
                    return;
                }
                // Execute IDAO method
                Department updated = updateDepartment(Integer.parseInt(input));
                System.out.println(updated.toString());
            } catch (IOException ioe) {
                System.err.println("ERROR: IOException error reported: " + ioe.getMessage());
            }
        } else {
            System.err.println("ERROR: You must first try to connect to the database with the method .connectDB()");
        }
    }

    // Implementation from Menu interface
    @Override
    public void executeDeleteDepartment() {
        if (this.connectionFlag) {
            BufferedReader reader = new BufferedReader(this.isr); // To read user input
            try {
                System.out.println("Insert Department's ID:");
                System.out.print(USER_INPUT);
                String input = reader.readLine();
                if (!input.matches("\\d+")) { // Check if the output is not numeric
                    System.err.println("ERROR: Please provide a valid Department ID. Department's ID are Integer values");
                    return;
                }
                Department returnDept = this.findDepartmentById(Integer.parseInt(input));
                if (returnDept == null) { // Check if there is an Employee with the indicated ID
                    System.out.println("There is no Department with DEPNO " + input);
                    return;
                }
                // Execute IDAO method
                Department deleted = deleteDepartment(Integer.parseInt(input));
                System.out.println(deleted.toString());
            } catch (IOException ioe) {
                System.err.println("ERROR: IOException error reported: " + ioe.getMessage());
            }
        } else {
            System.err.println("ERROR: You must first try to connect to the database with the method .connectDB()");
        }
    }

    // Implementation from Menu interface
    @Override
    public void executeFindEmployeesByDept() {
        if (this.connectionFlag) {
            BufferedReader reader = new BufferedReader(this.isr); // To read user input
            try {
                System.out.println("Insert Department's ID:");
                System.out.print(USER_INPUT);
                String input = reader.readLine();
                if (!input.matches("\\d+")) { // Check if the output is not numeric
                    System.err.println("ERROR: Please provide a valid Department ID. Department's ID are Integer values");
                    return;
                }
                Department returnDept = this.findDepartmentById(Integer.parseInt(input));
                if (returnDept == null) { // Check if there is an Employee with the indicated ID
                    System.out.println("There is no Department with DEPNO " + input);
                    return;
                }
                // Execute IDAO method
                ArrayList<Employee> departmentEmployees = (ArrayList<Employee>) findEmployeesByDept(Integer.parseInt(input));
                String row = "+" + "-".repeat(7) + "+" + "-".repeat(16) + "+" + "-".repeat(16) + "+";
                if (departmentEmployees == null || departmentEmployees.isEmpty()) { // No Employees in Department case
                    System.out.println("There are currently no Employees in the Department");
                } else {
                    System.out.println(row);
                    System.out.printf("| %-5s | %-14s | %-14s |\n", "EMPNO", "NOMBRE", "PUESTO");
                    System.out.println(row);
                    for (Employee e : departmentEmployees) {
                        System.out.printf("| %-5s | %-14s | %-14s |\n", e.getEmpno(), e.getName(), e.getPosition());
                    }
                    System.out.println(row);
                }
            } catch (IOException ioe) {
                System.err.println("ERROR: IOException error reported: " + ioe.getMessage());
            }
        } else {
            System.err.println("ERROR: You must first try to connect to the database with the method .connectDB()");
        }
    }
}
