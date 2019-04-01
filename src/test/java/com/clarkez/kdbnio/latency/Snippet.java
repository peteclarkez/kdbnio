package com.clarkez.kdbnio.latency;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Snippet {
//	public static void main(String... ignored) throws IOException {
//	    final String basePath = System.getProperty("java.io.tmpdir") + File.separator + "test";
//	    ChronicleTools.deleteOnExit(basePath);
//	    final int[] consolidates = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
//	    final int warmup = 500000;
//	    final int repeats = 20000000;
//	    //Write
//	    Thread t = new Thread(new Runnable() {
//	        @Override
//	        public void run() {
//	            try {
//	                final IndexedChronicle chronicle = new IndexedChronicle(basePath);
//	                chronicle.useUnsafe(true); // for benchmarks.
//	                final Excerpt excerpt = chronicle.createExcerpt();
//	                for (int i = -warmup; i < repeats; i++) {
//	                    doSomeThinking();
//	                    excerpt.startExcerpt(8 + 4 + 4 * consolidates.length);
//	                    excerpt.writeLong(System.nanoTime());
//	                    excerpt.writeUnsignedShort(consolidates.length);
//	                    for (final int consolidate : consolidates) {
//	                        excerpt.writeStopBit(consolidate);
//	                    }
//	                    excerpt.finish();
//	                }
//	                chronicle.close();
//	            } catch (Exception e) {
//	                e.printStackTrace();
//	            }
//	        }
//	
//	        private void doSomeThinking() {
//	            // real programs do some work between messages
//	            // this has an impact on the worst case latencies.
//	            Thread.yield();
//	        }
//	    });
//	    t.start();
//	    //Read
//	    final IndexedChronicle chronicle = new IndexedChronicle(basePath);
//	    chronicle.useUnsafe(true); // for benchmarks.
//	    final Excerpt excerpt = chronicle.createExcerpt();
//	    int[] times = new int[repeats];
//	    for (int count = -warmup; count < repeats; count++) {
//	        while (!excerpt.nextIndex()) {
//	        /* busy wait */
//	        }
//	        final long timestamp = excerpt.readLong();
//	        long time = System.nanoTime() - timestamp;
//	        if (count >= 0)
//	            times[count] = (int) time;
//	        final int nbConsolidates = excerpt.readUnsignedShort();
//	        assert nbConsolidates == consolidates.length;
//	        for (int i = 0; i < nbConsolidates; i++) {
//	            excerpt.readStopBit();
//	        }
//	        excerpt.finish();
//	    }
//	    Arrays.sort(times);
//	    for (double perc : new double[]{50, 90, 99, 99.9, 99.99}) {
//	        System.out.printf("%s%% took %.1f us, ", perc, times[((int) (repeats * perc / 100))] / 1000.0);
//	    }
//	    System.out.printf("worst took %d us%n", times[times.length - 1] / 1000);
//	    chronicle.close();
//	}
}

