package MultipleCarriages;
import java.util.*;

/**
 * An instant-moving elevator that efficiently transports guests to different floors
 * given pick-up and drop-off requests.
 * 
 * @author Victor Gong
 * @version October 26, 2025
 */

public class Elevator {
    private int id; //Unique identifier for every elevator
    private int currentFloor; //Current floor the elevator is on
    private int direction; //Direction that elevator is moving in
    private RequestHandler pickupReq; //Requests to pick up from floor
    private RequestHandler dropoffReq; //Requests to drop off to floor
    private int timestamp = 0; //Internal timestamp for operations

    //Initializes the elevator stationary and empty of requests
    public Elevator(int id) {
        this.id = id;
        this.currentFloor = 1;
        this.direction = 0;
        this.pickupReq = new RequestHandler();
        this.dropoffReq = new RequestHandler();
        this.timestamp = 0;
        update();
    }

    /**
     * Adds a pickup request to the set
     * @param floor the floor to pick up from
     */
    public void addPickup(int floor) {
        pickupReq.addRequest(floor, timestamp);
        timestamp++;
    }

    /**
     * Adds a dropoff request to the set
     * @param floor the floor to drop off to
     */
    public void addDropoff(int floor) {
        dropoffReq.addRequest(floor, timestamp);
        timestamp++;
    }
    
    /**
     * Completes a pickup request at specific floor
     * @param floor pickup floor
     */
    private void completePickup(int floor) {
        if (pickupReq.completeRequest(floor)) {
            System.out.println("Elevator " + id + " picked up guests at floor " + floor);
        }
    }

    /**
     * Completes a dropoff request at specific floor
     * @param floor dropoff floor
     */
    private void completeDropoff(int floor) {
        if (dropoffReq.completeRequest(floor)) {
            System.out.println("Elevator " + id + " dropped off guests at floor " + floor);
        }
    }

    /**
     * Moves elevator to a specific floor and completes all requests there
     * @param floor the floor to move to
     * @return true if any floors addressed, false otherwise
     */
    private boolean addressFloor(int floor) {
        System.out.println("-----------------------------");

        if (floor == currentFloor) {
            //If going to same floor as current and no more requests, reached steady-state
            if (getNumRequests() == 0) {
                return false;
            }
        }
        System.out.println("Elevator " + id + " moved to floor " + floor);

        //Complete any pickups or dropoffs (note: RequestTracker handles non-existence)
        completePickup(floor);
        completeDropoff(floor);

        //Update elevator states
        int delta = floor - currentFloor;
        currentFloor = floor;
        direction = delta == 0 ? 0 : delta/Math.abs(delta); 

        System.out.println("-----------------------------");

        try {
            Thread.sleep(Setting.SIMUL_TIMESTEP); //Delay between every action
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Updates decision-making after state change to elevator.
     * If currently moving up or down:
     * (+) Continue to complete pickup and dropoff requests in that direction until finished
     * (+) Reverse direction to address requests on other side (unless no requests, then return to floor N/2)
     * If no more requests:
     * (+) Return to middle floor to await more commands
     * @return true if the elevator's state changed, or false if idle
     */
    public boolean update() {

        int nextPickup = pickupReq.getNextFloor(currentFloor, direction);
        int nextDropoff = dropoffReq.getNextFloor(currentFloor, direction);
        int distPickup = Math.abs(currentFloor - nextPickup);
        int distDropoff = Math.abs(currentFloor - nextDropoff);

        //Address nearest floor first (if equal, addressFloor does both)
        if (nextPickup != -1 && nextDropoff != -1) {
            if (distPickup < distDropoff) {
                return addressFloor(nextPickup);
            }
            else {
                return addressFloor(nextDropoff);
            }
        }
        else if (nextPickup != -1) {
            return addressFloor(nextPickup);
        }
        else if (nextDropoff != -1) {
            return addressFloor(nextDropoff);
        }
        else {
            //Both invalid, flip direction if more requests, otherwise return to middle floor
            if (getNumRequests() == 0) {
                return addressFloor(Setting.NUM_FLOORS / 2); //Return to middle
            } else {
                direction = -direction; //Sweep other side
                return true;
            }
        }
        
    }

    public int getId() {
        return id;
    }

    public int getDirection() {
        return direction;
    }

    public int getFloor() {
        return currentFloor;
    }

    public int getNumRequests() {
        return pickupReq.getSize() + dropoffReq.getSize();
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
