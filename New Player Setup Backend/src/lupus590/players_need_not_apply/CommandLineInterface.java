package lupus590.players_need_not_apply;

import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandLineInterface {
    private final String rootConnectionUrl;

    private final ITurtleSpawner overworldTurtleSpawner;
    private final ITurtleSpawner netherTurtleSpawner;
    private final ITurtleSpawner endTurtleSpawner;

    private final Scanner keyboardScanner;

    public CommandLineInterface(String rootConnectionUrl, ITurtleSpawner overworldTurtleSpawner, ITurtleSpawner netherTurtleSpawner, ITurtleSpawner endTurtleSpawner) {
        this.overworldTurtleSpawner = overworldTurtleSpawner;
        this.netherTurtleSpawner = netherTurtleSpawner;
        this.endTurtleSpawner = endTurtleSpawner;
        this.rootConnectionUrl = rootConnectionUrl;
        keyboardScanner = new Scanner(System.in);
    }

    private UUID getUUIDFromSystemIn(){
        while (true) {
            System.out.println("Input existing connection URL");
            String input = keyboardScanner.nextLine();
            Pattern pattern = Pattern.compile("\\?ws=(.)*", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()){
                String uuid = matcher.group().substring(4);
                return UUID.fromString(uuid);
            } else {
                UUID uuid;
                // TODO: pattern match the actual UUID and check for test
                try{
                    return UUID.fromString(input);
                } catch(IllegalArgumentException e){
                    System.out.println("Couldn't extract connection UUID, please try again.");
                }
            }
        }
    }

    private UUID spawnTurtle(String world) throws IOException, InterruptedException {
        UUID connectionUUID;
        if (world.equalsIgnoreCase("o")){
            connectionUUID = overworldTurtleSpawner.spawnTurtle(true);
        } else if (world.equalsIgnoreCase("n")){
            connectionUUID = getUUIDFromSystemIn();
            connectionUUID = netherTurtleSpawner.spawnTurtle(connectionUUID,true);
        } else if (world.equalsIgnoreCase("e")) {
            connectionUUID = getUUIDFromSystemIn();
            connectionUUID = endTurtleSpawner.spawnTurtle(connectionUUID,true);
        } else {
            throw new RuntimeException("Unknown world.");
        }
        return connectionUUID;
    }

    public void run() throws IOException, InterruptedException {
        while (true) {
            System.out.println("(o)verworld, (n)ether, (e)nd, (q)uit)"); // wait for signal to spawn new turtle
            while(!keyboardScanner.hasNextLine()){
                //TODO: don't busy wait
            }
            String input = keyboardScanner.nextLine();
            UUID newPlayersUUID;
            if (input.equalsIgnoreCase("q")){
                return;
            } else if (input.equalsIgnoreCase("o") || input.equalsIgnoreCase("n") || input.equalsIgnoreCase("e")) {
                newPlayersUUID = spawnTurtle(input);
            } else {
                System.out.println("Invalid input");
                continue;
            }

            if (newPlayersUUID != null) {
                System.out.println("new turtle's connection url: "+rootConnectionUrl+newPlayersUUID.toString()); // tell player their UUID
            } else {
                System.out.println("error creating new turtle");
            }

        }
    }
}
