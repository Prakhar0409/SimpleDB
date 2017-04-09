package simpledb.query;

@SuppressWarnings("serial")
public class InvalidDateFormatError extends RuntimeException{
	
    //Parameterless Constructor
    public InvalidDateFormatError() {}

    //Constructor that accepts a message
    public InvalidDateFormatError(String message)
    {
       super(message);
    }
}