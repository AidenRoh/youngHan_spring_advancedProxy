package hello.proxy.config.v1_proxy;

import hello.proxy.app.v1.OrderControllerV1;
import hello.proxy.app.v1.OrderControllerV1Impl;
import hello.proxy.app.v1.OrderRepositoryV1;
import hello.proxy.app.v1.OrderRepositoryV1Impl;
import hello.proxy.app.v1.OrderServiceV1;
import hello.proxy.app.v1.OrderServiceV1Impl;
import hello.proxy.config.v1_proxy.interface_proxy.OrderControllerInterfaceProxy;
import hello.proxy.config.v1_proxy.interface_proxy.OrderRepositoryInterfaceProxy;
import hello.proxy.config.v1_proxy.interface_proxy.OrderServiceInterfaceProxy;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InterfaceProxyConfig {

	@Bean
	public OrderControllerV1 orderController(LogTrace logTrace) {
		OrderControllerV1Impl orderControllerImpl = new OrderControllerV1Impl(orderService(logTrace));
		return new OrderControllerInterfaceProxy(orderControllerImpl, logTrace);
	}

	@Bean
	public OrderServiceV1 orderService(LogTrace logtrace) {
		OrderServiceV1Impl orderServiceImpl = new OrderServiceV1Impl(orderRepository(logtrace));
		return new OrderServiceInterfaceProxy(orderServiceImpl, logtrace);
	}

	@Bean
	public OrderRepositoryV1 orderRepository(LogTrace logTrace) {
		OrderRepositoryV1Impl orderRepositoryImpl = new OrderRepositoryV1Impl();
		return new OrderRepositoryInterfaceProxy(orderRepositoryImpl, logTrace);
	}
}
