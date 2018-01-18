
# OpenShift cluster prometheus exporter

Prometheus exporter written to collect metrics on OpenShift objects.

This exporter is intended to be used as a complement of kube-state-metrics. 

Kube-state-metrics (https://github.com/kubernetes/kube-state-metrics.git) is a Prometheus exporter which collect data on all Kubernetes object.


# Job metrics

| Metric name| Metric type | Labels/tags |
| ---------- | ----------- | ----------- |
| openshift_deployment_configuration_spec_replicas | Gauge | `deployment_configuration_name`, `namespace` |
| openshift_deployment_configuration_status_readyReplicas | Gauge | `deployment_configuration_name`, `namespace` |
| openshift_deployment_configuration_status_replicas | Gauge | `deployment_configuration_name`, `namespace` |
| openshift_deployment_configuration_status_unavailableReplicas | Gauge | `deployment_configuration_name`, `namespace` |


