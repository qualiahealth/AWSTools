package HW1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.Scanner;
import java.util.UUID;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
 
public class CreateCredentials {
 
    private static BufferedWriter br;
    private static int option;
    private static AmazonS3 s3;
    private static String fName;
    private static String lName;
    private static String pNumber;
    private static String key;

	public static void main(String[] args) throws Exception {
    	
    	s3 = new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider());
		Region usWest2 = Region.getRegion(Regions.US_WEST_2);
		s3.setRegion(usWest2);
		
		System.out.println("===========================================");
		System.out.println("Create Your Credentials");
	    System.out.println("===========================================\n");
	     
		while (option != 8) {
       
	        System.out.println("What would you like to do: ");
	        System.out.println("--------------------------");
	        
	        System.out.println("1) Setup Secret/Access Keys (One-time setup)");
	        System.out.println("2) Create a Bucket");
	        System.out.println("3) List Buckets");
	        System.out.println("4) Create Object (credential file)");
	        System.out.println("5) Edit an Object");
	        System.out.println("6) Delete Objects from a bucket");
	        System.out.println("7) Delete Bucket");
	        System.out.println("8) Exit");
	        
	        Scanner scn = new Scanner(System.in); 
			System.out.println("Choose a number: ");
			option = scn.nextInt();
		
			if(option == 1){
				System.out.println();
				setKeys();
			}
			else if(option == 2){
				System.out.println();
				createBucket();
			}
			else if(option == 3){
				System.out.println();
				listBuckets();
			}
			else if(option == 4){
				System.out.println();
				createObject();
			}
			else if(option == 8){
				System.out.println("Goodbye.");
				System.exit(1);
			}
			else if(option == 5){
				System.out.println();
				editObject();
			}
			else if(option == 6){
				System.out.println();
				deleteObject();
			}
			else if(option == 7){
				System.out.println();
				deleteBucket();
			}
			else{
				System.out.println("Please select a valid option");
			}
		}
    	

    }
    
    private static void setKeys() throws IOException{
    	
    	//Ask users for AWS keys if this utility is used for the first time
    	
    	System.out.println("Create AWS Keys");
        System.out.println("--------------------------\n");
    	
    	
    	// Location of file to read
        File file = new File("src/AwsCredentials.properties");
        String secretK = "";
        String accessK = "";
        	
        System.out.println("AWS Access Keys One-Time Setup");
    	
    	Scanner scn = new Scanner(System.in); 
		System.out.println("Insert Secret Key: ");
		secretK = scn.nextLine();
		System.out.println("Insert Access Key: ");
		accessK = scn.nextLine();
		System.out.println("AWS Keys Vertified");
		
		FileWriter fr = new FileWriter(file);
		br = new BufferedWriter(fr);
		
		java.util.Date date= new java.util.Date();
		 
		 
		br.write("#Insert your AWS Credentials from http://aws.amazon.com/security-credentials");
		br.newLine();
		br.write("#" + new Timestamp(date.getTime()));
		br.newLine();
		br.write("secretKey=" + secretK);
		br.newLine();
		br.write("accessKey=" + accessK);
		br.close();
		System.out.println();
 
    }
    
    private static void createBucket(){
    	
    	String bucketName;
    	
    	System.out.println("Create Bucket: ");
        System.out.println("--------------------------\n");
		
		Scanner scn = new Scanner(System.in); 
		System.out.println("Choose a Bucket Name: ");
		bucketName = scn.nextLine();
		String bucketNameOriginal = bucketName + UUID.randomUUID();
		
        System.out.println("Creating bucket " + bucketNameOriginal + "\n");
        s3.createBucket(bucketNameOriginal);
        
    }
    
    private static void listBuckets(){
    	
    	System.out.println("List Buckets: ");
        System.out.println("--------------------------");
        
    	int count = 1;
    	for (Bucket bucket : s3.listBuckets()) {
            System.out.println(count + ") - " + bucket.getName());
            count++;
        }
        System.out.println();
    }
    
    private static void createObject() throws Exception{
    	
    	System.out.println("Create Object: ");
        System.out.println("--------------------------");
        
        
        System.out.println("Please choose a bucket: ");
        
    	int count = 1;
    	for (Bucket bucket : s3.listBuckets()) {
            System.out.println(count + ") - " + bucket.getName());
            count++;
        }
    	
    	Scanner scn = new Scanner(System.in); 
		int optionB = scn.nextInt();
		
		if(optionB > count - 1 || optionB < 1){
			System.out.println("Please select a proper bucket");
			createObject();
		}
		else{
	    	count = 1;
	    	String currentBucket = null;
	    	for (Bucket bucket : s3.listBuckets()) {
	            if(count == optionB){
	            	currentBucket = bucket.getName();
	            }
	            count++;
	        }
	    	System.out.println("Enter your first name: ");
	    	scn = new Scanner(System.in); 
	    	fName = scn.nextLine();
	    	System.out.println("Last Name: ");
	    	lName = scn.nextLine();
	    	System.out.println("Phone Number: xxx-xxx-xxxx");
	    	pNumber = scn.nextLine();
			System.out.println("Uploading a new object to S3 from a file\n");
			key = "mycredentials-" + fName;
	        s3.putObject(new PutObjectRequest(currentBucket, key, createCredentialsFile(fName, lName, pNumber)));
		}
        System.out.println();
    }
    
    private static File createCredentialsFile(String fName, String lName, String pNumber) throws IOException {
        File file = File.createTempFile("mycredentials", ".html");
        file.deleteOnExit();

        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
        writer.write("<h1>My Credentials</h1>");
        writer.write("<table border='1'>");
        writer.write("<tr>");
        writer.write("<td><strong>First Name</strong></td>");
        writer.write("<td><strong>Last Name</strong></td>");
        writer.write("<td><strong>Phone Number</strong></td>");
        writer.write("</tr>");
        writer.write("<tr>");
        writer.write("<td>" + fName + "</td>");
        writer.write("<td>" + lName + "</td>");
        writer.write("<td>" + pNumber + "</td>");
        writer.close();
        

        return file;
    }
    
    private static void editObject() throws Exception{
    	
    	System.out.println("Edit Object ");
        System.out.println("--------------------------");
        
        System.out.println("Please choose a bucket: ");
        
    	int count = 1;
    	for (Bucket bucket : s3.listBuckets()) {
            System.out.println(count + ") - " + bucket.getName());
            count++;
        }
    	
    	Scanner scn = new Scanner(System.in); 
		int optionB = scn.nextInt();
		
		if(optionB > count - 1 || optionB < 1){
			System.out.println("Please select a proper bucket");
			editObject();
		}
		else{
	    	count = 1;
	    	String currentBucket = null;
	    	for (Bucket bucket : s3.listBuckets()) {
	            if(count == optionB){
	            	currentBucket = bucket.getName();
	            }
	            count++;
	        }
	    	
	        System.out.println("Please choose an Object: ");
	        
	        ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
            .withBucketName(currentBucket)
            .withPrefix("my"));
	    	 count = 1;
	    	 for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
	    		 System.out.println(count + ") - " + objectSummary.getKey() + "  " +
                           "(size = " + objectSummary.getSize() + ")");
	    		 count++;
	    	 }
	    	
	    	scn = new Scanner(System.in); 
			optionB = scn.nextInt();
			
			if(optionB > count - 1 || optionB < 1){
				System.out.println("Please select a proper Object");
				editObject();
			}
			else{
		    	count = 1;
		    	String currentObject = null;
		    	for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {		            if(count == optionB){
		            	currentObject = objectSummary.getKey();
		            }
		            count++;
		        }
		    	System.out.println("Enter your first name: ");
		    	scn = new Scanner(System.in); 
		    	fName = scn.nextLine();
		    	System.out.println("Last Name: ");
		    	lName = scn.nextLine();
		    	System.out.println("Phone Number: xxx-xxx-xxxx");
		    	pNumber = scn.nextLine();
				System.out.println("Editing the current Object");
				key = currentObject;
		        s3.putObject(new PutObjectRequest(currentBucket, key, createCredentialsFile(fName, lName, pNumber)));
			
			}
			
		}
        System.out.println();
    }
    
    private static void deleteObject() throws Exception{
    	
    	System.out.println("Delete Object ");
        System.out.println("--------------------------");
        
        System.out.println("Please choose a bucket: ");
        
    	int count = 1;
    	for (Bucket bucket : s3.listBuckets()) {
            System.out.println(count + ") - " + bucket.getName());
            count++;
        }
    	
    	Scanner scn = new Scanner(System.in); 
		int optionB = scn.nextInt();
		
		if(optionB > count - 1 || optionB < 1){
			System.out.println("Please select a proper bucket");
			editObject();
		}
		else{
	    	count = 1;
	    	String currentBucket = null;
	    	for (Bucket bucket : s3.listBuckets()) {
	            if(count == optionB){
	            	currentBucket = bucket.getName();
	            }
	            count++;
	        }
	    	
	        System.out.println("Please choose an Object: ");
	        System.err.println("BEWARE: This will erase the object");
	        
	        ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
            .withBucketName(currentBucket)
            .withPrefix("my"));
	    	 count = 1;
	    	 for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
	    		 System.out.println(count + ") - " + objectSummary.getKey() + "  " +
                           "(size = " + objectSummary.getSize() + ")");
	    		 count++;
	    	 }
	    	
	    	scn = new Scanner(System.in); 
			optionB = scn.nextInt();
			
			if(optionB > count - 1 || optionB < 1){
				System.out.println("Please select a proper Object");
				editObject();
			}
			else{
		    	count = 1;
		    	String currentObject = null;
		    	for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {		            if(count == optionB){
		            	currentObject = objectSummary.getKey();
		            }
		            count++;
		        }
		    	
	            System.out.println("Deleting an object\n");
	            s3.deleteObject(currentBucket, currentObject);
	            
			}
			
		}
        System.out.println();
    }
    
    private static void deleteBucket(){
    	
    	System.out.println("Delete Buckets: ");
        System.out.println("--------------------------");
        
        System.out.println("Please choose a bucket to delete (must be empty): ");
        System.err.println("BEWARE: This will erase the bucket");
        
    	int count = 1;
    	for (Bucket bucket : s3.listBuckets()) {
            System.out.println(count + ") - " + bucket.getName());
            count++;
        }
    	
    	Scanner scn = new Scanner(System.in); 
		int optionB = scn.nextInt();
		
		if(optionB > count - 1 || optionB < 1){
			System.out.println("Please select a proper bucket");
			deleteBucket();
		}
		else{
	    	count = 1;
	    	String currentBucket = null;
	    	for (Bucket bucket : s3.listBuckets()) {
	            if(count == optionB){
	            	currentBucket = bucket.getName();
	            }
	            count++;
	        }
	    	System.out.println("Deleting a bucket\n");
            try {
				s3.deleteBucket(currentBucket);
			} catch (AmazonServiceException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.err.println("Cannot Delete. Contents must be empty");
			} catch (AmazonClientException e) {
				// TODO Auto-generated catch block
			}
		}
        System.out.println();
    }
}
