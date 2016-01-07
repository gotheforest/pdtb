package edu.upenn.cis.anntool.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import edu.upenn.cis.anntool.settings.Languages;

public class ExecutableFunctions {

	/**
	 * @param args
	 * 40600 total relations
	 */
	public static void main(String[] args) {
		String annFolder = "D:\\pdtb\\corpora\\ann_no_sent_nos";
		String rawFolder = "D:\\pdtb\\corpora\\raw";
		String commentFolder = "D:\\pdtb\\tmp";
//		System.out.println("Relation count: "
//				+ findDuplicateRelations(annFolder, rawFolder, commentFolder));
		 System.out.println("Relation count: " +
		 findDuplicateRelations(annFolder));
	}

	public static int findDuplicateRelations(String annRootStr,
			String rawRootStr, String commentRootStr) {
		int count = 0;
		int sectionIndex = -1;
		FileManager fileManager = new FileManager(rawRootStr, annRootStr, "",
				commentRootStr, new ArrayList<String>(), new ArrayList<String>(),
				new Languages().valueOf("English"));
		File rawRoot = new File(rawRootStr);
		File[] sections = rawRoot.listFiles();
		Arrays.sort(sections);
		for (int i = 0; i < sections.length; i++) {
			if (sections[i].isDirectory()) {
				File[] files = sections[i].listFiles();
				Arrays.sort(files);
				// If we include empty sections, it screws things up
				if (files.length > 0) {
					sectionIndex++;
					int fileIndex = -1;
					for (int j = 0; j < files.length; j++) {
						if (files[j].isFile()) {
							fileIndex++;
							fileManager.setSecFile(sectionIndex, fileIndex);
							List<Relation> relationList = fileManager
									.getRelationList();
							count += relationList.size();
						}
					}
				}
			}
		}
		return count;
	}

	public static int findDuplicateRelations(String annRootStr) {
		int count = 0;
		File annRoot = new File(annRootStr);
		File[] annSections = annRoot.listFiles();
		Arrays.sort(annSections);
		for (int i = 0; i < annSections.length; i++) {
			if (annSections[i].isDirectory()) {
				File[] annFiles = annSections[i].listFiles();
				Arrays.sort(annFiles);
				// If we include empty sections, it screws things up
				if (annFiles.length > 0) {
					for (int j = 0; j < annFiles.length; j++) {
						if (annFiles[j].isFile()) {
							String annFil = annFiles[j].getPath();
							List<Relation> relationList = new ArrayList<Relation>();
							try {
								BufferedReader in = new BufferedReader(
										new InputStreamReader(
												new BufferedInputStream(
														new FileInputStream(
																annFil)),
												"UTF8"));
								for (String next = in.readLine(); next != null; next = in
										.readLine()) {
									Relation relation = new Relation(null,
											next, new Properties());
									if (!relationList.contains(relation)) {
										relationList.add(relation);
									} else {
										System.err
												.println("Duplicate Relation in : "
														+ annFiles[j].getName()
														+ " - " + next);
									}
								}
								in.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
							count += relationList.size();
							//System.out.println(annFiles[j].getName() + ' '
								//	+ relationList.size());
						}
					}
				}
			}
		}
		return count;
	}

}
