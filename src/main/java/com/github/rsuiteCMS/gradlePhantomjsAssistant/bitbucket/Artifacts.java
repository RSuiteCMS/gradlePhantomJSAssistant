package com.github.rsuiteCMS.gradlePhantomjsAssistant.bitbucket;

import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.io.IOUtils.copy;
import groovy.json.JsonSlurper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Artifacts extends ArrayList<Artifact> {
	private static final long serialVersionUID = -3645029092082948012L;
	public boolean add(Artifact dl) {
		boolean ok = super.add(dl);
		return ok;
	}
	public Artifact latest() {
		return get(0);
	}
	public Artifacts getByName(String name) {
		Artifacts filt = new Artifacts();
		for (Artifact dl : this) {
			if (name.equals(dl.name)) {
				filt.add(dl);
			}
		}
		return filt;
	}
	public Artifacts getByVersion(String version) {
		Artifacts filt = new Artifacts();
		for (Artifact dl : this) {
			if (dl.version.startsWith(version)) {
				filt.add(dl);
			}
		}
		return filt;
	}
	public Artifacts getByPlatform(String type) {
		return getByPlatform(Platform.fromString(type));
	}
	public Artifacts getByPlatform(Platform type) {
		Artifacts filt = new Artifacts();
		for (Artifact dl : this) {
			if (type.equals(dl.platform)) {
				filt.add(dl);
			}
		}
		return filt;
	}
	public Artifacts getByArchive(Archive type) {
		Artifacts filt = new Artifacts();
		for (Artifact dl : this) {
			if (type.equals(dl.archiveType)) {
				filt.add(dl);
			}
		}
		return filt;
	}
	public Artifacts getByArchive(String type) {
		return getByArchive(Archive.fromString(type));
	}
	public static Object getUnknownPath(Object root, String[] path) {
		Object here = root;
		for (String seg : path) {
			if (!(here instanceof Map)) {
				return null;
			}
			here = ((Map<?, ?>) here).get(seg);
		}
		return here;
	}
	public static String getUrlText(URL url) throws IOException {
		InputStream is = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			is = url.openStream();
			copy(is, baos);
		} catch (IOException ioe) {
			System.err.println("Failed URL: " + url.toString());
			throw ioe;
		} finally {
			closeQuietly(is);
		}
		return new String(baos.toByteArray(), "utf-8");
	}
	public static Artifacts getBuilds(URL url) throws IOException {
		JsonSlurper slurper = new JsonSlurper();
		Object nextPage = slurper.parseText(getUrlText(url));
		Artifacts dls = new Artifacts();
		List<Map<?, ?>> builds = new ArrayList<Map<?, ?>>();
		if (nextPage instanceof Map) {
			Map<?, ?> page = (Map<?, ?>) nextPage;
			Object buildList = page.get("values");
			if (buildList instanceof List) {
				for (Object item : (List<?>) buildList) {
					if (item instanceof Map) {
						builds.add((Map<?, ?>) item);
					}
				}
				while (page != null && page.get("next") instanceof String) {
					nextPage = slurper.parseText(getUrlText(new URL((String) page.get("next"))));
					if (nextPage instanceof Map) {					
						page = (Map<?, ?>) nextPage;
						Object values = page.get("values");
						if (values instanceof List) {
							for (Object item : ((List<?>) values)) {
								if (item instanceof Map) {
									builds.add((Map<?, ?>) item);
								}
							}
						}
					} 
				}
			} 
			for (Map<?, ?> build : builds) {
				String href = (String) getUnknownPath(build, new String[]{ "links", "self", "href" });
				if (href != null) {
					Artifact dl = new Artifact(href);
					if (dl.getName() != null) {
						dls.add(dl);
					}
				}
			}
		}
		return dls.getByPlatform(Platform.CURRENT);
	}
}