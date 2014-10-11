package xyz.rjs.brandwatch.supermarkets.logistics.plugins.ugly;

import static org.junit.Assert.assertEquals;

import java.util.Random;
import java.util.function.Function;

import org.junit.Test;

/**
 * This is really a way to find good seeds to use.
 * 
 * @author matthew
 */
public class SeedTesterTest {

	private static final long MAX = Long.MAX_VALUE;
	private static final long PATTERN = 0xF0F0F0F0F0F0F0F0L;
	private static final long ONE_MILLION = 1000000;

	@Test
	public void testSeedTesterDoubleLow() {
		Function<Random, Boolean> objectTest = r -> {
			return r.nextDouble() < 0.1;
		};
		Function<Long, Integer> test = seed -> SeedTester.testSeed(seed, objectTest);

		assertEquals("Testing DOUBLE_LOW with seed 0", 0, test.apply(0L).intValue());
		assertEquals("Testing DOUBLE_LOW with seed 1", 0, test.apply(1L).intValue());
		assertEquals("Testing DOUBLE_LOW with seed ONE_MILLION", 0, test.apply(ONE_MILLION).intValue());
		assertEquals("Testing DOUBLE_LOW with seed MAX", 0, test.apply(MAX).intValue());
		assertEquals("Testing DOUBLE_LOW with seed MAX - 1", 0, test.apply(MAX - 1).intValue());
		assertEquals("Testing DOUBLE_LOW with seed MAX - ONE_MILLION", 0, test.apply(MAX - ONE_MILLION).intValue());

		long maxSeed = bestInRange(objectTest, PATTERN - ONE_MILLION, PATTERN + ONE_MILLION);
		assertEquals("Testing DOUBLE_LOW range", 6, test.apply(maxSeed).intValue());

		maxSeed = bestInRange(objectTest, 0, 2 * ONE_MILLION);
		assertEquals("Testing DOUBLE_LOW range", 5, test.apply(maxSeed).intValue());

		maxSeed = bestInRange(objectTest, MAX - (2 * ONE_MILLION), MAX);
		assertEquals("Testing DOUBLE_LOW range", 0, test.apply(maxSeed).intValue());

		maxSeed = bestInWalk(objectTest, PATTERN, 2 * ONE_MILLION);
		assertEquals("Testing DOUBLE_LOW walk", 5, test.apply(maxSeed).intValue());

		maxSeed = bestInWalk(objectTest, 0, 2 * ONE_MILLION);
		assertEquals("Testing DOUBLE_LOW walk", 4, test.apply(maxSeed).intValue());

		maxSeed = bestInWalk(objectTest, MAX, 2 * ONE_MILLION);
		assertEquals("Testing DOUBLE_LOW walk", 5, test.apply(maxSeed).intValue());
	}

	@Test
	public void testSeedTesterDoubleHigh() {
		Function<Random, Boolean> objectTest = r -> {
			return r.nextDouble() > 0.1;
		};
		Function<Long, Integer> test = seed -> SeedTester.testSeed(seed, objectTest);

		assertEquals("Testing DOUBLE_HIGH with seed 0", 13, test.apply(0L).intValue());
		assertEquals("Testing DOUBLE_HIGH with seed 1", 5, test.apply(1L).intValue());
		assertEquals("Testing DOUBLE_HIGH with seed ONE_MILLION", 1, test.apply(ONE_MILLION).intValue());
		assertEquals("Testing DOUBLE_HIGH with seed MAX", 1, test.apply(MAX).intValue());
		assertEquals("Testing DOUBLE_HIGH with seed MAX - 1", 2, test.apply(MAX - 1).intValue());
		assertEquals("Testing DOUBLE_HIGH with seed MAX - ONE_MILLION", 6, test.apply(MAX - ONE_MILLION).intValue());

		long maxSeed = bestInRange(objectTest, PATTERN - ONE_MILLION, PATTERN + ONE_MILLION);
		assertEquals("Testing DOUBLE_HIGH range", 128, test.apply(maxSeed).intValue());

		maxSeed = bestInRange(objectTest, 0, 2 * ONE_MILLION);
		assertEquals("Testing DOUBLE_HIGH range", 176, test.apply(maxSeed).intValue());

		maxSeed = bestInRange(objectTest, MAX - (2 * ONE_MILLION), MAX);
		assertEquals("Testing DOUBLE_HIGH range", 13, test.apply(maxSeed).intValue());

		maxSeed = bestInWalk(objectTest, PATTERN, 2 * ONE_MILLION);
		assertEquals("Testing DOUBLE_HIGH walk", 112, test.apply(maxSeed).intValue());

		maxSeed = bestInWalk(objectTest, 0, 2 * ONE_MILLION);
		assertEquals("Testing DOUBLE_HIGH walk", 114, test.apply(maxSeed).intValue());

		maxSeed = bestInWalk(objectTest, MAX, 2 * ONE_MILLION);
		assertEquals("Testing DOUBLE_HIGH walk", 105, test.apply(maxSeed).intValue());
	}

	@Test
	public void testSeedTester6IntHigh() {
		Function<Random, Boolean> objectTest = r -> {
			return r.nextInt(6) == 5;
		};
		Function<Long, Integer> test = seed -> SeedTester.testSeed(seed, objectTest);

		assertEquals("Testing 6_INT_HIGH with seed 0", 0, test.apply(0L).intValue());
		assertEquals("Testing 6_INT_HIGH with seed 1", 0, test.apply(1L).intValue());
		assertEquals("Testing 6_INT_HIGH with seed ONE_MILLION", 0, test.apply(ONE_MILLION).intValue());
		assertEquals("Testing 6_INT_HIGH with seed MAX", 2, test.apply(MAX).intValue());
		assertEquals("Testing 6_INT_HIGH with seed MAX - 1", 0, test.apply(MAX - 1).intValue());
		assertEquals("Testing 6_INT_HIGH with seed MAX - ONE_MILLION", 0, test.apply(MAX - ONE_MILLION).intValue());

		long maxSeed = bestInRange(objectTest, PATTERN - ONE_MILLION, PATTERN + ONE_MILLION);
		assertEquals("Testing 6_INT_HIGH range", 10, test.apply(maxSeed).intValue());

		maxSeed = bestInRange(objectTest, 0, 2 * ONE_MILLION);
		assertEquals("Testing 6_INT_HIGH range", 8, test.apply(maxSeed).intValue());

		maxSeed = bestInRange(objectTest, MAX - (2 * ONE_MILLION), MAX);
		assertEquals("Testing 6_INT_HIGH range", 0, test.apply(maxSeed).intValue());

		maxSeed = bestInWalk(objectTest, PATTERN, 2 * ONE_MILLION);
		assertEquals("Testing 6_INT_HIGH walk", 7, test.apply(maxSeed).intValue());

		maxSeed = bestInWalk(objectTest, 0, 2 * ONE_MILLION);
		assertEquals("Testing 6_INT_HIGH walk", 7, test.apply(maxSeed).intValue());

		maxSeed = bestInWalk(objectTest, MAX, 2 * ONE_MILLION);
		assertEquals("Testing 6_INT_HIGH walk", 7, test.apply(maxSeed).intValue());
	}

	@Test
	public void testSeedTester3IntNotHigh() {
		Function<Random, Boolean> objectTest = r -> {
			return r.nextInt(3) != 2;
		};
		Function<Long, Integer> test = seed -> SeedTester.testSeed(seed, objectTest);

		assertEquals("Testing 3_INT_NOT_HIGH with seed 0", 3, test.apply(0L).intValue());
		assertEquals("Testing 3_INT_NOT_HIGH with seed 1", 4, test.apply(1L).intValue());
		assertEquals("Testing 3_INT_NOT_HIGH with seed ONE_MILLION", 3, test.apply(ONE_MILLION).intValue());
		assertEquals("Testing 3_INT_NOT_HIGH with seed MAX", 0, test.apply(MAX).intValue());
		assertEquals("Testing 3_INT_NOT_HIGH with seed MAX - 1", 8, test.apply(MAX - 1).intValue());
		assertEquals("Testing 3_INT_NOT_HIGH with seed MAX - ONE_MILLION", 4, test.apply(MAX - ONE_MILLION).intValue());

		long maxSeed = bestInRange(objectTest, PATTERN - ONE_MILLION, PATTERN + ONE_MILLION);
		assertEquals("Testing 3_INT_NOT_HIGH range", 38, test.apply(maxSeed).intValue());

		maxSeed = bestInRange(objectTest, 0, 2 * ONE_MILLION);
		assertEquals("Testing 3_INT_NOT_HIGH range", 37, test.apply(maxSeed).intValue());

		maxSeed = bestInRange(objectTest, MAX - (2 * ONE_MILLION), MAX);
		assertEquals("Testing 3_INT_NOT_HIGH range", 3, test.apply(maxSeed).intValue());

		maxSeed = bestInWalk(objectTest, PATTERN, 2 * ONE_MILLION);
		assertEquals("Testing 3_INT_NOT_HIGH walk", 33, test.apply(maxSeed).intValue());

		maxSeed = bestInWalk(objectTest, 0, 2 * ONE_MILLION);
		assertEquals("Testing 3_INT_NOT_HIGH walk", 33, test.apply(maxSeed).intValue());

		maxSeed = bestInWalk(objectTest, MAX, 2 * ONE_MILLION);
		assertEquals("Testing 3_INT_NOT_HIGH walk", 30, test.apply(maxSeed).intValue());
	}

	/**
	 * This checks every single value within a range to find the one with the
	 * longest streak.
	 * 
	 * @param test
	 * @param min
	 * @param max
	 * @return
	 */
	public long bestInRange(Function<Random, Boolean> test, long min, long max) {
		int maxCount = 0;
		long maxSeed = 0;

		for (long seed = min; seed < max + 1; seed++) {
			int count = SeedTester.testSeed(seed, test);
			if (count > maxCount) {
				maxCount = count;
				maxSeed = seed;
			}
		}
		return maxSeed;
	}

	/**
	 * This repeatedly calls the random object starting from the initial seed to
	 * find the seed which starts the longest win streak.
	 * 
	 * @param test
	 * @param start
	 * @param count
	 * @return
	 */
	public long bestInWalk(Function<Random, Boolean> test, long start, long count) {
		long current = start, maxSeed = start;
		int run = 0, maxRun = 0;
		Random object = new Random(start);

		try {
			for (int i = 0; i < count; i++) {
				if (test.apply(object)) {
					if (run == 0) {
						current = Seed.initialScramble(Seed.getSeed(object));
					}
					run++;
					if (run > maxRun) {
						maxRun = run;
						maxSeed = current;
					}
				}
				else {
					run = 0;
				}
			}
			return maxSeed;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
