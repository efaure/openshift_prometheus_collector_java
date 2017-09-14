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

		List<String> labelsNameList = Arrays.asList("namespace", "deployment_configuration_name","monitoring-label");
		
		GaugeMetricFamily specReplicasNumber = new GaugeMetricFamily("openshift_deployment_configuration_spec_replicas",
				"number of replicas specified in DC", labelsNameList);

		GaugeMetricFamily statusReplicas = new GaugeMetricFamily("openshift_deployment_configuration_status_replicas",
				"deploymentConfig status replicas", labelsNameList);
		
		GaugeMetricFamily statusreadyReplicas = new GaugeMetricFamily("openshift_deployment_configuration_status_readyReplicas",
				"deploymentConfig status readyReplicas", labelsNameList);

		GaugeMetricFamily statusUnavailableReplicas = new GaugeMetricFamily("openshift_deployment_configuration_status_unavailableReplicas",
				"deploymentConfig status unavailableReplicas", labelsNameList);

		List<DeploymentConfig> deploymentConfigList =  osClient.deploymentConfigs().inAnyNamespace().list().getItems();

		for (DeploymentConfig deploymentConfig : deploymentConfigList) {
			
			String deploymentConfigName = deploymentConfig.getMetadata().getName();
			String namespaceName = deploymentConfig.getMetadata().getNamespace();
			String labels = deploymentConfig.getMetadata().getLabels().getOrDefault("monitoring", "none");

			List<String> labelsValueList = Arrays.asList(namespaceName, deploymentConfigName, labels);
			
			specReplicasNumber.addMetric(labelsValueList, 
					deploymentConfig.getSpec().getReplicas());
			
			Object statusReplicasValue = deploymentConfig.getStatus().getAdditionalProperties().get("replicas");
			Object statusReadyReplicasValue = deploymentConfig.getStatus().getAdditionalProperties().get("readyReplicas");
			Object statusUnavailableValue = deploymentConfig.getStatus().getAdditionalProperties().get("unavailableReplicas");

			if (statusReplicasValue !=  null ) statusReplicas.addMetric(labelsValueList, 
					(Integer)statusReplicasValue);

			if (statusReadyReplicasValue !=  null ) statusreadyReplicas.addMetric(labelsValueList, 
					(Integer)(statusReadyReplicasValue));

			if (statusUnavailableValue !=  null ) statusUnavailableReplicas.addMetric(labelsValueList, 
					(Integer)statusUnavailableValue);


		}				


		mfs.add(specReplicasNumber);
		mfs.add(statusReplicas);
		mfs.add(statusreadyReplicas);
		mfs.add(statusUnavailableReplicas);

		return mfs;

	}
}
