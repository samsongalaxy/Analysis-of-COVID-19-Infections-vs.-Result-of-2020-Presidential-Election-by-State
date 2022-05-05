package Proj3;
import static com.mongodb.client.model.Filters.eq;
import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.io.*;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

public class Proj3 {
	
	//returns given string with the first letter of each word capitalized
	//used to help in searching database
	public static String capitalize(String s){  
	    String words[]= s.split("\\s");  
	    String ret ="";  
	    for(String w:words){  
	        String first = w.substring(0,1);  
	        String rest = w.substring(1);  
	        ret += first.toUpperCase() + rest + " ";  
	    }  
	    return ret.trim();  
	}  
	
	public static void main(String[] args) throws IOException {
		BufferedReader readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
        String in; 
        //Asks user whether or not they wish to see MongoDB log messages while program runs
        //If anything other than 'n' or 'no' is entered, messages will be displayed
        System.out.println("Would you like MongoDB to display log messages? (Y/N): ");
        in = readKeyBoard.readLine();
        if (in.toLowerCase().equals("n") || 
        		in.toLowerCase().equals("no")) {
        	LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        	Logger rootLogger = loggerContext.getLogger("org.mongodb.driver");
        	rootLogger.setLevel(Level.OFF);
        	System.out.println("Ok, log messages will NOT be displayed.");
        }
        else System.out.println("Ok, log messages will be displayed.");
        //Connects to local MongoDB server
	    String uri = "mongodb://localhost:27017";
	    System.out.print("Connecting to MongoDB database... ");
	    try (MongoClient mongoClient = MongoClients.create(uri)) {
	        MongoDatabase database = mongoClient.getDatabase("Project_3");
	        MongoCollection<Document> collection = database.getCollection("Project_3");
	        System.out.println(" done!\nAll information on COVID-19 was last updated November 3rd, 2020.");      
	        //flag is used to keep program running, other values defined here are accumulators to calculate
	        //totals for all states and totals based off how states voted
	        int flag = 0, 
	        		totalInfected = 0,
	        		totalDeaths = 0,
	        		totalPop = 0,
	        		redStates = 0,
	        		redInfected = 0,
	        		redDeaths = 0,
	        		redPop = 0,
	        		blueStates = 0,
	        		blueInfected = 0,
	    	        blueDeaths = 0,
	    	        bluePop = 0;
	        MongoCursor cursor = collection.find().iterator();
    	    while(cursor.hasNext()){
    	        Document curr = (Document)cursor.next();
    	        if(curr != null){
    	            totalInfected += curr.getInteger("Infected");
    	            totalDeaths += curr.getInteger("Deaths");
    	            totalPop += curr.getInteger("Population");    	            
    	            if(curr.get("Voted").equals("Red")) {
    	            	redInfected += curr.getInteger("Infected");
        	            redDeaths += curr.getInteger("Deaths");
        	            redStates++;
        	            redPop += curr.getInteger("Population");
    	            }
    	            else {
    	            	blueInfected += curr.getInteger("Infected");
        	            blueDeaths += curr.getInteger("Deaths");
        	            blueStates++;
        	            bluePop += curr.getInteger("Population");
    	            }
    	        }
    	    }
    	    //total infections/deaths as a % of population
    	    float totalInfectedPercent = 100 * (float)totalInfected / (float)totalPop,
    	    		totalDeathPercent = 100 * (float)totalDeaths / (float)totalPop,
    	    		redInfectedPercent = 100 * (float)redInfected / (float)redPop,
    	    		redDeathPercent = 100 * (float)redDeaths / (float)redPop,
    	    		blueInfectedPercent = 100 * (float)blueInfected / (float)bluePop,
    	    		blueDeathPercent = 100 * (float)blueDeaths / (float)bluePop; 
    	    int i;
	        while(flag < 1) { //program runs until flag is set to 1
	        	//main menu for user to select options
		        System.out.println("\n\nSelect one of the following options (enter the number):\n"
		        		+ "\t1: Display a state's information\n"
		        		+ "\t2: View total infections based on how states voted\n"
		        		+ "\t\t(District of Columbia included)\n"
		        		+ "\t0: Exit");
	            readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
	            in = readKeyBoard.readLine();
	            try {
	            	i = Integer.parseInt(in);
			        switch(i) {
				        case 0: // sets flag to 1 to break loop and exit program
		                    System.out.println("Exiting program...");
		                    flag = 1;
		                    break;
				        case 1:
				        	//asks user what state they'd like to view information for
				        	System.out.println("Please enter the name of the state you would like to look at \n"
				        			+ "(enter 'all' if you would like to view information for all states): ");
				        	readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
				        	in = readKeyBoard.readLine();
				        	if(in.toLowerCase().equals("all")) { //user wishes to view total for all states
				        		System.out.print("Displaying information for all states:\n"
				        				+ "Total United States COVID-19 infections: " + totalInfected + 
				        				"\nUnited States COVID-19 infections as a % of the population: ");
				        		System.out.printf("%.2f", totalInfectedPercent);
				        		System.out.print("%\n"
				        				+ "Total United States COVID-19 deaths: " + totalDeaths + 
				        				"\nUnited States COVID-19 deaths as a % of the population: ");
				        		System.out.printf("%.4f", totalDeathPercent);
				        		System.out.print("%\n\n"
				        				+ "Press [ENTER] to return to main menu.\n");
					        	readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
					        	in = readKeyBoard.readLine();
				        	}
				        	else {
					        	try {				        		
					        		if(in.toLowerCase().equals("dc") || 
					        				in.toLowerCase().equals("washington dc") || 
					        				in.toLowerCase().equals("disctrict of columbia") ||
					        				in.toLowerCase().equals("d.c.") ||
					        				in.toLowerCase().equals("washington d.c.")) in = "District of Columbia"; //checks if user is requesting D.C. with different forms of input
					        		else in = capitalize(in.toLowerCase()); //formats input string to search database
						        	Document st = collection.find(eq("State", in)).first(); //finds requested state
						        	System.out.print("Displaying information for " + in + ":\n"
						        			+ "Presidential Election results: " + st.get("Voted") +
						        			"\nTotal COVID-19 infections: " + st.get("Infected") + 
						        			"\nCOVID-19 infections as a percentage of population: ");
						        	System.out.printf("%.2f", 100 * (float)st.getInteger("Infected") / (float)st.getInteger("Population"));
					        		System.out.print("%\n"
					        				+ "Total COVID-19 deaths: " + st.get("Deaths") +
					        				"\nCOVID-19 deaths as a percentage of population: ");
						        	System.out.printf("%.4f", 100 * (float)st.getInteger("Deaths") / (float)st.getInteger("Population"));
					        		System.out.print("%\n\n"
					        				+ "Press [ENTER] to return to main menu.");
						        	readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
						        	in = readKeyBoard.readLine();
					        	}
					        	catch(NullPointerException e) {
					        		System.out.println(in + " is invalid input, returning to main menu...");
					        	}
				        	}
				        	break;
				        case 2:
				        	//displays sub-menu asking whether user would like to view info for red states, blue states, or both at the same time 
				        	System.out.println("\n\nWhat would you like to see? (enter the number):\n"
				        			+ "\t1: Just Red States\n"
				        			+ "\t2: Just Blue States\n"
				        			+ "\t3: Red and Blue States\n"
				            		+ "\t0: Return to main menu");
				            readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
				            in = readKeyBoard.readLine();
				            try {
				            		i = Integer.parseInt(in);
				            		switch(i) {
							        case 0:
			                            System.out.println("Returning to main menu...");
			                            break;
							        case 1: //red states
							        	System.out.println("Number of states that voted red: " + redStates +
							        			"\nNumber of COVID-19 infections in red states: " + redInfected +
							        			"\nCOVID-19 infections as a % of the population of red states: ");
						        		System.out.printf("%.2f", redInfectedPercent);
						        		System.out.print("%\n\n"
						        				+ "Number of COVID-19 deaths in red states: " + redDeaths + 
						        				"\nCOVID-19 deaths as a % of the population of red states: ");
						        		System.out.printf("%.4f", redDeathPercent);
						        		System.out.print("%\n\n"
						        				+ "Press [ENTER] to return to main menu.");
							        	readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
							        	in = readKeyBoard.readLine();
						        		break;
							        case 2: //blue states
							        	System.out.println("Number of states that voted blue: " + blueStates);
							        	System.out.println("Number of COVID-19 infections in blue states: " + blueInfected);
							        	System.out.print("COVID-19 infections as a % of the population of blue states: ");
						        		System.out.printf("%.2f", blueInfectedPercent);
						        		System.out.print("%\n");
							        	System.out.println("Number of COVID-19 deaths in blue states: " + blueDeaths);
							        	System.out.print("COVID-19 deaths as a % of the population of blue states: ");
						        		System.out.printf("%.4f", blueDeathPercent);
						        		System.out.print("%\n");
						        		System.out.println("\nPress [ENTER] to return to main menu.");
							        	readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
							        	in = readKeyBoard.readLine();
						        		break;
							        case 3: //both red and blue states
							        	System.out.println("Number of states that voted red: " + redStates);
							        	System.out.println("Number of COVID-19 infections in red states: " + redInfected);
							        	System.out.print("COVID-19 infections as a % of the population of red states: ");
						        		System.out.printf("%.2f", redInfectedPercent);
						        		System.out.print("%\n");
							        	System.out.println("Number of COVID-19 deaths in red states: " + redDeaths);
							        	System.out.print("COVID-19 deaths as a % of the population of red states: ");
						        		System.out.printf("%.4f", redDeathPercent);
						        		System.out.print("%\n\n");
							        	System.out.println("Number of states that voted blue: " + blueStates);
							        	System.out.println("Number of COVID-19 infections in blue states: " + blueInfected);
							        	System.out.print("COVID-19 infections as a % of the population of blue states: ");
						        		System.out.printf("%.2f", blueInfectedPercent);
						        		System.out.print("%\n");
							        	System.out.println("Number of COVID-19 deaths in blue states: " + blueDeaths);
							        	System.out.print("COVID-19 deaths as a % of the population of blue states: ");
						        		System.out.printf("%.4f", blueDeathPercent);
						        		System.out.print("%\n");
						        		System.out.println("\nPress [ENTER] to return to main menu.");
							        	readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
							        	in = readKeyBoard.readLine();
						        		break;
							        default:
			                            System.out.println("Invalid input, returning to main menu...");
			                            break;
				            		}
				            	}
				            	catch(Exception e) {
				            		System.out.println("Invalid input, returning to main menu...");
				            	}			            
					        
				        	break;
				        default: System.out.println("Please select a valid option from the menu."); //input was not a given option, restarts loop
			        }
	            }
	            catch(Exception e) {
	            	System.out.println("Please select a valid option from the menu."); //input was not a given option, restarts loop
	            }
	            
	        }
	    }
	}
}
