package lupus590.players_need_not_apply;

import java.io.IOException;
import java.util.UUID;

public interface ITurtleSpawner {
    // Do I really need all of these?
    UUID spawnTurtle(UUID existingConnection, Coords offeringLocation, Boolean printTurtleId) throws IOException, InterruptedException;
    UUID spawnTurtle(UUID existingConnection, Coords offeringLocation) throws IOException, InterruptedException;
    UUID spawnTurtle(Coords offeringLocation, Boolean printTurtleId) throws IOException, InterruptedException;
    UUID spawnTurtle(Coords offeringLocation) throws IOException, InterruptedException;
    UUID spawnTurtle(UUID existingConnection, Boolean printTurtleId) throws IOException, InterruptedException;
    UUID spawnTurtle(UUID existingConnection) throws IOException, InterruptedException;
    UUID spawnTurtle(Boolean printTurtleId) throws IOException, InterruptedException;
    UUID spawnTurtle() throws IOException, InterruptedException;
}
