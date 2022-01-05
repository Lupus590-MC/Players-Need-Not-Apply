package Lupus590.PlayersNeedNotApply;

import java.nio.file.Path;
import java.util.UUID;

public class TurtleSpawner {

    private final Path computercraftComputerFolderPath;
    private final Integer commandComputerId;

    public TurtleSpawner(Path computercraftComputerFolderPath, Integer commandComputerId) {
        this.commandComputerId = commandComputerId;
        this.computercraftComputerFolderPath = computercraftComputerFolderPath;
    }


    public UUID spawnTurtle() {
        Integer turtleID = createTurtle();
        UUID newPlayersUUID = setUpTurtle(turtleID);
        cleanUpCreateTurtle();
        return newPlayersUUID;
    }

    private Integer createTurtle() {
        // make a file on the command computer's root
        // wait for responce file
        // responce contains new computer Id
        return 0;
    }

    private UUID setUpTurtle(Integer id) {
        // write file on new computer with it's unique web socket address
        return UUID.randomUUID();
    }

    private void cleanUpCreateTurtle() {
        // delete first file
        // wait for responce to be deleted
        // command computer is now ready for next request
    }

}
