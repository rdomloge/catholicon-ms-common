package com.domloge.catholicon.ms.common;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
public class Sync<K,T> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Sync.class);

	@Autowired
	ObjectMapper mapper;
	

	@PostConstruct
	public void configureMapper() {
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		//.withoutAttribute("property2");
	}

	public Diff<T> compare(Map<K, T> master, Map<K, T> db) throws ScraperException {
		
		StopWatch sw = new StopWatch("Sync");
		sw.start();
		
		Diff<T> diff = new Diff<>();
		
		// check other and remove items not in master
		Set<K> dbKeys = db.keySet();
		for (K dbKey : dbKeys) {
			if( ! master.containsKey(dbKey)) {
				LOGGER.info("Master no longer contains key {}, removing from DB", dbKey);
				diff.addToDelete(db.get(dbKey));
			}
		}
		
		// check master and 
		Set<K> masterKeys = master.keySet();
		for (K masterKey : masterKeys) {
			if( ! db.containsKey(masterKey)) {
				T t = master.get(masterKey);
				LOGGER.info("New value detected: {}, adding to DB", t);
				diff.addToNewValues(t);
			}
			else {
				T masterValue = master.get(masterKey);
				T dbValue = db.get(masterKey);
				ObjectWriter writer = mapper.writer().withoutAttribute("id");
				String masterValueStr;
				String dbValueStr;
				try {
					masterValueStr = writer.writeValueAsString(masterValue);
					dbValueStr = writer.writeValueAsString(dbValue);
				}
				catch(JsonProcessingException jpex) {
					throw new ScraperException(jpex);
				}

				if( ! masterValueStr.equals(dbValueStr)) {
					LOGGER.info("Master value {} has changed - updating DB", masterKey);
					DiffMatchPatch dmp = new DiffMatchPatch();
					LinkedList<org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Diff> diffMain = 
						dmp.diffMain(masterValueStr, dbValueStr, false);
					LOGGER.info(diffMain.toString());
					// Merge
					BeanUtils.copyProperties(dbValue, masterValue, "id");
					diff.addToUpdate(dbValue);
				}
				else {
					LOGGER.info("Master value {} matches DB value", masterKey);
				}
			}
		}
		
		sw.stop();
		LOGGER.debug(sw.shortSummary());
		return diff;
	}

}
