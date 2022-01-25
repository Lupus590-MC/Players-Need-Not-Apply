package Lupus590.PlayersNeedNotApply;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.UUID;

public class TurtleSpawner implements ITurtleSpawner{

    private final String computercraftComputerFolderName;
    private final String commandComputerRequestFileName;
    private final String commandComputerResponseFileName;
    private final String commandComputerAckFileName;
    private final Integer NoResponseFileTimeoutInSeconds = 10;

    public TurtleSpawner(Path computercraftComputerFolderPath, Integer commandComputerId) {
        this.computercraftComputerFolderName = computercraftComputerFolderPath.toString();
        String commandComputerPathAsString = Path.of(computercraftComputerFolderName, commandComputerId.toString()).toString();
        this.commandComputerRequestFileName = Path.of(commandComputerPathAsString, "request").toString();
        this.commandComputerResponseFileName = Path.of(commandComputerPathAsString, "response").toString();
        this.commandComputerAckFileName = Path.of(commandComputerPathAsString, "ack").toString();
    }


    public UUID spawnTurtle(Boolean printTurtleId) throws IOException, InterruptedException {
        UUID newPlayersUUID = null;
        try{
            Integer turtleID = createTurtle();
            newPlayersUUID = setUpTurtle(turtleID);
            if (printTurtleId) {
                System.out.println("new turtle id: " + turtleID.toString());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace(); // cleanUpCreateTurtle should recover most of these
            throw e;
        }
        return newPlayersUUID;
    }

    public UUID spawnTurtle() throws IOException, InterruptedException {
        return spawnTurtle(false);
    }

    private Integer createTurtle() throws IOException, InterruptedException {
        // make a file on the command computer's root
        File requestFile = new File(commandComputerRequestFileName);
        if (!requestFile.createNewFile()) {
            throw new IOException("Failed to create request file.");
        }

        // wait for response file
        File responseFile = new File(commandComputerResponseFileName);
        for (int i = 1; i < NoResponseFileTimeoutInSeconds; i++ ) {
            if (!responseFile.exists()){
                Thread.sleep(1000);
            }
        }
        if (!responseFile.exists()) {
            throw new FileNotFoundException("Command computer didn't respond in reasonable time.");
        }

        // response contains new computer Id
        Scanner responseFileReader = new Scanner(responseFile);
        Float newTurtleIdAsFloat = responseFileReader.nextFloat(); // lua writes it as a float
        Integer newTurtleId = newTurtleIdAsFloat.intValue();
        responseFileReader.close();
        return newTurtleId;
    }

    private UUID setUpTurtle(Integer id) throws IOException {
        // write file on new computer with it's unique web socket address

        String setUpFilePath = Path.of(computercraftComputerFolderName, id.toString()).toString();
        String setUpFileName = Path.of(setUpFilePath, "startup.lua").toString();
        File setUpFile = new File(setUpFilePath);
        setUpFile.mkdirs();
        FileWriter setUpFileWriter = new FileWriter(setUpFileName);
        UUID turtlesUuid = UUID.randomUUID();
        setUpFileWriter.write("shell.run(\"/rom/programs/websocket_repl.lua "+turtlesUuid.toString()+"\")");
        setUpFileWriter.close();

        File ackFile = new File(commandComputerAckFileName);
        if (!ackFile.createNewFile()) {
            throw new IOException("Failed to create ack file.");
        }

        return turtlesUuid;
    }

}
