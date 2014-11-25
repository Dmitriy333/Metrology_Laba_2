package by.bsuir.substance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PascalAnalyzer {
	private List<String> operators = getOperators("resources/Operators.txt");
	private List<String> conditionOperators = getConditionOperators();

	private List<String> getConditionOperators() {
		String[] conditionOperatorsArr = { "while", "for", "repeat", "case",
				"if", "else", "until" };
		List<String> conditionOperators = new ArrayList<String>();
		conditionOperators.addAll(Arrays.asList(conditionOperatorsArr));
		return conditionOperators;
	}

	private List<String> getOperators(String fileName) {
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

	private int countEmptyStrings(String[] codeStrings) {
		int n = 0;
		for (String string : codeStrings) {
			if (string.trim().equals("")) {
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

	private int countCommentedStrings(String[] codeStrings, String pascalCode) {
		int n = 0;
		boolean complexCommentBool = false;
		List<String> complexComment = null;
		List<List<String>> complexComments = new ArrayList<List<String>>();
		for (String string : codeStrings) {
			if (string.contains("{") && !string.contains("{$")) {
				complexCommentBool = true;
				complexComment = new ArrayList<>();
				complexComment.add(string);
			}
			if (complexCommentBool) {
				complexComment.add(string);
				if (string.contains("}")) {
					complexCommentBool = false;
					if (complexComment.contains(string)) {
						complexComment.remove(string);
					}
					complexComments.add(complexComment);
				}
			}
		}
		// delete of complex comments
		for (List<String> list : complexComments) {
			StringBuilder comment = new StringBuilder();
			for (String string : list) {
				n++;
				comment.append(string);
			}
			pascalCode = pascalCode.replace(comment.toString(), "");
		}
		String strings[] = pascalCode.split("\\r\\n");
		for (String string : strings) {
			if (string.contains("//")) {
				n++;
			}
		}
		return n;
	}

	private int countAvarageStringsInModules(String fileName, String pascalCode) {
		List<Module> modules = getModules(fileName, pascalCode);
		int fullStringsCount = 0;
		for (Module module : modules) {
			fullStringsCount += module.getBody().split("\\r\\n").length;
		}
		int n = fullStringsCount / modules.size();
		return n;
	}

	private List<String> parseTextIntoWords(String text) {
		List<String> words = new ArrayList<String>();
		Pattern pWord = Pattern.compile("([A-Za-z]+)|([\\W&&[^\\s\\n<>\\&]])");
		Matcher mWord = pWord.matcher(text);
		while (mWord.find()) {
			words.add(mWord.group());
		}
		return words;
	}

	private int countAllOperators(String pascalCode) {
		List<String> words = parseTextIntoWords(pascalCode);
		int numberOfOperators = 0;
		String string = null;
		for (int i = 0; i < words.size(); i++) {
			string = words.get(i);
			if (string.equals(":")) {
				if (words.get(i + 1).equals("=")) {
					string = ":=";
				}
			}
			if (string.equals("=")) {
				if (words.get(i - 1).equals(":")) {
					string = "nothing";
				}
			}
			if (operators.contains(string)) {
				numberOfOperators++;
			}
		}
		return numberOfOperators;
	}

	private int countConditionOperators(String pascalCode) {
		int n = 0;
		List<String> words = parseTextIntoWords(pascalCode);
		for (String string : words) {
			if (conditionOperators.contains(string.toLowerCase())) {
				n++;
			}
		}
		return n;
	}

	public SlocMetrics slocMetrics(String fileName) {
		SlocMetrics slocMetrics = new SlocMetrics();
		String pascalCode = getPascalCode(fileName);
		String[] strings = pascalCode.split("\\r\\n");
		slocMetrics.setNumberOfStrings(strings.length);
		slocMetrics.setNumberOfEmptyStrings(countEmptyStrings(strings));
		slocMetrics.setNumberOfCommentedStrings(countCommentedStrings(strings,
				pascalCode));
		slocMetrics.setAvarageStringsInModule(countAvarageStringsInModules(
				fileName, pascalCode));
		return slocMetrics;
	}

	private List<List<String>> getControlBlocksWhileFor(Module module,
			String blockName) {
		List<List<String>> controlBlocks = new ArrayList<List<String>>();
		List<String> controlBlock = null;
		List<String> blockStrings = new ArrayList<String>();
		blockStrings.addAll(Arrays.asList(module.getBody().split("\\r\\n")));
		boolean write = false;
		int beginCount = 0;
		int endCount = 0;
		boolean endFind = false;
		int innerBlock = 0;
		for (String blockString : blockStrings) {
			if (endFind == false) {
				if (blockString.toLowerCase().contains(blockName)) {
					if (write == false) {
						write = true;
						controlBlock = new ArrayList<String>();
					} else {
						innerBlock++;
					}
				}
				if (write == true) {
					controlBlock.add(blockString);
					if (blockString.toLowerCase().contains("begin")) {
						beginCount++;
					}
					if (blockString.toLowerCase().contains("end")) {
						endCount++;
						if (innerBlock == 0) {
							endFind = true;
						} else {
							innerBlock--;
						}
					}
				}
			} else {
				if (endCount == beginCount) {
					controlBlocks.add(controlBlock);
					endFind = false;
					write = false;
				} else {
					if (blockString.toLowerCase().contains("end")) {
						endCount++;
					}
					controlBlock.add(blockString);
				}
			}
		}
		return controlBlocks;
	}

	private Module getMainProcedure(String pascalCode, List<Module> modules) {
		String simpleCode = pascalCode;
		for (Module module : modules) {
			simpleCode = simpleCode.replace(module.getBody(), "");
		}
		Module simpleCodeProc = new Module();
		simpleCodeProc.setHeader(new Header("MainProgram", false));
		simpleCodeProc.setBody(simpleCode);
		return simpleCodeProc;
	}

	private List<List<String>> getRepeatUntilBlock(Module module) {
		List<List<String>> controlBlocks = new ArrayList<>();
		String blockToAnalize = module.getBody();
		String linesOfBlockArr[] = null;
		linesOfBlockArr = blockToAnalize.split("\\r\\n");
		List<String> linesOfBlock = new ArrayList<>();
		linesOfBlock.addAll(Arrays.asList(linesOfBlockArr));
		int lineWithRepeatPos = 0, lineWithUntilPos = 0;
		int rowCount = -1;
		for (String blockString : linesOfBlock) {
			rowCount++;
			if (blockString.toLowerCase().contains("repeat")) {
				lineWithRepeatPos = rowCount;
			} else if (blockString.toLowerCase().contains("until")) {
				lineWithUntilPos = rowCount;
				List<String> repeatUntilBlock = new ArrayList<>();
				for (int i = lineWithRepeatPos; i <= lineWithUntilPos; i++) {
					repeatUntilBlock.add(linesOfBlock.get(i));
				}
				controlBlocks.add(repeatUntilBlock);
			}
		}
		return controlBlocks;
	}

	private int countInsertionLevel(List<String> controlBlock,
			String cotrolBlockName) {
		int n = 0;
		for (String string : controlBlock) {
			if (string.toLowerCase().contains(cotrolBlockName)) {
				n++;
			}
		}
		n--;
		return n;
	}

	private List<DjilbaItem> getDjilbaItems(List<List<String>> controlBlocks,
			String cotrolBlockName) {
		List<DjilbaItem> djilbaItems = new LinkedList<>();
		DjilbaItem djilbaItem = null;
		for (List<String> controlBlock : controlBlocks) {
			djilbaItem = new DjilbaItem();
			StringBuilder sb = new StringBuilder();
			for (String string : controlBlock) {
				sb.append(string);
			}
			djilbaItem.setBody(sb.toString());
			djilbaItem.setInsertionLevel(countInsertionLevel(controlBlock,
					cotrolBlockName));
			djilbaItem.setName(cotrolBlockName);
			djilbaItems.add(djilbaItem);
		}
		return djilbaItems;

	}

	private String findControlBlocks(List<Module> modules) {
		StringBuilder sb = new StringBuilder();
		List<DjilbaItem> djilbaItems = new LinkedList<>();
		List<DjilbaItem> djilbaItemsFors = new LinkedList<DjilbaItem>();
		List<DjilbaItem> djilbaItemsWhiles = new LinkedList<DjilbaItem>();
		List<DjilbaItem> djilbaItemsIfs = new LinkedList<DjilbaItem>();
		List<DjilbaItem> djilbaItemsRepeats = new LinkedList<DjilbaItem>();
		for (Module module : modules) {
			List<List<String>> controlBlocksFor = getControlBlocksWhileFor(
					module, "for");
			djilbaItemsFors.addAll(getDjilbaItems(controlBlocksFor, "for"));
			List<List<String>> controlBlocksWhile = getControlBlocksWhileFor(
					module, "while");
			djilbaItemsWhiles
					.addAll(getDjilbaItems(controlBlocksWhile, "while"));
			List<List<String>> controlBlocksIf = getControlBlocksWhileFor(
					module, "if");
			djilbaItemsIfs.addAll(getDjilbaItems(controlBlocksIf, "if"));
			List<List<String>> controlBlocksRepeat = getRepeatUntilBlock(module);
			djilbaItemsRepeats.addAll(getDjilbaItems(controlBlocksRepeat,
					"repeat"));
		}
		djilbaItems.addAll(djilbaItemsFors);
		djilbaItems.addAll(djilbaItemsWhiles);
		djilbaItems.addAll(djilbaItemsIfs);
		djilbaItems.addAll(djilbaItemsRepeats);
		int maxLevelOfInsertion = 0;
		List<DjilbaItem> maxInsertDjilbaItems = new ArrayList<>();
		for (DjilbaItem djilbaItem : djilbaItems) {
			if(maxLevelOfInsertion < djilbaItem.getInsertionLevel()){
				maxLevelOfInsertion = djilbaItem.getInsertionLevel();
			}
		}
		for (DjilbaItem djilbaItem : djilbaItems) {
			if(djilbaItem.getInsertionLevel()== maxLevelOfInsertion){
				maxInsertDjilbaItems.add(djilbaItem);
			}
		}
		if(maxInsertDjilbaItems.size()>0 && maxLevelOfInsertion >= 1){
			sb.append("Max level of insertion: CLI = "+ maxLevelOfInsertion+ "\n" );
//			for (DjilbaItem djilbaItem : maxInsertDjilbaItems) {
//				//sb.append("Max level of insertion: \n" + djilbaItem+";");
//				sb.append(djilbaItem+"\n");
//			}
		}else{
			sb.append("Max level of insertion equals CLI = 0");
		}
		return sb.toString();
	}

	public String djilbaMetricsInfo(String fileName) {
		StringBuilder sb = new StringBuilder();
		String pascalCode = getPascalCode(fileName);
		List<Module> modules = getModules(fileName, pascalCode);
		Module mainModule = getMainProcedure(pascalCode, modules);
		for (Module module : modules) {
			mainModule.setBody(mainModule.getBody().replace(module.getBody(),
					""));
		}
		mainModule.getHeader().setName("procedure MainModule");
		modules.add(mainModule);
		int numberOfCondOperators = countConditionOperators(pascalCode);
		int numberOfAllOperators = countAllOperators(pascalCode);
		double cl = Double.valueOf(numberOfCondOperators)/Double.valueOf(numberOfAllOperators);
		sb.append("Number of operators: "
				+ numberOfAllOperators+"\n");
		sb.append("Number of condition operators: CL = " + numberOfCondOperators +"\n");
		sb.append("Saturation of program with condition operators: cl = " +numberOfCondOperators+"/" + numberOfAllOperators +" = "  + cl +"\n" );
		sb.append(findControlBlocks(modules));
		return sb.toString();
	}
}
