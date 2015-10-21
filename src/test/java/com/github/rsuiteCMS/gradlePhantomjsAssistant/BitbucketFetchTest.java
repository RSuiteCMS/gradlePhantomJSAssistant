package com.github.rsuiteCMS.gradlePhantomjsAssistant;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class BitbucketFetchTest {
	@Test
	public void configWorks() throws IOException {
		File tmp = File.createTempFile("phantomjs", "");
		tmp.delete();
		File binary = PhantomJSConfig.deployBitbucket("1.9.8", tmp);
		assertTrue("Binary doesn't exist", binary.exists());
		assertTrue("Binary is not executable", binary.canExecute());
		tmp.deleteOnExit();
	}
}
