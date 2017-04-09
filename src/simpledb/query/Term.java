package simpledb.query;

import simpledb.record.Schema;

/**
 * A term is a comparison between two expressions.
 * @author Edward Sciore
 *
 */
public class Term {
   private Expression lhs, rhs;
   private Expression extra;			//usage <lhs> between <rhs> <extra>
   
   /**
    * Creates a new term that compares two expressions
    * for equality.
    * @param lhs  the LHS expression
    * @param rhs  the RHS expression
    */
   public Term(Expression lhs, Expression rhs) {
      this.lhs = lhs;
      this.rhs = rhs;
      extra = null;
   }
   
   /**
    * Creates a new term that does the between operation
    * @param lhs  the LHS expression
    * @param rhs  the RHS expression
    */
   public Term(Expression lhs, Expression rhs,Expression extra) {
      this.lhs = lhs;
      this.rhs = rhs;
      this.extra = extra;
   }
   
   /**
    * Calculates the extent to which selecting on the term reduces 
    * the number of records output by a query.
    * For example if the reduction factor is 2, then the
    * term cuts the size of the output in half.
    * @param p the query's plan
    * @return the integer reduction factor.
    */
   public int reductionFactor(Plan p) {
      String lhsName, rhsName;
      if (lhs.isFieldName() && rhs.isFieldName()) {
         lhsName = lhs.asFieldName();
         rhsName = rhs.asFieldName();
         return Math.max(p.distinctValues(lhsName),
                         p.distinctValues(rhsName));
      }
      if (lhs.isFieldName()) {
         lhsName = lhs.asFieldName();
         return p.distinctValues(lhsName);
      }
      if (rhs.isFieldName()) {
         rhsName = rhs.asFieldName();
         return p.distinctValues(rhsName);
      }
      // otherwise, the term equates constants
      if (lhs.asConstant().equals(rhs.asConstant()))
         return 1;
      else
         return Integer.MAX_VALUE;
   }
   
   /**
    * Determines if this term is of the form "F=c"
    * where F is the specified field and c is some constant.
    * If so, the method returns that constant.
    * If not, the method returns null.
    * @param fldname the name of the field
    * @return either the constant or null
    */
   public Constant equatesWithConstant(String fldname) {
      if (lhs.isFieldName() &&
          lhs.asFieldName().equals(fldname) &&
          rhs.isConstant())
         return rhs.asConstant();
      else if (rhs.isFieldName() &&
               rhs.asFieldName().equals(fldname) &&
               lhs.isConstant())
         return lhs.asConstant();
      else
         return null;
   }
   
   /**
    * Determines if this term is of the form "F=c"
    * where F is the specified field and c is some constant.
    * If so, the method returns that constant.
    * If not, the method returns null.
    * @param fldname the name of the field
    * @return either the constant or null
    */
   public Constant equatesWithConstant2(String fldname) {
      if (lhs.isFieldName() &&
          lhs.asFieldName().equals(fldname) &&
          extra != null && extra.isConstant()){
         return extra.asConstant();
//      }else if (rhs.isFieldName() &&							//this case can never arise
//               rhs.asFieldName().equals(fldname) &&
//               lhs.isConstant())
//         return lhs.asConstant();
      }else{
         return null;
      }
   }
   
   /**
    * Determines if this term is of the form "F1=F2"
    * where F1 is the specified field and F2 is another field.
    * If so, the method returns the name of that field.
    * If not, the method returns null.
    * @param fldname the name of the field
    * @return either the name of the other field, or null
    */
   public String equatesWithField(String fldname) {
      if (lhs.isFieldName() &&
          lhs.asFieldName().equals(fldname) &&
          rhs.isFieldName())
         return rhs.asFieldName();
      else if (rhs.isFieldName() &&
               rhs.asFieldName().equals(fldname) &&
               lhs.isFieldName())
         return lhs.asFieldName();
      else
         return null;
   }
   
   /**
    * Returns true if both of the term's expressions
    * apply to the specified schema.
    * @param sch the schema
    * @return true if both expressions apply to the schema
    */
   public boolean appliesTo(Schema sch) {
      return lhs.appliesTo(sch) && rhs.appliesTo(sch);
   }
   
   /**
    * Returns true if both of the term's expressions
    * evaluate to the same constant,
    * with respect to the specified scan.
    * @param s the scan
    * @return true if both expressions have the same value in the scan
    */
   public boolean isSatisfied(Scan s) {
	  if(extra!=null){			// means timestamp between was used
		  Constant lhsval = lhs.evaluate(s);
		  Constant rhsval = rhs.evaluate(s);
		  Constant extraval = extra.evaluate(s);
		  
		  TimestampConstant mainval, smallval,bigval;
		  if(lhsval instanceof TimestampConstant){
			  mainval = (TimestampConstant)lhsval;
		  }else{
			  mainval = new TimestampConstant((String) lhsval.asJavaVal());
		  }
		  
		  if(rhsval instanceof TimestampConstant){
			  smallval = (TimestampConstant)rhsval;
		  }else{
			  smallval = new TimestampConstant((String)rhsval.asJavaVal());
		  }
		  
		  if(extraval instanceof TimestampConstant){
			  bigval = (TimestampConstant)extraval;
		  }else{
			  bigval = new TimestampConstant((String)extraval.asJavaVal());
		  }
		  
		  long mvv = mainval.asJavaVal().getTime();
		  long sv = smallval.asJavaVal().getTime();
		  long bv = bigval.asJavaVal().getTime();
		  if(sv<= mvv && mvv<=bv){
			  return true;
		  }else{
			  return false;
		  }
	  }
	  
	  Constant lhsval = lhs.evaluate(s);
      Constant rhsval = rhs.evaluate(s);
      
//      System.out.println("Comparing this: lhs="+lhsval.asJavaVal() +"      rhs="+rhsval.asJavaVal());
      
      if(lhsval instanceof TimestampConstant && !(rhsval instanceof TimestampConstant)){	
    	  //if one is timestampConstant then other should also have been
    	  rhsval = new TimestampConstant((String)rhsval.asJavaVal());
      }else if(rhsval instanceof TimestampConstant && !(lhsval instanceof TimestampConstant)){
    	  lhsval = new TimestampConstant((String)lhsval.asJavaVal());
      }
	  
      return rhsval.equals(lhsval);
   }
   
   public String toString() {
      return lhs.toString() + "=" + rhs.toString();
   }
}
