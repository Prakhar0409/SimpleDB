package simpledb.parse;

@SuppressWarnings("serial")
public class InvalidIntervalError extends RuntimeException{
	
      //Parameterless Constructor
      public InvalidIntervalError() {}

      //Constructor that accepts a message
      public InvalidIntervalError(String message)
      {
         super(message);
      }
}
