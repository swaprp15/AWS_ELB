import java.util.List;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;


public class EC2 
{
	BasicAWSCredentials awsCredentials;
	ClientConfiguration clientConfiguration;
	
	public EC2(BasicAWSCredentials awsCredentials, ClientConfiguration clientConfiguration)
	{
		this.awsCredentials = awsCredentials;
		this.clientConfiguration = clientConfiguration;
	}
	
	/*
	 * List all running instances
	 */
	public void listRunningInstances()
	{
		AmazonEC2 ec2 = new AmazonEC2Client(awsCredentials, clientConfiguration);
		
		DescribeInstancesRequest req = new DescribeInstancesRequest(); 
		
		List<Reservation> result = ec2.describeInstances(req).getReservations();
		
		for(Reservation reservation : result)
        {
            for(Instance runningInstance : reservation.getInstances())
            {
                System.out.println(runningInstance.getInstanceId());
                System.out.println(runningInstance.getInstanceType());
            }
        }
	}

}
