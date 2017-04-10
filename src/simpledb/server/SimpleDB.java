package simpledb.server;

import simpledb.file.FileMgr;

import java.io.PrintWriter;

import simpledb.buffer.*;
import simpledb.tx.Transaction;
import simpledb.log.LogMgr;
import simpledb.metadata.MetadataMgr;
import simpledb.planner.*;
import simpledb.opt.HeuristicQueryPlanner;
import simpledb.index.planner.IndexUpdatePlanner;

/**
 * The class that provides system-wide static global values.
 * These values must be initialized by the method
 * {@link #init(String) init} before use.
 * The methods {@link #initFileMgr(String) initFileMgr},
 * {@link #initFileAndLogMgr(String) initFileAndLogMgr},
 * {@link #initFileLogAndBufferMgr(String) initFileLogAndBufferMgr},
 * and {@link #initMetadataMgr(boolean, Transaction) initMetadataMgr}
 * provide limited initialization, and are useful for 
 * debugging purposes.
 * 
 * @author Edward Sciore
 */
public class SimpleDB {
   public static int BUFFER_SIZE = 8;
   public static String LOG_FILE = "simpledb.log";
   
   private static FileMgr     fm;
   private static BufferMgr   bm;
   private static LogMgr      logm;
   private static MetadataMgr mdm;
   public static PrintWriter writer;
   
   /**
    * Initializes the system.
    * This method is called during system startup.
    * @param dirname the name of the database directory
    */
   public static void init(String dirname) {
      initFileLogAndBufferMgr(dirname);
      Transaction tx = new Transaction();		//create a new transaction
      boolean isnew = fm.isNew();				//check filemanager for if new
      if (isnew)
         System.out.println("creating new database");
      else {
         System.out.println("recovering existing database");
         tx.recover();
         System.out.println("Txn recovered");
      }
      initMetadataMgr(isnew, tx);
      tx.commit();
      try{
    	  writer = new PrintWriter("Queries.txt", "UTF-8");
      }catch(Exception e){
    	  System.out.println("Could not open a write file");
      }
   }
   
   // The following initialization methods are useful for 
   // testing the lower-level components of the system 
   // without having to initialize everything.
   
   /**
    * Initializes only the file manager.
    * @param dirname the name of the database directory
    */
   // FileMgr: The database system stores its data as files within a specified directory.
   // The file manager provides methods for reading the contents of
   // a file block to a Java byte buffer,
   public static void initFileMgr(String dirname) {
      fm = new FileMgr(dirname);
   }
   
   /**
    * Initializes the file and log managers.
    * @param dirname the name of the database directory
    */
   // LogMgr
    // The low-level log manager.
    // This log manager is responsible for writing log records
    // into a log file.
    
   public static void initFileAndLogMgr(String dirname) {
      initFileMgr(dirname);
      logm = new LogMgr(LOG_FILE);			//logfile is simpledb.log
   }
   
   /**
    * Initializes the file, log, and buffer managers.
    * @param dirname the name of the database directory
    */
   public static void initFileLogAndBufferMgr(String dirname) {
      initFileAndLogMgr(dirname);
      //BufferMgr(BasicBufferMgr, maxtime)  ---> BasicBufferMgr( private Buffer[] bufferpool;numAvailable;)
      bm = new BufferMgr(BUFFER_SIZE);			//buffer_sze is 8 - denotes number of buffers
   }
   
   /**
    * Initializes metadata manager.
    * @param isnew an indication of whether a new
    * database needs to be created.
    * @param tx the transaction performing the initialization
    */
   public static void initMetadataMgr(boolean isnew, Transaction tx) {
      mdm = new MetadataMgr(isnew, tx);
   }
   
   public static FileMgr     fileMgr()   { return fm; }
   public static BufferMgr   bufferMgr() { return bm; }
   public static LogMgr      logMgr()    { return logm; }
   public static MetadataMgr mdMgr()     { return mdm; }
   
   /**
    * Creates a planner for SQL commands.
    * To change how the planner works, modify this method.
    * @return the system's planner for SQL commands
    */public static Planner planner() {
//      QueryPlanner  qplanner = new BasicQueryPlanner();
    	QueryPlanner  qplanner = new HeuristicQueryPlanner();
//      UpdatePlanner uplanner = new BasicUpdatePlanner();
    	UpdatePlanner uplanner = new IndexUpdatePlanner();
      return new Planner(qplanner, uplanner);
   }
}
