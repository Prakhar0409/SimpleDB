package simpledb.query;

import static java.sql.Types.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import simpledb.tx.Transaction;
import simpledb.record.*;

/**
 * The Scan class corresponding to a table.
 * A table scan is just a wrapper for a RecordFile object;
 * most methods just delegate to the corresponding
 * RecordFile methods.
 * @author Edward Sciore
 *
 */
public class TableScan implements UpdateScan {
   private RecordFile rf;
   private Schema sch;
   
   /**
    * Creates a new table scan,
    * and opens its corresponding record file. 
    * @param ti the table's metadata
    * @param tx the calling transaction
    */
   public TableScan(TableInfo ti, Transaction tx) {
      rf  = new RecordFile(ti, tx);
      sch = ti.schema();
   }
   
   // Scan methods
   
   public void beforeFirst() {
      rf.beforeFirst();
   }
   
   public boolean next() {
      return rf.next();
   }
   
   public void close() {
      rf.close();
   }
   
   /**
    * Returns the value of the specified field, as a Constant.
    * The schema is examined to determine the field's type.
    * If INTEGER, then the record file's getInt method is called;
    * otherwise, the getString method is called.
    * @see simpledb.query.Scan#getVal(java.lang.String)
    */    	  //will be used to compare expression from the term in a predicate
   public Constant getVal(String fldname) {
      if (sch.type(fldname) == INTEGER){
         return new IntConstant(rf.getInt(fldname));
      }else if(sch.type(fldname) == TIMESTAMP){
    	  											//converting retrieved timestamp to string
    	  SimpleDateFormat ts = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	  
    	  String tp = ts.format(rf.getTimestamp(fldname));
//    	  System.out.println("TableScan  String (timestamp) value: "+tp);
//    	  return new StringConstant(tp);
    	  
    	  return new TimestampConstant(ts.format(rf.getTimestamp(fldname)));
      }else if(sch.type(fldname) == VARCHAR ){
//    	  System.out.println("TableScan String value: "+rf.getString(fldname));
    	  return new StringConstant(rf.getString(fldname));  
      }else{
    	  System.out.println("TableScan Panic: Do not recognize the type of the constant being retrieved");
    	  return new StringConstant(rf.getString(fldname));
      }
   }
   
   public int getInt(String fldname) {
      return rf.getInt(fldname);
   }
   
   public String getString(String fldname) {
      return rf.getString(fldname);
   }

   public Date getTimestamp(String fldname) {
	    return rf.getTimestamp(fldname);
   }
   
   public boolean hasField(String fldname) {
      return sch.hasField(fldname);
   }
   
   public int  fieldType(String fldname){
	   return sch.type(fldname);
   }
   
   // UpdateScan methods
   
   /**
    * Sets the value of the specified field, as a Constant.
    * The schema is examined to determine the field's type.
    * If INTEGER, then the record file's setInt method is called;
    * otherwise, the setString method is called.
    * @see simpledb.query.UpdateScan#setVal(java.lang.String, simpledb.query.Constant)
    */ 
   public void setVal(String fldname, Constant val) {
      if (sch.type(fldname) == INTEGER){
//    	 System.out.println("TableScan: INTEGER to INTEGER CONSTANT");
         rf.setInt(fldname, (Integer)val.asJavaVal());
      }else if(sch.type(fldname) == VARCHAR){
//    	 System.out.println("TableScan: STRING TO STRINGCONST");
    	 rf.setString(fldname, (String)val.asJavaVal());
      }else if(sch.type(fldname) == TIMESTAMP){
//    	  System.out.println("TableScan: converting STRING to TIMESTAMPCONST - done in the indexUpdatePlan");
//		  TimestampConstant ts = new TimestampConstant((String)val.asJavaVal());
//		  rf.setTimestamp(fldname, (Date)ts.asJavaVal());		
    	  rf.setTimestamp(fldname, (Date)val.asJavaVal());
      }
   }
   
   public void setInt(String fldname, int val) {
      rf.setInt(fldname, val);
   }
   
   public void setString(String fldname, String val) {
      rf.setString(fldname, val);
   }
   
   public void setTimestamp(String fldname, Date val) {
	   rf.setTimestamp(fldname, val);
   }
   
   public void delete() {
      rf.delete();
   }
   
   public void insert() {
      rf.insert();
   }
   
   public RID getRid() {
      return rf.currentRid();
   }
   
   public void moveToRid(RID rid) {
      rf.moveToRid(rid);
   }
   
   public Schema schema(){
	   return sch;
   }
}
