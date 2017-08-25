package collector.prometheus.openshift.example;

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

		GaugeMetricFamily labeledGauge = new GaugeMetricFamily("openshift_deployment_configuration_spec_replicas",
				"number of replicas in DC", Arrays.asList("namespace", "deployment_configuration_name"));


		List<DeploymentConfig> deploymentConfigList =  osClient.deploymentConfigs().inAnyNamespace().list().getItems();

		for (DeploymentConfig deploymentConfig : deploymentConfigList) {
			String deploymentConfigName = deploymentConfig.getMetadata().getName();
			String namespaceName = deploymentConfig.getMetadata().getNamespace();

			labeledGauge.addMetric(Arrays.asList(namespaceName, deploymentConfigName), deploymentConfig.getSpec().getReplicas());
		}				

		mfs.add(labeledGauge);

		return mfs;

	}
}
