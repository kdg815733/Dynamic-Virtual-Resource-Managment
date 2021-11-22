

public class MyAWSKit 
{
    static AmazonEC2 ec2;
    
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
}

}
