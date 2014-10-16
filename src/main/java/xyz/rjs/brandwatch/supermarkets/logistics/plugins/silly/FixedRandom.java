package xyz.rjs.brandwatch.supermarkets.logistics.plugins.silly;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

/**
 * This will return fixed values for the desired call.
 * 
 * @author matthew
 */
// I don't have time to complete this plugin but basically any amount of balance
// can be achieved in just a few turns:

// TURN ONE
// 1) Reflect into Supplier and set the price to 1, and disable the timer
// (e.g. makeFixedDoubles(1.0))
// 2) Reflect into Supplier and set the distance to 0
// 3) Order as much as required (each unit sells for 10 and is purchased for 1,
// so to MAX_INT order:
// Math.floor(Integer.MAX_INT / 9) - Math.ceil(balance / 10)
// (Integer.MAX_INT / 9 is because you need to pay for the stock that is
// purchased)
// With a balance of 100 this is: 238,609,284
// 4) Reflect into the AbstractTravellingAction and disable the delay

// TURN TWO
// 1) Order will arrive
// 2) Transfer all stock into the shop

// TURN THREE
// 1) Reflect into CustomerService and make the customer buy the entire stock
// next turn only
// (this class allows the return value of nextInt(6) to be any value).
// (only one sale is required unless you want to get exactly MAX_INT which may
// require a little fiddling).

// TURN FOUR (or possibly three)
// Customer buys all stock. You retire.
// Depending on the order that things are called, this may occur in step three
// if the reflection occurs before the probabalistic timer is checked. This is
// why the shop is restocked on a separate turn.
public class FixedRandom {

	/**
	 * Creates a Random object with a changed nextDouble method that will return
	 * the values in order. Once the values have all been returned the last
	 * value will be repeated.
	 * 
	 * @param values
	 * @return
	 */
	@SuppressWarnings("serial")
	// not intended for serialization
	public static Random makeFixedDoubles(Double... values) {
		final Iterator<Double> v = Arrays.asList(values).iterator();
		final double last = values[values.length - 1];

		return new Random() {

			@Override
			public double nextDouble() {
				return v.hasNext() ? v.next() : last;
			}
		};
	}

	/**
	 * Creates a Random object with a changed nextInt(bound) method that will
	 * return the values in order. Once the values have all been returned the
	 * last value will be repeated.
	 * 
	 * @param values
	 * @return
	 */
	@SuppressWarnings("serial")
	// not intended for serialization
	public static Random makeFixedInts(Integer... values) {
		final Iterator<Integer> v = Arrays.asList(values).iterator();
		final int last = values[values.length - 1];

		return new Random() {

			@Override
			public int nextInt(int bound) {
				return v.hasNext() ? v.next() : last;
			}
		};
	}

	private FixedRandom() {
	}
}
