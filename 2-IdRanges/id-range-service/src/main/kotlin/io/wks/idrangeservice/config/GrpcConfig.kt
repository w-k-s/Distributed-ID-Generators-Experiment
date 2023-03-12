package io.wks.idrangeservice.config

import net.devh.boot.grpc.common.autoconfigure.GrpcCommonCodecAutoConfiguration
import net.devh.boot.grpc.common.autoconfigure.GrpcCommonTraceAutoConfiguration
import net.devh.boot.grpc.server.autoconfigure.*
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.context.annotation.Configuration

// https://github.com/yidongnan/grpc-spring-boot-starter/pull/775#issuecomment-1329023335
@Configuration
@ImportAutoConfiguration(
    GrpcCommonCodecAutoConfiguration::class,
    GrpcCommonTraceAutoConfiguration::class,
    GrpcAdviceAutoConfiguration::class,
    GrpcHealthServiceAutoConfiguration::class,
    GrpcMetadataConsulConfiguration::class,
    GrpcMetadataEurekaConfiguration::class,
    GrpcMetadataNacosConfiguration::class,
    GrpcMetadataZookeeperConfiguration::class,
    GrpcReflectionServiceAutoConfiguration::class,
    GrpcServerAutoConfiguration::class,
    GrpcServerFactoryAutoConfiguration::class,
    GrpcServerMetricAutoConfiguration::class,
    GrpcServerSecurityAutoConfiguration::class,
    GrpcServerTraceAutoConfiguration::class
)
class GrpcConfig