package VirtualGuests;

/**
 * Handles listening to user inputs and sending actions to elevator
 */
class Main {
    public static void main(String args[]) {
        Dispatcher dispatcher = new Dispatcher();
        boolean isRunning = true;
        while (isRunning) {
            dispatcher.step();
        }
    }
}