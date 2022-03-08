package lupus590.players_need_not_apply;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;


// TODO: discord bot
public class BotInterface {
    public BotInterface(String rootConnectionUrl, ITurtleSpawner overworldTurtleSpawner, ITurtleSpawner netherTurtleSpawner, ITurtleSpawner endTurtleSpawner, boolean requireOfferingsForOtherWorld) {
        //TODO: use args
    }

    public void run() {
        // Insert your bot's token here
        String token = ""; // TODO: load from file or command line

        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

        // Add a listener which answers with "Pong!" if someone writes "!ping"
            api.addMessageCreateListener(event ->

        {
            if (event.getMessageContent().equalsIgnoreCase("!ping")) {
                event.getChannel().sendMessage("Pong!");
            }
        });

        // Print the invite url of your bot
            System.out.println("You can invite the bot by using the following url: "+api.createBotInvite());
    }
}
