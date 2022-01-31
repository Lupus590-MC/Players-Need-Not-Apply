package lupus590.players_need_not_apply;

import java.io.IOException;
import java.util.UUID;

public class MockTurtleSpawner implements ITurtleSpawner {
    private final String world;

    public MockTurtleSpawner(String world){
        this.world = world;
    }

    @Override
    public UUID spawnTurtle(UUID existingConnection, Boolean printTurtleId) {
        System.out.println("Created mock turtle in the "+world);
        if (existingConnection != null){
            return existingConnection;
        }
        return UUID.randomUUID();
    }

    @Override
    public UUID spawnTurtle(UUID existingConnection) {
        return spawnTurtle(existingConnection, false);
    }

    @Override
    public UUID spawnTurtle(Boolean printTurtleId) {
        return spawnTurtle(null, printTurtleId);
    }

    @Override
    public UUID spawnTurtle() {
        return spawnTurtle(null, false);
    }
}
