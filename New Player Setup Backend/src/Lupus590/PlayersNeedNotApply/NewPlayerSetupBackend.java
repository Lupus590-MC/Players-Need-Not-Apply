package Lupus590.PlayersNeedNotApply;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.UUID;

public class NewPlayerSetupBackend {

    private static Path computercraftComputerFolderPath = Path.of("C:\\Games\\Minecraft\\MultiMC\\instances\\Players Need Not Apply - 1.16\\.minecraft\\saves\\New World\\computercraft\\computer");
    private static Integer commandComputerId = 6;

    private static String rootConnectionUrl = "file:///C:/MyStuff/Projects/CC/Players-Need-Not-Apply/Turtle%20UI%20Code/Web%20Dashboard/index.html?ws=";


    public static void main(String[] args) throws IOException, InterruptedException {

        Scanner keyboardScanner = new Scanner(System.in);
        TurtleSpawner turtleSpawner = new TurtleSpawner(computercraftComputerFolderPath, commandComputerId);

        while (true) {
            System.out.println("press enter to spawn new turtle (q to quit)"); // wait for signal to spawn new turtle
            String input = keyboardScanner.nextLine();
            if (input.equalsIgnoreCase("q")){
                return;
            }

            UUID newPlayersUUID = turtleSpawner.spawnTurtle();
            if (newPlayersUUID != null) {
                System.out.println("new turtle's connection url: "+rootConnectionUrl+newPlayersUUID.toString()); // tell player their UUID
            } else {
                System.out.println("error creating new turtle");
            }


        }
    }
}
