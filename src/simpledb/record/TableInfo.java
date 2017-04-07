package simpledb.record;

import static java.sql.Types.*;
import static simpledb.file.Page.*;
import java.util.*;

/**
 * The metadata about a table and its records.
 * @author Edward Sciore
 */
public class TableInfo {
   private Schema schema;
   private Map<String,Integer> offsets;
   private int recordlen;
   private String tblname;
   
   /**
    * Creates a TableInfo object, given a table name
    * and schema. The constructor calculates the
    * physical offset of each field.
    * This constructor is used when a table is created. 
    * @param tblname the name of the table
    * @param schema the schema of the table's records
    */
   public TableInfo(String tblname, Schema schema) {
      this.schema = schema;
      this.tblname = tblname;
      offsets  = new HashMap<String,Integer>();
      int pos = 0;
      for (String fldname : schema.fields()) {
    	  int len = lengthInBytes(fldname);
//    	  System.out.println("fieldname: "+fldname+"   pos:"+pos+"    length in bytes:"+len);
         offsets.put(fldname, pos);				//store the information which fieldname was stored at what position
         pos += len;			//increment by number of bytes it takes to save the field name
      }
      recordlen = pos;
   }
   
   /**
    * Creates a TableInfo object from the 
    * specified metadata.
    * This constructor is used when the metadata
    * is retrieved from the catalog.
    * @param tblname the name of the table
    * @param schema the schema of the table's records
    * @param offsets the already-calculated offsets of the fields within a record
    * @param recordlen the already-calculated length of each record
    */
   public TableInfo(String tblname, Schema schema, Map<String,Integer> offsets, int recordlen) {
      this.tblname   = tblname;
      this.schema    = schema;
      this.offsets   = offsets;
      this.recordlen = recordlen;
   }
   
   /**
    * Returns the filename assigned to this table.
    * Currently, the filename is the table name
    * followed by ".tbl".
    * @return the name of the file assigned to the table
    */
   public String fileName() {
      return tblname + ".tbl";
   }
   
   /**
    * Returns the schema of the table's records
    * @return the table's record schema
    */
   public Schema schema() {
      return schema;
   }
   
   /**
    * Returns the offset of a specified field within a record
    * @param fldname the name of the field
    * @return the offset of that field within a record
    */
   public int offset(String fldname) {
      return offsets.get(fldname);
   }
   
   /**
    * Returns the length of a record, in bytes.
    * @return the length in bytes of a record
    */
   public int recordLength() {
      return recordlen;
   }
   
   private int lengthInBytes(String fldname) {
      int fldtype = schema.type(fldname);
//      System.out.println("fldname:"+fldname+"    fldtype: "+fldtype +"      INTEGER:"+INTEGER+"       TIMESTAMP:"+TIMESTAMP+"    varchar:"+VARCHAR);
      if (fldtype == INTEGER)
         return INT_SIZE;
      else if(fldtype == TIMESTAMP){
    	  return DATE_SIZE;
      }else if(fldtype == VARCHAR){
         return STR_SIZE(schema.length(fldname));
      }else{
    	  System.out.println("TabelInfo Panic: data field size calculation not known");
    	  return STR_SIZE(schema.length(fldname));
      }
   }
}