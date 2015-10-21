package com.github.rsuiteCMS.gradlePhantomjsAssistant.bitbucket;

public enum Archive {
	ZIP("zip"),
	TARBZ2("tar.bz2"),
	TAR("tar"),
	TARGZ("tar.gz");
	private String value;
	private Archive(String value) {
		this.value = value;
	}
	public String toString() {
		return this.value;
	}
	public static final Archive fromString(String v) {
		Archive[] vals = values();
		for (int i = 0; i < vals.length; i += 1) {
			if (vals[i].value.equals(v)) {
				return vals[i];
			}
		}
		return null;
	}
}