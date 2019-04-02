package it.vitalegi.neat.impl;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class RandomTest {

	@Test
	public void testNextRandomPreserveWeights() {

		Random.init();
		int[] indexes = new int[3];
		for (int i = 0; i < 100; i++) {
			indexes[Random.nextRandom(new double[] { 1, 98, 1 })]++;
		}
		Assert.assertThat(indexes[1], Matchers.greaterThan(80));
	}
}
