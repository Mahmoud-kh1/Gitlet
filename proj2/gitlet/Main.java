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
    public static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            errorMessage("Incorrect operands.");
        }
    }
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            errorMessage("Please enter a command.");
        }
        // TODO : create instance of repo to work with it
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                validateNumArgs(args, 1);
                Repository.init();
                // TODO: handle the `init` command
                break;
            case "add":
                validateNumArgs(args, 2);
                Repository.add(args[1]);
                // TODO: handle the `add [filename]` command
                break;
            // TODO: FILL THE REST IN
            case "commit":
                if (args.length == 1){
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                validateNumArgs(args, 2);
                Repository.commit(args[1]);
                // TODO : fill commit command
                break;
            case "rm":
                validateNumArgs(args, 2);
                Repository.rm(args[1]);
                // TODO : fill rm command
                break;
            case "log" :
                validateNumArgs(args, 1);
                Repository.log();
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
                if (args.length == 1){
                    errorMessage("Incorrect operands.");
                }
                if (args.length == 3){
                    Repository.checkOutFile(args[2]);
                }
                if (args.length == 4){
                    Repository.checkOutFileFromGivenCommit(args[1], args[3]);
                }
                else {
                    errorMessage("Incorrect operands.");
                }

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
