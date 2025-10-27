package SimpleElevatorMVP;

/**
 * Handles listening to user inputs and sending actions to elevator
 */
class Main {
    public static void main(String args[]) {
        Elevator elevator = new Elevator();
        elevator.start();
    }
}