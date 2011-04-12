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


import com.dtrules.testsupport.ATestHarness;
import com.dtrules.testsupport.ITestHarness;

public class TestTIERS extends ATestHarness {
    
        static{ new TestTIERS(); }
    
	    public boolean  Trace()                   { return true;                            }
	    public boolean  Console()                 { return true;                            }
	    public String   getPath()                 { return CompileTIERS.path;               }
	    public String   getRulesDirectoryPath()   { return getPath()+"xml/";                }
	    public String   getRuleSetName()          { return "TIERS";                         }
	    public String   getDecisionTableName()    { return "";                              }
	    public String   getRulesDirectoryFile()   { return "DTRules.xml";                   }             
	   
	    static String fields[] = {"file_name", "Table_Name"};
	    public static void main(String[] args) {
	        ITestHarness t = new TestTIERS();
	        t.runTests();
	        t.writeDecisionTables("tables",fields,true,10);
	    }
	    
	}    
	    
