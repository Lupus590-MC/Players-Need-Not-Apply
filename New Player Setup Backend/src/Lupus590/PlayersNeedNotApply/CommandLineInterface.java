package Lupus590.PlayersNeedNotApply;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.UUID;

public class CommandLineInterface {

    private final Path computercraftComputerFolderPath;
    private final Integer commandComputerId;

    private final String rootConnectionUrl;

    private final ITurtleSpawner turtleSpawner;

    public CommandLineInterface(ITurtleSpawner turtleSpawner, String rootConnectionUrl, Path computercraftComputerFolderPath, Integer commandComputerId) {
        this.turtleSpawner = turtleSpawner;
        this.rootConnectionUrl = rootConnectionUrl;
        this.computercraftComputerFolderPath = computercraftComputerFolderPath;
        this.commandComputerId = commandComputerId;
    }

    public void run() throws IOException, InterruptedException {
        Scanner keyboardScanner = new Scanner(System.in);

        while (true) {
            System.out.println("press enter to spawn new turtle (q to quit)"); // wait for signal to spawn new turtle
            while(!keyboardScanner.hasNextLine()){
                //TODO: don't busy wait
            }
            String input = keyboardScanner.nextLine();
            if (input.equalsIgnoreCase("q")){
                return;
            }

            UUID newPlayersUUID = turtleSpawner.spawnTurtle(true);
            if (newPlayersUUID != null) {
                System.out.println("new turtle's connection url: "+rootConnectionUrl+newPlayersUUID.toString()); // tell player their UUID
            } else {
                System.out.println("error creating new turtle");
            }

        }
    }
}
