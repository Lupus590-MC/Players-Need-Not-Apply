package lupus590.players_need_not_apply;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.UUID;

public class CommandLineInterface {
    private final String rootConnectionUrl;

    private final ITurtleSpawner overworldTurtleSpawner;
    private final ITurtleSpawner netherTurtleSpawner;
    private final ITurtleSpawner endTurtleSpawner;

    public CommandLineInterface(String rootConnectionUrl, ITurtleSpawner overworldTurtleSpawner, ITurtleSpawner netherTurtleSpawner, ITurtleSpawner endTurtleSpawner) {
        this.overworldTurtleSpawner = overworldTurtleSpawner;
        this.netherTurtleSpawner = netherTurtleSpawner;
        this.endTurtleSpawner = endTurtleSpawner;
        this.rootConnectionUrl = rootConnectionUrl;
    }

    public void run() throws IOException, InterruptedException {
        Scanner keyboardScanner = new Scanner(System.in);

        while (true) {
            System.out.println("(o)verworld, (n)ether, (e)nd, (q)uit)"); // wait for signal to spawn new turtle
            while(!keyboardScanner.hasNextLine()){
                //TODO: don't busy wait
            }
            String input = keyboardScanner.nextLine();
            UUID newPlayersUUID;
            if (input.equalsIgnoreCase("q")){
                return;
            } else if (input.equalsIgnoreCase("o")){
                newPlayersUUID = overworldTurtleSpawner.spawnTurtle(true);
            } else if (input.equalsIgnoreCase("n")){
                newPlayersUUID = netherTurtleSpawner.spawnTurtle(true);
            } else if (input.equalsIgnoreCase("e")) {
                newPlayersUUID = endTurtleSpawner.spawnTurtle(true);
            } else {
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
