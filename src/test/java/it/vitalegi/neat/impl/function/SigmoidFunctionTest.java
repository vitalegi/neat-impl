package it.vitalegi.neat.impl.function;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import it.vitalegi.neat.AbstractTest;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class SigmoidFunctionTest  extends AbstractTest{

	@Test
	public void testSigmoid0() {
		Assert.assertEquals(0.5, SigmoidFunction.sigmoid(0), 0.1);
	}

	@Test
	public void testSigmoid6() {
		Assert.assertEquals(1.0, SigmoidFunction.sigmoid(6), 0.1);
	}

	@Test
	public void testSigmoidMinus6() {
		Assert.assertEquals(0.0, SigmoidFunction.sigmoid(-6), 0.1);
	}

	@Test
	public void testCustomSigmoid0() {
		Assert.assertEquals(0.5, SigmoidFunction.customSigmoid(0), 0.1);
	}

	@Test
	public void testCustomSigmoid6() {
		Assert.assertEquals(1.0, SigmoidFunction.customSigmoid(1), 0.1);
	}

	@Test
	public void testCustomSigmoidMinus6() {
		Assert.assertEquals(0.0, SigmoidFunction.customSigmoid(-1), 0.1);
	}
}