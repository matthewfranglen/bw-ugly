package xyz.rjs.brandwatch.supermarkets.logistics.plugins.ugly;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import xyz.rjs.brandwatch.supermarkets.logistics.plugins.AbstractPlugin;
import xyz.rjs.brandwatch.supermarkets.model.events.ArrivalNotification;
import xyz.rjs.brandwatch.supermarkets.model.events.ClockTick;
import xyz.rjs.brandwatch.supermarkets.model.events.Order;
import xyz.rjs.brandwatch.supermarkets.sim.AbstractProbabalisticTickingService;
import xyz.rjs.brandwatch.supermarkets.sim.CustomerService;
import xyz.rjs.brandwatch.supermarkets.sim.Shop;
import xyz.rjs.brandwatch.supermarkets.sim.Supplier;
import xyz.rjs.brandwatch.supermarkets.sim.Warehouse;

import com.google.common.eventbus.Subscribe;

/**
 * The ugly plugin worms it's way around the program altering the random objects
 * at the heart of it.
 * 
 * This alters the Supplier so that it never raises it's price. This alters the
 * CustomerService so that there is a trade every tick for the maximum amount
 * possible.
 * 
 * This has a short pause at the beginning when it is waiting for the first
 * delivery. Reducing that would involve looking for all of the Delivery objects
 * that are created and updating their random objects after creating them. While
 * possible it really only affects the very first delivery.
 * 
 * <pre>
 * Total Events: 5660
 * ClockTick{tick=1000}
 * Balance{balance=58672}
 * </pre>
 * 
 * @author matthew
 *
 */
@Component
public class UglyPlugin extends AbstractPlugin {

	private static final Logger logger = LoggerFactory.getLogger(UglyPlugin.class);

	/**
	 * This provides access to the timer random object in the
	 * AbstractProbabalisticTickingService.
	 */
	private static final Field TIMER_TICK_RANDOM;
	/**
	 * This provides access to the sale price random object in CustomerService.
	 */
	private static final Field SALE_PRICE_RANDOM;
	/**
	 * This provides access to the supplier price random object in Supplier.
	 */
	private static final Field SUPPLIER_PRICE_RANDOM;

	static {
		try {
			TIMER_TICK_RANDOM = AbstractProbabalisticTickingService.class.getDeclaredField("random");
			SALE_PRICE_RANDOM = CustomerService.class.getDeclaredField("random");
			SUPPLIER_PRICE_RANDOM = Supplier.class.getDeclaredField("random");

			TIMER_TICK_RANDOM.setAccessible(true);
			SALE_PRICE_RANDOM.setAccessible(true);
			SUPPLIER_PRICE_RANDOM.setAccessible(true);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get stock to cover 35 days of maximum trades (6)
	 */
	private static final int DESIRED_TOTAL_STOCK = 35 * 6;
	/**
	 * Have the shop hold two days of stock
	 */
	private static final int DESIRED_SHOP_STOCK = 2 * 6;

	/**
	 * The warehouse is required to create orders.
	 */
	@Autowired
	private Warehouse warehouse;

	/**
	 * The shop is required to stock from the warehouse.
	 */
	@Autowired
	private Shop shop;

	/**
	 * The customer service creates purchasers.
	 */
	private CustomerService customerService;

	/**
	 * The supplier changes prices.
	 */
	private Supplier supplier;

	/**
	 * Keeps track of orders that have not yet arrived.
	 */
	@Autowired
	private OrderTracker orders;

	/**
	 * The fixers guarantee the outcome of the random generators associated with
	 * them.
	 */
	private RandomFixer customerServiceTicker, customerServicePricer;
	private RandomFixer supplierTicker, supplierPricer;

	/**
	 * The current state of the plugin.
	 */
	private STATE state;

	public UglyPlugin() {
		state = STATE.START;
	}

	@Subscribe
	public void arrivalListener(ArrivalNotification arrival) {
		state.arrivalListener(this);
	}

	@Subscribe
	public void tickListener(ClockTick tick) throws Exception {
		state.tickListener(this);
	}

	/**
	 * Transitions the state of the plugin.
	 * 
	 * @param state
	 */
	private void setState(STATE state) {
		logger.info(String.format("STATE TRANSITION: %s to %s", this.state, state));

		this.state = state;
	}

	/**
	 * Creates and tracks the order.
	 * 
	 * @param volume
	 */
	private void placeOrder(int volume) {
		Order order = new Order();
		order.setWarehouse(warehouse);
		order.setVolume(volume);
		eventBus.post(order);
	}

	/**
	 * Restocks the shop that makes the sales.
	 */
	private void stockShop() {
		if (shop.getStock() < DESIRED_SHOP_STOCK && warehouse.getStock() > 0) {
			int volume = Math.min(DESIRED_SHOP_STOCK - shop.getStock(), warehouse.getStock());
			shop.addStock(volume);
			warehouse.setStock(warehouse.getStock() - volume);
		}
	}

	/**
	 * Issues any orders required to bring the total stock to the desired
	 * amount.
	 */
	private void stockWarehouse() {
		final int currentStock = getTotalStock();
		if (currentStock < DESIRED_TOTAL_STOCK) {
			placeOrder(DESIRED_TOTAL_STOCK - currentStock);
		}
	}

	/**
	 * Resets every random object that this depends on. I did all that work to
	 * find sequences of numbers just to reset them every cycle. Heh.
	 * 
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private void refreshFixers() throws IllegalArgumentException, IllegalAccessException {
		customerServiceTicker.fix();
		customerServicePricer.fix();
		supplierTicker.fix();
		supplierPricer.fix();
	}

	/**
	 * Gets the total stock available if all orders arrived.
	 * 
	 * @return
	 */
	private int getTotalStock() {
		return warehouse.getStock() + shop.getStock() + orders.size();
	}

	private enum STATE {
		/**
		 * The initial state just hooks up the wiring and makes an initial
		 * order. Once the order has arrived the trading can commence.
		 */
		START {

			@Override
			void arrivalListener(UglyPlugin plugin) {
				plugin.setState(TRADE);
			}

			@Override
			void tickListener(UglyPlugin plugin) throws NoSuchMethodException, SecurityException, IllegalAccessException,
					IllegalArgumentException, InvocationTargetException {
				if (plugin.customerService != null) {
					return;
				}

				plugin.customerService = EventBusAbuser.get(CustomerService.class, plugin.eventBus);
				plugin.supplier = EventBusAbuser.get(Supplier.class, plugin.eventBus);

				plugin.customerServiceTicker = new RandomFixer(Seed.GET_DOUBLE_LOW, (Random) TIMER_TICK_RANDOM.get(plugin.customerService));
				plugin.customerServicePricer = new RandomFixer(Seed.GET_6_INT_HIGH, (Random) SALE_PRICE_RANDOM.get(plugin.customerService));
				plugin.supplierTicker = new RandomFixer(Seed.GET_DOUBLE_HIGH, (Random) TIMER_TICK_RANDOM.get(plugin.supplier));
				plugin.supplierPricer = new RandomFixer(Seed.GET_3_INT_NOT_HIGH, (Random) SUPPLIER_PRICE_RANDOM.get(plugin.supplier));

				plugin.supplierTicker.fix();
				plugin.supplierPricer.fix();

				plugin.stockWarehouse();
			}
		},
		/**
		 * This performs all housekeeping duties required to keep the maximized
		 * trades occurring.
		 */
		TRADE {

			@Override
			void tickListener(UglyPlugin plugin) throws IllegalArgumentException, IllegalAccessException {
				plugin.stockWarehouse();
				plugin.stockShop();
				plugin.refreshFixers();
			}
		};

		void arrivalListener(UglyPlugin plugin) {
		}

		abstract void tickListener(UglyPlugin plugin) throws Exception;
	}
}
