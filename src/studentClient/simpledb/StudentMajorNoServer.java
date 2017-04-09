package studentClient.simpledb;

import simpledb.tx.Transaction;

import java.util.Date;

import simpledb.query.*;
import simpledb.server.SimpleDB;

/* This is a version of the StudentMajor program that
 * accesses the SimpleDB classes directly (instead of
 * connecting to it as a JDBC client).  You can run it
 * without having the server also run.
 * 
 * These kind of programs are useful for debugging
 * your changes to the SimpleDB source code.
 */

public class StudentMajorNoServer {
	public static void main(String[] args) {
		try {
			// analogous to the driver
			SimpleDB.init("studentdb");
			
			// analogous to the connection
//			Transaction tx = new Transaction();
			
//			 analogous to the statement
//			String qry = "select SName, DName "
//		        + "from DEPT, STUDENT "
//		        + "where MajorId = DId";
//			Plan p = SimpleDB.planner().createQueryPlan(qry, tx);
			
			// analogous to the result set
//			Scan s = p.open();
//			
//			System.out.println("Name\tMajor");
//			while (s.next()) {
//				String sname = s.getString("sname"); //SimpleDB stores field names
//				String dname = s.getString("dname"); //in lower case
//				System.out.println(sname + "\t" + dname);
//			}
//			s.close();
			
			
//			Transaction tx = new Transaction();
//			/// MODIFICATION
//			String qry = "create table MAN1(Id int,Name varchar(10),Dob timestamp)";
//			int i = SimpleDB.planner().executeUpdate(qry, tx);
//			tx.commit();
//			System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
////	
//			
//			
//			Transaction tx1 = new Transaction();
//			String qry1 = "insert into MAN1(Id ,Name ,Dob ) values (1,'Prakhar','2011-10-09 20:00:00')";
//			int j = SimpleDB.planner().executeUpdate(qry1, tx1);
//			
//			tx1.commit();
////			
//			System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
//			
//			
//			
//			Transaction tx2 = new Transaction();
//			String qry2 = "select Id,Name,Dob from MAN1 where dob='2011-10-09 20:00:00';";
//			Plan p = SimpleDB.planner().createQueryPlan(qry2, tx2);			//this is ProjectPlan
//		
//			System.out.println("Plan done");
//			Scan s = p.open();
//			System.out.println("scan done");
//			((ProjectScan) s).printFields();
//				
//			System.out.println("Id\tName\tDob");
//			while (s.next()) {
//				int sid = s.getInt("id"); //SimpleDB stores field names
//				String dname = s.getString("name"); //in lower case
//				Date dob = s.getTimestamp("dob"); //in lower case
//				System.out.println(sid + "\t" + dname+ "\t"+dob);
//			}
//			s.close();
//			tx2.commit();
//			System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
//			
			
			
//			Transaction tx3 = new Transaction();
//			/// MODIFICATION
//			String qry3 = "create index sleep on MAN1 ( Id )";
//			int i3 = SimpleDB.planner().executeUpdate(qry3, tx3);
//			tx3.commit();
//			System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
	
			
			
			
			
			Transaction tx4 = new Transaction();
			String qry4 = "select Id,Name,Dob from MAN1 where dob='2011-10-09 20:00:00';";
			Plan p = SimpleDB.planner().createQueryPlan(qry4, tx4);			//this is ProjectPlan
		
			System.out.println("-----------yo baby");
			Scan s = p.open();
			System.out.println("-------------Printing out the fields");
			((ProjectScan) s).printFields();
				
			System.out.println("Id\tName\tDob");
			while (s.next()) {
				int sid = s.getInt("id"); //SimpleDB stores field names
				String dname = s.getString("name"); //in lower case
				Date dob = s.getTimestamp("dob"); //in lower case
				System.out.println(sid + "\t" + dname+ "\t"+dob);
			}
			s.close();
			tx4.commit();
			System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			
			
			
			
			Transaction tx5 = new Transaction();
			String qry5 = "select id,name,dob from man1 where dob between '2011-10-08 20:00:00' '2011-10-10 22:00:00';";
			Plan p5 = SimpleDB.planner().createQueryPlan(qry5, tx5);			//this is ProjectPlan
		
			
			Scan s5 = p5.open();
			System.out.println("-------------Printing out the fields");
			((ProjectScan) s5).printFields();
				
			System.out.println("Id\tName\tDob");
			while (s5.next()) {
				int sid = s5.getInt("id"); //SimpleDB stores field names
				String dname = s5.getString("name"); //in lower case
				Date dob = s5.getTimestamp("dob"); //in lower case
				System.out.println(sid + "\t" + dname+ "\t"+dob);
			}
			s5.close();
			tx5.commit();
			System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			
			
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
