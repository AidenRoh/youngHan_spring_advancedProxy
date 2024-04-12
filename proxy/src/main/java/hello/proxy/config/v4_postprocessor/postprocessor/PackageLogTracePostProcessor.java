package hello.proxy.config.v4_postprocessor.postprocessor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
@Slf4j
public class PackageLogTracePostProcessor implements BeanPostProcessor {

	private final String basePackage;
	private final Advisor advisor;

	public PackageLogTracePostProcessor(String basePackage, Advisor advisor) {
		this.basePackage = basePackage;
		this.advisor = advisor;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		log.info("param beanName={}, bean={}", beanName, bean.getClass());

		//check application target of proxy
		//if Object is not the application target, then it will proceed with original Object
		String packageName = bean.getClass().getPackageName();
		if (!packageName.startsWith(basePackage)) {
			return bean;
		}

		//if Object is target of proxy, it will be converted as proxy Object and return
		ProxyFactory factory = new ProxyFactory(bean);
		factory.addAdvisor(advisor);

		Object proxy = factory.getProxy();
		log.info("create proxy: target={}, proxy={}", bean.getClass(), proxy.getClass());
		return proxy;
	}
}
