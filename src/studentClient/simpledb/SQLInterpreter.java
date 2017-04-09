package studentClient.simpledb;

import java.sql.*;
import java.text.SimpleDateFormat;

import simpledb.query.Scan;
import simpledb.record.Schema;
import simpledb.remote.RemoteResultSet;
import simpledb.remote.SimpleDriver;
import simpledb.remote.SimpleResultSet;

import java.io.*;


public class SQLInterpreter {
    private static Connection conn = null;

    public static void main(String[] args) {
	   try {
			Driver d = new SimpleDriver();
			conn = d.connect("jdbc:simpledb://localhost", null);

			Reader rdr = new InputStreamReader(System.in);
			BufferedReader br = new BufferedReader(rdr);

			while (true) {
				// process one line of input
				System.out.print("\nSQL> ");
				String cmd = br.readLine().trim();
				System.out.println();
				if (cmd.startsWith("exit"))
					break;
				else if (cmd.startsWith("select"))
					doQuery(cmd);
				else{
//					System.out.println("Doing an update");
					doUpdate(cmd);
				}
		    }
	    }
	    catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (conn != null)
					conn.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void doQuery(String cmd) {
		try {
		    Statement stmt = conn.createStatement();
		    ResultSet rs = stmt.executeQuery(cmd);	//returns RemoteResultSetImpl extends UnicastRemoteObject implements RemoteResultSet {
		    										//private Scan s;
		    										//private Schema sch;
		    										//private RemoteConnectionImpl rconn
		    if(rs == null){
		    	//InvalidIntervalError
		    	System.out.println("InvalidIntervalError");
		    	return;
		    }
		    ResultSetMetaData md = rs.getMetaData();
		    int numcols = md.getColumnCount();
		    int totalwidth = 0;
//		    System.out.println("yo man    INTEGER:"+Types.INTEGER+"      TIMESTAMP:"+Types.TIMESTAMP+"      VARCHAR"+Types.VARCHAR);
		    // print header
		    for(int i=1; i<=numcols; i++) {
				int width = md.getColumnDisplaySize(i);
				totalwidth += width;
				String fmt = "%" + width + "s";
//				System.out.println("col_name:"+md.getColumnName(i)+"       type:"+md.getColumnType(i)+"     fmt:"+fmt);
				System.out.format(fmt, md.getColumnName(i));
				
			}
			System.out.println();
			for(int i=0; i<totalwidth; i++)
			    System.out.print("-");
		    System.out.println();

		    // print records
		    while(rs.next()) {
				for (int i=1; i<=numcols; i++) {
					String fldname = md.getColumnName(i);
					int fldtype = md.getColumnType(i);
					String fmt = "%" + md.getColumnDisplaySize(i);
					if (fldtype == Types.INTEGER)
						System.out.format(fmt + "d", rs.getInt(fldname));
					else if(fldtype == Types.TIMESTAMP){
						SimpleDateFormat ts = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						java.util.Date d = ((SimpleResultSet)rs).getTimestampP2(fldname);
						System.out.format(fmt + "s", ts.format(d));

//						System.out.format(fmt + "s", rs.getDate(fldname).to);
					}else
						System.out.format(fmt + "s", rs.getString(fldname));
				}
				System.out.println();
			}
			rs.close();
		}
		catch (SQLException e) {
			System.out.println("SQL Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void doUpdate(String cmd) {
		try {
		    Statement stmt = conn.createStatement();
		    int howmany = stmt.executeUpdate(cmd);
		    if(howmany == -1){
		    	System.out.println("MemoryError");
		    }else if(howmany == -2){
		    	System.out.println("InvalidDateFormatError");
		    }else if(howmany == -3){
		    	System.out.println("InvalidIntervalError");
		    }else{
		    	System.out.println(howmany + " records processed");
		    }
		}
		catch (SQLException e) {
			System.out.println("SQL Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
}