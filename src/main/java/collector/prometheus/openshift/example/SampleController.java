package collector.prometheus.openshift.example;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {
	
	static final DeploymentConfigurationCollector deploymentConfigurationCollector = new DeploymentConfigurationCollector().register();
	
}