package lupus590.players_need_not_apply;

import java.util.UUID;

public class MockTurtleSpawner implements ITurtleSpawner {
    private final String world;

    public MockTurtleSpawner(String world){
        this.world = world;
    }

    @Override
    public UUID spawnTurtle(UUID existingConnection, Coords offeringLocation, Boolean printTurtleId) {
        System.out.println("Created mock turtle in the "+world);
        if (existingConnection != null){
            return existingConnection;
        }
        return UUID.randomUUID();
    }

    @Override
    public UUID spawnTurtle(UUID existingConnection, Coords offeringLocation) {
        return spawnTurtle(existingConnection, offeringLocation,  false);
    }

    @Override
    public UUID spawnTurtle(Coords offeringLocation, Boolean printTurtleId) {
        return spawnTurtle(null, offeringLocation,  printTurtleId);
    }

    @Override
    public UUID spawnTurtle(Coords offeringLocation){
        return spawnTurtle(null, offeringLocation,  false);
    }

    @Override
    public UUID spawnTurtle(UUID existingConnection, Boolean printTurtleId) {
        return spawnTurtle(existingConnection, null,  printTurtleId);
    }

    @Override
    public UUID spawnTurtle(UUID existingConnection) {
        return spawnTurtle(existingConnection, null, false);
    }

    @Override
    public UUID spawnTurtle(Boolean printTurtleId) {
        return spawnTurtle(null, null,  printTurtleId);
    }

    @Override
    public UUID spawnTurtle() {
        return spawnTurtle(null, null, false);
    }
}
