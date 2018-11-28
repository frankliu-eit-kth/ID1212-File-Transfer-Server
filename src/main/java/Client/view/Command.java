
package Client.view;

/**
 * Defines all commands that can be performed by a user of the chat application.
 * @author Liming Liu
 * 
 */
public enum Command {
  /**
   * @syntax REGISTER username password	
   */
  REGISTER,
  /**
   * @syntax LOGIN username password
   */
  LOGIN,
  /**
   * @syntax CONNECT hostname
   * connect to the file transfer server + fetch remote controller stub from the register
   */
  CONNECT,
  /**
   * @syntax QUIT
   * clean all user status, disconnect to the server, cancel remote client stub from register
   */
  QUIT,
  /**
   * @syntax LISTALL
   * list all file metadatas
   */
  LISTALL,
  /**
   * @syntax STORE filename file_path
   * store local file to server
   * 
   */
  STORE,
  /**
   * @syntax PERMISSION filename read/write
   * change the permission for other users, only owner is able to change
   */
  PERMISSION,
  /**
   * @syntax REMOVE filename
   * remove file from server
   * if owner set permission to write then everyone can remove
   */
  REMOVE,
  /**
   * @syntax UPDATE filename file_path
   * update file in database
   * if owner set permission to write then everyone can update
   */
  UPDATE,
  /**
   * @syntax RETRIEVE filename
   * retrieve file from server
   * everyone can retrieve every file in database
   */
  RETRIEVE,
  /**
   * for error control
   */
  NO_COMMAND
  
}
