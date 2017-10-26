package collector.prometheus.openshift;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;
import io.fabric8.openshift.client.OpenShiftConfig;
import io.fabric8.openshift.client.OpenShiftConfigBuilder;


public class OpenShiftClientProvider {

	private static final String OCP_MASTER_URL = "OCP_MASTER_URL";
	private static final String OCP_USERNAME = "OCP_USERNAME";
	private static final String OCP_PASSWORD = "OCP_PASSWORD";
	private static final String OCP_TOKEN = "OCP_TOKEN";
	private static final String OCP_TOKEN_PATH = "OCP_TOKEN_PATH";
	private DefaultOpenShiftClient osClient;

	
	private OpenShiftClientProvider()
	{

		OpenShiftConfig openshiftConfig;
		String envToken = getEnvVar(OCP_TOKEN);
		String envTokenPath = getEnvVar(OCP_TOKEN_PATH);
		
		String masterUrl = getEnvVar(OCP_MASTER_URL);
		String token = null;
		
		if (masterUrl == null){
			masterUrl="https://kubernetes.default.svc.cluster.local";
		}
		
		if (envToken != null) token = envToken;
		else if (envTokenPath != null ) token = readFirstLineFromFile(envTokenPath);
		
		if (token != null){			
			System.out.println("token : "+token);
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
				throw new IllegalArgumentException("Authentication to OpenShift cluster require env var "+OCP_TOKEN+" or "+OCP_TOKEN_PATH+" or ("+OCP_USERNAME+" and "+OCP_PASSWORD+").");
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
	
    private String readFirstLineFromFile(String filepath) {
        String contents;
		try {
			contents = Files.lines(Paths.get(filepath)).findFirst().get();
		} catch (IOException e) {
			throw new IllegalArgumentException("Impossible to read content from file "+filepath);
		}
        return contents;
    }



}