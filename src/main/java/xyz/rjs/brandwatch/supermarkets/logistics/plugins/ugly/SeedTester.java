package xyz.rjs.brandwatch.supermarkets.logistics.plugins.ugly;

import java.util.Random;
import java.util.function.Function;


/**
 * @author matthew
 *
 */
public class SeedTester {

	public static int testSeed(long seed, Function<Random, Boolean> test) {
		Random object = new Random(seed);

		for (int i = 0;;i++) {
			if (! test.apply(object)) {
				return i;
			}
		}
	}
}