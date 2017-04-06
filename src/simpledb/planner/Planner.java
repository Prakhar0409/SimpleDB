package simpledb.planner;

import simpledb.tx.Transaction;
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
      Parser parser = new Parser(qry);
      QueryData data = parser.query();
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
	  System.out.println("Executing update");
	  Parser parser = new Parser(cmd);
      System.out.println("Parser created successfuly");
      Object obj = parser.updateCmd();
      System.out.println("Cmd updated by parser");
      if (obj instanceof InsertData){
    	  System.out.println("***************Inserting into table***********");
    	  int ret = uplanner.executeInsert((InsertData)obj, tx);
    	  System.out.println("---------------Insertion Complete------------------");
    	  return ret;
   	  }else if (obj instanceof DeleteData)
         return uplanner.executeDelete((DeleteData)obj, tx);
      else if (obj instanceof ModifyData)
         return uplanner.executeModify((ModifyData)obj, tx);
      else if (obj instanceof CreateTableData){
    	 System.out.println("Creating table");
         int ret = uplanner.executeCreateTable((CreateTableData)obj, tx);
         System.out.println("Table created");
         return ret;
      }else if (obj instanceof CreateViewData)
         return uplanner.executeCreateView((CreateViewData)obj, tx);
      else if (obj instanceof CreateIndexData)
         return uplanner.executeCreateIndex((CreateIndexData)obj, tx);
      else
         return 0;
   }
}
