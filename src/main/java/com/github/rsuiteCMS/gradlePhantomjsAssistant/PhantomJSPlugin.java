package com.github.rsuiteCMS.gradlePhantomjsAssistant;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;

public class PhantomJSPlugin implements Plugin<Project> {
	
	private void initLog(Project project) {
		String version = BuildInfo.VERSION;
		String className = getClass().getSimpleName();
		String releases = "https://github.com/RSuiteCMS/gradlePhantomjsAssistant/releases";
		project.getLogger().info(className + ": Version $version, Built: $timestamp, Commit: $commit, URL: $url");
		if (!BuildInfo.GIT_IS_CLEAN) {
			project.getLogger().warn("WARNING: " + className + " was built with local git modification: " + releases);
		} else if (version.contains("SNAPSHOT")) {
			project.getLogger().warn("WARNING: " + className + " plugin was built with SNAPSHOT version: " + releases);
		}
	}
	PhantomJSConfig config = null;
	@Override
	public void apply(Project project) {
		initLog(project);
		project.getPluginManager().apply(JavaPlugin.class);
		// Create the phantomjs configuration point
		config = project.getExtensions().create("phantomjs", PhantomJSConfig.class, project);
	}

}
