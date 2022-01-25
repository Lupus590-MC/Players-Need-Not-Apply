package lupus590.players_need_not_apply;

import java.util.UUID;

public class MockTurtleSpawner implements ITurtleSpawner {

    @Override
    public UUID spawnTurtle(Boolean printTurtleId) {
        System.out.println("Created mock turtle");
        return UUID.randomUUID();
    }

    @Override
    public UUID spawnTurtle() {
        return spawnTurtle(false);
    }
}