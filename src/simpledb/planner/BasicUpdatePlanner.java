package simpledb.planner;

import java.util.Iterator;
import simpledb.server.SimpleDB;
import simpledb.tx.Transaction;
import simpledb.parse.*;
import simpledb.query.*;

/**
 * The basic planner for SQL update statements.
 * @author sciore
 */
public class BasicUpdatePlanner implements UpdatePlanner {
   
   public int executeDelete(DeleteData data, Transaction tx) {
      Plan p = new TablePlan(data.tableName(), tx);
      p = new SelectPlan(p, data.pred());
      UpdateScan us = (UpdateScan) p.open();
      int count = 0;
      while(us.next()) {
         us.delete();
         count++;
      }
      us.close();
      return count;
   }
   
   public int executeModify(ModifyData data, Transaction tx) {
      Plan p = new TablePlan(data.tableName(), tx);
      p = new SelectPlan(p, data.pred());
      UpdateScan us = (UpdateScan) p.open();
      int count = 0;
      while(us.next()) {
         Constant val = data.newValue().evaluate(us);
         us.setVal(data.targetField(), val);
         count++;
      }
      us.close();
      return count;
   }
   
   public int executeInsert(InsertData data, Transaction tx) {
      Plan p = new TablePlan(data.tableName(), tx);
      UpdateScan us = (UpdateScan) p.open();
      us.insert();					// adds a new block if required by inserting a blank record
      System.out.println("Executed insert");
      Iterator<Constant> iter = data.vals().iterator();
      for (String fldname : data.fields()) {
    	 System.out.println("fldname: "+fldname);
    	 Constant val = iter.next();
         System.out.println("fldname: "+fldname+"   val: "+val+  "     type:"+val.getClass().getName());
         us.setVal(fldname, val);
      }
      us.close();
      return 1;
   }
   
   public int executeCreateTable(CreateTableData data, Transaction tx) {
	   //this basically puts table name and record len in tcatinfo file and records corresponding to fields in the fcatinfo file
      SimpleDB.mdMgr().createTable(data.tableName(), data.newSchema(), tx);		//mdMgr() - returns meta data manager (static since we assume only 1 server per execution of the code)
      return 0;
   }
   
   public int executeCreateView(CreateViewData data, Transaction tx) {
      SimpleDB.mdMgr().createView(data.viewName(), data.viewDef(), tx);
      return 0;
   }
   public int executeCreateIndex(CreateIndexData data, Transaction tx) {
      SimpleDB.mdMgr().createIndex(data.indexName(), data.tableName(), data.fieldName(), tx);
      return 0;  
   }
}
