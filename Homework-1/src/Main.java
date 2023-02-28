import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static Map<String, String> variables;

    public static void main(String[] args) {
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
        Pattern pattern = Pattern.compile("\\w+\\=\\w+", Pattern.CASE_INSENSITIVE);
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
        for (int i = 0; i < commands.length; i++) {
            String[] functionAndArguments = commands[i].split("\\s+");
            // TODO: вызывать нужные команды, при этом не забывать передавать результат в следующий pipe
        }
    }
}
