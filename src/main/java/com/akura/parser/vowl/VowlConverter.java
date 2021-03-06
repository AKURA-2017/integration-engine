package com.akura.parser.vowl;

import com.akura.utility.CommandExecutor;

import java.io.*;
import java.util.UUID;

/**
 * Class representing a VowlConverter.
 */
public class VowlConverter {

    /**
     * Method used to convert the OWL to vowl.
     *
     * @return - string value.
     */
    public static String convert() {

        String domainName;
        String source;
        String target;
        String uuid = UUID.randomUUID().toString();

        if (System.getProperty("os.name").startsWith("Windows")) {

            domainName = "java -jar " + System.getProperty("user.dir") + "\\lib\\owl2vowl.jar " +
                    "-file " + System.getProperty("user.dir") + "\\test-1.owl  " +
                    "-output " + System.getProperty("user.dir") + "\\webvowl\\data\\foaf.json  ";

            source = System.getProperty("user.dir") + "\\test-1.owl ";
            target = System.getProperty("user.dir") + "\\webvowl\\ontologies\\" + uuid + ".owl";


        } else {
            domainName = "java -jar " + System.getProperty("user.dir") + "/lib/owl2vowl.jar " +
                    "-file " + System.getProperty("user.dir") + "/test-1.owl  " +
                    "-output " + System.getProperty("user.dir") + "/webvowl/data/foaf.json  ";

            source = System.getProperty("user.dir") + "/test-1.owl";
            target = System.getProperty("user.dir") + "/webvowl/ontologies/" + uuid + ".owl";
        }

        System.out.println("source: " + source);
        System.out.println("target: " + target);
        String output = CommandExecutor.executeCommand(domainName);
        System.out.println(output);

        try {

            CommandExecutor.copyFileUsingApacheCommonsIO(new File(source), new File(target));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uuid + ".owl";
    }
}
