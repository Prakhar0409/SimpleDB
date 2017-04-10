package simpledb.query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * The class that wraps Java strings as database constants.
 * @author Edward Sciore
 */
public class TimestampConstant implements Constant {
   private Date val;
   
   /**
    * Create a constant by wrapping the specified string.
    * @param s the string value
    */
   public TimestampConstant(Date s) {
      val = s;
   }
   
   /**
    * Create a constant by wrapping the specified string.
    * @param s the string value
    */
   public TimestampConstant(String s) {
	   SimpleDateFormat tmp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   tmp.setLenient(false);
      try {
    	 val = tmp.parse(s);
      } catch (ParseException e) {
		 //		e.printStackTrace();
	 	 System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> SERVER: InvalidDateFormatError <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
//	 	 throw new InvalidDateFormatError();
	 	throw new RuntimeException("InvalidDateFormatError");
      }  
   }
   
   
   /**
    * Unwraps the timestamp and returns it.
    * @see simpledb.query.Constant#asJavaVal()
    */
   public Date asJavaVal() {
      return val;
   }
   
   public boolean equals(Object obj) {
      TimestampConstant sc = (TimestampConstant) obj;
      return sc != null && val.equals(sc.val);
   }
   
   public int compareTo(Constant c) {
	   TimestampConstant sc = (TimestampConstant) c;
      return val.compareTo(sc.val);
   }
   
   public int hashCode() {
      return val.hashCode();
   }
   
   public String toString() {
      return val.toString();
   }
}
