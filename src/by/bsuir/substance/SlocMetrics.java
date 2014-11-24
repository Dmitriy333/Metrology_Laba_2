package by.bsuir.substance;

public class SlocMetrics {
	private int numberOfStrings;
	private int numberOfEmptyStrings;
	private int numberOfCommentedStrings;
	private int avarageStringsInModule;
	public int getNumberOfStrings() {
		return numberOfStrings;
	}
	public void setNumberOfStrings(int numberOfStrings) {
		this.numberOfStrings = numberOfStrings;
	}
	public int getNumberOfEmptyStrings() {
		return numberOfEmptyStrings;
	}
	public void setNumberOfEmptyStrings(int numberOfEmptyStrings) {
		this.numberOfEmptyStrings = numberOfEmptyStrings;
	}
	public int getNumberOfCommentedStrings() {
		return numberOfCommentedStrings;
	}
	public void setNumberOfCommentedStrings(int numberOfCommentedStrings) {
		this.numberOfCommentedStrings = numberOfCommentedStrings;
	}
	public int getAvarageStringsInModule() {
		return avarageStringsInModule;
	}
	public void setAvarageStringsInModule(int avarageStringsInModule) {
		this.avarageStringsInModule = avarageStringsInModule;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Number of strings with comments: " + this.getNumberOfCommentedStrings()+"\n");
		sb.append("Number of all strings: " + this.getNumberOfStrings()+"\n");
		sb.append("Number of average strings in modules: " + this.getAvarageStringsInModule()+"\n");
		sb.append("Empty strings: " + this.getNumberOfEmptyStrings()+"\n");
		double div = Double.valueOf(this.getNumberOfCommentedStrings())/Double.valueOf(this.getNumberOfStrings())*100;
		sb.append("Procents of comments: " + this.getNumberOfCommentedStrings() + "/" +this.getNumberOfStrings()+"*"+100 +"="+ div+"%");
		return sb.toString();
	}
}
