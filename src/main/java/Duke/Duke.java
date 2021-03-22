package Duke;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import Duke.Task.*;

public class Duke {
    private static final String VERSION = "Duke - Version 1.0";
    private static final String DIVIDER = "===================================================";
    private static final String LOGO = " ____        _        \n" + "|  _ \\ _   _| | _____ \n"
            + "| | | | | | | |/ / _ \\\n" + "| |_| | |_| |   <  __/\n" + "|____/ \\__,_|_|\\_\\___|\n";
    private static final String GREETING = "Hello! I'm Duke\n" + "What can I do for you?\n";

    private static final Scanner SCANNER = new Scanner(System.in);

    private static final char INPUT_COMMENT_MARKER = '#';
    private static final String COMMAND_TODO_WORD = "todo";
    private static final String COMMAND_EVENT_WORD = "event";
    private static final String COMMAND_DEADLINE_WORD = "deadline";
    private static final String COMMAND_DONE_WORD = "done";
    private static final String COMMAND_LIST_WORD = "list";
    private static final String COMMAND_END_WORD = "bye";
    private static final String COMMAND_DELETE_WORD = "delete";
    private static final String BYE = "Bye. Hope to see you again soon!";
    private static final String ERROR_MESSAGE = "☹ OOPS!!! I'm sorry, but I don't know what that means :-(";
    private static final String FILE_LOCATION = "src/main/java/Duke/Duke.txt";
    private static final String COMMAND_FIND_WORD = "find";
    private static final String COMMAND_HELP_WORD = "help";
    private static final String HELP = "You can type:\n"+
    "| List             | list                    |list                              |\n"+
    "| Mark As Done     | done [index]            |done 1                            |\n"+
    "| Delete Task      |delete [index]           |delete 1                          |\n"+
    "| Done Task        |delete [index]`          |done 1                            |\n"+
    "| Find With Keyword|keyword [keyword]        |keyword football                  |\n"+
    "| Add Deadline Task|deadline [task]/by [time]|deadline book update/by 2021-01-03|\n"+
    "| Add Event Task   |event [task]/at [time]   |event FOOD delivery/at 2021-01-01 |\n"+
    "| Add Todo Task    |todo [task]              |todo upgrade game                 |\n"+
    "| Exit Program     |bye                      |bye                               |";
    private static int count;

    public static List<Task> lists = new ArrayList<Task>();

    public static void main(String[] args) {
        showWelcomeMessage();
        readFile(lists);
        showWelcomeMessage();
        while (true) {
            String userCommand = getUserInput();
            echoUserCommand(userCommand);
            int returnValue = executeCommand(userCommand);
            if (returnValue == 0)
                break;
        }
    }

    /***
     *  Before running the system, read the file which contains the information which user type inside before
     ***/
    private static void readFile(List<Task> lists){
        try {
            File file = new File(FILE_LOCATION);
            if (file.createNewFile()) {
                System.out.println("A new file has been created!");
            } else {
                System.out.println("Reading saved Task Lists!");
                Scanner readingFile = new Scanner(file);
                while (readingFile.hasNextLine()) {
                    String line = readingFile.nextLine();
                    String[] parts = line.split("-", 3);
                    String type = parts[0];
                    String isDone = parts[1];
                    String task = parts[2];
                    if (type.equals(COMMAND_EVENT_WORD)) {
                        Task taskInFile = new EventTask(task);
                        lists.add(taskInFile);
                        showToUser(taskInFile.toString());
                        if (isDone.equals("true")) {
                            taskInFile.markAsDone();
                        }
                    }else if (type.equals(COMMAND_DEADLINE_WORD)) {
                        Task taskInFile = new DeadlineTask(task);
                        lists.add(taskInFile);
                        showToUser(taskInFile.toString());
                        if (isDone.equals("true")) {
                            taskInFile.markAsDone();
                        }
                    }else if (type.equals(COMMAND_TODO_WORD)) {
                        Task taskInFile = new TodoTask(task);
                        lists.add(taskInFile);
                        showToUser(taskInFile.toString());
                        if (isDone.equals("true")) {
                            taskInFile.markAsDone();
                        }
                    }
                    count++;
                }
        }
    } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     *  Before end the system, write all the list information inside the file
     ***/
    private static void writeFile(List<Task> lists) {
        try {
            FileWriter writer = new FileWriter(FILE_LOCATION,false);
            for (Task taskInList : lists) {
                writer.write(taskInList.getTaskType() + "-" + taskInList.isDone() + "-" + taskInList.getTask());
                writer.write("\r\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     *  Show welcome message
     ***/
    private static void showWelcomeMessage() {
        showToUser(DIVIDER, DIVIDER, VERSION, LOGO, GREETING, DIVIDER);
    }

    /***
     *  Show users information one line by one line
     ***/
    private static void showToUser(String... message) {
        for (String m : message) {
            System.out.println(m);
        }
    }


    /***
     *  Receive users input
     ***/

    private static String getUserInput() {
        System.out.print("Enter command: ");
        String inputLine = SCANNER.nextLine();
        // silently consume all blank and comment lines
        while (inputLine.trim().isEmpty() || inputLine.trim().charAt(0) == INPUT_COMMENT_MARKER) {
            inputLine = SCANNER.nextLine();
        }
        return inputLine;
    }

    /***
     *  Repeat users command after user input
     ***/
    private static void echoUserCommand(String userCommand) {
        showToUser("[Command entered:" + userCommand + "]", DIVIDER);
    }

    /***
     *  Run users input with a split of type and content
     ***/
    private static int executeCommand(String userInputString) {
        final String[] commandTypeAndParams = splitCommandWordAndArgs(userInputString);
        final String commandType = commandTypeAndParams[0];
        String commandArgs = commandTypeAndParams[1];
        Task taskInput;
        switch (commandType) {
        case COMMAND_TODO_WORD:
            if (checkError(commandArgs)){
            taskInput = new TodoTask(commandArgs);
            lists.add(taskInput);
            showToUser("You have add "+commandType+" "+commandArgs);
            count++;}
            return 1;
        case COMMAND_EVENT_WORD:
            if (checkError(commandArgs)){
            commandArgs = commandArgs.replace("/", "(");
            commandArgs = commandArgs.replace("at", "at:");
            commandArgs = commandArgs+")";
            taskInput = new EventTask(commandArgs);
            showToUser("You have add "+commandType+" "+commandArgs);
            lists.add(taskInput);
            count++;}
            return 1;
        case COMMAND_DEADLINE_WORD:
            if (checkError(commandArgs)){
            commandArgs = commandArgs.replace("/", "(");
            commandArgs = commandArgs.replace("by", "by:");
            commandArgs = commandArgs+")";
            taskInput = new DeadlineTask(commandArgs);
            showToUser("You have add "+commandType+" "+commandArgs);
            lists.add(taskInput);
            count++;}
            return 1;
        case COMMAND_LIST_WORD:
            printList(0,count);
            return 1;
        case COMMAND_DONE_WORD:
            if(checkError(commandArgs)){
            doneItem(commandArgs);
            showToUser("You have "+commandType+" "+commandArgs);}
            return 1;
        case COMMAND_END_WORD:
            endSystem();
            return 0;
        case COMMAND_DELETE_WORD:
            if(checkError(commandArgs)){
            deleteItem(commandArgs);
            showToUser("You have "+commandType+" "+commandArgs);}
            return 1;
        case COMMAND_FIND_WORD:
            if(checkError(commandArgs)){
            findKeyword(commandArgs);}
            return 1;
        case COMMAND_HELP_WORD:
            showHelpMenu();
            return 1;
        default:
            showError();
            return 1;
        }
    }

    /***
     *  Trims the input of when there is a empty space
     ***/
    private static String[] splitCommandWordAndArgs(String rawUserInput) {
        final String[] split = rawUserInput.trim().split("\\s+", 2);
        return split.length == 2 ? split : new String[] { split[0] , "" }; // else case: no parameters
    }

    /***
     *  Print the list when user type list
     ***/
    public static void printList(int startIndex, int endIndex) {
        if (endIndex == 0) {
            System.out.println("List is empty :o\n" + "\n");
        } else {
            for(int i = startIndex; i < endIndex; ++i) {
                System.out.println(" " + (i + 1) + ": " + lists.get(i).toString());
            }
        }
    }

    /***
     *  Show the error message with one divider
     ***/
    public static void showError(){
        showToUser(ERROR_MESSAGE,DIVIDER);
    }

    /***
     *  Mark the selected item as done
     ***/
    public static void doneItem(String doneStringNumber){
        checkError(doneStringNumber);
        int doneInteger = Integer.parseInt(doneStringNumber)-1;
        lists.get(doneInteger).markAsDone();
    }

    /***
     *  Delete the selected item
     ***/
    public static void deleteItem(String deleteStringNumber){
        checkError(deleteStringNumber);
        if(Integer.parseInt(deleteStringNumber)>count){
            showError();
        } else{
            lists.remove(Integer.parseInt(deleteStringNumber)-1);
            count--;
        }
    }

    /***
     *  End the system by write the list into the file and show bye message
     ***/
    public static void endSystem (){
        writeFile(lists);
        showToUser(BYE);
    }

    public static void showHelpMenu(){
        showToUser(HELP);
    }


    /***
     *  Show error message if error appear
     ***/
    public static boolean checkError (String commandArgs){
        if (commandArgs.isEmpty()){
            showError();
            return false;
        }else{
            return true;
        }
    }

    /***
     *  Print the keyword related item if found the keyword inside the content
     ***/
    public static void findKeyword (String keyword){
        for(int i = 0; i < count; ++i){
            if(lists.get(i).toString().contains(keyword)){
                showToUser(lists.get(i).toString());
            }
        }
    }
}