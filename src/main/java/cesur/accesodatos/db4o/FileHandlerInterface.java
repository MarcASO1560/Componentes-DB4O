package cesur.accesodatos.db4o;

/**
 * Interface for local DB4O database.
 *
 * @author Marc Albert Seguí Olmos
 */

public interface FileHandlerInterface {
    /**
     * Method to check the existence of a file.
     * @return Boolean indicating if the file exists or not.
     */
    public boolean checkDBExists();
    /**
     * Closes the file resource if it is open.
     */
    public void closeConnection(); // The name is the same as the components that use a remote database but since we use the same menu, I have considered calling it the same so as not to have to change anything.
}
