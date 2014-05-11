package nativelib;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class Native {
    private static final Logger LOGGER = Logger.getLogger(Native.class.getName());

    public static String uname() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("mac")) {
            return "darwin";
        }
        return osName;
    }

    public static String arch() {
        String osArch = System.getProperty("os.arch");
        if (osArch.equals("amd64")) {
            return "x86_64";
        }
        return osArch;
    }

    public static void loadLibrary(String libname) {
        try {
            System.loadLibrary(libname);
            LOGGER.info("Found library in java.library.path");
        } catch (UnsatisfiedLinkError e) {
            try {
                String libraryName = System.mapLibraryName(libname);

                File tempDir = Files.createTempDirectory(libname).toFile();
                tempDir.deleteOnExit();

                String pathInJar = Paths.get("/", uname(), arch(), libraryName).toString();

                LOGGER.info("Loading from JAR: " + pathInJar);

                InputStream inputStream = Native.class.getResourceAsStream(pathInJar);

                if (inputStream == null) throw new RuntimeException("Can't find " + libraryName + " on classpath!");

                Path extractedLibraryPath = Paths.get(tempDir.toString(), libraryName);
                extractedLibraryPath.toFile().deleteOnExit();

                LOGGER.info(extractedLibraryPath.toString());

                Files.copy(inputStream, extractedLibraryPath);

                inputStream.close();

                LOGGER.info("loading " + libraryName);
                System.load(extractedLibraryPath.toString());
            } catch (IOException ioe) {
                throw new RuntimeException(e);
            }
        }
    }
}
