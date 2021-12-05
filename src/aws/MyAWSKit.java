package aws;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.UnmonitorInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.Image;
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
import com.amazonaws.services.costexplorer.model.DateInterval;
import com.amazonaws.services.costexplorer.model.Dimension;
import com.amazonaws.services.costexplorer.model.DimensionValues;
import com.amazonaws.services.costexplorer.model.Expression;
import com.amazonaws.services.costexplorer.model.GetCostAndUsageRequest;
import com.amazonaws.services.costexplorer.model.GetCostAndUsageResult;
import com.amazonaws.services.costexplorer.model.Granularity;
import com.amazonaws.services.costexplorer.model.GroupDefinition;
import com.amazonaws.services.dlm.AmazonDLMAsyncClientBuilder;
import com.amazonaws.services.ec2.model.MonitorInstancesRequest;
import com.amazonaws.services.costexplorer.AWSCostExplorer;
import com.amazonaws.services.costexplorer.AWSCostExplorerClientBuilder;
import com.amazonaws.auth.BasicSessionCredentials;


public class MyAWSKit 
    {
        static AmazonEC2 ec2;
        static AWSCostExplorer ce;
        static ProfileCredentialsProvider credentialsProvider;
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
           credentialsProvider = new ProfileCredentialsProvider();
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
            ce = AWSCostExplorerClientBuilder.standard()
            .withCredentials(credentialsProvider)
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

            ec2.rebootInstances(request);
        }

        public void availableZones()
        {
            int count = 0;
            DescribeAvailabilityZonesResult zones_response = ec2.describeAvailabilityZones();
        
            for(AvailabilityZone zone : zones_response.getAvailabilityZones()) 
            {
                System.out.printf(
                    "[id] %s, "+
                    "[region]\t%s, "+
                    "[zone]\t%s\n",
                    zone.getZoneId(),
                    zone.getRegionName(),
                    zone.getZoneName());
                count++;
            }
            System.out.printf("You have access to %d Availability Zones.\n",count);
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
            System.out.println("Available regions ....");
            DescribeRegionsResult regions_response = ec2.describeRegions();
            for(Region region : regions_response.getRegions()) 
            {
                System.out.printf(
                    "[region] %15s, " +
                    "[endpoint] %s \n",
                    region.getRegionName(),
                    region.getEndpoint());
            }
        }

        public void listImages()
        {
            System.out.println("Listing images ....");
            DescribeImagesRequest request = new DescribeImagesRequest()
                .withOwners("self");
            DescribeImagesResult response = ec2.describeImages(request);

            for(Image image : response.getImages())
            {
                System.out.printf(
                    "[ImageID] %s, "+
                    "[Name] %s, "+
                    "[Owner] %s\n",
                    image.getImageId(),
                    image.getName(),
                    image.getOwnerId());
            }

        }

        public void getCost()
        {
            Expression expression = new Expression();
            DimensionValues dimensions = new DimensionValues();
            dimensions.withKey(Dimension.SERVICE);
            dimensions.withValues("EC2 - Other");
            expression.withDimensions(dimensions);
            GetCostAndUsageRequest request = new GetCostAndUsageRequest()
                .withTimePeriod(new DateInterval().withStart("2021-11-01").withEnd("2021-12-05"))
                .withGranularity(Granularity.MONTHLY)
                .withMetrics("BLENDED_COST")
                .withFilter(expression);

            GetCostAndUsageResult result = ce.getCostAndUsage(request); 
            System.out.println(result);
        }

        public void monitorInstances(String instanceId)
        {
            MonitorInstancesRequest request = new MonitorInstancesRequest()
                .withInstanceIds(instanceId);

            ec2.monitorInstances(request);
        }
        public void unmonitorInstances(String instanceId)
        {
            UnmonitorInstancesRequest request = new UnmonitorInstancesRequest()
                .withInstanceIds(instanceId);

            ec2.unmonitorInstances(request);
        }
    }