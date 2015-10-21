package com.github.rsuiteCMS.gradlePhantomjsAssistant.bitbucket;

import com.github.rsuiteCMS.gradlePhantomjsAssistant.OS;

public enum Platform {
	WINDOWS("windows"),
	LINUX32("linux-i686"),
	LINUX64("linux-x86_64"),
	MAC("macosx");
	private String value;
	private Platform(String value) {
		this.value = value;
	}
	public static final Platform fromString(String v) {
		Platform[] vals = values();
		for (int i = 0; i < vals.length; i += 1) {
			if (vals[i].value.equals(v)) {
				return vals[i];
			}
		}
		return null;
	}
	public final static Platform CURRENT;
	static {
		if (OS.isWindows) {
			CURRENT = Platform.WINDOWS;
		} else if (OS.isMac) {
			CURRENT = Platform.MAC;
		} else if (OS.isNIX) {
			if (OS.is64Bit) {
				CURRENT = Platform.LINUX64;
			} else {
				CURRENT = Platform.LINUX32;
			}
		} else {
			CURRENT = null;
		}
	}
	
}