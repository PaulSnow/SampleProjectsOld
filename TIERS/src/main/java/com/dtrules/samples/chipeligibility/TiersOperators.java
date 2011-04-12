package com.dtrules.samples.chipeligibility;

import com.dtrules.entity.IREntity;
import com.dtrules.infrastructure.RulesException;
import com.dtrules.interpreter.IRObject;
import com.dtrules.interpreter.RArray;
import com.dtrules.interpreter.RDate;
import com.dtrules.interpreter.RName;
import com.dtrules.interpreter.RString;
import com.dtrules.interpreter.operators.ROperator;
import com.dtrules.session.DTState;

public class TiersOperators {
    static {
        new GtGt(); new TableDateLookup();
    }
    
    // >>
    // ( procedure --> )   The relationships rlist on the current individual
    //                     is searched for the first entry for which the given
    //                     procedure returns true.  The proceedure must handle
    //                     return an object that provides a program code.
    //                           ( programCode --> programCode ProgramCode )
    static class GtGt extends ROperator {
        static RName n_individual           = RName.getRName("individual.individual");
        static RName n_relationships        = RName.getRName("relationships");
        static RName n_inverserelationship  = RName.getRName("inverserelationship");
        static RName n_relationshipcd       = RName.getRName("relationshipcd");
        GtGt() {
            super(">>");
        }
        public void execute(DTState state) throws RulesException {
            
            IRObject  code          = state.datapop();      // Get the programCode/procedure
            IREntity  individual    = state.find(GtGt.n_individual).rEntityValue();
            RArray    relationships = individual.get(GtGt.n_relationships).rArrayValue();
            
            for (IRObject r : relationships){
               IREntity relationship  =  r.rEntityValue();
               IREntity irelationship =  relationship.get(GtGt.n_inverserelationship).rEntityValue();
               state.entitypush(irelationship);
               state.entitypush(relationship.get(GtGt.n_individual).rEntityValue());
               state.datapush(relationship.get(GtGt.n_relationshipcd));
               code.execute(state);
               String programCode1 = state.datapop().stringValue();
               String programCode2 = state.datapop().stringValue();
               if(programCode1.equals(programCode2)){
                  return;
               }   
               state.entitypop();
               state.entitypop();
            }                   
            throw new RulesException("undefined",">>","no valid context found");
         }
    }
    
    
    /**
     * TableDateLookup -- Supporting the syntax:
     * 
     *      LOOKUP expr:e1 USINGCOLUMN expr:e2 FROMTABLE expr:e3 FORDATE expr:e4
     * 
     * ( row:e1  column:e2 tablename:e3 date:e4 -> value )  For now we will just remove our parameters, and
     * return a value.  We should do a table lookup.
     * 
     * 
     * @author paul
     *
     */
    static class TableDateLookup extends ROperator {
        TableDateLookup() {
            super("tabledatelookoup");
        }
        @SuppressWarnings("unused")
        public void execute(DTState state) throws RulesException {
            RDate    date       = state.datapop().rTimeValue();
            RString  tablename  = state.datapop().rStringValue();
            RString  column     = state.datapop().rStringValue();
            RString  row        = state.datapop().rStringValue();
            
            state.datapush(RString.newRString("rs"));
        }
    }
    
    /**
     * TableLookup -- Supporting the syntax:
     * 
     *      LOOKUP expr:e1 USINGCOLUMN expr:e2 
     * 
     * ( row:e1  column:e2 tablename:e3 -> value )  For now we will just remove our parameters, and
     * return a value.  We should do a table lookup.
     * 
     * 
     * @author paul
     *
     */
    static class TableLookup extends ROperator {
        TableLookup() {
            super("tablelookoup");
        }
        @SuppressWarnings("unused")
        public void execute(DTState state) throws RulesException {
            RString  tablename  = state.datapop().rStringValue();
            RString  column     = state.datapop().rStringValue();
            RString  row        = state.datapop().rStringValue();
            
            state.datapush(RString.newRString("rs"));
        }
    }

    /**
     * TableDateLookupForCode -- Supporting the syntax:
     * 
     *      LOOKUP CODE expr:e1 USINGCOLUMN expr:e2 FROMTABLE expr:e3 FORDATE expr:e4
     * 
     * ( row:e1  column:e2 tablename:e3 -> value )  For now we will just remove our parameters, and
     * return a value.  We should do a table lookup.
     * 
     * 
     * @author paul
     *
     */
    static class TableDateLookupForCode extends ROperator {
        TableDateLookupForCode() {
            super("tabledatelookoupforcode");
        }
        @SuppressWarnings("unused")
        public void execute(DTState state) throws RulesException {
            RDate    date       = state.datapop().rTimeValue();
            RString  tablename  = state.datapop().rStringValue();
            RString  column     = state.datapop().rStringValue();
            RString  row        = state.datapop().rStringValue();
            
            state.datapush(RString.newRString("rs"));
        }
    }

    /**
     * TableLookup -- Supporting the syntax:
     * 
     *      LOOKUP expr:e1 USINGCOLUMN expr:e2 
     * 
     * ( row:e1  column:e2 tablename:e3 -> value )  For now we will just remove our parameters, and
     * return a value.  We should do a table lookup.
     * 
     * 
     * @author paul
     *
     */
    static class TableLookupForCode extends ROperator {
        TableLookupForCode() {
            super("tablelookoupforcode");
        }
        @SuppressWarnings("unused")
        public void execute(DTState state) throws RulesException {
            RString  tablename  = state.datapop().rStringValue();
            RString  column     = state.datapop().rStringValue();
            RString  row        = state.datapop().rStringValue();
            
            state.datapush(RString.newRString("rs"));
        }
    }
    
}
