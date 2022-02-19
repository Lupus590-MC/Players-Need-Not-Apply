package lupus590.players_need_not_apply;

import org.jetbrains.annotations.NotNull;
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


    @Override
    public UUID spawnTurtle(UUID existingConnection, Coords offeringLocation, Boolean printTurtleId) throws IOException, InterruptedException {
        UUID connectionId = existingConnection;
        if (connectionId == null){
            connectionId = UUID.randomUUID();
        }
        try{
            Integer turtleID = createTurtle(offeringLocation);
            setUpTurtle(turtleID, connectionId);
            if (printTurtleId) {
                System.out.println("new turtle id: " + turtleID.toString());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace(); // cleanUpCreateTurtle should recover most of these
            throw e;
        }
        return connectionId;
    }

    @Override
    public UUID spawnTurtle(UUID existingConnection, Coords offeringLocation) throws IOException, InterruptedException {
        return spawnTurtle(existingConnection, offeringLocation,false);
    }

    @Override
    public UUID spawnTurtle(Coords offeringLocation, Boolean printTurtleId) throws IOException, InterruptedException {
        return spawnTurtle(null, offeringLocation,false);
    }

    @Override
    public UUID spawnTurtle(Coords offeringLocation) throws IOException, InterruptedException {
        return spawnTurtle(null, offeringLocation,false);
    }

    @Override
    public @NotNull UUID spawnTurtle(UUID existingConnection, Boolean printTurtleId) throws IOException, InterruptedException {
        return spawnTurtle(existingConnection, null,false);
    }

    @Override
    public UUID spawnTurtle(UUID existingConnection) throws IOException, InterruptedException {
        return spawnTurtle(existingConnection, null,false);
    }

    public UUID spawnTurtle(Boolean printTurtleId) throws IOException, InterruptedException {
        return spawnTurtle(null, null, printTurtleId);
    }

    public UUID spawnTurtle() throws IOException, InterruptedException {
        return spawnTurtle(null, null,false);
    }

    private @NotNull Integer createTurtle(Coords invPos) throws IOException, InterruptedException {
        // make a file on the command computer's root
        File requestFile = new File(commandComputerRequestFileName);
        if (!requestFile.createNewFile()) {
            throw new IOException("Failed to create request file.");
        }
        if (invPos != null ){
            FileWriter myWriter = new FileWriter(commandComputerRequestFileName);
            myWriter.write(String.format("{ x = %d, y = %d, z = %d, dim = \"%s\" }", invPos.x, invPos.y, invPos.z, invPos.dim));
            myWriter.close();
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
        // TODO: response might be an error message
        Scanner responseFileReader = new Scanner(responseFile);
        Float newTurtleIdAsFloat = responseFileReader.nextFloat(); // lua writes it as a float
        Integer newTurtleId = newTurtleIdAsFloat.intValue();
        responseFileReader.close();
        return newTurtleId;
    }

    private void setUpTurtle(@NotNull Integer id, UUID connectionId) throws IOException {
        // write file on new computer with it's unique web socket address

        String setUpFilePath = Path.of(computercraftComputerFolderName, id.toString()).toString();
        String setUpFileName = Path.of(setUpFilePath, "startup.lua").toString();
        File setUpFile = new File(setUpFilePath);
        setUpFile.mkdirs();
        FileWriter setUpFileWriter = new FileWriter(setUpFileName);
        setUpFileWriter.write("shell.run(\"/rom/programs/websocket_repl.lua "+connectionId.toString()+"\")");
        setUpFileWriter.close();

        File ackFile = new File(commandComputerAckFileName);
        if (!ackFile.createNewFile()) {
            throw new IOException("Failed to create ack file.");
        }
    }

}
