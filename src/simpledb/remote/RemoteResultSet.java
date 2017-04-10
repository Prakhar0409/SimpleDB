package simpledb.remote;

import java.rmi.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * The RMI remote interface corresponding to ResultSet.
 * The methods are identical to those of ResultSet, 
 * except that they throw RemoteExceptions instead of SQLExceptions.
 * @author Edward Sciore
 */
public interface RemoteResultSet extends Remote {
   public boolean next()                   throws RemoteException;
   public int getInt(String fldname)       throws RemoteException;
   public String getString(String fldname) throws RemoteException;
   public java.util.Date getTimestamp(String fldname) throws RemoteException;
   public java.util.Date getTimestampP2(String fldname) throws RemoteException;
   public Date getDate(String fldname) throws RemoteException;
   public RemoteMetaData getMetaData()     throws RemoteException;
   public void close()                     throws RemoteException;
}

