package lupus590.players_need_not_apply;

import java.io.IOException;
import java.nio.file.Path;

public class NewPlayerSetupBackend {
    private static final boolean useMockTurtleSpawner = false;
    private static  final boolean useCli = true;

    //TODO: could do with a config for this stuff
    private static final Path computercraftComputerFolderPath = Path.of("C:\\Games\\Minecraft\\MultiMC\\instances\\Players Need Not Apply - 1.16\\.minecraft\\saves\\New World\\computercraft\\computer");
    private static final Integer overworldCommandComputerId = 6;
    private static final Integer netherCommandComputerId = 45;
    private static final Integer endCommandComputerId = 45;

    private static final String rootConnectionUrl = "file:///C:/MyStuff/Projects/CC/Players-Need-Not-Apply/Web%20Dashboard/index.html?ws=";


    public static void main(String[] args) throws IOException, InterruptedException {
        ITurtleSpawner overworldTurtleSpawner;
        ITurtleSpawner netherTurtleSpawner;
        ITurtleSpawner endTurtleSpawner;

        if(useMockTurtleSpawner){
            overworldTurtleSpawner = new MockTurtleSpawner("overworld");
            netherTurtleSpawner = new MockTurtleSpawner("nether");
            endTurtleSpawner = new MockTurtleSpawner("end");
        }
        else{
            overworldTurtleSpawner = new TurtleSpawner(computercraftComputerFolderPath, overworldCommandComputerId);
            netherTurtleSpawner = new TurtleSpawner(computercraftComputerFolderPath, netherCommandComputerId);
            endTurtleSpawner = new TurtleSpawner(computercraftComputerFolderPath, endCommandComputerId);
        }

        if(useCli){
            CommandLineInterface cli = new CommandLineInterface(rootConnectionUrl, overworldTurtleSpawner, netherTurtleSpawner, endTurtleSpawner);
            cli.run();
        }
        else{
            // TODO: discord bot
        }
    }
}
