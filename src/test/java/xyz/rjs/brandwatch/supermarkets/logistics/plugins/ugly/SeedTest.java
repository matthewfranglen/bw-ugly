package xyz.rjs.brandwatch.supermarkets.logistics.plugins.ugly;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import org.junit.Test;


/**
 * @author matthew
 *
 */
public class SeedTest {
	private static final Map<Seed, Function<Random, Boolean>> mapping;
	
	static {
		mapping = new HashMap<>();
		mapping.put(Seed.GET_3_INT_NOT_HIGH, r -> r.nextInt(3) != 2);
		mapping.put(Seed.GET_6_INT_HIGH, r -> r.nextInt(6) == 5);
		mapping.put(Seed.GET_DOUBLE_HIGH, r -> r.nextDouble() > 0.1);
		mapping.put(Seed.GET_DOUBLE_LOW, r -> r.nextDouble() < 0.1);
	}

	@Test
	public void testSeedAccuracy() {
		for (Seed seed : Seed.values()) {
			assertEquals("Test " + seed, seed.calls, SeedTester.testSeed(seed.seed, mapping.get(seed)));
		}
	}

	@Test
	public void testSeedCallTracking() throws IllegalArgumentException, IllegalAccessException {
		for (Seed seed : Seed.values()) {
			Random random = new Random(seed.seed);

			assertEquals(0, seed.getCalls(random));
			for (int i = 0;i < seed.calls;i++) {
				assertEquals(i, seed.getCalls(random));
				seed.call.apply(random);
			}
		}
	}
}
