package hello.proxy.jdk_dynamic;

import hello.proxy.jdk_dynamic.code.AImpl;
import hello.proxy.jdk_dynamic.code.AInterface;
import hello.proxy.jdk_dynamic.code.BImpl;
import hello.proxy.jdk_dynamic.code.BInterface;
import hello.proxy.jdk_dynamic.code.TimeInvocationHandler;
import java.lang.reflect.Proxy;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class JdkDynamicProxyTest {

	@Test
	void dynamicA() {
		AInterface target = new AImpl();
		TimeInvocationHandler handler = new TimeInvocationHandler(target);

		AInterface proxy = (AInterface) Proxy.newProxyInstance(AInterface.class.getClassLoader(), new Class[]{AInterface.class}, handler);

		proxy.call();
		log.info("targetClass= {}", target.getClass());
		log.info("proxyClass= {}", proxy.getClass());
	}

	@Test
	void dynamicB() {
		BInterface target = new BImpl();
		TimeInvocationHandler handler = new TimeInvocationHandler(target);

		BInterface proxy = (BInterface) Proxy.newProxyInstance(BInterface.class.getClassLoader(), new Class[]{BInterface.class}, handler);

		proxy.call();
		log.info("targetClass= {}", target.getClass());
		log.info("proxyClass= {}", proxy.getClass());
	}
}
