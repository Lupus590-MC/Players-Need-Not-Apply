package lupus590.players_need_not_apply;

import java.util.UUID;

public class MockTurtleSpawner implements ITurtleSpawner {
    private final String world;

    public MockTurtleSpawner(String world){
        this.world = world;
    }

    @Override
    public UUID spawnTurtle(Boolean printTurtleId) {
        System.out.println("Created mock turtle in the "+world);
        return UUID.randomUUID();
    }

    @Override
    public UUID spawnTurtle() {
        return spawnTurtle(false);
    }
}
