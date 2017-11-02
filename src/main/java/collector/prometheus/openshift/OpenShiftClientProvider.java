package collector.prometheus.openshift;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;
import io.fabric8.openshift.client.OpenShiftConfig;
import io.fabric8.openshift.client.OpenShiftConfigBuilder;


public class OpenShiftClientProvider {

	private static final Logger logger = LoggerFactory.getLogger(OpenShiftClientProvider.class);
	
	private static final String OCP_MASTER_URL = "OCP_MASTER_URL";
	private static final String OCP_USERNAME = "OCP_USERNAME";
	private static final String OCP_PASSWORD = "OCP_PASSWORD";
	private static final String OCP_TOKEN = "OCP_TOKEN";
	private static final String OCP_TOKEN_PATH = "OCP_TOKEN_PATH";
	private static final String OCP_CA_PATH = "OCP_CA_PATH";
	private static final String OCP_HTTP_PROXY = "OCP_HTTP_PROXY";
	private static final String OCP_HTTPS_PROXY = "OCP_HTTPS_PROXY";
	private DefaultOpenShiftClient osClient;

	
	private OpenShiftClientProvider()
	{

		OpenShiftConfig openshiftConfig;
		String masterUrl = getEnvVar(OCP_MASTER_URL);
		String envToken = getEnvVar(OCP_TOKEN);
		String envTokenPath = getEnvVar(OCP_TOKEN_PATH);
		String envCAPath = getEnvVar(OCP_CA_PATH);		
		String envHttpProxy = getEnvVar(OCP_HTTP_PROXY);		
		String envHttpsProxy = getEnvVar(OCP_HTTPS_PROXY);		
		

		// Add master URL to builder
		if (masterUrl == null){
			masterUrl="https://kubernetes.default.svc.cluster.local";
		}		
		OpenShiftConfigBuilder openshiftConfigBuilder = new OpenShiftConfigBuilder()
				.withMasterUrl(masterUrl);
		
		logger.debug("Connection to OpenShift "+masterUrl);
		
		
		// Add token or username password to builder
		if (envToken != null) {
			openshiftConfigBuilder.withOauthToken(envToken);
		}			
		else if (envTokenPath != null ) {
			String token = readFirstLineFromFile(envTokenPath);
			openshiftConfigBuilder.withOauthToken(token);
		}						
		else {
			String username = getEnvVar(OCP_USERNAME);
			String password = getEnvVar(OCP_PASSWORD);
			if (username != null && password != null) {
				openshiftConfigBuilder.withUsername(username)
						.withPassword(password);
			}
			else {
				throw new IllegalArgumentException("Authentication to OpenShift cluster require env var "+OCP_TOKEN+" or "+OCP_TOKEN_PATH+" or ("+OCP_USERNAME+" and "+OCP_PASSWORD+").");
			}
		}
		
		// Add CA to builder if provided
		if (envCAPath !=  null){
			if (new File(envCAPath).isFile() ){
				openshiftConfigBuilder.withCaCertFile(envCAPath);	
			}else {
				throw new IllegalArgumentException(envCAPath+" is not a valid file path for CA");
			}
		}
		// Add proxy settings to builder if provided
		if (envHttpProxy !=  null){
			openshiftConfigBuilder.withHttpProxy(envHttpProxy);
		}		
		if (envHttpsProxy !=  null){
			openshiftConfigBuilder.withHttpProxy(envHttpsProxy);
		}		
		
		// Build openshfit config
		openshiftConfig =  openshiftConfigBuilder
				.build();			

		osClient = new DefaultOpenShiftClient(openshiftConfig);

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
			logger.info(envVarName+" = "+ value);
		} else {
			logger.info(envVarName + " is not assigned");
		}
		return value;
	}


	public OpenShiftClient getOpenShiftClient() {
		logger.debug("Getting  openshiftClient");
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