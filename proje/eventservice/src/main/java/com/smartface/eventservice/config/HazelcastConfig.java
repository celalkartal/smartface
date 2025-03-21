//package com.smartface.eventservice.config;
//
//import com.hazelcast.config.Config;
//import com.hazelcast.config.JoinConfig;
//import com.hazelcast.config.NetworkConfig;
//import com.hazelcast.config.QueueConfig;
//import com.hazelcast.core.Hazelcast;
//import com.hazelcast.core.HazelcastInstance;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//
////@Configuration
//public class HazelcastConfig {
//
//    //@Bean
//    public HazelcastInstance hazelcastInstance() {
//        // Genel Hazelcast ayarları
//        Config config = new Config();
//        config.setClusterName("hazelcast-cluster");
//
//        // Ağ ayarları
//        NetworkConfig networkConfig = config.getNetworkConfig();
//        networkConfig.setPort(5701); // Varsayılan Hazelcast portu
//        networkConfig.setPortAutoIncrement(true); // Port çakışmalarını önlemek için otomatik artırma
//
//        // Kubernetes entegrasyonu için Join ayarları
//        JoinConfig joinConfig = networkConfig.getJoin();
//        joinConfig.getMulticastConfig().setEnabled(false); // Multicast devre dışı
//        joinConfig.getKubernetesConfig()
//            .setEnabled(true) // Kubernetes entegrasyonu aktif
//            .setProperty("namespace", "default") // Kubernetes namespace
//            .setProperty("service-name", "hazelcast-service"); // Hazelcast headless service adı
//
//        // Event kuyruğu ayarları
//        QueueConfig queueConfig = config.getQueueConfig("keycloak-event-queue");
//        queueConfig.setMaxSize(Integer.MAX_VALUE) // Maksimum kuyruk boyutu
//                   .setBackupCount(1) // 1 yedek kopya
//                   .setStatisticsEnabled(false);
//
//        // Hazelcast instance oluşturma
//        return Hazelcast.newHazelcastInstance(config);
//    }
//}