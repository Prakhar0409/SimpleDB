package simpledb.index.query;

import static java.sql.Types.*;

import simpledb.tx.Transaction;
import simpledb.record.Schema;
import simpledb.metadata.IndexInfo;
import simpledb.query.*;
import simpledb.index.Index;

/** The Plan class corresponding to the <i>indexselect</i>
  * relational algebra operator.
  * @author Edward Sciore
  */
public class IndexSelectPlan implements Plan {
   private Plan p;
   private IndexInfo ii;
   private Constant val;
   private boolean between;
   private Constant bigger;
   
   /**
    * Creates a new indexselect node in the query tree
    * for the specified index and selection constant.
    * @param p the input table
    * @param ii information about the index
    * @param val the selection constant
    * @param tx the calling transaction 
    */
   public IndexSelectPlan(Plan p, IndexInfo ii, Constant val, Transaction tx) {
      this.p = p;
      this.ii = ii;
      this.val = val;
      this.between = false;
      this.bigger = null;
   }
   
   public IndexSelectPlan(Plan p, IndexInfo ii, Constant val,Constant bigger, Transaction tx) {
      this.p = p;
      this.ii = ii;
      this.val = val;
      this.bigger = bigger;
      this.between = true;
   }
   
   /** 
    * Creates a new indexselect scan for this query
    * @see simpledb.query.Plan#open()
    */
   public Scan open() {
	   System.out.println("IndexSelectPlan.open returns IndexSelectScan(idx,val,Tablescan)");
      // throws an exception if p is not a tableplan.
      TableScan ts = (TableScan) p.open();
      Index idx = ii.open();
      if(p.schema().type(ii.getField()) == TIMESTAMP){
    	  val = new TimestampConstant((String)val.asJavaVal());
      }
      if(between){
//    	  System.out.println("QUERY BETWEEN WITH INDICES smaller:"+val +"       bigger:"+bigger);
    	  return new IndexSelectScan(idx, val, bigger, ts);
      }
      IndexSelectScan iss = new IndexSelectScan(idx, val, ts);
      return iss;
   }
   
   /**
    * Estimates the number of block accesses to compute the 
    * index selection, which is the same as the 
    * index traversal cost plus the number of matching data records.
    * @see simpledb.query.Plan#blocksAccessed()
    */
   public int blocksAccessed() {
      return ii.blocksAccessed() + recordsOutput();
   }
   
   /**
    * Estimates the number of output records in the index selection,
    * which is the same as the number of search key values
    * for the index.
    * @see simpledb.query.Plan#recordsOutput()
    */
   public int recordsOutput() {
      return ii.recordsOutput();
   }
   
   /** 
    * Returns the distinct values as defined by the index.
    * @see simpledb.query.Plan#distinctValues(java.lang.String)
    */
   public int distinctValues(String fldname) {
      return ii.distinctValues(fldname);
   }
   
   /**
    * Returns the schema of the data table.
    * @see simpledb.query.Plan#schema()
    */
   public Schema schema() {
      return p.schema(); 
   }
}
