package aws;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.model.Reservation;

public class MyAWSKit 
    {
        static AmazonEC2 ec2;
        MyAWSKit() throws Exception{
            init();
        }
    
        private static void init() throws Exception 
        {
        /*
        * The ProfileCredentialsProvider will return your [default]
        * credential profile by reading from the credentials file located at
        * (~/.aws/credentials).
        */
           ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
           try 
            {
                credentialsProvider.getCredentials();
            } catch (Exception e) 
            {
            throw new AmazonClientException(
                "Cannot load the credentials from the credential profiles file. " +
                "Please make sure that your credentials file is at the correct " +
                "location (~/.aws/credentials), and is in valid format.",
                e);
            }
            ec2 = AmazonEC2ClientBuilder.standard()
            .withCredentials(credentialsProvider)
            .withRegion("us-east-2") /* check the region at AWS console */
            .build();
        }
        public void listInstances()
        {
            System.out.println("Listing instances....");
            boolean done = false;

            DescribeInstancesRequest request = new DescribeInstancesRequest();

            while(!done)
            {
                DescribeInstancesResult response = ec2.describeInstances(request);
                for(Reservation reservation : response.getReservations()) 
                {
                    for(Instance instance : reservation.getInstances()) 
                    {
                        System.out.printf(
                        "[id] %s, " +
                        "[AMI] %s, " +
                        "[type] %s, " +
                        "[state] %10s, " +
                        "[monitoring state] %s",
                        instance.getInstanceId(),
                        instance.getImageId(),
                        instance.getInstanceType(),
                        instance.getState().getName(),
                        instance.getMonitoring().getState());
                    }
                    System.out.println();
                }
                request.setNextToken(response.getNextToken());
                if(response.getNextToken() == null) 
                {
                    done = true;
                }
            }
        }
        
        public void startInstance()
        {

        }

        public void stoptInstance()
        {
            
        }

        public void rebootInstance()
        {
            
        }

        public void availableZones()
        {

        }

        public void createInstance()
        {
            
        }

        public void availableRegions()
        {
            
        }

        public void listImages()
        {

        }


    }