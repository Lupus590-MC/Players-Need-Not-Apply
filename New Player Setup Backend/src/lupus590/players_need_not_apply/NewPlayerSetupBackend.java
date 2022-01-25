package lupus590.players_need_not_apply;

import java.io.IOException;
import java.nio.file.Path;

public class NewPlayerSetupBackend {
    private static final boolean useMockTurtleSpawner = true;
    private static  final boolean useCli = true;

    private static final Path computercraftComputerFolderPath = Path.of("C:\\Games\\Minecraft\\MultiMC\\instances\\Players Need Not Apply - 1.16\\.minecraft\\saves\\New World\\computercraft\\computer");
    private static final Integer commandComputerId = 6;

    private static final String rootConnectionUrl = "file:///C:/MyStuff/Projects/CC/Players-Need-Not-Apply/Web%20Dashboard/index.html?ws=";


    public static void main(String[] args) throws IOException, InterruptedException {
        ITurtleSpawner turtleSpawner;
        if(useMockTurtleSpawner){
            turtleSpawner = new MockTurtleSpawner();
        }
        else{
            turtleSpawner = new TurtleSpawner(computercraftComputerFolderPath, commandComputerId);
        }

        if(useCli){
            CommandLineInterface cli = new CommandLineInterface(turtleSpawner, rootConnectionUrl, computercraftComputerFolderPath, commandComputerId);
            cli.run();
        }
        else{
            // TODO: discord bot
        }
    }
}
