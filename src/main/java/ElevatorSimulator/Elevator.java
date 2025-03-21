package ElevatorSimulator;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
/**
 * ElevatorWaiting represents the state when the elevator is waiting
 */
class ElevatorWaiting implements ElevatorState {
    @Override
    public void handle(Elevator elevator) {
        if (elevator.isEnabled()){
            if(elevator.getDirectionLamp().isLampOn()) {
                elevator.getDirectionLamp().turnOffLamp();
            }

            // receive instruction packet from scheduler
            for (int i = 0; i < 10; i++) {
                try {
                    elevator.receiveInstructionFromScheduler();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    System.out.println("Elevator " + elevator.getElevatorID() + ": Error executing Instruction");
                }
            }


            // get instruction from box and set new floor
            if (!elevator.getInstructionBox().isEmpty()) { // we have an instruction
                elevator.setNextFloor(elevator.calculateNextFloor());
                System.out.println("Elevator " + elevator.getElevatorID() + ": Next floor set to " + elevator.calculateNextFloor());

                //Turn on direction lamp
                elevator.getDirectionLamp().turnOnLamp(elevator.getMotor().getCurrentDirection());
                elevator.setCurrentState(new ElevatorMoving());
            }
        }
    }
}

/**
 * ElevatorMoving represents when the elevator is in the moving state
 */
class ElevatorMoving implements ElevatorState {
    boolean counter = false;
    @Override
    public void handle(Elevator elevator) {
        if (!counter) {
            System.out.println("Elevator " + elevator.getElevatorID() + ": Changed to ElevatorMoving State");
            counter = true;
            elevator.goToFloor(elevator);
        } else {
            elevator.setCurrentState(new ElevatorWaiting());
        }
    }
}
/**
 * ElevatorHandlingDoor represents when the elevator is in the handling door state
 */
class ElevatorHandlingDoor implements ElevatorState{
    @Override
    public void handle(Elevator elevator) {
        // wait for elevator to be stopped
        System.out.println("Elevator " + elevator.getElevatorID() + ": Changed to HandlingDoor State");

        while (!elevator.isStopped()) {
        }

        // open door
        System.out.println("Elevator " + elevator.getElevatorID() + " opening doors\n");
        elevator.setDoorOpen(true);
        elevator.setTimer(10);

        // Check if the door opened successfully
        if (!elevator.checkDoorOpenedSuccessfully()) {
            // The door didn't open successfully, handle the stuck door
            elevator.handleStuckDoor();
        } else {
            // If the door opened successfully, proceed with normal operation
            try {
                Thread.sleep(3000); // Simulate the door being open for a period
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Handle thread interruption
                System.out.println("Elevator " + elevator.getElevatorID() + ": Interrupted while door open.");
            }

            if(elevator.getFloorFaults().get(elevator.getCurrentFloor()) == 1) {
                System.out.println("Triggering door fault in elevator " + elevator.getElevatorID());
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                }
                elevator.getFloorFaults().set(elevator.getCurrentFloor(), 0);
            }

            elevator.setDoorOpen(false); // Simulating door closing
            elevator.killTimer();

            if (elevator.isEnabled()){
                System.out.println("Elevator " + elevator.getElevatorID() + " closing doors.");
            }
        }


        // remove latest instruction from box
        elevator.getInstructionBox().removeIf(instruction -> instruction.getFloorNumber() == elevator.getCurrentFloor());

        for (Instruction instruction : elevator.getInstructionBox()){
            System.out.println(instruction.getFloorNumber());
        }

        // Sets state to waiting
        System.out.println("Elevator " + elevator.getElevatorID() + ": Changed to Waiting State");
        elevator.setCurrentState(new ElevatorWaiting());
    }
}

/**
 * The Elevator system
 */
public class Elevator implements Runnable {

    private final int elevatorID;
    private boolean stopped;
    private int currentFloor;
    private int nextFloor;
    private boolean doorOpen;
    private boolean elevatorEnabled;
    private ElevatorState currentState;
    DatagramSocket sendSocket, receiveSocket, acknowledgementSocket, requestSocket, endSocket;
    private ArrayList<ElevatorButton> buttonList;
    private ArrayList<ArrivalSensor> arrivalSensors;
    private ArrayList<Instruction> instructionBox;
    private ElevatorMotor motor;
    private DirectionLamp directionLamp;
    private boolean doorOperationSuccess = true; // Assume door operation is successful by default
    private int doorRetryCounter = 0; // Counter for door operation attempts
    private Timer timer;
    private ArrayList<Integer> floorFaults;
    private static int totalMoves = 0;
    private static int totalInstructions;
    private static long startTime;
    private static int totalFloorChanges;


    /**
     * Constructor for Elevator
     *
     * @param elevatorID  - Takes the elevators ID (Cart #)
     * @param sendPort    - Port number the elevator sends from
     * @param receivePort - Port the elevator receives from
     */
    public Elevator(int elevatorID, int sendPort, int receivePort, int acknowledgmentPort, int requestPort) {
        this.elevatorID = elevatorID;
        this.currentFloor = 1;
        this.nextFloor = 1;
        this.doorOpen = false;
        this.stopped = true;
        this.currentState = new ElevatorWaiting();
        this.buttonList = new ArrayList<>();
        this.arrivalSensors = new ArrayList<ArrivalSensor>();
        this.instructionBox = new ArrayList<Instruction>();
        this.motor = new ElevatorMotor();
        this.directionLamp = new DirectionLamp(this);
        this.elevatorEnabled = true;
        this.floorFaults = new ArrayList<>();

        for(int i = 1; i <= 23; i++){
            this.floorFaults.add(0);
        }

        // Set up sockets
        try {
            this.sendSocket = new DatagramSocket(sendPort);
            this.receiveSocket = new DatagramSocket(receivePort);
            this.acknowledgementSocket = new DatagramSocket(acknowledgmentPort);
            this.requestSocket = new DatagramSocket(requestPort);
            this.endSocket = new DatagramSocket(748 + (elevatorID * 20) - 20);
        } catch (SocketException se) {
            se.printStackTrace();
            System.out.println("Failed to create Elevator Sockets");
            System.exit(1);
        }

        System.out.println("Elevator " + elevatorID + " created\n");

        // adds 7 buttons to the elevator's button list
        for (int i = 1; i <= 22; i++) {
            ElevatorButton newButton = new ElevatorButton(i);
            addButton(newButton);
            newButton.setElevator(this);
        }

        // adds an arrival sensor for each floor
        for (int i = 1; i <= 22; i++) {
            ArrivalSensor newSensor = new ArrivalSensor(i, this);
            arrivalSensors.add(newSensor);
        }

        startTime = System.currentTimeMillis();
    }

    /**
     * Makes an empty Elevator
     */
    public Elevator(int elevatorID){
        this.elevatorID = elevatorID;
        this.currentFloor = 1;
        this.nextFloor = 1;
        this.doorOpen = false;
        this.stopped = true;
        this.currentState = new ElevatorWaiting();
        this.buttonList = new ArrayList<>();
        this.arrivalSensors = new ArrayList<ArrivalSensor>();
        this.instructionBox = new ArrayList<Instruction>();
        this.motor = new ElevatorMotor();
        this.directionLamp = new DirectionLamp(this);
        this.elevatorEnabled = true;
        this.floorFaults = new ArrayList<>();
    }

    /**
     * Purely for testing, uses similar logic
     */
    public void runElevator(int floor){
        while(currentFloor != floor){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if(currentFloor < floor) {
                setCurrentFloor(currentFloor + 1);
            }
            else{
                setCurrentFloor(currentFloor - 1);
            }
        }
    }

    /***
     * Get the start time of timer
     * @return timer
     */

    public long getStartTime() {
        return startTime;
    }


    /**
     * this method increments total moves and it compares it to total instructions
     * @return boolean
     */
    public boolean incrementTotalMoves(){
        totalMoves++;
        if (totalMoves == totalInstructions){
            return true;
        }
        return false;
    }

    /**
     * Getter for floor faults
     * @return floor faults
     */
    public ArrayList<Integer> getFloorFaults(){
        return floorFaults;
    }

    /**
     * This method disables the elevator
     */
    public void disableElevator() {
        this.currentState = new ElevatorWaiting();
        this.elevatorEnabled = false;
        this.getMotor().setDirection(-1); //set direction to -1 to signify a disabled elevator
        this.sendResponse(true);
        try {
            Thread.sleep(999999999);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Reset interrupt status
            System.out.println("Sleep failed");
        }
    }

    /**
     * This method returns elevatorEnabled
     * @return - elevatorEnabled
     */
    public boolean isEnabled(){
        return this.elevatorEnabled;
    }

    /**
     * Getter for the elevator ID
     * @return - Elevator ID (Cart #)
     */
    public int getElevatorID() {
        return elevatorID;
    }

    public ElevatorMotor getMotor() {
        return motor;
    }

    /**
     * Getter for lampDirection variable
     *
     * @return
     */
    public DirectionLamp getDirectionLamp() {
        return directionLamp;
    }

    /**
     * Getter for the status of the Cart to see if it's stopped or not
     *
     * @return True = Stopped, False = Running
     */
    public boolean isStopped() {
        return stopped;
    }

    /**
     * This method returns the current floor
     *
     * @return int
     */
    public int getCurrentFloor() {
        return currentFloor;
    }

    /**
     * This method sets the current floor to the specified int
     *
     * @param newFloor represents the next floor to be visited
     */
    public void setCurrentFloor(int newFloor) {
        currentFloor = newFloor;
        totalFloorChanges++; //keep track of floor changes
    }

    /**
     * Returns the next floor to be visited
     *
     * @return int
     */
    public int getNextFloor() {
        return nextFloor;
    }

    /**
     * Sets the next floor to be visited to the specified floor
     *
     * @param newFloor represents the floor to be visited
     */
    public void setNextFloor(int newFloor) {
        nextFloor = newFloor;
    }

    /**
     * This method calculates the next floor to go to
     * @return next floor
     */
    public int calculateNextFloor() {
        int direction = motor.getCurrentDirection();
        int floorToGo = instructionBox.get(0).getFloorNumber();

        if (direction == 1) {
            for (Instruction instruction : instructionBox) {
                if (instruction.getFloorNumber() > floorToGo) {
                    floorToGo = instruction.getFloorNumber();
                }
            }
        } else {
            for (Instruction instruction : instructionBox) {
                if (instruction.getFloorNumber() < floorToGo) {
                    floorToGo = instruction.getFloorNumber();
                }
            }
        }
        // if instruction box is only of size 1, correct the motor direction
        if (instructionBox.size() == 1 && floorToGo < currentFloor) {
            motor.setDirection(0);
        } else if (instructionBox.size() == 1){
            motor.setDirection(1);
        }
        return floorToGo;
    }

    /**
     * Takes elevator to the needed floor
     * @param elevator
     */
    public void goToFloor(Elevator elevator) {
        // go to next floor
        elevator.getMotor().startMotor();
        elevator.setTimer(5);

        while (elevator.getNextFloor() != elevator.getCurrentFloor()) {
            //If statement for the elevator to go up
            if (elevator.getNextFloor() > elevator.getCurrentFloor()) {
                elevator.getMotor().setDirection(1);
                System.out.println("Elevator " + elevator.getElevatorID() + " Going to floor " + elevator.getNextFloor() + " Current floor " + elevator.getCurrentFloor());

                // handle arrival sensor of current floor
                if (elevator.getArrivalSensors().get(elevator.getCurrentFloor() - 1).handleArrivalSensor()) {
                    //sleep for time it takes to go to next floor
                    try {
                        Thread.sleep(2602);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // Reset interrupt status
                        System.out.println("Sleep failed");
                    }
                    //increment floor
                    elevator.setCurrentFloor(elevator.getCurrentFloor() + 1);

                    elevator.setTimer(5);

                    break;
                }

                //sleep for time it takes to go to next floor
                try {
                    Thread.sleep(2602);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Reset interrupt status
                    System.out.println("Sleep failed");
                }

                //increment floor
                elevator.setCurrentFloor(elevator.getCurrentFloor() + 1);
                elevator.setTimer(5); //reset timer
                //send scheduler current floor and direction
                elevator.sendResponse(false);

            }
            //If statement for the elevator to go down
            else if (elevator.getNextFloor() < elevator.getCurrentFloor()) {
                elevator.getMotor().setDirection(0);
                System.out.println("Elevator " + elevator.getElevatorID() + " Going to floor " + elevator.getNextFloor() + " Current floor " + elevator.getCurrentFloor());


                // handle arrival sensor of current floor
                if (elevator.getArrivalSensors().get(elevator.getCurrentFloor() - 1).handleArrivalSensor()) {
                    //sleep for time it takes to go to next floor
                    try {
                        Thread.sleep(2602);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // Reset interrupt status
                        System.out.println("Sleep failed");
                    }
                    //Decrement floor
                    elevator.setCurrentFloor(elevator.getCurrentFloor() - 1);
                    elevator.setTimer(5); //reset timer
                    break;
                }

                //sleep for time it takes to go to next floor
                try {
                    Thread.sleep(2602);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Reset interrupt status
                    System.out.println("Sleep failed");
                }

                //increment floor
                elevator.setCurrentFloor(elevator.getCurrentFloor() - 1);
                elevator.setTimer(5);
                //send scheduler current floor and direction
                elevator.sendResponse(false);
            }
        }

        // Logic to cause a fault if needed
        if (this.floorFaults.get(currentFloor) == 2) {
            System.out.println("Triggering a hard fault in elevator " + elevatorID);
            try { // CORN
                Thread.sleep(10000);
            } catch (InterruptedException e) {
            }
        }

        if (this.isEnabled()){

            System.out.println("Elevator " + elevator.getElevatorID() + " Arrived at floor " + elevator.getCurrentFloor());
            elevator.killTimer(); //stop timer
            Instruction instructionToRemove = elevator.isFloorInQueue(elevator.getCurrentFloor());
            elevator.getInstructionBox().remove(instructionToRemove);

            // stop elevator motor
            elevator.getMotor().stop();

            if (currentFloor >= 11) {
                elevator.getMotor().setDirection(0);
            }
            else {
                elevator.getMotor().setDirection(1);
            }

            elevator.sendResponse(true);

            // Increment total moves
            if (elevator.incrementTotalMoves()){
                long endTime = System.currentTimeMillis();
                System.out.println("Total time elapsed: " + (endTime - elevator.getStartTime()) + "ms");
                System.out.println("Total moves: " + totalFloorChanges);
            }

            // set state to door handling
            elevator.setCurrentState(new ElevatorHandlingDoor());
        }
    }

    /**
     * This method returns true if the door is opened and false otherwise
     *
     * @return boolean
     */
    public boolean isDoorOpen() {
        return doorOpen;
    }

    /**
     * this method sets the doorOpen field to the specified boolean value
     *
     * @param open represents the value to set the field to
     */
    public void setDoorOpen(boolean open) {
        doorOpen = open;
    }

    /**
     * Simulates checking if the elevator door opened successfully.
     * @return true if the door operation was successful, false if the door is stuck.
     */
    public boolean checkDoorOpenedSuccessfully() {
        // Simulate a scenario where the door fails to open correctly after 3 attempts
        if (doorRetryCounter >= 3) {
            doorOperationSuccess = false;
        }
        return doorOperationSuccess;
    }

    /**
     * Handles scenarios where the elevator door is stuck open.
     */
    public void handleStuckDoor() {
        System.out.println("Elevator " + this.elevatorID + ": Door is stuck open. Attempting to resolve.");

        // Increment the retry counter for door operation
        doorRetryCounter++;

        // Attempt to resolve the door issue by simulating a retry operation
        if (doorRetryCounter < 3) {
            System.out.println("Elevator " + this.elevatorID + ": Attempting to close the door, attempt " + doorRetryCounter);

            doorOperationSuccess = true;
        } else {
            // If retries exceed a threshold, log the error
            System.out.println("Elevator " + this.elevatorID + ": Door remains stuck after multiple attempts. Elevator taken out of service for maintenance.");
            this.stopped = true;

        }
    }


    /**
     * Sets current state to the specified ElevatorState
     *
     * @param state represents the state to change the current state to
     */
    public void setCurrentState(ElevatorState state) {
        this.currentState = state;
    }

    /**
     * Adds ElevatorButton to the inside of the elevator
     *
     * @param button specifies the button to be added
     */
    public void addButton(ElevatorButton button) {
        this.buttonList.add(button);
    }

    /**
     * sets the elevator's timer to the specified time.
     *
     * @param time the time to set the timer to
     */
    public void setTimer(int time) {
        this.timer.setTimer(time);
    }

    /**
     * stops the elevator's timer
     */
    public void killTimer() {
        this.timer.killTimer();
    }



    /**
     * Returns an arrayList of instructions
     *
     * @return ArrayList<Instruction>
     */

    public ArrayList<Instruction> getInstructionBox() {
        return this.instructionBox;
    }

    /**
     * Returns an arrayList of arrival sensors
     * @return ArrayList<ArrivalSensor>
     */
    public ArrayList<ArrivalSensor> getArrivalSensors() {
        return this.arrivalSensors;
    }

    /**
     * This method checks if a floor number is in the instruction box
     * If so, it returns the matching instruction
     * @param floorNumber
     * @return instruction
     */
    public Instruction isFloorInQueue(int floorNumber) {
        for (Instruction instruction : this.instructionBox) {
            if (instruction.getFloorNumber() == floorNumber) {
                return instruction;
            }
        }
        return null;
    }

    /**
     * Executes code section in the current state
     */
    public void request() {
        this.currentState.handle(this);
    }

    /**
     * Constant loop that continuously handles any instructions fed to the elevator by the scheduler
     */
    public void sendRequest(Request requestToSend) {
        try {
            // data is the byte array that represents the Request
            byte[] data = requestToSend.toByteArray(requestToSend);

            // Send packet to Scheduler receive socket
            DatagramPacket packetToSend = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), 41);
            sendSocket.send(packetToSend);
            System.out.println("Elevator " + this.elevatorID + " Sent Request from Button " + requestToSend.getButtonId());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to send request to Scheduler");
            System.exit(1);
        }
        //receive acknowledgment from scheduler after sending request
        this.receiveAcknowledgment();
    }

    /**
     * This method receives instruction from the  scheduler
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void receiveInstructionFromScheduler() throws IOException, ClassNotFoundException {
        byte[] data = new byte[200];
        DatagramPacket packetToReceive = null;

        // packet to receive the data
        packetToReceive = new DatagramPacket(data, data.length);

        // receive data
        try {
            receiveSocket.setSoTimeout(100);
            receiveSocket.receive(packetToReceive);
            System.out.println("Elevator " + this.elevatorID + " Received Instruction from Scheduler");
        } catch (SocketTimeoutException e) {
            return;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to receive packet - IOException");
            return;
        }

        // Turn data into instruction
        Instruction instruction = Instruction.toInstruction(packetToReceive.getData());

        //determine if instruction causes a fault
        this.floorFaults.set(instruction.getFloorNumber(), instruction.getTriggerFault());

        // add instruction to instructionbox if floor number is not already in box
        if (isFloorInQueue(instruction.getFloorNumber()) == null) {
            this.instructionBox.add(instruction);
        }
    }

    /**
     * This method receives acknowledgement from the scheduler
     */
    public void receiveAcknowledgment() {
        byte[] data = new byte[200];
        DatagramPacket acknowledgementPacket = null;

        // packet to receive the data
        acknowledgementPacket = new DatagramPacket(data, data.length);

        // receive data
        try {
            acknowledgementSocket.receive(acknowledgementPacket);
            System.out.println("Elevator " + this.elevatorID + " received acknowledgment from scheduler");
        } catch (IOException e) {
            System.out.println("Failed to Receive Request - IOException");
        }
    }

    /**
     * Sends a response to the Scheduler
     */
    public void sendResponse(boolean stoppingAtFloor) {

        Response response = new Response(currentFloor, getMotor().getCurrentDirection(), elevatorID, stoppingAtFloor);
        DatagramPacket newPacket = null;
        try {

            // Serialize instruction object to bytes
            byte[] responseBytes = response.toByteArray(response);

            // Create UDP packet with instruction bytes, destination IP, and port

            newPacket = new DatagramPacket(responseBytes, responseBytes.length, InetAddress.getLocalHost(), 70);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.out.println("Failed to find packet host - UnknownHostException");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("IOException");
            throw new RuntimeException(e);
        }

        // Sending response packet
        try {
            sendSocket.send(newPacket);
            System.out.println("Elevator " + this.elevatorID + " Sent Response to Scheduler");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to send response packet");
            System.exit(1);
        }
    }

    /**
     * This method receives request from the scheduler
     */
    public void receiveRequest(){
        byte[] data = new byte[2];
        DatagramPacket packetToReceive = new DatagramPacket(data, data.length);

        try {
            requestSocket.setSoTimeout(1);
            requestSocket.receive(packetToReceive);
        } catch (SocketTimeoutException e) {
            return;
        } catch (IOException e) {
            System.out.println("Elevator failed to receive request - IOException");
            return;
        }

        System.out.println("Elevator Receiving request");
        //collect the received packet and convert it to response

        int buttonID = packetToReceive.getData()[0];
        int triggerFault = packetToReceive.getData()[1];

        this.buttonList.get(buttonID).pushButton(triggerFault);
    }

    /**
     * This method receives request from the scheduler
     */
    public void receiveEndRequest(){
        byte[] data = new byte[2];
        DatagramPacket packetToReceive = new DatagramPacket(data, data.length);

        try {
            endSocket.setSoTimeout(1);
            endSocket.receive(packetToReceive);
        } catch (SocketTimeoutException e) {
            return;
        } catch (IOException e) {
            System.out.println("Elevator failed to receive request - IOException");
            return;
        }

        System.out.println("Elevator Receiving Number of Instructions");

        int moveNumber = packetToReceive.getData()[0];

        totalInstructions = moveNumber;
        System.out.println(totalInstructions);
    }

    /**
     * This method sets floor faults
     * @param floorFaults
     */
    public void setFloorFaults(ArrayList<Integer> floorFaults) {
        this.floorFaults = floorFaults;
    }

    /**
     * This method enables or disables elevator
     * @param elevatorEnabled
     */
    public void setElevatorEnabled(boolean elevatorEnabled) {
        this.elevatorEnabled = elevatorEnabled;
    }

    @Override
    public void run() {
        // Timer setup
        Timer elevatorTimer = new Timer();
        Thread timerThread = new Thread(elevatorTimer, "ElevatorTimer");
        this.timer = elevatorTimer;
        this.timer.setElevator(this);

        timerThread.start();

        while (true) {
            receiveRequest();
            receiveEndRequest();
            request();
        }
    }
}