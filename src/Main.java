import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancing.model.Instance;
import com.amazonaws.services.elasticloadbalancing.model.Listener;
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerRequest;

public class Main {
	
	public static String configFile = "config.properties";
	
	public static String accessKeyConfigName = "AWSAccessKey";
	public static String secretKeyConfigName = "AWSSecretKey";
	public static String httpProxyConfigName = "HttpProxy";
	
	public static String accessKey = null;	
	public static String secretKey = null;
	public static String httpProxy = null;
	
	// Method to read configs file AWS access key, secret key, HTTP proxy etc.
	public static void readConfigurations() throws IOException
	{
		Properties properties = new Properties();
		
		InputStream input = null;
		
		try
		{
			input = new FileInputStream(configFile);
			
			properties.load(input);

			accessKey = properties.getProperty(accessKeyConfigName);
			secretKey = properties.getProperty(secretKeyConfigName);
			httpProxy = properties.getProperty(httpProxyConfigName);
		}
		catch(IOException e)
		{	
			throw e;
		}
		finally
		{
			input.close();
		}
	}
	
	public static void main(String[] args) 
	{	
		try
		{	
			readConfigurations();
			
			BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
			
			ClientConfiguration clientConfiguration = new ClientConfiguration();
			
			if(httpProxy != null && !httpProxy.equals(""))
				clientConfiguration.setProxyHost(httpProxy);
			
			clientConfiguration.setProtocol(Protocol.HTTP);
			clientConfiguration.setProxyPort(8080);
			
			LoadBalancer loadBalancer = new LoadBalancer(awsCredentials, clientConfiguration);
			
			String loadBalancerName = "firstLB";
			int instancePort = 80;
			String instanceProtocol = "HTTP";
			int loadBalancerPort = 80; 
			String protocol = "HTTP";
			String availabilityZone = "us-east-1a";
			
			loadBalancer.createLoadBalancer(loadBalancerName, instancePort, instanceProtocol, loadBalancerPort, protocol, availabilityZone);
			
			// For now I have hard coded instance ID.
			// We can use listRunningInstances() method of EC2 class to get the list of all running instances and choose
			Instance instance = new Instance();
			instance.setInstanceId("i-d27b0022");
			
			List<Instance> instances = new ArrayList<>();
			instances.add(instance);
			
			loadBalancer.registerInstanceToLoadBalancer(loadBalancerName, instances);
			
			// Comment the following method for the first run to see instances attached to load balancers
			loadBalancer.deregisterInstanceToLoadBalancer(loadBalancerName, instances);			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
