package hello.proxy.code_generator_library.code;

import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

@Slf4j
public class TimeMethodInterceptor implements MethodInterceptor {

	private final Object target;

	public TimeMethodInterceptor(Object target) {
		this.target = target;
	}

	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
		log.info("TimeProxy 실행");
		long startTime = System.currentTimeMillis();
		//biz logic
//		Object result = method.invoke(target, objects);
		// CGLIB 에선 위에 방법보다 아래 방법이 최적화에 더 좋다고 한다.
		Object result = methodProxy.invoke(target, args);

		long endTime = System.currentTimeMillis();
		long resultTime = endTime - startTime;
		log.info("TimeProxy 종료 resultTime= {}", resultTime);
		return result;
	}
}
