package xyz.rjs.brandwatch.supermarkets.logistics.plugins.ugly;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This holds the different valuable seeds to use.
 * 
 * This also contains the code to access and set the seed on a Random object. It
 * is important to note that seeds passed to a Random constructor are
 * 'scrambled' which is a reversible process (loses bits 49-64).
 * 
 * @author matthew
 */
public enum Seed {
	/**
	 * This will return less than 0.1 when nextDouble is called. This is good
	 * for N calls.
	 */
	GET_DOUBLE_LOW(-1085102592571702774L, 6, r -> r.nextDouble()),
	/**
	 * This will return more than 0.1 when nextDouble is called. This is good
	 * for N calls.
	 */
	GET_DOUBLE_HIGH(625839L, 176, r -> r.nextDouble()),
	/**
	 * This will return 6 when nextInt(6) is called. This is good for N calls.
	 */
	GET_6_INT_HIGH(-1085102592571605280L, 10, r -> r.nextInt(6)),
	/**
	 * This will return 0 or 1 when nextInt(3) is called. This is good for N
	 * calls.
	 */
	GET_3_INT_NOT_HIGH(-1085102592570157490L, 38, r -> r.nextInt(3));

	/**
	 * A copy of the multiplier field from Random.
	 */
	private static final long SCRAMBLE_MULTIPLIER = 0x5DEECE66DL;
	/**
	 * A copy of the mask field from Random.
	 */
	private static final long SCRAMBLE_MASK = (1L << 48) - 1;
	/**
	 * This is the field on the Random object that holds the seed.
	 */
	private static final Field SEED_ACCESSOR;

	/**
	 * This initializes the SEED_ACCESSOR. The SecurityManager can prevent this.
	 * If there are any problems here then this class is unusable.
	 */
	static {
		try {
			SEED_ACCESSOR = Random.class.getDeclaredField("seed");
			SEED_ACCESSOR.setAccessible(true);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the current value of the seed within the provided Random object.
	 * 
	 * @param random
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	static long getSeed(Random random) throws IllegalArgumentException, IllegalAccessException {
		AtomicLong field = (AtomicLong) SEED_ACCESSOR.get(random);
		return field.get();
	}

	/**
	 * Sets the current value of the seed within the provided Random object.
	 * 
	 * @param random
	 * @param seed
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	static void setSeed(Random random, long seed) throws IllegalArgumentException, IllegalAccessException {
		AtomicLong field = (AtomicLong) SEED_ACCESSOR.get(random);
		field.set(seed);
	}

	/**
	 * This applies the initial scrambling to the seed, which is reversible by
	 * calling this again. The resulting seed is truncated to 48 bytes, but
	 * Random does not use more anyway.
	 * 
	 * @param seed
	 */
	static long initialScramble(long seed) {
		return (seed ^ SCRAMBLE_MULTIPLIER) & SCRAMBLE_MASK;
	}

	public final long seed;
	/**
	 * When a Random object is constructed with a seed it gets an
	 * initialScramble. This holds the result of that scramble.
	 */
	private final long postScrambleSeed;
	public final int calls;
	public final RandomMethodCall call;

	private Seed(long seed, int calls, RandomMethodCall call) {
		this.seed = seed;
		postScrambleSeed = initialScramble(seed);
		this.calls = calls;
		this.call = call;
	}

	/**
	 * Searches for the number of times that the provided random object has been
	 * called. This has a limit of 249 invocations, so if the search exceeds
	 * that then -1 will be returned.
	 * 
	 * @param random
	 *            - Random object to inspect.
	 * @return - Number of times method has been called on the Random object, or
	 *         -1 if depth limit exceeded.
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public int getCalls(Random random) throws IllegalArgumentException, IllegalAccessException {
		final long currentSeed = getSeed(random);
		if (currentSeed == postScrambleSeed) {
			return 0;
		}

		Random compare = new Random(seed);

		for (int i = 1; i < 250; i++) {
			call.apply(compare);

			if (getSeed(compare) == currentSeed) {
				return i;
			}
		}
		return -1;
	}
}

interface RandomMethodCall {

	public void apply(Random random);
}