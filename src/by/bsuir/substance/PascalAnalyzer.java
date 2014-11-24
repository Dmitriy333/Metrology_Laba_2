package by.bsuir.substance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PascalAnalyzer {
	private List<String> reservedWords = getReservedWords("resources/ReservedWords.txt");
	private List<String> getReservedWords(String fileName) {
		File f = new File(fileName);
		List<String> reservedWords = null;
		try {
			reservedWords = new ArrayList<String>();
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line = br.readLine();
			while (line != null) {
				reservedWords.add(line.toLowerCase().trim());
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return reservedWords;
	}
	private String getPascalCode(String fileName) {
		StringBuilder sb = new StringBuilder();
		File f = new File(fileName);
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	private int countEmptyStrings(String[] codeStrings){
		int n = 0;
		for (String string : codeStrings) {
			if(string.trim().equals("")){
				n++;
			}
		}
		return n;
	}
	private List<Header> getProceduresHeaders(String pascalCode) {
		List<Header> headers = new ArrayList<Header>();
		Pattern pHeader = Pattern.compile("(?<!\\w)procedure\\s+[\\w\\s.]+;");
		Matcher mWord = pHeader.matcher(pascalCode);
		while (mWord.find()) {
			headers.add(new Header(mWord.group(), false));
		}
		pHeader = Pattern
				.compile("(?<!\\w)procedure\\s+[\\w\\s.]+\\([\\w\\s,.=':;$/*()]*?\\)\\s*;");
		mWord = pHeader.matcher(pascalCode);
		while (mWord.find()) {
			headers.add(new Header(mWord.group(), true));
		}
		return headers;
	}
	private String getProcedureCode(String fileName, String procedureName) {
		StringBuilder sb = new StringBuilder();
		File ff = new File(fileName);
		try {
			BufferedReader br = new BufferedReader(new FileReader(ff));
			String line = br.readLine();
			int beginCount = 0;
			boolean writeFlag = false;
			while (!(line.contains("end") && (writeFlag == true))) {
				if (line.trim().equals(procedureName)) {
					writeFlag = true;
					sb.append(System.lineSeparator());
				}
				if (writeFlag) {
					sb.append(line);
					sb.append(System.lineSeparator());
					if (line.toLowerCase().contains("begin")
							|| line.contains("Begin")) {
						beginCount++;
					}
				}
				line = br.readLine();
			}
			int endCount = 0;
			while (endCount != beginCount) {
				sb.append(line);
				sb.append(System.lineSeparator());
				if (line.toLowerCase().contains("end") || line.contains("End")) {
					endCount++;
				}
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	private List<Header> getFunctionsHeaders(String text) {
		List<Header> headers = new ArrayList<Header>();
		Pattern pHeader = Pattern
				.compile("(?<!\\w)function\\s+[\\w\\s.]+:\\s*\\w+\\s*;");
		Matcher mWord = pHeader.matcher(text);
		while (mWord.find()) {
			headers.add(new Header(mWord.group(), false));
		}
		pHeader = Pattern
				.compile("(?<!\\w)function\\s+[\\w\\s.]+\\([\\w\\s,.=':;$/*()]*?\\)\\s*:\\s*\\w+\\s*;");
		mWord = pHeader.matcher(text);
		while (mWord.find()) {
			headers.add(new Header(mWord.group(), true));
		}
		return headers;
	}
	private List<Module> getModules(String fileName, String pascalCode) {
		List<Module> modules = new ArrayList<Module>();
		for (Header header : getProceduresHeaders(pascalCode)) {
			Module procedure = new Module();
			procedure.setHeader(header);
			procedure.setBody(getProcedureCode(fileName, header.getName()));
			modules.add(procedure);
		}
		for (Header header : getFunctionsHeaders(pascalCode)) {
			Module function = new Module();
			function.setHeader(header);
			function.setBody(getProcedureCode(fileName, header.getName()));
			modules.add(function);
		}
		return modules;
	}
	
	private int countCommentedStrings(String[] codeStrings, String pascalCode){
		int n = 0;
		boolean complexCommentBool = false;
		List<String> complexComment = null;
		List<List<String>> complexComments = new ArrayList<List<String>>();
		for (String string : codeStrings) {
			if(string.contains("{") && !string.contains("{$")){
				complexCommentBool = true;
				complexComment = new ArrayList<>();
				complexComment.add(string);
			}
			if(complexCommentBool){
				complexComment.add(string);
				if(string.contains("}")){
					complexCommentBool = false;
					if(complexComment.contains(string)){
						complexComment.remove(string);
					}
					complexComments.add(complexComment);
				}
			}
		}
		//удаляем сложные комментарии
		for (List<String> list : complexComments) {
			StringBuilder comment = new StringBuilder();
			for (String string : list) {
				n++;
				comment.append(string);
			}
			//System.out.println(comment);
			pascalCode = pascalCode.replace(comment.toString(), "");
		}
		String strings[] = pascalCode.split("\\r\\n");
		for (String string : strings) {
			if(string.contains("//")){
				//System.out.println(string);
				n++;
			}
		}
		
		
		//System.out.println("Number of strings with comments: " + n);
		return n;
	}
	private int countAvarageStringsInModules(String fileName, String pascalCode){
		List<Module> modules = getModules(fileName, pascalCode);
		int fullStringsCount = 0;
		for (Module module : modules) {
			fullStringsCount += module.getBody().split("\\r\\n").length;
		}
		int n = fullStringsCount/modules.size();
		return n;
	}
	public SlocMetrics slocMetrics(String fileName){
		SlocMetrics slocMetrics = new SlocMetrics();
		String pascalCode = getPascalCode(fileName);
		//System.out.println(pascalCode);
		String[] strings = pascalCode.split("\\r\\n");
		//System.out.println(strings.length);
		slocMetrics.setNumberOfStrings(strings.length);
		slocMetrics.setNumberOfEmptyStrings(countEmptyStrings(strings));
		slocMetrics.setNumberOfCommentedStrings(countCommentedStrings(strings, pascalCode));
		slocMetrics.setAvarageStringsInModule(countAvarageStringsInModules(fileName, pascalCode));
		return slocMetrics;
	}
}
