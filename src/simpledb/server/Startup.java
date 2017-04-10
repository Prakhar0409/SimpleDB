package simpledb.server;

import simpledb.remote.*;
import java.rmi.registry.*;

public class Startup {
   public static void main(String args[]) throws Exception {
	  if(args.length < 1){
		  System.out.println("Usage: java simpledb.server.Startup <dbname>");
		  System.exit(-1);
	  } 
	  // configure and initialize the database
      SimpleDB.init(args[0]);
      System.out.println("Started. Creating registery");
      
      // create a registry specific for the server on the default port
      Registry reg = LocateRegistry.createRegistry(1099);
      
      // and post the server entry in it
      RemoteDriver d = new RemoteDriverImpl();
      reg.rebind("simpledb", d);
      
      System.out.println("database server ready");
   }
}
