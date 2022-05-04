package Proj3;
import static com.mongodb.client.model.Filters.eq;
import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.math.*;
import java.io.*;
import java.awt.*;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

public class Proj3 {
	public static void main(String[] args) throws IOException {
		BufferedReader readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
        String in; 
        System.out.println("Would you like MongoDB to display log messages? (Y/N): ");
        in = readKeyBoard.readLine();
        if (in.equals("N") || in.equals("n") || in.equals("No") || in.equals("no") || in.equals("NO")) {
        	LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        	Logger rootLogger = loggerContext.getLogger("org.mongodb.driver");
        	rootLogger.setLevel(Level.OFF);
        	System.out.println("Ok, log messages will NOT be displayed.");
        }
        else System.out.println("Ok, log messages will be displayed.");
	    String uri = "mongodb://localhost:27017";
	    System.out.print("Connecting to MongoDB database... ");
	    try (MongoClient mongoClient = MongoClients.create(uri)) {
	        MongoDatabase database = mongoClient.getDatabase("Project_3");
	        MongoCollection<Document> collection = database.getCollection("Project_3");
	        System.out.println(" done!\nAll information on COVID-19 was last updated November 3rd, 2020.");      
	        int flag = 0;
	        while(flag < 1) {
		        System.out.println("Select one of the following options (enter the number):");
	            System.out.println("1: Display a state's information");
	            System.out.println("2: View total infections based on how states voted");
	            System.out.println("0: Exit");
	            in = readKeyBoard.readLine();
	            int i = Integer.parseInt(in);
		        switch(i) {
			        case 0: // sets flag to 1 to break loop and exit program
	                    System.out.println("Exiting program...");
	                    flag = 1;
	                    break;
			        case 1:
			        	System.out.println("Please enter the name of the state you would like to look at: ");
			        	readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
			        	in = readKeyBoard.readLine();
			        	try {
				        	Document st = collection.find(eq("State", in)).first();
				        	System.out.println("Displaying information for " + in + ": ");
				        	System.out.println("Presidential Election results: " + st.get("Voted"));
				        	System.out.println("COVID-19 infections: " + st.get("Infected"));
				        	System.out.println("COVID-19 deaths: " + st.get("Deaths") + "\n\n");
			        	}
			        	catch(NullPointerException e) {
			        		System.out.println(in + " is invalid input, returning to main menu...\n\n");
			        	}
			        	break;
			        case 2:
			        	System.out.println("Number of states that voted red: ");
			        	System.out.println("Number of infections in red states: " + "\n");
			        	System.out.println("Number of states that voted blue: ");
			        	System.out.println("Number of infections in blue states: " + "\n\n");
			        	
			        default: System.out.println("Please select a valid option.");
		        }
	        }
	    }
	}
}
