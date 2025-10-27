package SimpleElevatorMVP;
import java.util.*;

/**
 * An instant-moving elevator that efficiently transports guests to different floors
 * given pick-up and drop-off requests.
 * 
 * @author Victor Gong
 * @version October 26, 2025
 */

public class Elevator {
    private int currentFloor; //Current floor the elevator is on
    private int direction; //Direction that elevator is moving in
    private RequestTracker pickupReq; //Requests to pick up from floor
    private RequestTracker dropoffReq; //Requests to drop off to floor
    private int timestamp = 0; //Internal timestamp for operations

    //Initializes the elevator stationary and empty of requests
    public Elevator() {
        this.currentFloor = 1;
        this.direction = 0;
        this.pickupReq = new RequestTracker();
        this.dropoffReq = new RequestTracker();
        this.timestamp = 0;
        update();
    }

    /**
     * Starts the operation of the elevator
     */
    public void start() {
        update();
    }

    /**
     * Prompts user for further actions to elevator
     */
    private void promptUserAction() {
        /*
        String actionPrompt = "What would you like to do:\n(1) Pickup\n(2) Dropoff";
        int actionType = TextPrompt.promptInt(actionPrompt, 1, 2);
        */
        String pickupPrompt = "What floor(s) would you like to call pick up?";
        Vector<Integer> pickupFloors = TextPrompt.promptSequence(pickupPrompt, 1, Setting.NUM_FLOORS, " ");
        String dropoffPrompt = "What floor(s) would you like to call drop off?";
        Vector<Integer> dropoffFloors = TextPrompt.promptSequence(dropoffPrompt, 1, Setting.NUM_FLOORS, " ");
        
        for (int f: pickupFloors) addPickup(f);
        for (int f: dropoffFloors) addDropoff(f);

        System.out.println(this);

        update();
    }

    /**
     * Adds a pickup request to the set
     * @param floor the floor to pick up from
     */
    private void addPickup(int floor) {
        pickupReq.addRequest(floor, timestamp);
        timestamp++;
    }

    /**
     * Adds a dropoff request to the set
     * @param floor the floor to drop off to
     */
    private void addDropoff(int floor) {
        dropoffReq.addRequest(floor, timestamp);
        timestamp++;
    }
    
    /**
     * Completes a pickup request at specific floor
     * @param floor pickup floor
     */
    private void completePickup(int floor) {
        if (pickupReq.completeRequest(floor)) {
            System.out.println("Elevator picked up guests at floor " + floor);
        }
    }

    /**
     * Completes a dropoff request at specific floor
     * @param floor dropoff floor
     */
    private void completeDropoff(int floor) {
        if (dropoffReq.completeRequest(floor)) {
            System.out.println("Elevator dropped off guests at floor " + floor);
        }
    }

    /**
     * Moves elevator to a specific floor and completes all requests there
     * @param floor the floor to move to
     */
    private void addressFloor(int floor) {
        System.out.println("-----------------------------");

        //If going to same floor as current, prompt user for more actions
        if (floor == currentFloor) {
            direction = 0;
            promptUserAction();
        }
        System.out.println("Elevator moved to floor " + floor);

        //Complete any pickups or dropoffs (note: RequestTracker handles non-existence)
        completePickup(floor);
        completeDropoff(floor);

        //Update elevator states
        int delta = floor - currentFloor;
        currentFloor = floor;
        direction = delta/Math.abs(delta); 

        System.out.println("-----------------------------");
        
        try {
            Thread.sleep(Setting.SIMUL_TIMESTEP); //Delay between every action
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        update();
    }

    /**
     * Updates decision-making after state change to elevator.
     * If currently moving up or down:
     * (1) Continue to complete pickup and dropoff requests in that direction until finished
     * (2) Reverse direction to address requests on other side (unless no requests, then return to floor N/2)
     * If stationary:
     * (1) Move toward 
     */
    private void update() {

        int nextPickup = pickupReq.getNextFloor(currentFloor, direction);
        int nextDropoff = dropoffReq.getNextFloor(currentFloor, direction);
        int distPickup = Math.abs(currentFloor - nextPickup);
        int distDropoff = Math.abs(currentFloor - nextDropoff);

        //Address nearest floor first (if equal, addressFloor does both)
        if (nextPickup != -1 && nextDropoff != -1) {
            if (distPickup < distDropoff) {
                addressFloor(nextPickup);
            }
            else {
                addressFloor(nextDropoff);
            }
        }
        else if (nextPickup != -1) {
            addressFloor(nextPickup);
        }
        else if (nextDropoff != -1) {
            addressFloor(nextDropoff);
        }
        else {
            //Both invalid, flip direction if more requests, otherwise return to middle floor
            if (pickupReq.isEmpty() && dropoffReq.isEmpty()) {
                addressFloor(Setting.NUM_FLOORS / 2); //Return to middle
            } else {
                direction = -direction; //Sweep other side
            }
            update();
        }
        
    }

    public String toString() {
        String ret = "";
        ret += "Elevator State:\n-----------------------------\n";
        ret += "Current Floor: " + currentFloor + "\n";
        ret += "Pickups: " + pickupReq + "\n";
        ret += "Dropoffs: " + dropoffReq + "\n";
        ret += "-----------------------------\n";
        return ret;
    }
}
