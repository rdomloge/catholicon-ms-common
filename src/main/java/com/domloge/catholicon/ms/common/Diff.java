package com.domloge.catholicon.ms.common;

import java.util.LinkedList;
import java.util.List;

public class Diff<T> {
	
	private List<T> delete = new LinkedList<>();
	private List<T> update = new LinkedList<>();
	private List<T> newValues = new LinkedList<>();

	public void addToDelete(T t) {
		delete.add(t);
	}
	
	public void addToUpdate(T t) {
		update.add(t);
	}
	
	public void addToNewValues(T t) {
		newValues.add(t);
	}

	public List<T> getDelete() {
		return delete;
	}

	public List<T> getUpdate() {
		return update;
	}

	public List<T> getNewValues() {
		return newValues;
	}
}