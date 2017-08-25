package collector.prometheus.openshift.example;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.prometheus.client.spring.boot.EnablePrometheusEndpoint;
 
@SpringBootApplication
@EnablePrometheusEndpoint
public class PrometheusOpenShiftCollectorApplication {
 
    public static void main(String[] args) {
        SpringApplication.run(PrometheusOpenShiftCollectorApplication.class, args);
    }      
}