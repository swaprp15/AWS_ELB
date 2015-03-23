import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancing.model.DeregisterInstancesFromLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.DeregisterInstancesFromLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancing.model.Instance;
import com.amazonaws.services.elasticloadbalancing.model.Listener;
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerResult;
import com.amazonaws.services.opsworks.model.AttachElasticLoadBalancerRequest;


public class LoadBalancer 
{
	BasicAWSCredentials awsCredentials;
	ClientConfiguration clientConfiguration;
	
	public LoadBalancer(BasicAWSCredentials awsCredentials, ClientConfiguration clientConfiguration)
	{
		this.awsCredentials = awsCredentials;
		this.clientConfiguration = clientConfiguration;
	}

	/*
	 * Create Load Balancer with given properties
	 */
	public void createLoadBalancer(String name, int instancePort, String instanceProtocol, int loadBalancerPort, String protocol, String availabilityZone)
	{
		try
		{
			CreateLoadBalancerRequest createLoadBalancerRequest = new CreateLoadBalancerRequest(name);
			
			Listener listner = new Listener();
			listner.setInstancePort(instancePort);
			listner.setInstanceProtocol(instanceProtocol);
			listner.setLoadBalancerPort(loadBalancerPort);
			listner.setProtocol(protocol);
			
			List<Listener> listeners = new ArrayList<>();
			listeners.add(listner);
			
			createLoadBalancerRequest.setListeners(listeners);
			
			Set<String> availabilityZones = new HashSet<>();
			availabilityZones.add(availabilityZone);
			
			createLoadBalancerRequest.setAvailabilityZones(availabilityZones);
			
			AmazonElasticLoadBalancingClient lbClient = new AmazonElasticLoadBalancingClient(awsCredentials, clientConfiguration);
			
			CreateLoadBalancerResult clbResult = lbClient.createLoadBalancer(createLoadBalancerRequest);
			
			System.out.println("Load Balancer DSN Name : " + clbResult.getDNSName());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/*
	 * Register given instances with the load balancer
	 */
	public void registerInstanceToLoadBalancer(String loadBalancerName, List<Instance> instances)
	{
		try
		{
			RegisterInstancesWithLoadBalancerRequest registerInstancesWithLoadBalancerRequest = new RegisterInstancesWithLoadBalancerRequest(loadBalancerName, instances);
		
			AmazonElasticLoadBalancingClient lbClient = new AmazonElasticLoadBalancingClient(awsCredentials, clientConfiguration);
			
			RegisterInstancesWithLoadBalancerResult result = lbClient.registerInstancesWithLoadBalancer(registerInstancesWithLoadBalancerRequest);
			
			List<Instance> currentInstances = result.getInstances();
			
			System.out.println("All currently attached instances to load balancer " + loadBalancerName);
			
			for(Instance instance : currentInstances)
			{
				System.out.println(instance.getInstanceId());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/*
	 * Deregister instances from the load balancer
	 */
	public void deregisterInstanceToLoadBalancer(String loadBalancerName, List<Instance> instances)
	{
		try
		{
			DeregisterInstancesFromLoadBalancerRequest deregisterInstancesFromLoadBalancerRequest = new DeregisterInstancesFromLoadBalancerRequest(loadBalancerName, instances);
		
			AmazonElasticLoadBalancingClient lbClient = new AmazonElasticLoadBalancingClient(awsCredentials, clientConfiguration);
			
			DeregisterInstancesFromLoadBalancerResult result = lbClient.deregisterInstancesFromLoadBalancer(deregisterInstancesFromLoadBalancerRequest);
			
			List<Instance> currentInstances = result.getInstances();
			
			System.out.println("All currently attached instances to load balancer " + loadBalancerName);
			
			for(Instance instance : currentInstances)
			{
				System.out.println(instance.getInstanceId());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
}
