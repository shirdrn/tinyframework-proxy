package org.shirdrn.tinyframework.proxy.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileUtils {

	public static Set<String> populateSetWithLines(File file, String charSet) {
		return (Set<String>) readLines(file.getAbsolutePath(), charSet, true);
	}
	
	public static List<String> populateListWithLines(File file, String charSet) {
		return (List<String>) readLines(file.getAbsolutePath(), charSet, false);
	}
	
	private static Collection<String> readLines(String file, String charSet, boolean deduplate) {
		Collection<String> lines = null;
		if(deduplate) {
			lines = new HashSet<String>();
		} else {
			lines = new ArrayList<String>();
		}
		FileInputStream fis = null;
		BufferedReader reader = null;
		try {
			if(charSet==null) {
				charSet = Charset.defaultCharset().toString();
			}
			fis = new FileInputStream(file);
			reader = new BufferedReader(new InputStreamReader(fis, charSet));
			String line;
			while((line=reader.readLine())!=null) {
				line = line.trim();
				if(!line.isEmpty()) {
					lines.add(line);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(reader!=null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return lines;
	}
	
}
