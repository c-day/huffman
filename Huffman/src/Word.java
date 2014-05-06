//lol java doesn't have structs... :(
public class Word implements Comparable<Word> {
	
	private String name;
	private int value;
	private int EQUAL = 0;
	private int BEFORE = 1;
	private int AFTER = -1;
	
	public Word(String name, int value) {
		this.name = name;
		this.value = value;
	}
	
	public String toString() {
		return this.name + ":" + this.value;
	}
	
    @Override
    public int compareTo(Word wordIn) {
        int returnVal = EQUAL;
        
        if (this.getValue() < wordIn.getValue() ) {
            returnVal = BEFORE;
        } else if (this.getValue() > wordIn.getValue()) {
            returnVal = AFTER;
        }
        
        return returnVal;
    }
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
