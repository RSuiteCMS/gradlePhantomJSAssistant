package com.github.rsuiteCMS.gradlePhantomjsAssistant.bitbucket;

import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.io.IOUtils.copy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;


public class Artifact {
	String name;
	String href;
	String version;
	Platform platform;
	Archive archiveType;
	static Pattern fmt = Pattern.compile("^([^-]+)-([\\d\\.]+\\d)-(windows|macosx|linux-i686|linux-x86_64)\\.(zip|tar\\.(?:gz|bz2))$");
	public Artifact(String href) {
		this.href = href;
		String file = href.replaceAll("^.*/([^/]+)", "$1");
		Matcher m = fmt.matcher(file);
		if (m.find()) {
			name = m.group(1);
			version = m.group(2);
			platform = Platform.fromString(m.group(3));
			archiveType = Archive.fromString(m.group(4));
		}
	}
	public String toString() {
		return name + ":" + version + ":" + platform + ":" + archiveType + "@" + href;
	}
	private File tempFile() throws IOException {
		return File.createTempFile("phantomjs", archiveType.toString());
	}
	
	
	private File downloadToTemp() throws IOException {
		File tmp = tempFile();
		URL u = new URL(href);
		InputStream is = null;
		OutputStream os = null;
		try {
			is = u.openStream();
			os = new FileOutputStream(tmp);
			copy(is, os);
		} finally {
			closeQuietly(is);
			closeQuietly(os);
		}
		tmp.deleteOnExit();
		return tmp;
	}
	private void unzipTo(File path) throws IOException {
		File tmp = downloadToTemp();
		ZipFile zip = null;
		try {
			zip = new ZipFile(tmp);
			Enumeration<? extends ZipEntry> zipEntries = zip.entries();
			while (zipEntries.hasMoreElements()) {
				ZipEntry entry = zipEntries.nextElement();
				File target = new File(path, entry.getName());
				ensureParentDirectory(target);
				InputStream is = null;
				OutputStream os = null;
				try {
					is = zip.getInputStream(entry);
					os = new FileOutputStream(target);
					copy(is, os);
				} finally {
					closeQuietly(is);
					closeQuietly(os);
				}
			}
		} finally {
			if (zip != null) { try { zip.close(); } catch (IOException ioe) {} }
		}
	}
	private void untarBz2To(File path) throws FileNotFoundException, IOException {
		untarTo(new BZip2CompressorInputStream(new FileInputStream(downloadToTemp())), path);
	}
	private void untarGzTo(File path) throws FileNotFoundException, IOException {
		untarTo(new GZIPInputStream(new FileInputStream(downloadToTemp())), path);
	}
	private void ensureParentDirectory(File file) throws IOException {
		File parent = file.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
		if (!parent.isDirectory()) {
			throw new IOException("Cannot overwrite file with directory: " + parent.getAbsolutePath());
		}

	}
	private void untarTo(File path) throws IOException {
		untarTo(new FileInputStream(downloadToTemp()), path);
	}
	private void untarTo(InputStream source, File path) throws IOException {
		TarArchiveInputStream is = new TarArchiveInputStream(source);
		TarArchiveEntry entry = null;
		try {
			while (null != (entry = is.getNextTarEntry())) {
				File target = new File(path, entry.getName());
				ensureParentDirectory(target);
				if (entry.isDirectory()) {
					target.mkdirs();
				} else {
					if (target.exists()) {
						target.delete();
					}
					FileOutputStream os = new FileOutputStream(target);
					try {
						copy(is, os);
					} finally { closeQuietly(os); }
				}
			}
		} finally {
			closeQuietly(is);
		}
	}
	public String getName() {
		return name;
	}
	public String getVersion() {
		return version;
	}
	public Platform getPlatform() {
		return platform;
	}
	public Archive getArchiveType() {
		return archiveType;
	}
	public String getHref() {
		return href;
	}
	public void extractTo(File phantomHome) throws IOException {
		switch (archiveType) {
		case TAR: untarTo(phantomHome); break;
		case TARBZ2: untarBz2To(phantomHome); break;
		case TARGZ: untarGzTo(phantomHome); break;
		case ZIP: unzipTo(phantomHome); break;
		default:
			throw new IOException("Couldn't fetch phantomjs binary");
		}
	}
}