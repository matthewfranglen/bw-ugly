package xyz.rjs.brandwatch.supermarkets.logistics.plugins.ugly;

import java.util.Random;

/**
 * This will take a Random object and reflect within it to alter the seed.
 * Prefixed seeds are available with known properties.
 * 
 * It is required to refresh the seeds periodically as they are only good for a
 * limited number of uses. This class can provide the number of invocations that
 * have occurred. This functionality is only reliable if a single method is
 * repeatedly called on the same random object.
 * 
 * @author matthew
 */
public class RandomFixer {

	/**
	 * This is the desired seed to fix.
	 */
	private final Seed seed;
	/**
	 * This is the random object that is being fixed.
	 */
	private final Random target;

	public RandomFixer(Seed seed, Random target) {
		this.seed = seed;
		this.target = target;
	}

	/**
	 * This fixes the outcome of the contained random object.
	 * 
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public void fix() throws IllegalArgumentException, IllegalAccessException {
		Seed.setSeed(target, Seed.initialScramble(seed.seed));
	}

	/**
	 * This returns the number of calls that can be made with a guaranteed
	 * outcome.
	 * 
	 * @return - The number of calls remaining. If 0 then the target must be
	 *         fixed immediately.
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public int callsRemaining() throws IllegalArgumentException, IllegalAccessException {
		int made = seed.getCalls(target);
		return made == -1 ? 0 : Math.max(seed.calls - made, 0);
	}
}
