package simpledb.parse;

import java.util.*;

import simpledb.query.*;
import simpledb.record.Schema;

/**
 * The SimpleDB parser.
 * @author Edward Sciore
 */
public class Parser {
   private Lexer lex;
   
   public Parser(String s) {
      lex = new Lexer(s);					//define streamtokenizer on the query
   }
   
// Methods for parsing predicates, terms, expressions, constants, and fields
   
   public String field() {
      String stok = lex.eatId();
      return stok;
   }
   
   public Constant constant() {
      if (lex.matchStringConstant()){						//first character is quote then string
    	 StringConstant sc = new StringConstant(lex.eatStringConstant());
//    	 System.out.print("string const is: "+sc.asJavaVal()+   "     next tok:");
//    	 lex.currTok();
    	 return sc;
      }else													//first character is not quote then int
         return new IntConstant(lex.eatIntConstant());
   }
   
   public Expression expression() {
      if (lex.matchId())
         return new FieldNameExpression(field());
      else
         return new ConstantExpression(constant());
   }
   
   public Term term() {
      Expression lhs = expression();
      if(lex.matchDelim('=')){
    	  lex.eatDelim('=');
    	  Expression rhs = expression();
    	  return new Term(lhs, rhs);
      }else if(lex.matchKeyword("between")){
    	  lex.eatKeyword("between");
    	  Expression rhs = expression();
    	  Expression extra = expression();
    	  long small = (new TimestampConstant ( (String)rhs.asConstant().asJavaVal())).asJavaVal().getTime();
    	  long big = (new TimestampConstant ( (String)extra.asConstant().asJavaVal())).asJavaVal().getTime();
    	  if(big<small){
    		  System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> InvalidIntervalError <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    		  throw new InvalidIntervalError();
    	  }else{
    		  return new Term(lhs, rhs, extra);
    	  }
      }else{
    	  System.out.println("Parser Panic: neither = nor between");
    	  lex.eatDelim('=');			//defaulting to what was present earlier in the code
    	  Expression rhs = expression();
    	  return new Term(lhs, rhs);
      }
   }
   
   public Predicate predicate() {
	  Term t = term();
      Predicate pred = new Predicate(t);
      if (lex.matchKeyword("and")) {
         lex.eatKeyword("and");
         pred.conjoinWith(predicate());
      }
      return pred;
   }
   
// Methods for parsing queries
   
   public QueryData query() {
      lex.eatKeyword("select");
      Collection<String> fields = selectList();
      lex.eatKeyword("from");
      Collection<String> tables = tableList();
      Predicate pred = new Predicate();
      if (lex.matchKeyword("where")) {
         lex.eatKeyword("where");
         pred = predicate();
      }
      return new QueryData(fields, tables, pred);	
   }
   
   private Collection<String> selectList() {
      Collection<String> L = new ArrayList<String>();
      L.add(field());
      if (lex.matchDelim(',')) {
         lex.eatDelim(',');
         L.addAll(selectList());
      }
      return L;
   }
   
   private Collection<String> tableList() {
      Collection<String> L = new ArrayList<String>();
      L.add(lex.eatId());
      if (lex.matchDelim(',')) {
         lex.eatDelim(',');
         L.addAll(tableList());
      }
      return L;
   }
   
// Methods for parsing the various update commands
   
   public Object updateCmd() {
      if (lex.matchKeyword("insert"))
         return insert();
      else if (lex.matchKeyword("delete"))
         return delete();
      else if (lex.matchKeyword("update"))
         return modify();
      else if (lex.matchKeyword("create"))
         return create();
      else{
    	  System.out.println("Parser: Could not match the first word in the query");
    	  return create();		//or better return null
      }
   }
   
   private Object create() {
      lex.eatKeyword("create");
      if (lex.matchKeyword("table"))
         return createTable();
      else if (lex.matchKeyword("view"))
         return createView();
      else if (lex.matchKeyword("index"))
         return createIndex();
      else{
    	  System.out.println("Parser panic: Unknown create <unknown_keyword> command.");
    	  return createIndex();
      }
   }
   
// Method for parsing delete commands
   
   public DeleteData delete() {
      lex.eatKeyword("delete");
      lex.eatKeyword("from");
      String tblname = lex.eatId();
      Predicate pred = new Predicate();
      if (lex.matchKeyword("where")) {
         lex.eatKeyword("where");
         pred = predicate();
      }
      return new DeleteData(tblname, pred);
   }
   
// Methods for parsing insert commands
   
   public InsertData insert() {
      lex.eatKeyword("insert");
      lex.eatKeyword("into");
      String tblname = lex.eatId();
      lex.eatDelim('(');
      List<String> flds = fieldList();
      lex.eatDelim(')');
      lex.eatKeyword("values");
      lex.eatDelim('(');
      List<Constant> vals = constList();
      lex.eatDelim(')');
      return new InsertData(tblname, flds, vals);
   }
   
   private List<String> fieldList() {
      List<String> L = new ArrayList<String>();
      L.add(field());
      if (lex.matchDelim(',')) {
         lex.eatDelim(',');
         L.addAll(fieldList());
      }
      return L;
   }
   
   private List<Constant> constList() {
      List<Constant> L = new ArrayList<Constant>();
      L.add(constant());
      if (lex.matchDelim(',')) {
         lex.eatDelim(',');
         L.addAll(constList());
      }
      return L;
   }
   
// Method for parsing modify commands
   
   public ModifyData modify() {
      lex.eatKeyword("update");
      String tblname = lex.eatId();
      lex.eatKeyword("set");
      String fldname = field();
      lex.eatDelim('=');
      Expression newval = expression();
      Predicate pred = new Predicate();
      if (lex.matchKeyword("where")) {
         lex.eatKeyword("where");
         pred = predicate();
      }
      return new ModifyData(tblname, fldname, newval, pred);
   }
   
// Method for parsing create table commands
   
   public CreateTableData createTable() {
      lex.eatKeyword("table");
      String tblname = lex.eatId();
      lex.eatDelim('(');
      Schema sch = fieldDefs();
      lex.eatDelim(')');
      // parsed upto create table <tbname> (f1 t1,f2 t2)
      //schema is a map<string,fieldinfo> where field info is (int type, int len)
      return new CreateTableData(tblname, sch);
   }
   
   private Schema fieldDefs() {
	  Schema schema = fieldDef();
      if (lex.matchDelim(',')) {
         lex.eatDelim(',');
         Schema schema2 = fieldDefs();
         schema.addAll(schema2);
      }
      return schema;
   }
   
   private Schema fieldDef() {
      String fldname = field();
      Schema s = fieldType(fldname);		//scehma is a simple map<string,fieldinfo> where fieldinfo is int,int
      return s;
   }
   
   private Schema fieldType(String fldname) {
      Schema schema = new Schema();
      if (lex.matchKeyword("int")) {
         lex.eatKeyword("int");
         schema.addIntField(fldname);
      }else if(lex.matchKeyword("varchar")){
         lex.eatKeyword("varchar");
         lex.eatDelim('(');
         int strLen = lex.eatIntConstant();
         lex.eatDelim(')');
         schema.addStringField(fldname, strLen);
      }else if(lex.matchKeyword("timestamp")){
    	  lex.eatKeyword("timestamp");
    	  schema.addTimestampField(fldname);
      }else{
    	  System.out.println("Unknown data type");
      }
      return schema;
   }
   
// Method for parsing create view commands
   
   public CreateViewData createView() {
      lex.eatKeyword("view");
      String viewname = lex.eatId();
      lex.eatKeyword("as");
      QueryData qd = query();
      return new CreateViewData(viewname, qd);
   }
   
   
//  Method for parsing create index commands
   
   public CreateIndexData createIndex() {		//create index <idxname> on movies(dob)
      lex.eatKeyword("index");
      lex.currTok();
      String idxname = lex.eatId();				//index name
      lex.eatKeyword("on");					
      String tblname = lex.eatId();				//tblname
      lex.eatDelim('(');					
      String fldname = field();
      lex.eatDelim(')');
      return new CreateIndexData(idxname, tblname, fldname);
   }
}

