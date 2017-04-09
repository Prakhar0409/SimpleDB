package simpledb.remote;

import java.sql.*;
import java.text.SimpleDateFormat;

/**
 * An adapter class that wraps RemoteResultSet.
 * Its methods do nothing except transform RemoteExceptions
 * into SQLExceptions.
 * @author Edward Sciore
 */
public class SimpleResultSet extends ResultSetAdapter {
   private RemoteResultSet rrs;
   
   public SimpleResultSet(RemoteResultSet s) {
      rrs = s;
   }
   
   public boolean next() throws SQLException {
      try {
         return rrs.next();
      }
      catch (Exception e) {
         throw new SQLException(e);
      }
   }
   
   public int getInt(String fldname) throws SQLException {
      try {
         return rrs.getInt(fldname);
      }
      catch (Exception e) {
         throw new SQLException(e);
      }
   }
   
   public String getString(String fldname) throws SQLException {
      try {
         return rrs.getString(fldname);
      }
      catch (Exception e) {
         throw new SQLException(e);
      }
   }
   
   public Timestamp getTimestamp(String fldname) throws SQLException {
      try {
//    	  Timestamp tc = rrs.getTimestamp(fldname);
//    	  SimpleDateFormat ts = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    	  ts.format(tc);
         return rrs.getTimestamp(fldname);
      }
      catch (Exception e) {
         throw new SQLException(e);
      }
   }
   
   public Date getDate(String fldname) throws SQLException {
	      try {  
	    	 long utilDate = rrs.getDate(fldname).getTime();
	    	 Date d = new Date(utilDate);
	         return d; 
	      }
	      catch (Exception e) {
	         throw new SQLException(e);
	      }
	   }
   
   public ResultSetMetaData getMetaData() throws SQLException {
      try {
         RemoteMetaData rmd = rrs.getMetaData();
         return new SimpleMetaData(rmd);
      }
      catch (Exception e) {
         throw new SQLException(e);
      }
   }
   
   public void close() throws SQLException {
      try {
         rrs.close();
      }
      catch (Exception e) {
         throw new SQLException(e);
      }
   }
}

