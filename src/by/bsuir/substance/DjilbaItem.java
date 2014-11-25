package by.bsuir.substance;

public class DjilbaItem {
	private String name;
	private String body;
	private int insertionLevel;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public int getInsertionLevel() {
		return insertionLevel;
	}
	public void setInsertionLevel(int insertionLevel) {
		this.insertionLevel = insertionLevel;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Block name: " + name + "\n");
		sb.append("Body:\n " + body + "\n");
		sb.append("Level of insertion: " + insertionLevel + "\n");
		return sb.toString();
	}
}
