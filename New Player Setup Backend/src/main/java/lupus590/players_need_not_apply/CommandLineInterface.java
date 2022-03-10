package lupus590.players_need_not_apply;

import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandLineInterface extends BaseInterface {

    private final Scanner keyboardScanner;

    public CommandLineInterface(String rootConnectionUrl, ITurtleSpawner overworldTurtleSpawner, ITurtleSpawner netherTurtleSpawner, ITurtleSpawner endTurtleSpawner, boolean requireOfferingsForOtherWorld) {
        super(rootConnectionUrl, overworldTurtleSpawner, netherTurtleSpawner, endTurtleSpawner, requireOfferingsForOtherWorld);
        keyboardScanner = new Scanner(System.in);
    }

    private UUID getUUIDFromSystemIn(){
        while (true) {
            System.out.println("Input existing connection URL");
            String input = keyboardScanner.nextLine();
            try{
                return extractUUIDFromURL(input);
            } catch(IllegalArgumentException e){
                System.out.println("Couldn't extract connection UUID, please try again.");
            }

        }
    }

    private int getCoord(String coordLabel){
        while (true) {
            System.out.println("Input the "+coordLabel+" coord of inventory with offerings");
            String input = keyboardScanner.nextLine();
            try{
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please input an integer");
            }
        }
    }

    private Coords getInvCoordsFromSystemIn(){
        if (!requireOfferingsForOtherWorld){
            return null;
        }

        Coords coords = new Coords();
        coords.x = getCoord("x");
        coords.y = getCoord("y");
        coords.z = getCoord("z");

        while (true) {
            System.out.println("Input the dimension of inventory with offerings [(o)verworld | (n)ether | (e)nd]");
            String input = keyboardScanner.nextLine().toLowerCase(Locale.ROOT);
            if (input.equals("overworld") || input.equals("o") || input.equals("nether") || input.equals("n") || input.equals("end") || input.equals("e")) {
                coords.dim = input; // TODO: convert "overworld" to "o" etc.
                break;
            } else {
                System.out.println("Invalid dimension");
            }
        }

        return coords;
    }

    private UUID spawnTurtle(String world) throws IOException, InterruptedException {
        UUID connectionUUID = null;
        Coords coords = null;
        if (world.equalsIgnoreCase("n") || world.equalsIgnoreCase("e")){
            connectionUUID = getUUIDFromSystemIn();
            coords = getInvCoordsFromSystemIn();
        }
        return spawnTurtle(world, coords, connectionUUID);
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
