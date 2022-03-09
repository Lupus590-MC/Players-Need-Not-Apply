package lupus590.players_need_not_apply;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.io.IOException;
import java.util.UUID;


// TODO: discord bot
public class BotInterface {
    private final String rootConnectionUrl;

    private final ITurtleSpawner overworldTurtleSpawner;
    private final ITurtleSpawner netherTurtleSpawner;
    private final ITurtleSpawner endTurtleSpawner;

    private final boolean requireOfferingsForOtherWorld;

    public BotInterface(String rootConnectionUrl, ITurtleSpawner overworldTurtleSpawner, ITurtleSpawner netherTurtleSpawner, ITurtleSpawner endTurtleSpawner, boolean requireOfferingsForOtherWorld) {
        this.overworldTurtleSpawner = overworldTurtleSpawner;
        this.netherTurtleSpawner = netherTurtleSpawner;
        this.endTurtleSpawner = endTurtleSpawner;
        this.rootConnectionUrl = rootConnectionUrl;
        this.requireOfferingsForOtherWorld = requireOfferingsForOtherWorld;
    }

    private UUID spawnTurtle(String world, Coords coords, UUID connectionUUID) throws IOException, InterruptedException {
        if (world.equalsIgnoreCase("o")){
            connectionUUID = overworldTurtleSpawner.spawnTurtle(true);
        } else if (world.equalsIgnoreCase("n")){
            connectionUUID = netherTurtleSpawner.spawnTurtle(connectionUUID, coords,true);
        } else if (world.equalsIgnoreCase("e")) {
            connectionUUID = endTurtleSpawner.spawnTurtle(connectionUUID, coords,true);
        } else {
            throw new RuntimeException("Unknown world.");
        }
        return connectionUUID;
    }

    public void run() {
        String token = ""; // TODO: load from file or command line

        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

        api.addMessageCreateListener(event -> {
            if (event.getMessageContent().equalsIgnoreCase("!create overworld")) {
                UUID connectionUUID = null;
                try {
                    connectionUUID = spawnTurtle("o", null, null);
                }
                catch (IOException | InterruptedException e) {
                    event.getChannel().sendMessage("An exception was caught while trying to make your turtle.");
                    e.printStackTrace();
                    return;
                }
                event.getChannel().sendMessage("Your connection URL: "+rootConnectionUrl+connectionUUID);
            }
        });

        // Print the invite url of your bot
        System.out.println("You can invite the bot by using the following url: "+api.createBotInvite());
    }
}
