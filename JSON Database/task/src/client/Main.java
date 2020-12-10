package client;

import com.beust.jcommander.JCommander;

/**
 * @author namvdo
 */
public class Main {
    public static void main(String[] args) {
        Args arg = new Args();
        JCommander commander = JCommander.newBuilder()
                .addObject(arg)
                .build();
        commander.parse(args);
        (new Client()).executeClient(args);
    }
}


