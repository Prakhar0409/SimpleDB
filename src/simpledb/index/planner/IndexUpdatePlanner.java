package simpledb.index.planner;

import java.util.Iterator;
import java.util.Map;
import static java.sql.Types.*;

import simpledb.record.RID;
import simpledb.record.TableInfo;
import simpledb.server.SimpleDB;
import simpledb.tx.Transaction;
import simpledb.exceptions.MemoryError;
import simpledb.index.Index;
import simpledb.metadata.IndexInfo;
import simpledb.metadata.StatInfo;
import simpledb.parse.*;
import simpledb.planner.*;
import simpledb.query.*;

/**
 * A modification of the basic update planner.
 * It dispatches each update statement to the corresponding
 * index planner.
 * @author Edward Sciore
 */
public class IndexUpdatePlanner implements UpdatePlanner {
   
   public int executeInsert(InsertData data, Transaction tx) {	//InsertData(tblname-string,flds-List<sting>,vals<Constant>)
      String tblname = data.tableName();
      Plan p = new TablePlan(tblname, tx);						//TablePlan(TableInfo,StatInfo,tx);
      															//TableInfo(schema,Map of fields-offsets,reclen,tbllen)
      															//Schema(fldname,Fieldinfo<type,len>)
      
      // first, insert the record								//actually a TableScan casted to UpdateScan
      UpdateScan s = (UpdateScan) p.open();						// Opens a scan corresponding to this plan.
      															// The scan will be positioned before its first record.
      
      StatInfo si = SimpleDB.mdMgr().calcTableStats(tblname, tx);	//count number of records just before the insert
      //StatInfo getStatInfo(String tblname, TableInfo ti, Transaction tx)
	  
      System.out.println("NUM RECORDS (before insertion): "+si.recordsOutput());
      if(si.recordsOutput() >= 100000){
    	  System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> SERVER: MemoryError <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
//    	  throw new MemoryError();
    	  throw new RuntimeException("MemoryError");
      }
      s.insert();												//Inserts a new record somewhere in the scan.
      RID rid = s.getRid();										//gets the RID of the current record.
      
      
      //then modify each field, inserting an index record if appropriate
      //indexes(fldname,IndexInfo<idxname, tblname, fldname, tx>)
      Map<String,IndexInfo> indexes = SimpleDB.mdMgr().getIndexInfo(tblname, tx);	
      Iterator<Constant> valIter = data.vals().iterator();
      for (String fldname : data.fields()) {
         Constant val = valIter.next();
         
         int fldtype = p.schema().type(fldname);
         if(fldtype == TIMESTAMP){				//converting string constant to timestamp constant if required 
        	 val = new TimestampConstant((String)val.asJavaVal());
         }
         
         System.out.println("Modify field " + fldname + " to val " + val);
         s.setVal(fldname, val);
      
         //updating indexes
         IndexInfo ii = indexes.get(fldname);			//IndexInfo<idxname, tblname, fldname, tx>
         
         //indexing the inserted record
         if (ii != null) {
            Index idx = ii.open();	//opens a new BTree index on the fieldname and creates the corresponding files in not created already
            idx.insert(val, rid);	//insert into btree the pointer to rid and val
            idx.close();
         }
      }
      s.close();
      return 1;
   }
   
   public int executeDelete(DeleteData data, Transaction tx) {
      String tblname = data.tableName();
      Plan p = new TablePlan(tblname, tx);
      p = new SelectPlan(p, data.pred());
      Map<String,IndexInfo> indexes = SimpleDB.mdMgr().getIndexInfo(tblname, tx);
      
      UpdateScan s = (UpdateScan) p.open();
      int count = 0;
      while(s.next()) {
         // first, delete the record's RID from every index
         RID rid = s.getRid();
         for (String fldname : indexes.keySet()) {
            Constant val = s.getVal(fldname);
            Index idx = indexes.get(fldname).open();
            idx.delete(val, rid);
            idx.close();
         }
         // then delete the record
         s.delete();
         count++;
      }
      s.close();
      return count;
   }
   
   public int executeModify(ModifyData data, Transaction tx) {
      String tblname = data.tableName();
      String fldname = data.targetField();
      Plan p = new TablePlan(tblname, tx);
      p = new SelectPlan(p, data.pred());
      
      IndexInfo ii = SimpleDB.mdMgr().getIndexInfo(tblname, tx).get(fldname);
      Index idx = (ii == null) ? null : ii.open();
      
      UpdateScan s = (UpdateScan) p.open();
      int count = 0;
      while(s.next()) {
         // first, update the record
         Constant newval = data.newValue().evaluate(s);
         Constant oldval = s.getVal(fldname);
         s.setVal(data.targetField(), newval);
         
         // then update the appropriate index, if it exists
         if (idx != null) {
            RID rid = s.getRid();
            idx.delete(oldval, rid);
            idx.insert(newval, rid);
         }
         count++;
      }
      if (idx != null) idx.close();
      s.close();
      return count;
   }
   
   public int executeCreateTable(CreateTableData data, Transaction tx) {
      SimpleDB.mdMgr().createTable(data.tableName(), data.newSchema(), tx);
      return 0;
   }
   
   public int executeCreateView(CreateViewData data, Transaction tx) {
      SimpleDB.mdMgr().createView(data.viewName(), data.viewDef(), tx);
      return 0;
   }
   
   public int executeCreateIndex(CreateIndexData data, Transaction tx) {
	   //creating index  - does not index currently present entries. only indexes the newer entries with insert
      SimpleDB.mdMgr().createIndex(data.indexName(), data.tableName(), data.fieldName(), tx);
      return 0;
   }
}
