package simpledb.exceptions;

@SuppressWarnings("serial")
public class MemoryError extends RuntimeException{
	  //Parameterless Constructor
    public MemoryError() {}

    //Constructor that accepts a message
    public MemoryError(String message)
    {
       super(message);
    }

}
