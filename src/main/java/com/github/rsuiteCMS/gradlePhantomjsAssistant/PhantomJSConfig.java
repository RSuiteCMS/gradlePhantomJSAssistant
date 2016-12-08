package com.github.rsuiteCMS.gradlePhantomjsAssistant;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.inject.Inject;

import org.gradle.api.Project;

import com.github.rsuiteCMS.gradlePhantomjsAssistant.bitbucket.Artifact;
import com.github.rsuiteCMS.gradlePhantomjsAssistant.bitbucket.Artifacts;


public class PhantomJSConfig {
	private final static String PHANTOM_JS_BUILDS_URL = "https://api.bitbucket.org/2.0/repositories/ariya/phantomjs/downloads";
	public static PhantomJSConfig from(Project project) {
		return project.getExtensions().findByType(PhantomJSConfig.class);
	}
	final protected Project project;
	@Inject public PhantomJSConfig(Project project) {
		assert(project != null);
		this.project = project;
	}
	
	private File binary = null;
	private String version = null;
	File getBinary() {
		return binary;
	}
	void setBinary(File binary) {
		this.binary = binary;
		this.version = null;
	}
	void setBinary(String binaryPath) {
		setBinary(project.file(binary));
	}
	void setVersion(String version) {
		this.version = version;
		this.binary = null;
	}
	private static Artifacts buildsForPlatform = null;
	private static Artifacts getPhantomJSBuilds(String version) throws IOException {
		if (buildsForPlatform != null) {
			return buildsForPlatform;
		}
		Artifacts dls = Artifacts.getBuilds(new URL(PHANTOM_JS_BUILDS_URL)).getByName("phantomjs");
		Artifacts ret = dls;
		if (version != null) {
			ret = ret.getByVersion(version);
			if (ret.size() == 0) {
				StringBuilder sb = new StringBuilder();
				sb.append("Couldn't locate a binary distribution of PhantomJS for version string: " + version + "\n");
				sb.append("Known versions include:\n");
				for (Artifact a : dls) {
					sb.append("\t" + a.getVersion());
				}
				throw new IOException(sb.toString());
				
			}
		}
		buildsForPlatform = dls;
		return dls;
	}
	private static final String phantomBinary = OS.isWindows ? "phantomjs.exe" : "phantomjs";
	public void bitbucket() throws IOException {
		bitbucket(null);
	}
	public static File findBitbucket(File phantomHome, Artifact phantomJS) {
		File bin = new File(phantomHome, phantomJS.getName() + "-" + phantomJS.getVersion() + "-" + phantomJS.getPlatform().getValue() + "/bin/" + phantomBinary);
		if (bin.exists()) {
			return bin;
		}
		/*
		for (File home : phantomHome.listFiles()) {
			bin = new File(home, "bin/" + phantomBinary);
			if (bin.exists()) {
				return bin;
			}
		}
		*/
		return null;
	}
	public static boolean haveBitbucket(String version, File phantomHome) throws IOException {
		Artifacts phantomJSArtifacts = getPhantomJSBuilds(version);
		Artifact phantomJSArtifact = phantomJSArtifacts.latest();
		File bin = findBitbucket(phantomHome, phantomJSArtifact);
		return bin != null;
	}
	public static File deployBitbucket(String version, File phantomHome) throws IOException {
		Artifacts phantomJSArtifacts = getPhantomJSBuilds(version);
		Artifact phantomJSArtifact = phantomJSArtifacts.latest();
		File bin = findBitbucket(phantomHome, phantomJSArtifact);
		if (bin != null) {
			return bin;
		}
		phantomJSArtifact.extractTo(phantomHome);
		bin = new File(phantomHome, "bin/" + phantomBinary);
		if (!bin.exists()) {
			bin = null;
			for (File home : phantomHome.listFiles()) {
				if (bin != null) {
					String maybeError = "Downloaded archive from " + phantomJSArtifact.getHref() + ", but it did not contain a build of PhantomJS that could be recognized";
					throw new IOException(maybeError);
				}
				bin = new File(home, "bin/" + phantomBinary);
				if (!bin.exists()) {
					bin = null;
				}
			}
		}
		if (bin == null) {
			String maybeError = "Downloaded archive from " + phantomJSArtifact.getHref() + ", but it did not contain a build of PhantomJS that could be recognized";
			throw new IOException(maybeError);
		}
		if (OS.isNIX) {
			bin.setExecutable(true);
		}
		return bin;
	}
	public void bitbucket(String version) throws IOException {
		if (!haveBitbucket(version, project.file("phantomjs"))) {
			project.getLogger().info("No phantomjs found; downloading");
		}
		this.binary = deployBitbucket(version, project.file("phantomjs"));
	}
}
