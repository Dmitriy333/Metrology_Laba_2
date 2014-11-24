package by.bsuir.substance;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Header{
	private String name;
	private boolean complexHeader = false;
	public Header(String name, boolean complexHeader){
		this.name = name;
		this.complexHeader = complexHeader;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isComplexHeader() {
		return complexHeader;
	}
	public void setComplexHeader(boolean complexHeader) {
		this.complexHeader = complexHeader;
	}
	public String getSimpleName(){
		String simpleName = name.trim().replace(name.substring(0, name.indexOf(" ")), "").trim();
		List<String> words = new ArrayList<String>();
		Pattern pWord = Pattern.compile("([A-Za-z]+)|([\\W&&[^\\s\\n=<>\\&]])");
		Matcher mWord = pWord.matcher(simpleName);
		while (mWord.find()) {
			words.add(mWord.group());
		}
		return words.get(0);
	}
}
