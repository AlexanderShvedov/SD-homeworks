import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.io.*;


public class CommandLine {
    private static Map<String, String> variables;
    private static boolean testFlag = false;
    private static String testAnswer = "";

    public CommandLine() {
        //initialization for all global variables
        initialization();

        // line reading
        while (true) {
            Scanner in = new Scanner(System.in);
            String command = in.nextLine();

            // line parsing
            parser(command);
        }
    }

    private static void initialization() {
        variables = new HashMap<>();
    }

    private static void parser(String command) {

        // finding all variables in comandline
        Pattern pattern = Pattern.compile("\\w+\\=[\\w!,.;/:]+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);
        List<String> expressions = new ArrayList<>();
        while (matcher.find()) {
            String expression = command.substring(matcher.start(), matcher.end());
            expressions.add(expression + " ");
            String[] variableAndValue = expression.split("\\=");
            variables.put(variableAndValue[0], variableAndValue[1]);
        }
        for (String expression: expressions) {
            command = command.replace(expression, "");
        }

        // replace all variables with their values
        pattern = Pattern.compile("\\$(\\w)+", Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(command);
        String commandWithoutVariables = command;

        while (matcher.find()) {
            String expression = command.substring(matcher.start(), matcher.end());
            String variable = expression.substring(1);
            if (variables.containsKey(variable)) {
                commandWithoutVariables = commandWithoutVariables.replace(expression, variables.get(variable));
            } else {
                System.out.println("Error: Unknown variable");
            }
        }

        // split line on commands
        String[] commands = commandWithoutVariables.split(" \\| ");

        //commands execution
        String lastCommandResult = "";
        boolean correctFlag = true;
        for (int i = 0; i < commands.length; i++) {
            if (!correctFlag) {
                break;
            }
            String[] functionAndArguments = commands[i].split("\\s+");
            String function = functionAndArguments[0];
            String[] arguments;
            if (i == 0) {
                arguments = new String[functionAndArguments.length - 1];
                System.arraycopy(functionAndArguments, 1, arguments, 0, functionAndArguments.length - 1);
            } else {
                arguments = new String[functionAndArguments.length];
                arguments[0] = lastCommandResult;
                System.arraycopy(functionAndArguments, 1, arguments, 1, functionAndArguments.length - 1);
            }
            switch (function) {
                case "cat":
                    if (arguments.length == 1) {
                        lastCommandResult = cat(arguments[0]);
                    } else {
                        correctFlag = false;
                        if (testFlag) {
                            testAnswer = "cat error: expected 1 argument, get: " + arguments.length;
                        } else {
                            System.err.format("cat error: expected 1 argument, get: %d\n", arguments.length);
                        }
                    }
                    break;
                case "echo":
                    if (arguments.length > 0) {
                        lastCommandResult = echo(arguments);
                    } else {
                        correctFlag = false;
                        if (testFlag) {
                            testAnswer = "echo error: expected at least 1 argument";
                        } else {
                            System.err.format("echo error: expected at least 1 argument\n");
                        }
                    }
                    break;
                case "wc":
                    if (arguments.length == 1) {
                        lastCommandResult = String.join(" ", Arrays.toString(wc(arguments[0])).split("[\\[\\]]")[1].split(", "));
                    } else {
                        correctFlag = false;
                        if (testFlag) {
                            testAnswer = "wc error: expected 1 argument, get: " + arguments.length;
                        } else {
                            System.err.format("wc error: expected 1 argument, get: %d\n", arguments.length);
                        }
                    }
                    break;
                case "pwd":
                    if (arguments.length == 0) {
                        lastCommandResult = pwd();
                    } else {
                        correctFlag = false;
                        if (testFlag) {
                            testAnswer = "pwd error: expected 0 argument, get: " + arguments.length;
                        } else {
                            System.err.format("pwd error: expected 0 argument, get: %d\n", arguments.length);
                        }
                    }
                    break;
                case "ls":
                    if (arguments.length == 1) {
                        lastCommandResult = String.join(" ", pwd1(arguments[0]));
                    } else {
                        correctFlag = false;
                        if (testFlag) {
                            testAnswer = "ls error: expected 1 argument, get: " + arguments.length;
                        } else {
                            System.err.format("ls error: expected 1 argument, get: %d\n", arguments.length);
                        }
                    }
                    break;
                case "exit":
                    exit();
                    break;
                default:
                    correctFlag = false;
                    if (testFlag) {
                        testAnswer = "get unknown comamnd: " + function;
                    } else {
                        System.err.format("get unknown comamnd: %s\n", function);
                    }
                    break;
            }
        }

        if (correctFlag) {
            if (testFlag) {
                testAnswer = lastCommandResult;
            } else {
                System.out.println(lastCommandResult);
            }
        }
    }

    public static String pwd() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }

    public static String[] pwd1(String pathName) { // список файлов (ls)
        File path = new File(pathName);
        return path.list();
    }

    public static Integer[] wc(String fileName) {
        String data = cat(fileName);

        // Считаем строки
        Integer countLines = 0;
        if(data.length() != 0) {
            countLines += 1;
        }
        for(int i=0; i<data.length(); ++i) {
            if (data.charAt(i) == '\n') {
                countLines += 1;
            }
        }

        // Считаем слова
        Integer countWords = 0;
        String trimData = data.trim();
        if (!trimData.isEmpty())
            countWords = trimData.split("\\s+").length;


        // Считаем символы
        Integer countSymbols = data.length();

        return new Integer[] { countLines, countWords, countSymbols };

    }

    public static String echo(String[] arr) {
        return String.join(" ", arr);
    }

    public static void exit() {
        System.exit(0);
    }

    public static String cat(String fileName) {
        String s = new String();
        try {
            s = new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
        return s;
    }

    public CommandLine(boolean flag) {
        if (flag) {
            initialization();
            testFlag = true;
        }
    }

    public String testParser(String command) {
        if (testFlag) {
            parser(command);
        }
        return testAnswer;
    }
}