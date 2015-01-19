package edu.dartmouth.cs.myruns;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.labs.repackaged.org.json.JSONArray;

import edu.dartmouth.cs.myruns.gae.HistoryEntry;
import edu.dartmouth.cs.myruns.gae.HistoryEntryEntityConverter;

/**
 * Simple implementation of a data store using standard Java collections.
 * <p>
 * This class is neither persistent (it will lost the data when the app is
 * restarted) nor thread safe.
 */
public final class Datastore {

	static final int MULTICAST_SIZE = 1000;
	private static final String ENTITY_KIND_DEVICE = "Device";
	private static final String DEVICE_REG_ID_PROPERTY = "regId";

	private static final String ENTITY_KIND_HISTORY_ENTRY = "HistoryEntry";

	private static final FetchOptions DEFAULT_FETCH_OPTIONS = FetchOptions.Builder
			.withPrefetchSize(MULTICAST_SIZE).chunkSize(MULTICAST_SIZE);

	private static final Logger logger = Logger.getLogger(Datastore.class
			.getName());
	private static final DatastoreService datastore = DatastoreServiceFactory
			.getDatastoreService();

	private Datastore() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Registers a device.
	 * 
	 * @param regId
	 *            device's registration id.
	 */
	public static void register(String regId) {
		logger.info("Registering " + regId);

		Entity entity = new Entity(ENTITY_KIND_DEVICE, regId);
		entity.setProperty(DEVICE_REG_ID_PROPERTY, regId);
		datastore.put(entity);
	}

	/**
	 * Unregisters a device.
	 * 
	 * @param regId
	 *            device's registration id.
	 */
	public static void unregister(String regId) {
		logger.info("Unregistering " + regId);

		Key deviveKey = KeyFactory.createKey(ENTITY_KIND_DEVICE, regId);
		datastore.delete(deviveKey);
	}

	/**
	 * Gets all registered devices.
	 */
	public static List<String> getDevices() {
		List<String> devices;

		Query query = new Query(ENTITY_KIND_DEVICE);
		Iterable<Entity> entities = datastore.prepare(query).asIterable(
				DEFAULT_FETCH_OPTIONS);
		devices = new ArrayList<String>();
		for (Entity entity : entities) {
			String device = (String) entity.getProperty(DEVICE_REG_ID_PROPERTY);
			devices.add(device);
		}

		return devices;
	}

	private static Entity findDeviceByRegId(String regId) {
		Key deviveKey = KeyFactory.createKey(ENTITY_KIND_DEVICE, regId);

		Entity entity = null;

		try {
			entity = datastore.get(deviveKey);
		} catch (Exception ex) {
			return null;
		}

		return entity;
	}

	public static Key getRegDeviceKey(String regId) {
		Key deviveKey = KeyFactory.createKey(ENTITY_KIND_DEVICE, regId);

		return deviveKey;
	}

	public static void deleteHistoryEntry(String regId, String entryId) {
		Key deleteKey = KeyFactory.createKey(getRegDeviceKey(regId),
				ENTITY_KIND_HISTORY_ENTRY, entryId);
		datastore.delete(deleteKey);
	}

	private static void clearHistoryEntry(String regId) {
		List<HistoryEntry> oldRecord = getHistoryEntry(regId);
		for (HistoryEntry entry : oldRecord) {
			deleteHistoryEntry(regId, Long.toString(entry.id));
		}
	}

	/**
	 * Save data from device.
	 * 
	 * @param regId
	 *            Device's registration id
	 * @param data
	 *            The data posted by the device
	 */
	public static void saveData(String regId, String data) {
		logger.info("Saving " + data);

		JSONArray historyEntryList = null;
		try {
			historyEntryList = new JSONArray(data);
		} catch (Exception ex) {
			return;
		}

		Transaction txn = datastore.beginTransaction();
		try {
			Entity parentEntity = findDeviceByRegId(regId);
			if (parentEntity == null) {
				return;
			}

			// clear
			clearHistoryEntry(regId);

			// add
			for (int i = 0; i < historyEntryList.length(); i++) {
				HistoryEntry entry = new HistoryEntry();
				entry.fromJSONObject(historyEntryList.getJSONObject(i));

				Entity entity = HistoryEntryEntityConverter.toEntity(entry,
						ENTITY_KIND_HISTORY_ENTRY, getRegDeviceKey(regId));
				datastore.put(entity);
			}
			txn.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	/**
	 * Gets the existing data from datastore
	 */
	public static List<HistoryEntry> getHistoryEntry(String regId) {
		ArrayList<HistoryEntry> result = new ArrayList<HistoryEntry>();
		if (regId != null) {
			Query query = new Query(ENTITY_KIND_HISTORY_ENTRY);
			
			query.setAncestor(getRegDeviceKey(regId));

			Iterable<Entity> entities = datastore.prepare(query).asIterable(
					DEFAULT_FETCH_OPTIONS);
			for (Entity entity : entities) {
				logger.info("entitiy: " + entity);
				result.add(HistoryEntryEntityConverter.fromEntity(entity));
			}
		} else {
			Query regQuery = new Query(ENTITY_KIND_DEVICE);
			regQuery.setKeysOnly();
			
			Iterable<Entity> regKeyEntity = datastore.prepare(regQuery).asIterable(
					DEFAULT_FETCH_OPTIONS);
			for (Entity regEntity : regKeyEntity) {
				Query query = new Query(ENTITY_KIND_HISTORY_ENTRY);
				
				query.setAncestor(regEntity.getKey());

				Iterable<Entity> entities = datastore.prepare(query).asIterable(
						DEFAULT_FETCH_OPTIONS);
				for (Entity entity : entities) {
					logger.info("entitiy: " + entity);
					result.add(HistoryEntryEntityConverter.fromEntity(entity));
				}
			}
		}
		return result;
	}
}
