package com.domloge.catholicon.ms.common;

import java.io.IOException;

@SuppressWarnings("serial")
public class ScraperException extends Exception {

	public ScraperException(IOException e) {
		super(e);
	}

	public ScraperException(String msg) {
		super(msg);
	}

}
