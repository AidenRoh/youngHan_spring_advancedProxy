package hello.aop.pointcut;

import static org.assertj.core.api.Assertions.assertThat;

import hello.aop.member.MemberServiceImpl;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

@Slf4j
public class ArgsTest {

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

	private AspectJExpressionPointcut pointcut(String expression) {
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression(expression);
		return pointcut;
	}

	@Test
	void args() {
		//hello(String)과 매칭
		assertThat(pointcut("args(String)")
				.matches(helloMethod, MemberServiceImpl.class)).isTrue();
		assertThat(pointcut("args(Object)")
				.matches(helloMethod, MemberServiceImpl.class)).isTrue();
		assertThat(pointcut("args()")
				.matches(helloMethod, MemberServiceImpl.class)).isFalse();
		assertThat(pointcut("args(..)")
				.matches(helloMethod, MemberServiceImpl.class)).isTrue();
		assertThat(pointcut("args(*)")
				.matches(helloMethod, MemberServiceImpl.class)).isTrue();
		assertThat(pointcut("args(String, ..)")
				.matches(helloMethod, MemberServiceImpl.class)).isTrue();
	}

	@Test
	//args 와 execution 의 차이는 args 는 런타임에서 작동하는 하지만 (동적) execution 은 정적으로 작동한다.
	void argsVsExecution() {
		//Args
		assertThat(pointcut("args(String)")
				.matches(helloMethod, MemberServiceImpl.class)).isTrue();
		assertThat(pointcut("args(java.io.Serializable)")
				.matches(helloMethod, MemberServiceImpl.class)).isTrue();
		assertThat(pointcut("args(Object)")
				.matches(helloMethod, MemberServiceImpl.class)).isTrue();

		//Execution
		assertThat(pointcut("execution(* *(String))")
				.matches(helloMethod, MemberServiceImpl.class)).isTrue();
		assertThat(pointcut("execution(* *(java.io.Serializable))") //매칭 실패: 정적이기 때문에 동적인 Serializable 사용 불가
				.matches(helloMethod, MemberServiceImpl.class)).isFalse();
		assertThat(pointcut("execution(* *(Object))") //매칭 실패: 매칭 타입이 정확하게 일치해야 한다.
				.matches(helloMethod, MemberServiceImpl.class)).isFalse();
	}

}
