
package Client.view;

import java.rmi.RemoteException;
import java.util.Scanner;

/**
 * Reads and interprets user commands. The command interpreter will run in a separate thread, which
 * is started by calling the <code>start</code> method. Commands are executed in a thread pool, a
 * new prompt will be displayed as soon as a command is submitted to the pool, without waiting for
 * command execution to complete.
 * @author Liming Liu
 */
public class NonBlockingInterpreter implements Runnable {
	/**
	 * console objects
	 */
    private static final String PROMPT = "> ";
    private final Scanner console = new Scanner(System.in);
    
    private boolean receivingCmds = false;
    private CommandActions commandActions=new CommandActions();
    private ThreadSafeStdOut outMgr=new ThreadSafeStdOut();
    
    public NonBlockingInterpreter() throws RemoteException {
       super();
    }    
    /**
     * Interprets and performs user commands.
     */
    @Override
    public void run() {
        while (receivingCmds) {
            try {
                CmdLineParser cmdLine = new CmdLineParser(readNextLine());
                switch (cmdLine.getCmd()) {
                case REMOVE:
                	commandActions.remove(cmdLine);
                	break;
                case UPDATE:
                	commandActions.update(cmdLine);
                	break;
                case STORE:
                	commandActions.store(cmdLine);
                	break;
                case PERMISSION:
                	commandActions.permission(cmdLine);
                	break;
                case RETRIEVE:
                	commandActions.retrieve(cmdLine);
                	break;
                case CONNECT:
                	commandActions.connect(cmdLine);
                	break;
                case REGISTER:
                	commandActions.register(cmdLine);
                	break;
                case LOGIN:
                	commandActions.login(cmdLine);
                	break;
                case LISTALL:
                	commandActions.listall(cmdLine);
                	break;
                case QUIT:
                	receivingCmds = false;
                	commandActions.quit(cmdLine);
                	break;
                default:
                	break;
                } 
            }catch (Exception e) {
                outMgr.println("Operation failed");
                e.printStackTrace();
            }
        }
    }

    /**
     * Starts the interpreter. The interpreter will be waiting for user input when this method
     * returns. Calling <code>start</code> on an interpreter that is already started has no effect.
     */
    public void start() {
        if (receivingCmds) {
            return;
        }
        receivingCmds = true;
        new Thread(this).start();
    }
   
    
    private String readNextLine() {
        outMgr.print(PROMPT);
        return console.nextLine();
    }
	
}
