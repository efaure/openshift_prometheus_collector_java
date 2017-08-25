package collector.prometheus.openshift;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class CollectorController {
	
	static final DeploymentConfigurationCollector deploymentConfigurationCollector = new DeploymentConfigurationCollector().register();
	
}