package gitlet;

import java.io.IOException;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Mahmoud Khaled
 */
public class Main {

    /**
     * this fucntion is to print a given message and then stop the program
     * it happens when somthing goes wrong
     * @param message
     */
    public static void errorMessage(String message){
        System.out.println(message);
        System.exit(0);
    }

    /**
     * function to validate number of operands entered in command line
     * @param args array of Strings contain the operands
     * @param n the number we compare with it  the length of the array
     */
    public static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            errorMessage("Incorrect operands.");
        }
    }
    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args)  {
        if (args.length == 0) {
            errorMessage("Please enter a command.");
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                validateNumArgs(args, 1);
                Repository.init();
                break;
            case "add":
                validateNumArgs(args, 2);
                Repository.add(args[1]);
                break;
            case "commit":
                if (args.length == 1){
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                validateNumArgs(args, 2);
                Repository.commit(args[1]);
                break;
            case "rm":
                validateNumArgs(args, 2);
                Repository.rm(args[1]);
                break;
            case "log" :
                validateNumArgs(args, 1);
                Repository.log();
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
              else  if (args.length == 3){
                    Repository.checkOutFile(args[2]);
                }
               else if (args.length == 4){
                    Repository.checkOutFileFromGivenCommit(args[1], args[3]);
                }
                else {
                    errorMessage("Incorrect operands.");
                }

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
