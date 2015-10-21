package com.github.rsuiteCMS.gradlePhantomjsAssistant;

import java.io.PrintStream;

public class OS {
	private static String name = System.getProperty("os.name").toLowerCase();
	private static String arch = System.getProperty("os.arch").toLowerCase();
	public static boolean isMac = name.contains("mac os x") || name.contains("darwin"); 
	public static boolean isWindows = name.contains("windows");
	public static boolean isNIX = name.contains("nix") || name.contains("nux") || name.contains("aix") || name.contains("sunos");
	private static String envArch = System.getenv("PROCESSOR_ARCHITECTURE");
	private static String envWowArch = System.getenv("PROCESSOR_ARCHITECTUREW6432");
	public static boolean is64Bit = arch.endsWith("64") || 
			(envArch != null && envArch.endsWith("64")) || 
			(envWowArch != null && envWowArch.endsWith("64"));
	public static boolean is32Bit = !is64Bit;
	
	public static void printOSInfo(PrintStream out) {
		out.println("  OS Name: " + name);
		out.println("  OS Arch: " + arch);
		out.println("    isMac: " + (    isMac ? "yes" : "no"));
		out.println("isWindows: " + (isWindows ? "yes" : "no"));
		out.println("    isNIX: " + (    isNIX ? "yes" : "no"));
		out.println("  is64Bit: " + (  is64Bit ? "yes" : "no"));
		out.println("  is32Bit: " + (  is32Bit ? "yes" : "no"));
	}
	public static void printOSInfo() {
		printOSInfo(System.out);
	}
	
}
