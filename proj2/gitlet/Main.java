package gitlet;

import java.io.IOException;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Mahmoud Khaled
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void errorMessage(String message){
        System.out.println(message);
        System.exit(0);
    }
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            errorMessage("Please enter a command.");
        }
        // TODO : create instance of repo to work with it
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                Repository.init();
                // TODO: handle the `init` command
                break;
            case "add":
                Repository.add(args[1]);
                // TODO: handle the `add [filename]` command
                break;
            // TODO: FILL THE REST IN
            case "commit":
                Repository.commit(args[1]);
                // TODO : fill commit command
                break;
            case "rm":
                Repository.rm(args[1]);
                // TODO : fill rm command
                break;
            case "log" :
                //TODO : fill log command
                break;
            case "global-log" :
                // TODO : fill global command
                break;
            case  "find" :
                // TODO : fill find command
                break;
            case "status" :
                // TODO : fill the status command
                break;
            case "checkout" :
                // TODO : fill the checkout command
                break;
            case "branch" :
                // TODO : fill branch command
                break;
            case "rm-branch":
                // TODO : fill the rm-branch command
                break;
            case "reset" :
                // TODO : fill the reset command
                break;
            case "merge":
                // TODO : fill the merge command
                break;
            default:
                errorMessage("No command with that name exists.");
                break;
        }
    }
}
