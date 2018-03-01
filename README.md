
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

# Paramters

IT is possible to define connection information and proxy parameters by using environement var.

| Parameters | Description | Default Value |     
| ---------- | ----------- | ------------- |
| OCP_MASTER_URL | API master URL | https://kubernetes.default.svc.cluster.local  |
| OCP_USERNAME | Username to authenticate to master API in case of login password authentication  | - |
| OCP_PASSWORD | Password to authenticate to  master API in case of login password authentication | - |
| OCP_TOKEN | Token string value to authenticate to  master API  with token | - |
| OCP_TOKEN_PATH | Path to a file containing token to authenticate to  master API   | - |
| OCP_CA_PATH | Path to a CA certificate to authenticate to master API | - |
| OCP_HTTP_PROXY | Http proxy  | - |
| OCP_HTTPS_PROXY | Https proxy | - |

