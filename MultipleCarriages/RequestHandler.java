package MultipleCarriages;

import java.util.*;

/**
 * Tracks elevator requests to specific floors at given times.
 */
public class RequestHandler {
    private TreeSet<Request> req; //Set of all requests sorted by floor

    /**
     * Initializes a new RequestTracker with empty set of requests
     */
    public RequestHandler() {
        req = new TreeSet<>();
    }

    /**
     * Adds a request to the current set
     * @param floor the floor to add
     * @param timestamp timestamp of action
     */
    public void addRequest(int floor, int timestamp) {
        req.add(new Request(floor, timestamp));
    }

    /**
     * Completes a request to a floor by removing it from the set.
     * If request does not exist, does nothing.
     * 
     * @param floor the floor to complete
     * @return whether or not the request was found and completed
     */
    public boolean completeRequest(int floor) {
        return req.remove(new Request(floor, 0));
    }
    /**
     * Computes nearest floor request to current floor given transit direction.
     * If direction is 0 (stationary), then returns the floor with earliest timestamp.
     * If the request set is empty, the middle floor (N/2) is returned instead.
     * 
     * @param currentFloor the elevator's current floor
     * @param direction the elevator's transit direction
     * @return next immediate floor, or -1 if none exist
     */
    public int getNextFloor(int currentFloor, int direction) {
        if (direction < 0) {
            //First request below current floor
            Request next = req.floor(new Request(currentFloor, 0));
            if (next == null) return -1;
            return next.floor;
        }
        else if (direction > 0) {
            //First request above current floor
            Request next = req.ceiling(new Request(currentFloor, 0));
            if (next == null) return -1;
            return next.floor;
        }
        else {
            //Find earliest request via timestamp
            Request earliest = null;
            for (Request r: req) {
                if (earliest == null || r.timestamp < earliest.timestamp) {
                    earliest = r;
                }
            }
            if (earliest == null) return -1;
            return earliest.floor;
        }
    }

    /**
     * Checks if request set is empty
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return req.isEmpty();
    }

    public String toString() {
        return req.toString();
    }

    /*
     * Stores requests at floor and given time
     */
    private class Request implements Comparable<Request>{
        int floor;
        int timestamp;
        public Request(int floor, int timestamp) {
            this.floor = floor;
            this.timestamp = timestamp;
        }
        public int compareTo(Request o) {
            return floor - o.floor;
        }
        public String toString() {
            return "" + floor;
        }
    }

}
