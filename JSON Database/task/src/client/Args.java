package client;

import com.beust.jcommander.Parameter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author namvdo
 */
public class Args {
    @Parameter (
            names="-t",
            description = "The type of the command",
            required = true
    )
    public String type;

    @Parameter (
            names="-k",
            description = "K is the key"
    )
    public String index;

    @Parameter (
            names="-v",
            description = "V is the value to set"
    )
    public String message;
}
