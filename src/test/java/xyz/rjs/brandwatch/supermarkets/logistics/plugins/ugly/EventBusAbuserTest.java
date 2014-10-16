package xyz.rjs.brandwatch.supermarkets.logistics.plugins.ugly;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import xyz.rjs.brandwatch.supermarkets.sim.CustomerService;
import xyz.rjs.brandwatch.supermarkets.sim.Supplier;

import com.google.common.eventbus.EventBus;


/**
 * @author matthew
 *
 */
public class EventBusAbuserTest {

	@Test
	public void testEventBusAbuser() throws Exception {
		EventBus bus = new EventBus();
		CustomerService customerService = new CustomerService(bus);
		Supplier supplier = new Supplier(bus);

		bus.register(customerService);
		bus.register(supplier);

		assertEquals(customerService, EventBusAbuser.get(CustomerService.class, bus));
		assertEquals(supplier, EventBusAbuser.get(Supplier.class, bus));
	}
}
