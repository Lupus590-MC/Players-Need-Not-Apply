package lupus590.players_need_not_apply;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BaseInterface {

    protected final String rootConnectionUrl;

    protected final ITurtleSpawner overworldTurtleSpawner;
    protected final ITurtleSpawner netherTurtleSpawner;
    protected final ITurtleSpawner endTurtleSpawner;

    protected final boolean requireOfferingsForOtherWorld;

    protected BaseInterface(String rootConnectionUrl, ITurtleSpawner overworldTurtleSpawner, ITurtleSpawner netherTurtleSpawner, ITurtleSpawner endTurtleSpawner, boolean requireOfferingsForOtherWorld) {
        this.overworldTurtleSpawner = overworldTurtleSpawner;
        this.netherTurtleSpawner = netherTurtleSpawner;
        this.endTurtleSpawner = endTurtleSpawner;
        this.rootConnectionUrl = rootConnectionUrl;
        this.requireOfferingsForOtherWorld = requireOfferingsForOtherWorld;
    }

    protected UUID extractUUIDFromURL(String url) throws IllegalArgumentException {
        Pattern pattern = Pattern.compile("\\?ws=(.)*", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()){
            String uuid = matcher.group().substring(4);
            return UUID.fromString(uuid);
        } else {
            return UUID.fromString(url);
        }
    }

    protected UUID spawnTurtle(String world, Coords coords, UUID connectionUUID, boolean printTurtleId) throws IOException, InterruptedException {
        if (world.equalsIgnoreCase("o")){
            connectionUUID = overworldTurtleSpawner.spawnTurtle(printTurtleId);
        } else if (world.equalsIgnoreCase("n")){
            connectionUUID = netherTurtleSpawner.spawnTurtle(connectionUUID, coords,printTurtleId);
        } else if (world.equalsIgnoreCase("e")) {
            connectionUUID = endTurtleSpawner.spawnTurtle(connectionUUID, coords,printTurtleId);
        } else {
            throw new RuntimeException("Unknown world.");
        }
        return connectionUUID;
    }

    protected UUID spawnTurtle(String world, Coords coords, UUID connectionUUID) throws IOException, InterruptedException {
        return spawnTurtle(world, coords, connectionUUID, false);
    }

}
