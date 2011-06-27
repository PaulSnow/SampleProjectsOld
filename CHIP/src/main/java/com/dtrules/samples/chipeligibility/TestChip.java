/** 
 * Copyright 2004-2009 DTRules.com, Inc.
 *   
 * Licensed under the Apache License, Version 2.0 (the "License");  
 * you may not use this file except in compliance with the License.  
 * You may obtain a copy of the License at  
 *   
 *      http://www.apache.org/licenses/LICENSE-2.0  
 *   
 * Unless required by applicable law or agreed to in writing, software  
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  
 * See the License for the specific language governing permissions and  
 * limitations under the License.  
 **/ 
package com.dtrules.samples.chipeligibility;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;

import com.dtrules.entity.IREntity;
import com.dtrules.infrastructure.RulesException;
import com.dtrules.interpreter.IRObject;
import com.dtrules.interpreter.RArray;
import com.dtrules.interpreter.RName;
import com.dtrules.mapping.DataMap;
import com.dtrules.session.DTState;
import com.dtrules.session.IRSession;
import com.dtrules.session.RuleSet;
import com.dtrules.session.RulesDirectory;
import com.dtrules.testsupport.ATestHarness;
import com.dtrules.testsupport.Coverage;
import com.dtrules.testsupport.ITestHarness;
import com.dtrules.xmlparser.XMLPrinter;

public class TestChip extends ATestHarness {
    	
		static int threads = 1;
		
	    public static String path    = System.getProperty("user.dir")+"/";
		
		static  Date start = new Date();
	    @Override
    	public boolean  Verbose()                 { return false;	                        }
		public boolean  Trace()                   { return false;                           }
	    public boolean  Console()                 { return false;                           }
	    public boolean  coverageReport()          { return true;                       		}
		public String   getPath()                 { return path;                            }
	    public String   getRulesDirectoryPath()   { return getPath()+"xml/";                }
	    public String   getRuleSetName()          { return "CHIP";                          }
	    public String   getDecisionTableName()    { return "Compute_Eligibility";           }
	    public String   getRulesDirectoryFile()   { return "DTRules.xml";                   }             
	   
	    static String         ruleset;  
        static RName          rsName;   
        static RuleSet        rs;       
        static File           dir;
        
	    static int top = 0;
	    static int done = 0;
		static File files[];
		static RulesDirectory rd;
		
		static synchronized File next(){
			top++;
			if(top<=files.length){
				if(top% 100 == 0){ 
					System.out.print(top+" ");
					if(top%1000 == 0 )System.out.println();
					System.out.flush();
				}
				return files[top-1];
			}
			return null;
		}
	    
	    public static void main(String[] args) {
	    	if(args.length>0){
	    		try {
					threads = Integer.parseInt(args[0]);
					if(threads <=0)   threads = 1;
				} catch (NumberFormatException e) {
					System.out.println("The valid Argument to TestChip is a thread count greater than 0.");
				}
	    	}
	    	System.out.println("Executing Tests with "+threads+" Threads.");
	    	
	    	ITestHarness t = new TestChip();
	        t.runTests();
	        String fields[] = { "table number" };
	        //t.writeDecisionTables("tables",fields,true,10);
	    }
	    
	    public void runTests(){
	    	
	         
	        try{
	            // Delete old output files
	            File dir         = new File(getOutputDirectory());
	            if(!dir.exists()){
	            	dir.mkdirs();
	            }
	            File oldOutput[] = dir.listFiles();
	            for(File file : oldOutput){
	               file.delete(); 
	            }
	        }catch(Exception e){
	            throw new RuntimeException(e);
	        }
	        try {
	             
	             // Allocate a RulesDirectory.  This object can contain many different Rule Sets.
	             // A Rule set is a set of decision tables defined in XML, 
	             // the Entity Description Dictionary (the EDD, or schema) assumed by those tables, and
	             // A Mapping file that maps data into this EDD.
	             
	             rd = new RulesDirectory(getPath(),getRulesDirectoryFile());
	             
	             // Select a particular rule set and create a session to load the data and evaluate
	             // that data against the rules within this ruleset.  
	             ruleset  = getRuleSetName();
	             rsName   = RName.getRName(ruleset);
	             rs       = rd.getRuleSet(rsName);
	         	 rs.newSession(); // Force the creation of the EntityFactory
	             dir      = new File(getTestDirectory());
	             files    = getFiles();

	             System.out.println("Processing "+files.length+" tests");
	             
	             if(rs == null){
	            	 System.out.println("Could not find the Rule Set '"+ruleset+"'");
	            	 throw new RuntimeException("Undefined: '"+ruleset+"'");
	             }

	             {
	            	 Thread ts [] = new Thread[threads];
	            	 for(int i=0;i<threads; i++){
	            		 ts[i] = new RunThread(i+1, this);
	            	 }
		             
	            	
		             for(Thread t : ts) t.start();
	                 
		             while(threads>0){
	                	 Thread.sleep(100);
	                 }
	             }
	             Date now = new Date ();
	             int dfcnt = files.length;
	             int filecnt = dfcnt-1;
                 if(filecnt == 0) filecnt = 1;
                 { 
                	 long dt  = (now.getTime()-start.getTime())/(filecnt);
                	 long sec = dt/1000;
                	 long lms  = dt-(sec*1000);
                	 String ms = lms<100 ? lms<10 ? "00"+lms : "0"+lms : ""+lms;
                	 System.out.println("\nDone.  Avg execution time: "+sec+"."+ms);
                 }
                 { 
                	 long dt  = (now.getTime()-start.getTime());
                	 long sec = dt/1000;
                	 long lms  = dt-(sec*1000);
                	 String ms = lms<100 ? lms<10 ? "00"+lms : "0"+lms : ""+lms;
                	 System.out.println("\nTotal time: "+sec+"."+ms);
               }
	         } catch ( Exception ex ) {
	             if(Console()){
	                 System.out.print(ex);
	             }
	         }
	         
	         try{
	             compareTestResults();
	         }catch(Exception e){
	             System.out.println("Error comparing Test Results: "+e);
	         }
	     }

	    public static class RunThread extends Thread {
	    	TestChip dad;
	    	int      t;
	    	RunThread(int t, TestChip dad){
	    		System.out.println("Starting "+t);
	    		this.dad = dad;
	    	}
	    	public void run () {
	    		File file = next();
	    		while(file != null){
	    			String err=null;
					err = dad.runfile(rd,rs,dir.getAbsolutePath(),file.getName());
					if(err != null)System.err.println(err);
	    			file = next();
	    		}
	    		threads--;
	    	}
	    }
	    
	    /**
	      * Returns the error if an error is thrown.  Otherwise, a null.
	      * @param rd
	      * @param rs
	      * @param dfcnt
	      * @param path
	      * @param dataset
	      * @return
	      */
	     public String runfile(RulesDirectory rd, RuleSet rs, String path, String dataset) {
	         
	         PrintStream    out          = null;
	         OutputStream   tracefile    = null;
	         
	         String root = dataset;
	         int offset = dataset.indexOf(".");
	         
	         if(offset >= 0){
	        	 root = dataset.substring(0,offset);
	         }	         
	         
	         try {
	              
	              			     out        = new PrintStream     (getOutputDirectory()+root+"_results.xml");
	              IRSession      session    = rs.newSession();
	              DTState        state      = session.getState();
	              state.setOutput(tracefile, out);
	              
	              // Get the XML mapping for the rule set, and load a set of data into the EDD
	               
	              loadData(session, path, dataset);
		          
	              // Once the data is loaded, execute the rules.
	              RulesException ex = null;
	              try{
	            	  executeDecisionTables(session);
			      }catch(RulesException e){
	                  ex = e;
	              }
	              
	              
	              if(ex!=null)throw ex;
	              
	              // Print the report
	              try{
	            	  printReport(0, session, out);
			      }catch(Throwable e){
	            	  if(!Console()){                   	// If we are going to the console, assume the same
	            		  System.out.println(e.toString());	// error will get thrown, so don't print twice.
	            	  }
	              }
	              
	              // If asked, print the report again to the console.
	              if(Console()){
	                  try{
	                	  printReport(0, session, System.out);
			          }catch(Throwable e){
	                      System.out.println(e.toString());
	                   }
	              }
	              
	              if(Trace()){
	                  session.getState().traceEnd();
	              }
	             
	          } catch ( Exception ex ) {
	              System.out.print("<-ERR  ");
	              if(Console()){
	                  System.out.print(ex);
	              }
	              return "\nAn Error occurred while running the example:\n"+ex+"\n";
	          }
	          return null;
	      }
	    
	    public void printReport(int runNumber, IRSession session, PrintStream _out) throws RulesException {
	        XMLPrinter xout = new XMLPrinter(_out);
	        xout.opentag("results","runNumber",runNumber);
	        RArray results = session.getState().find("job.results").rArrayValue();
	        for(IRObject r :results){
	            IREntity result = r.rEntityValue();

	            xout.opentag("Client","id",result.get("client_id").stringValue());
	            prt(xout,result,"totalGroupIncome");
	            prt(xout,result,"client_fpl");
	            if(result.get("eligible").booleanValue()){
	                xout.opentag("Approved");
		                prt(xout,result,"program");
		                prt(xout,result,"programLevel");
		                RArray notes = result.get("notes").rArrayValue();
		                xout.opentag("Notes");
		                    for(IRObject n : notes){
		                       xout.printdata("note",n.stringValue());
		                    }
	                    xout.closetag();
	                xout.closetag();
	            }else{
	                xout.opentag("NotApproved");
	                    prt(xout,result,"program");
	                    RArray notes = result.get("notes").rArrayValue();
	                    xout.opentag("Notes");
	                        for(IRObject n : notes){
	                           xout.printdata("note",n.stringValue());
	                        }
	                    xout.closetag();
	                xout.closetag();
	            }
	            xout.closetag();
	        }
	        xout.close();
	    }
	 
	    private void prt(XMLPrinter xout, IREntity entity, String attrib){
	        IRObject value = entity.get(attrib);
	        xout.printdata(attrib,value.stringValue());
	    }
	    
	}    
	    
