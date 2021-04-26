package randomizer.nds.utils.ndsTool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NdsToolCommandCreator {

    public static List<String> createCommand(File ndsTool, String function, File folder) throws IOException {

        String ndsToolPath = ndsTool.toPath().toRealPath().toString();
        String folderPath = folder.toPath().toRealPath().toString();

        String baseCommand = function.substring(0, function.indexOf(' '));
        String target = function.substring(function.indexOf(' ') + 1);

        List<String> commands = new ArrayList<>();
        commands.add(ndsToolPath);
        commands.add(baseCommand);
        commands.add(target);

        commands.add("-9");
        commands.add(folderPath + File.separator + "arm9.bin");

        commands.add("-7");
        commands.add(folderPath + File.separator + "arm7.bin");

        commands.add("-y9 ");
        commands.add(folderPath + File.separator + "y9.bin");

        commands.add("-y7");
        commands.add(folderPath + File.separator + "y7.bin");

        commands.add("-d");
        commands.add(folderPath + File.separator + "data");

        commands.add("-y");
        commands.add(folderPath + File.separator + "overlay");

        commands.add("-t");
        commands.add(folderPath + File.separator + "banner.bin");

        commands.add("-h");
        commands.add(folderPath + File.separator + "header.bin");

        return commands;
    }
}
