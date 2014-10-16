package xyz.rjs.brandwatch.supermarkets.logistics.plugins.ugly;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import xyz.rjs.brandwatch.supermarkets.model.events.ClockTick;

import com.google.common.collect.SetMultimap;
import com.google.common.eventbus.EventBus;

/**
 * This plugin requires access to the CustomerService and Supplier objects.
 * These are not available as \@autowired fields. However they are registered on
 * the EventBus. Since reflection is already being used, extracting the required
 * objects from the EventBus is no big deal.
 * 
 * It happens that the objects of desire subscribe to the ClockTick event.
 * 
 * The ServiceManager created by SimConfiguration could also be an avenue.
 * 
 * @author matthew
 *
 */
public class EventBusAbuser {
	private static final Field SUBSCRIBERS_BY_TYPE;

	static {
		try {
			SUBSCRIBERS_BY_TYPE = EventBus.class.getDeclaredField("subscribersByType");
			SUBSCRIBERS_BY_TYPE.setAccessible(true);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T get(Class<T> type, EventBus bus) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException  {
		SetMultimap<Class<?>, ?> subscribersByType = (SetMultimap<Class<?>, ?>)SUBSCRIBERS_BY_TYPE.get(bus);

		for (Object eventSubscriber : subscribersByType.get(ClockTick.class)) {
			// The class of the target field is not visible so this Method cannot be created in advance.
			Method TARGET = eventSubscriber.getClass().getMethod("getSubscriber");
			TARGET.setAccessible(true);
			Object target = TARGET.invoke(eventSubscriber);

			if (type.isInstance(target)) {
				return (T)target;
			}
		}
		return null;
	}
}
