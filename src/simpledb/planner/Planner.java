package simpledb.planner;

import simpledb.tx.Transaction;

import java.util.List;

import simpledb.parse.*;
import simpledb.query.*;
import simpledb.server.SimpleDB;

/**
 * The object that executes SQL statements.
 * @author sciore
 */
public class Planner {
   private QueryPlanner qplanner;
   private UpdatePlanner uplanner;
   
   public Planner(QueryPlanner qplanner, UpdatePlanner uplanner) {
      this.qplanner = qplanner;
      this.uplanner = uplanner;
   }
   
   /**
    * Creates a plan for an SQL select statement, using the supplied planner.
    * @param qry the SQL query string
    * @param tx the transaction
    * @return the scan corresponding to the query plan
    */
   public Plan createQueryPlan(String qry, Transaction tx) {
	  if(SimpleDB.writer != null){
		  SimpleDB.writer.println(qry);
		  SimpleDB.writer.flush();
	  }
	  if(qry.equals("close")){
		  if(SimpleDB.writer != null){
			  SimpleDB.writer.close();
		  }
	  }
	  Parser parser = new Parser(qry);		//just tokenizes the command by calling a streamtokeniser on it. Does not even starts eating the tokens
      QueryData data = parser.query();		//returns QueryData-- note select conditions on timestamp fields are still stringConstants
      										//QueryData(collection<string> fields, collection<string> tables, predicate)
      										//Predicate(List<Terms>), Term(Expression lhs, Expression rhs)
      if(data.pred() == null){
    	  //invalidIntervalError
    	  return null;
      }
      return qplanner.createPlan(data, tx);
   }
   
   /**
    * Executes an SQL insert, delete, modify, or
    * create statement.
    * The method dispatches to the appropriate method of the
    * supplied update planner,
    * depending on what the parser returns.
    * @param cmd the SQL update string
    * @param tx the transaction
    * @return an integer denoting the number of affected records
    */
   public int executeUpdate(String cmd, Transaction tx) {
	  if(SimpleDB.writer != null){
		  SimpleDB.writer.println(cmd);
		  SimpleDB.writer.flush();
	  }
	  if(cmd.equals("close")){
		  if(SimpleDB.writer != null){
			  SimpleDB.writer.close();
		  }
//		  return 0;
	  }
	  Parser parser = new Parser(cmd);					//just tokenizes the command by calling a streamtokeniser on it. Does not even starts eating the tokens
      Object obj = parser.updateCmd();					//Obj is the following
      													// - create table=> CreateTableData(tblname-string, schema- map<string,fieldinfo>)
      													// - insert => InsertData(tblname-string,flds-List<sting>,vals<Constant>)
      													// - create index=> IndexData(indexname,tablename,fieldname)
      if (obj instanceof InsertData){
    	  return uplanner.executeInsert((InsertData)obj, tx);		//gets IndexQueryPlanner and calls function
   	  }else if (obj instanceof DeleteData)
         return uplanner.executeDelete((DeleteData)obj, tx);
      else if (obj instanceof ModifyData)
         return uplanner.executeModify((ModifyData)obj, tx);
      else if (obj instanceof CreateTableData){
         int ret = uplanner.executeCreateTable((CreateTableData)obj, tx);
         return ret;
      }else if (obj instanceof CreateViewData)
         return uplanner.executeCreateView((CreateViewData)obj, tx);
      else if (obj instanceof CreateIndexData){
    	  //System.out.println("%%%%%%%%%%%%%%%%%%% Index: "+((CreateIndexData)obj).indexName());
    	  return uplanner.executeCreateIndex((CreateIndexData)obj, tx);
      }else
         return 0;
   }
}
