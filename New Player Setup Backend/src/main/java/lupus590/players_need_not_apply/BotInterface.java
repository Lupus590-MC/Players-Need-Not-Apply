package lupus590.players_need_not_apply;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

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
        super(rootConnectionUrl, overworldTurtleSpawner, netherTurtleSpawner, endTurtleSpawner, requireOfferingsForOtherWorld);
    }

    private void processCreateRequest(MessageCreateEvent event){
        System.out.println(event.getMessageContent());
        if (event.getMessageContent().equalsIgnoreCase("!create overworld")) {
            System.out.println("overworld");
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
        else if (event.getMessageContent().startsWith("!create nether")) {
            System.out.println("nether");
            UUID connectionUUID = null;
            try {
                String unprocessedUUID = event.getMessageContent().substring(14).trim();
                System.out.println(unprocessedUUID);
                connectionUUID = extractUUIDFromURL(unprocessedUUID);
                connectionUUID = spawnTurtle("n", null, connectionUUID);
            }
            catch (IOException | InterruptedException | IllegalArgumentException e) {
                event.getChannel().sendMessage("An exception was caught while trying to make your turtle.").join();
                e.printStackTrace();
                return;
            }
            event.getChannel().sendMessage("Your connection URL: "+rootConnectionUrl+connectionUUID);
        }
        else if (event.getMessageContent().startsWith("!create end")) {
            System.out.println("end");
            UUID connectionUUID = null;
            try {
                String unprocessedUUID = event.getMessageContent().substring(11).trim();
                System.out.println(unprocessedUUID);
                connectionUUID = extractUUIDFromURL(unprocessedUUID);
                connectionUUID = spawnTurtle("e", null, connectionUUID);
            }
            catch (IOException | InterruptedException | IllegalArgumentException e) {
                event.getChannel().sendMessage("An exception was caught while trying to make your turtle.");
                e.printStackTrace();
                return;
            }
            event.getChannel().sendMessage("Your connection URL: "+rootConnectionUrl+connectionUUID);
        }


    }

    public void run() {
        String token = ""; // TODO: load from file or command line

        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

        api.addMessageCreateListener(event -> {
            if (event.getMessageContent().startsWith("!create ")) {
                processCreateRequest(event);
            }
        });

        // Print the invite url of your bot
        System.out.println("You can invite the bot by using the following url: "+api.createBotInvite());
    }
}
