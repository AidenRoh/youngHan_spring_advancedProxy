package hello.aop.proxyconspros;

import hello.aop.member.MemberService;
import hello.aop.member.MemberServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;

@Slf4j
public class ProxyCastingTest {

	@Test
	void jdkProxy() {
		MemberServiceImpl target = new MemberServiceImpl();
		ProxyFactory proxyFactory = new ProxyFactory(target);
		proxyFactory.setProxyTargetClass(false); // JDK 동적 프록시

		//프록시를 인터페이스로 캐스팅 성공
		MemberService memberServiceProxy = (MemberService) proxyFactory.getProxy();

		//JDK 동적 프록시를 구현 클래스로 캐스팅 시도 실패, ClassCastException 예외 발생

		//Why? 동적 프록시는 인터페이스를 기반으로 프록시를 만들기 때문에, 인터페이스를 상속하는 하위클래스의 존재 자체를 모른다.
		Assertions.assertThrows(ClassCastException.class, () -> {
			MemberServiceImpl castingMemberService = (MemberServiceImpl) memberServiceProxy;
		});
	}

	@Test
	void cglibProxy() {
		MemberServiceImpl target = new MemberServiceImpl();
		ProxyFactory proxyFactory = new ProxyFactory(target);
		proxyFactory.setProxyTargetClass(true); // CGLIB 프록시

		//프록시를 인터페이스로 캐스팅 성공
		MemberService memberServiceProxy = (MemberService) proxyFactory.getProxy();

		//CGLIB 프록시를 구현 클래스로 캐스팅 시도 성공
			MemberServiceImpl castingMemberService = (MemberServiceImpl) memberServiceProxy;
	}
}
