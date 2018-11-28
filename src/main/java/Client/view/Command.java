/*
 * The MIT License
 *
 * Copyright 2017 Leif Lindbäck <leifl@kth.se>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package Client.view;

/**
 * Defines all commands that can be performed by a user of the chat application.
 * @author Frank
 * 
 */
public enum Command {

  REGISTER,
  
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
