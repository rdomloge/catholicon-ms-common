package com.domloge.catholicon.ms.common;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SyncTest extends Sync {
	
	private Sync<Integer,String> target;
	
	@Before
	public void setup() {
		target = new Sync<>();
		target.mapper = new ObjectMapper();
	}

	@Test
	public void compare_itemAdded_diffContains() throws ScraperException {
		// given
		Map<Integer, String> db = new HashMap<>();
		Map<Integer, String> master = new HashMap<>();
		master.put(1, "Test");
		
		// when
		Diff<String> compare = target.compare(master, db);
		
		// then
		Assert.assertThat(1, CoreMatchers.equalTo(compare.getNewValues().size()));
	}
	
	@Test
	public void compare_itemRemoved_diffContains() throws ScraperException {
		// given
		Map<Integer, String> db = new HashMap<>();
		Map<Integer, String> master = new HashMap<>();
		db.put(2, "another");
		
		// when
		Diff<String> compare = target.compare(master, db);
		
		// then
		Assert.assertThat(1, CoreMatchers.equalTo(compare.getDelete().size()));
	}
	
	@Test
	public void compare_itemUpdated_diffContains() throws ScraperException {
		// given
		Map<Integer, String> db = new HashMap<>();
		Map<Integer, String> master = new HashMap<>();
		master.put(3, "flange");
		db.put(3, "fart");
		
		// when
		Diff<String> compare = target.compare(master, db);
		
		// then
		Assert.assertThat(1, CoreMatchers.equalTo(compare.getUpdate().size()));
	}

}
