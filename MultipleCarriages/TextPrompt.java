package MultipleCarriages;
import java.util.*;

/**
 * Makes textual user input easy with collection of methods for console I/O
 */
public final class TextPrompt {
    /**
     * Get numerical user input in the range lowValid to highValid with prompt message
     * @param prompt prompt message
     * @param lowValid lower bound of response
     * @param highValid upper bound of response
     * @return the user's response
     */
    public static int promptInt(String prompt, int lowValid, int highValid) {
        boolean validInput = false;
        int response = -1;
        while (!validInput) {
            System.out.println(prompt);
            Scanner sc = new Scanner(System.in);
            String line = sc.nextLine();
            try {
                response = Integer.parseInt(line);
                if (lowValid <= response && response <= highValid) {
                    validInput = true;
                } else System.out.println("Invalid response! Try again."); 
                
            } catch (NumberFormatException e) {
                System.out.println("Invalid response! Try again.");
            }
        }
        return response;
    }

    /**
     * Get sequential user input in range lowValid to highValid with prompt message
     * @param prompt prompt message
     * @param lowValid lower bound of response
     * @param highValid upper bound of response
     * @return the user's response
     */
    public static Vector<Integer> promptSequence(String prompt, int lowValid, int highValid, String delim) {
        boolean validInput = false;
        Vector<Integer> numSequence = new Vector<>();
        while (!validInput) {
            System.out.println(prompt);
            Scanner sc = new Scanner(System.in);
            String line = sc.nextLine();
            String[] seq = line.split(delim);
            try {
                validInput = true;
                numSequence = new Vector<>();
                for (String s: seq) {
                    int response = Integer.parseInt(s);
                    if (!(lowValid <= response && response <= highValid)) {
                        validInput = false;
                        System.out.println("Invalid response! Try again.");
                        break;
                    }
                    numSequence.add(response);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid response! Try again.");
            }
        }
        return numSequence;
    }
}
