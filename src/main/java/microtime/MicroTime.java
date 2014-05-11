package microtime;

import nativelib.Native;

public class MicroTime {

    private static final String LIBRARY_NAME = "microtime";

    static {
        Native.loadLibrary(LIBRARY_NAME);
    }

    native public static long currentTimeMicros();

    public static void main(String[] args) {
        System.out.println(Native.arch());
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
