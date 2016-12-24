
public class FileSaveException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1723973820551072397L;
	
	private int lineNumber = 0;

	public FileSaveException() {
		this("Couldn't save file", 0);
	}
	
	public FileSaveException(String errorMsg) {
		this("Couldn't save file", 0);
	}
	
	public FileSaveException(String errorMsg, int line) {
		super(errorMsg);
		this.lineNumber = line;
	}
	
	public int getLineNumber() {
		return lineNumber;
	}
	
	
}
