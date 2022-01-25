package Lupus590.PlayersNeedNotApply;

import java.io.IOException;
import java.util.UUID;

public interface ITurtleSpawner {
    UUID spawnTurtle(Boolean printTurtleId) throws IOException, InterruptedException;
    UUID spawnTurtle() throws IOException, InterruptedException;
}
