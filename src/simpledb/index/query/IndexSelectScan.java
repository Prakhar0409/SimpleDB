package simpledb.index.query;

import simpledb.record.RID;
import simpledb.query.*;

import java.util.Date;

import simpledb.index.Index;
import simpledb.index.btree.BTreeIndex;

/**
 * The scan class corresponding to the select relational
 * algebra operator.
 * @author Edward Sciore
 */
public class IndexSelectScan implements Scan {
   private Index idx;
   private Constant val;
   private Constant bigger;
   private boolean between;
   private TableScan ts;
   
   /**
    * Creates an index select scan for the specified
    * index and selection constant.
    * @param idx the index
    * @param val the selection constant
    */
   public IndexSelectScan(Index idx, Constant val, TableScan ts) {
      this.idx = idx;
      this.val = val;
      this.ts  = ts;
      this.between = false;
      this.bigger = null;
      beforeFirst();
   }

   /**
    * Creates an index select scan for the specified
    * index and selection constant.
    * @param idx the index
    * @param val the smaller constant
    * @param bigger the bigger constant
    */
   public IndexSelectScan(Index idx, Constant val,Constant bigger, TableScan ts) {
      this.idx = idx;
      this.val = val;
      this.bigger = bigger;
      this.between = true;
      System.out.println("INITIALIZATION 2 form indexselectscan");
      this.ts  = ts;
      beforeFirstBetween();
   }
   
   /**
    * Positions the scan before the first record,
    * which in this case means positioning the index
    * before the first instance of the selection constant.
    * @see simpledb.query.Scan#beforeFirst()
    */
   public void beforeFirst() {
      idx.beforeFirst(val);			//go to before first smaller value
   }
   
   /**
    * Positions the scan before the first record,
    * which in this case means positioning the index
    * before the first instance of the selection constant.
    * @see simpledb.query.Scan#beforeFirst()
    */
   public void beforeFirstBetween() {
	   if(bigger == null){
		   System.out.println("Index Select Scan in between still bigger is null");
	   }
      ((BTreeIndex) idx).beforeFirstBetween(val, bigger);		//go to before first smaller value
   }
   
   /**
    * Moves to the next record, which in this case means
    * moving the index to the next record satisfying the
    * selection constant, and returning false if there are
    * no more such index records.
    * If there is a next record, the method moves the 
    * tablescan to the corresponding data record.
    * @see simpledb.query.Scan#next()
    */
   public boolean next() {
	  System.out.println("IndexSelectScan: Calls next");
	  boolean ok = false;
	  if(between){
		  //do this for between things else do idx.next
//		  boolean ok = idx.nextBetween();
		  System.out.println("QUERY BETWEEN INDEXSCAN POINT 2");
		  ok = ((BTreeIndex) idx).nextBetween();
	  }else{
		  ok = idx.next();
	  }
      
      System.out.println("IndexSelectScan idx: "+ok+"     idx"+idx);
      if (ok) {
         RID rid = idx.getDataRid();
         ts.moveToRid(rid);
      }
      return ok;
   }
   
   /**
    * Closes the scan by closing the index and the tablescan.
    * @see simpledb.query.Scan#close()
    */
   public void close() {
      idx.close();
      ts.close();
   }
   
   /**
    * Returns the value of the field of the current data record.
    * @see simpledb.query.Scan#getVal(java.lang.String)
    */
   public Constant getVal(String fldname) {
      return ts.getVal(fldname);
   }
   
   /**
    * Returns the value of the field of the current data record.
    * @see simpledb.query.Scan#getInt(java.lang.String)
    */
   public int getInt(String fldname) {
      return ts.getInt(fldname);
   }
   
   /**
    * Returns the value of the field of the current data record.
    * @see simpledb.query.Scan#getString(java.lang.String)
    */
   public String getString(String fldname) {
      return ts.getString(fldname);
   }
   
   /**
    * Returns the value of the field of the current data record.
    * @see simpledb.query.Scan#getTimestamp(java.lang.String)
    */
   public Date getTimestamp(String fldname) {
	   System.out.println("Getting timestamp from field: "+fldname);
      return ts.getTimestamp(fldname);
   }
   
   /**
    * Returns whether the data record has the specified field.
    * @see simpledb.query.Scan#hasField(java.lang.String)
    */
   public boolean hasField(String fldname) {
      return ts.hasField(fldname);
   }
}
