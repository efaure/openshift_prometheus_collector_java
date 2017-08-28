package collector.prometheus.openshift;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import io.fabric8.openshift.api.model.DeploymentConfig;
import io.fabric8.openshift.client.OpenShiftClient;
import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;

@Component
class DeploymentConfigurationCollector extends Collector {

	public List<MetricFamilySamples> collect() {

		OpenShiftClient osClient = OpenShiftClientProvider.getInstance().getOpenShiftClient();

		List<MetricFamilySamples> mfs = new ArrayList<MetricFamilySamples>();

		GaugeMetricFamily specReplicasNumber = new GaugeMetricFamily("openshift_deployment_configuration_spec_replicas",
				"number of replicas specified in DC", Arrays.asList("namespace", "deployment_configuration_name"));

		GaugeMetricFamily statusReplicas = new GaugeMetricFamily("openshift_deployment_configuration_status_replicas",
				"deploymentConfig status replicas", Arrays.asList("namespace", "deployment_configuration_name"));
		
		GaugeMetricFamily statusreadyReplicas = new GaugeMetricFamily("openshift_deployment_configuration_status_readyReplicas",
				"deploymentConfig status readyReplicas", Arrays.asList("namespace", "deployment_configuration_name"));

		GaugeMetricFamily statusUnavailableReplicas = new GaugeMetricFamily("openshift_deployment_configuration_status_unavailableReplicas",
				"deploymentConfig status unavailableReplicas", Arrays.asList("namespace", "deployment_configuration_name"));

		List<DeploymentConfig> deploymentConfigList =  osClient.deploymentConfigs().inAnyNamespace().list().getItems();

		for (DeploymentConfig deploymentConfig : deploymentConfigList) {
			String deploymentConfigName = deploymentConfig.getMetadata().getName();
			String namespaceName = deploymentConfig.getMetadata().getNamespace();


			specReplicasNumber.addMetric(Arrays.asList(namespaceName, deploymentConfigName), 
					deploymentConfig.getSpec().getReplicas());
			
			Object statusReplicasValue = deploymentConfig.getStatus().getAdditionalProperties().get("replicas");
			Object statusReadyReplicasValue = deploymentConfig.getStatus().getAdditionalProperties().get("readyReplicas");
			Object statusUnavailableValue = deploymentConfig.getStatus().getAdditionalProperties().get("unavailableReplicas");

			if (statusReplicasValue !=  null ) statusReplicas.addMetric(Arrays.asList(namespaceName, deploymentConfigName), 
					(Integer)statusReplicasValue);

			if (statusReadyReplicasValue !=  null ) statusreadyReplicas.addMetric(Arrays.asList(namespaceName, deploymentConfigName), 
					(Integer)(statusReadyReplicasValue));

			if (statusUnavailableValue !=  null ) statusUnavailableReplicas.addMetric(Arrays.asList(namespaceName, deploymentConfigName), 
					(Integer)statusUnavailableValue);


		}				


		mfs.add(specReplicasNumber);
		mfs.add(statusReplicas);
		mfs.add(statusreadyReplicas);
		mfs.add(statusUnavailableReplicas);

		return mfs;

	}
}
