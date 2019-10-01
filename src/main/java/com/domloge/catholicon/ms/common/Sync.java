package com.domloge.catholicon.ms.common;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
public class Sync<T> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Sync.class); 
	
	public Diff<T> compare(Map<Integer, T> master, Map<Integer, T> db) {
		
		StopWatch sw = new StopWatch("Sync");
		sw.start();
		
		Diff<T> diff = new Diff<>();
		
		// check other and remove items not in master
		Set<Integer> dbKeys = db.keySet();
		for (Integer dbKey : dbKeys) {
			if( ! master.containsKey(dbKey)) {
				LOGGER.info("Master no longer contains key {}, removing from DB", dbKey);
				diff.addToDelete(db.get(dbKey));
			}
		}
		
		// check master and 
		Set<Integer> masterKeys = master.keySet();
		for (Integer masterKey : masterKeys) {
			if( ! db.containsKey(masterKey)) {
				T t = master.get(masterKey);
				LOGGER.info("New value detected: {}, adding to DB", t);
				diff.addToNewValues(t);
			}
			else {
				T masterValue = master.get(masterKey);
				T dbValue = db.get(masterKey);
				if( ! masterValue.equals(dbValue)) {
					LOGGER.info("Master value {} has changed - updating DB", masterValue);
					// Merge
					try {
						BeanUtils.copyProperties(dbValue, masterValue);
						diff.addToUpdate(dbValue);
					} 
					catch (IllegalAccessException | InvocationTargetException e) {
						throw new RuntimeException(e);
					}
				}
				else {
					LOGGER.info("Master value {} matches DB value {}", masterValue, dbValue);
				}
			}
		}
		
		sw.stop();
		LOGGER.debug(sw.prettyPrint());
		return diff;
	}

}
