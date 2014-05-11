package microtime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class MicroTime {

    private static final Logger LOGGER = Logger.getLogger(MicroTime.class.getName());

    private static final String LIBRARY_NAME = "microtime";

    static String uname() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("mac")) {
            return "darwin";
        }
        return osName;
    }

    static String arch() {
        String osArch = System.getProperty("os.arch");
        if (osArch.equals("amd64")) {
            return "x86_64";
        }
        return osArch;
    }

    static {
        try {
            System.loadLibrary(LIBRARY_NAME);
            LOGGER.info("Found library in java.library.path");
        } catch (UnsatisfiedLinkError e) {
            try {
                String libraryName = System.mapLibraryName(LIBRARY_NAME);

                File tempDir = Files.createTempDirectory(LIBRARY_NAME).toFile();
                tempDir.deleteOnExit();

                String pathInJar = Paths.get("/", uname(), arch(), libraryName).toString();

                LOGGER.info("Loading from JAR " + pathInJar);

                InputStream inputStream = MicroTime.class.getResourceAsStream(pathInJar);

                if (inputStream == null) throw new RuntimeException("Can't find library in jar!");

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

    native public static long currentTimeMicros();

    public static void main(String[] args) {
        System.out.println(System.getProperty("os.arch"));
        System.out.println(MicroTime.currentTimeMicros());
        benchmark("Warmup");
        benchmark("Real");
    }

    static void benchmark(String name) {
        System.out.println("Benchmark " + name);
        long start = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            MicroTime.currentTimeMicros();
        }
        long end = System.nanoTime();
        System.out.println((end - start) / 1000 / 1000 + " ms");
    }
}
