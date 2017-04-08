package simpledb.planner;

import simpledb.tx.Transaction;

import java.util.List;

import simpledb.parse.*;
import simpledb.query.*;

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
	  System.out.println("Creating query plan");
	  Parser parser = new Parser(qry);		//just tokenizes the command by calling a streamtokeniser on it. Does not even starts eating the tokens
      System.out.println("query parsed successfully");
      QueryData data = parser.query();		//returns QueryData-- note select conditions on timestamp fields are still stringConstants
      System.out.println("got the query data");
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
	  Parser parser = new Parser(cmd);					//just tokenizes the command by calling a streamtokeniser on it. Does not even starts eating the tokens
      Object obj = parser.updateCmd();					//Obj is the following
      													// - for create => CreateTableData(tblname-string, schema- map<string,fieldinfo>)
      													// - for insert => InsertData(tblname-string,flds-List<sting>,vals<Constant>)
      if (obj instanceof InsertData){
    	  System.out.println("*********Inserting into table***********");
    	  InsertData t = (InsertData) obj;
    	  System.out.println("TABLE NAME: "+t.tableName());
    	  List<String> flds = t.fields();
    	  List<Constant> vals = t.vals();
    	  for(int i=0;i< flds.size();i++){
    		  System.out.println(flds.get(i) + " -- "+vals.get(i).getClass().getName());
    	  }
    	  int ret = uplanner.executeInsert((InsertData)obj, tx);
    	  return ret;
   	  }else if (obj instanceof DeleteData)
         return uplanner.executeDelete((DeleteData)obj, tx);
      else if (obj instanceof ModifyData)
         return uplanner.executeModify((ModifyData)obj, tx);
      else if (obj instanceof CreateTableData){
    	  //--reading here
         int ret = uplanner.executeCreateTable((CreateTableData)obj, tx);
         return ret;
      }else if (obj instanceof CreateViewData)
         return uplanner.executeCreateView((CreateViewData)obj, tx);
      else if (obj instanceof CreateIndexData)
         return uplanner.executeCreateIndex((CreateIndexData)obj, tx);
      else
         return 0;
   }
}
