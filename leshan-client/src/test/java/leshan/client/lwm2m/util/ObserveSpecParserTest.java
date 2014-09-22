package leshan.client.lwm2m.util;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import leshan.server.lwm2m.observation.ObserveSpec;

import org.junit.Test;

public class ObserveSpecParserTest {

	@Test(expected=IllegalArgumentException.class)
	public void testInvalidFormat() {
		ObserveSpecParser.parse(Arrays.asList("a=b=c"));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testInvalidKey() {
		ObserveSpecParser.parse(Arrays.asList("a=b"));
	}

	@Test
	public void testCancel() {
		testCorrectSpec(new ObserveSpec.Builder().cancel().build(),
				"cancel");
	}

	@Test
	public void testGreaterThan6() {
		testCorrectSpec(new ObserveSpec.Builder().greaterThan(6).build(),
				"gt=6.0");
	}

	@Test
	public void testGreaterThan8() {
		testCorrectSpec(new ObserveSpec.Builder().greaterThan(8).build(),
				"gt=8.0");
	}

	@Test
	public void testLessThan8() {
		testCorrectSpec(new ObserveSpec.Builder().lessThan(8).build(),
				"lt=8.0");
	}

	@Test
	public void testLessThan8AndGreaterThan14() {
		testCorrectSpec(new ObserveSpec.Builder().greaterThan(14).lessThan(8).build(),
				"lt=8.0",
				"gt=14.0");
	}

	@Test
	public void testAllTheThings() {
		final ObserveSpec spec = new ObserveSpec.Builder()
				.greaterThan(14)
				.lessThan(8)
				.minPeriod(5)
				.maxPeriod(10)
				.step(1)
				.build();
		testCorrectSpec(spec,
				"gt=14.0",
				"lt=8.0",
				"pmin=5",
				"pmax=10",
				"st=1.0");
	}

	@Test(expected=IllegalStateException.class)
	public void testOutOfOrderPminPmax() {
		ObserveSpecParser.parse(Arrays.asList("pmin=50", "pmax=10"));
	}

	private void testCorrectSpec(final ObserveSpec expected, final String... inputs) {
		final List<String> queries = Arrays.asList(inputs);
		final ObserveSpec actual = ObserveSpecParser.parse(queries);
		assertSameSpecs(expected, actual);
	}

	private void assertSameSpecs(final ObserveSpec expected, final ObserveSpec actual) {
		assertEquals(expected.toString(), actual.toString());
	}

}