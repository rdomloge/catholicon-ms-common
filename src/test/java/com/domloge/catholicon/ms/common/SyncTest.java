package com.domloge.catholicon.ms.common;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SyncTest {
	
	private Sync<String> target;
	
	@Before
	public void setup() {
		target = new Sync<>();
	}

	@Test
	public void compare_itemAdded_diffContains() {
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
	public void compare_itemRemoved_diffContains() {
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
	public void compare_itemUpdated_diffContains() {
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
