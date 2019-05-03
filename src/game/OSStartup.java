package game;

import java.io.IOException;

public class OSStartup {

    public static void main(String[] args) {

        /*
         * mac startup sequence
         *
         * have to add the command beforehand then re run jar
         */
        String os = System.getProperty("os.name");

        if (os.indexOf("mac") >= 0){
            if (args.length == 0) {
                try {
                    Runtime.getRuntime().exec(new String[]{"java", "-XstartOnFirstThread", "-jar", "Spark Runner 2.jar", "noReRun"});
                    System.exit(-1);
                } catch (IOException ex) { ex.printStackTrace(); }
            }else {
                new Main();
            }
        }else {
            new Main();
        }

    }

}
