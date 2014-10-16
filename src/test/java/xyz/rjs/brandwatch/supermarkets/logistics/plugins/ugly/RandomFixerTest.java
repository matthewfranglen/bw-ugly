package xyz.rjs.brandwatch.supermarkets.logistics.plugins.ugly;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;


/**
 * @author matthew
 *
 */
public class RandomFixerTest {

	@Test
	public void testRandomFixer() throws IllegalArgumentException, IllegalAccessException {
		for (Seed seed : Seed.values()) {
			Random random = new Random(seed.seed);
			RandomFixer fixer = new RandomFixer(seed, random);

			fixer.fix();
			assertEquals("Test initial call count", seed.calls, fixer.callsRemaining());
			for (int i = seed.calls;i > 0;i--) {
				seed.call.apply(random);
				assertEquals("Test reduced call count", i - 1, fixer.callsRemaining());
			}
			assertEquals("Test end call count", 0, fixer.callsRemaining());

			for (int i = 0;i < 500;i++) {
				seed.call.apply(random);
			}
			assertEquals("Test end call count", 0, fixer.callsRemaining());
		}
	}
}
