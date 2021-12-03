package aws;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.Region;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.RebootInstancesResult;
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
        
        public void startInstance(String instanceId)
        {
            StartInstancesRequest request = new StartInstancesRequest()
                .withInstanceIds(instanceId);

            ec2.startInstances(request);   
        }

        public void stoptInstance(String instanceId)
        {
            StopInstancesRequest request = new StopInstancesRequest()
                .withInstanceIds(instanceId);

            ec2.stopInstances(request);

        }

        public void rebootInstance(String instanceId)
        {
            RebootInstancesRequest request = new RebootInstancesRequest()
                .withInstanceIds(instanceId);

            RebootInstancesResult response = ec2.rebootInstances(request);

        }

        public void availableZones()
        {
            DescribeAvailabilityZonesResult zones_response = ec2.describeAvailabilityZones();
        
            for(AvailabilityZone zone : zones_response.getAvailabilityZones()) 
            {
                System.out.printf(
                    "Found availability zone %s " +
                    "with status %s " +
                    "in region %s",
                    zone.getZoneName(),
                    zone.getState(),
                    zone.getRegionName());
            }
        }

        public void createInstance(String imageId)
        {
            RunInstancesRequest request = new RunInstancesRequest()
                .withImageId(imageId)
                .withInstanceType(InstanceType.T2Micro)
                .withMaxCount(1)
                .withMinCount(1);
            
            RunInstancesResult response = ec2.runInstances(request);
            String instanceId = response.getReservation().getInstances().get(0).getInstanceId();
            System.out.println(instanceId);
            
        }

        public void availableRegions()
        {
            DescribeRegionsResult regions_response = ec2.describeRegions();

            for(Region region : regions_response.getRegions()) 
            {
                System.out.printf(
                    "Found region %s " +
                    "with endpoint %s",
                    region.getRegionName(),
                    region.getEndpoint());
            }
        }

        public void listImages()
        {

        }


    }