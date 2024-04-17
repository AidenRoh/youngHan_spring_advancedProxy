package hello.aop.pointcut;

import static org.assertj.core.api.Assertions.assertThat;

import hello.aop.member.MemberServiceImpl;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

@Slf4j
public class ExecutionTest {

	AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
	Method helloMethod;

	@BeforeEach
	public void init() throws NoSuchMethodException {
		helloMethod = MemberServiceImpl.class.getMethod("hello", String.class);
	}

	@Test
	void printMethod() {
		//public java.lang.String hello.aop.member.MemberServiceImpl.hello(java.lang.String)
		log.info("helloMethod={}", helloMethod );
	}

	@Test
	void exactMatch() {
		// execution 으로 (접근제어자 반환타입 선언타입 메서드이름 파라미터) 가 기입됨 예외는 생략
		pointcut.setExpression("execution(public String hello.aop.member.MemberServiceImpl.hello(String))");
		assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
	}

	@Test
	void allMatch() {
		// * = 전부 가능하다는 소리 / (..) = 파라미터의 타입과 개수 상관없다
		// 접근제어자: 생략 / (반환타입 = * , 선언타입 : 생략, 메서드 이름 = *, 파라미터 = (..) )
		pointcut.setExpression("execution(* *(..))");
		assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
	}

	@Test
	void nameMatchStar1() {
		//'hel' 이 포함되고 그 뒤엔 상관없이 무엇이든
		pointcut.setExpression("execution(* hel*(..))");
		assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
	}

	@Test
	void nameMatchFalse() {
		pointcut.setExpression("execution(* nono(..))");
		assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
	}

	@Test
	void packageExactMatch1() {
		pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.hello(..))");
		assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
	}

	@Test
	void packageExactMatch2() {
		// 패키지에도 * 을 사용할 수 있다.
		pointcut.setExpression("execution(* hello.aop.member.*.*(..))");
		assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
	}

	@Test
	void packageExactFalse() {
		// 밑에 execution에 나오는 .*.* 는 클래스명과 메서드명 자리이다. 즉 aop 패키지에서의 클래스명 메서드명인 것이지 하위 패키지를 포함하는 말은 아님
		pointcut.setExpression("execution(* hello.aop.*.*(..))");
		assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
	}

	@Test
	void packageMatchIncludingSubpackages1() {
		// 하위 패키지까지 맞추고 싶다면 .. 을 사용하자
		pointcut.setExpression("execution(* hello.aop.member..*.*(..))");
		assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
	}

	@Test
	void packageMatchIncludingSubpackages2() {
		// 하위 패키지까지 맞추고 싶다면 .. 을 사용하자
		pointcut.setExpression("execution(* hello.aop..*.*(..))");
		assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
	}

	@Test
	void typeExactMatch() {
		pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.*(..))");
		assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
	}

	@Test
	void typeMatchSuperType() {
		//부모 타입도 가능
		pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");
		assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
	}

	@Test
	void typeMatchInternal() throws NoSuchMethodException {
		//부모타입을 사용할 순 있지만, 부모타입에 선언한 메서드 까지만 가능
		pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.*(..))");
		//자식타입에만 있는 메서드를 부모타입으로 호출 시 실패
		Method internalMethod = MemberServiceImpl.class.getMethod("internal", String.class);
		assertThat(pointcut.matches(internalMethod, MemberServiceImpl.class)).isTrue();
	}

	@Test
	void typeMatchNoSuperTypeMethodFalse() throws NoSuchMethodException {
		//부모타입을 사용할 순 있지만, 부모타입에 선언한 메서드 까지만 가능
		pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");
		//자식타입에만 있는 메서드를 부모타입으로 호출 시 실패
		Method internalMethod = MemberServiceImpl.class.getMethod("internal", String.class);
		assertThat(pointcut.matches(internalMethod, MemberServiceImpl.class)).isFalse();
	}

	//String 타입의 파라미터 허용
	//(String)
	@Test
	void argsMatch() {
		pointcut.setExpression("execution(* *(String))");
		assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
	}

	@Test
	void argsMatchNoArgs() {
		pointcut.setExpression("execution(* *())");
		assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
	}

	//정확히 하나의 파라미터 허용, 모든 타입 허용
	//(xxx)
	@Test
	void argsMatchStar() {
		pointcut.setExpression("execution(* *(*))");
		assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
	}

	//숫자와 무관하게 모든 파라미터, 모든 타입 허용
	//(), (xxx), (xxx, xxx)
	@Test
	void argsMatchAll() {
		pointcut.setExpression("execution(* *(..))");
		assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
	}

	//String 타입으로 시작, 이후 숫자와 무관하게 무든 파라미너, 모든 타입 허용
	//(String), (String, xxx), (String, xxx, xxx)
	@Test
	void argsMatchComplex() {
		pointcut.setExpression("execution(* *(String, ..))");
		assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
	}
}
