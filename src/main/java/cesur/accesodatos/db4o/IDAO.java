package cesur.accesodatos.db4o;

import java.util.List;

/**
 * Data operation related interface. This interface is meant to have all needed methods to
 * retrieve and write data regardless the information storage system (database system or files).
 *
 * @author Pascual Barrer Ferrer
 */

public interface IDAO {
    /**
     * Method to get all {@link Employee}s from the storage system.
     * @return List of {@link Employee} objects. The list could be empty.
     */
    public List<Employee> findAllEmployees();
    /**
     * Method to get the {@link Employee} object from a given ID.
     * @param id Employee's ID (Integer value).
     * @return Corresponding {@link Employee} object or null in case of no matches found.
     */
    public Employee findEmployeeById(Object id);
    /**
     * Method to add new {@link Employee} to the storage system.
     * @param employee {@link Employee} object with all attributes set.
     */
    public void addEmployee(Employee employee);
    /**
     * Method to update an existing {@link Employee}.
     * This method asks for all the information required to update an {@link Employee}.
     * @param id Employee's ID (Integer value).
     * @return Updated {@link Employee} object. Null values are not possible to be returned since the object existence must be checked before calling.
     */
    public Employee updateEmployee(Object id);
    /**
     * Method to delete an existing {@link Employee} from the storage system.
     * @param id Employee's ID (Integer value).
     * @return Deleted {@link Employee} object.  Null values are not possible to be returned since the object existence must be checked before calling.
     */
    public Employee deleteEmployee(Object id);
    /**
     * Method to get all {@link Department}s from the storage system.
     * @return List of {@link Department} objects. The list could be empty.
     */
    public List<Department> findAllDepartments();
    /**
     * Method to get the {@link Department} object from a given ID.
     * @param id Department's ID (Integer value).
     * @return Corresponding {@link Department} object or null in case of no matches found.
     */
    public Department findDepartmentById(Object id);
    /**
     * Method to add new {@link Department} to the storage system.
     * @param department {@link Department} object with all attributes set.
     */
    public void addDepartment(Department department);
    /**
     * Method to update an existing {@link Department}.
     * This method asks for all the information required to update a {@link Department}.
     * @param id Department's ID (Integer value).
     * @return Updated {@link Department} object. Null values are not possible to be returned since the object existence must be checked before calling.
     */
    public Department updateDepartment(Object id);
    /**
     * Method to delete an existing {@link Department} from the storage system.
     * @param id Department's ID (Integer value).
     * @return Deleted {@link Department} object.  Null values are not possible to be returned since the object existence must be checked before calling.
     */
    public Department deleteDepartment(Object id);
    /**
     * Method to get an {@link Employee} objects list from a given {@link Department}'s ID.
     * @param idDept Department's ID (Integer value).
     * @return List of {@link Employee} objects that have the depno attribute with the given ID. The list could be empty.
     */
    public List<Employee> findEmployeesByDept(Object idDept);
}
