package by.bsuir.main;


import by.bsuir.substance.PascalAnalyzer;

public class Main {
	public static void main(String[] args) {
		PascalAnalyzer pascalAnalyzer = new PascalAnalyzer();
		System.out.println(pascalAnalyzer.slocMetrics("resources/Pascal.txt"));
		System.out.println(pascalAnalyzer.djilbaMetricsInfo("resources/Pascal.txt"));
	}
	
}
