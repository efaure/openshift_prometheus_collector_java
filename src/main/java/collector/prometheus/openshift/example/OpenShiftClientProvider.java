package collector.prometheus.openshift.example;

import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;
import io.fabric8.openshift.client.OpenShiftConfig;
import io.fabric8.openshift.client.OpenShiftConfigBuilder;


public class OpenShiftClientProvider {

	private static final String OCP_MASTER_URL = "OCP_MASTER_URL";
	private static final String OCP_USERNAME = "OCP_USERNAME";
	private static final String OCP_PASSWORD = "OCP_PASSWORD";
	private static final String OCP_TOKEN = "OCP_TOKEN";
	private DefaultOpenShiftClient osClient;

	
	private OpenShiftClientProvider()
	{

		OpenShiftConfig openshiftConfig;
		String token = getEnvVar(OCP_TOKEN);

		String masterUrl = getEnvVar(OCP_MASTER_URL);
		if (masterUrl == null){
			throw new IllegalArgumentException("Connectin to OpenShift cluster require env var "+OCP_MASTER_URL);
		}
		
		if (token != null){
			openshiftConfig =  new OpenShiftConfigBuilder()
					.withMasterUrl(masterUrl)
					.withOauthToken(token)
					.build();			
		}
		else {
			String username = getEnvVar(OCP_USERNAME);
			String password = getEnvVar(OCP_PASSWORD);
			if (username != null && password != null) {

				openshiftConfig =  new OpenShiftConfigBuilder()
						.withMasterUrl(masterUrl)
						.withUsername(username)
						.withPassword(password)
						.build();
			}
			else {
				throw new IllegalArgumentException("Authentication to OpenShift cluster require env var "+OCP_TOKEN+" or "+OCP_USERNAME+" and "+OCP_PASSWORD);
			}
		}

		osClient = new DefaultOpenShiftClient(openshiftConfig);
		System.out.println("\n namespace "+osClient.getNamespace());
	}
 
	private static OpenShiftClientProvider OCPCLIENTSINGLETON_INSTANCE = null;
 
	public static OpenShiftClientProvider getInstance()
	{	
		if (OCPCLIENTSINGLETON_INSTANCE == null)
		{ 	
			synchronized(OpenShiftClientProvider.class)
			{
				if (OCPCLIENTSINGLETON_INSTANCE == null)
				{	OCPCLIENTSINGLETON_INSTANCE = new OpenShiftClientProvider();
				}
			}
		}
		return OCPCLIENTSINGLETON_INSTANCE;
	}
	
	private String getEnvVar(String envVarName){
		String value = System.getenv(envVarName);
		if (value != null) {
			System.out.format("%s=%s%n",
					envVarName, value);
		} else {
			System.out.format("%s is"
					+ " not assigned.%n", envVarName);
		}
		return value;
	}


	public OpenShiftClient getOpenShiftClient() {
		System.out.println(" \n Getting  openshiftClient");
		return osClient;
	}



}