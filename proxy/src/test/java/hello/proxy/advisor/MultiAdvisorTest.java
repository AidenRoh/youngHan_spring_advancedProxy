package hello.proxy.advisor;

import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;

@Slf4j
public class MultiAdvisorTest {

	@Test
	@DisplayName("여러 프록시")
	void multiAdvisorTest1() {
		//client -> proxy2(advisor2) -> proxy1(advisor1) -> target
		//construct proxy1
		ServiceInterface target = new ServiceImpl();
		ProxyFactory proxyFactory1 = new ProxyFactory(target);
		DefaultPointcutAdvisor advisor1 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice1());
		proxyFactory1.addAdvisor(advisor1);
		ServiceInterface proxy1 = (ServiceInterface) proxyFactory1.getProxy();

		//construct proxy2
		ProxyFactory proxyFactory2 = new ProxyFactory(proxy1);
		DefaultPointcutAdvisor advisor2 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice2());
		proxyFactory2.addAdvisor(advisor2);
		ServiceInterface proxy2 = (ServiceInterface) proxyFactory2.getProxy();

		//execute
		proxy2.save();
	}

	@Test
	@DisplayName("하나의 프록시, 여러 어드바이져")
	void multiAdvisorTest2() {
		//client -> proxy -> advisor2 -> advisor1 -> target
		DefaultPointcutAdvisor advisor1 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice1());
		DefaultPointcutAdvisor advisor2 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice2());
		//construct proxy
		ServiceInterface target = new ServiceImpl();
		ProxyFactory proxyFactory = new ProxyFactory(target);

		proxyFactory.addAdvisor(advisor2);
		proxyFactory.addAdvisor(advisor1);
		ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

		//execute
		proxy.save();
	}

	@Slf4j
	static class Advice1 implements MethodInterceptor {
		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			log.info("call advice1");
			return invocation.proceed();
		}
	}

	@Slf4j
	static class Advice2 implements MethodInterceptor {
		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			log.info("call advice2");
			return invocation.proceed();
		}
	}
}
