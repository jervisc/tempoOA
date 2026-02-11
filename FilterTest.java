import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FilterTest {
	@Test
	void testFilter() {
		Hierarchy unfiltered = new ArrayBasedHierarchy(
				new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11},
				new int[]{0, 1, 2, 3, 1, 0, 1, 0, 1, 1, 2});
		Hierarchy filteredActual = HierarchyFilter.filter(unfiltered,
				nodeId -> nodeId % 3 != 0);
		Hierarchy filteredExpected = new ArrayBasedHierarchy(
				new int[]{1, 2, 5, 8, 10, 11}, new int[]{0, 1, 1, 0, 1, 2});
		assertEquals(filteredExpected.formatString(),
				filteredActual.formatString());
		assertEquals(6, filteredActual.size());
	}

	@Test
	void testRootFilter() {
		Hierarchy unfiltered = new ArrayBasedHierarchy(new int[]{1, 2, 3},
				new int[]{0, 1, 2});
		Hierarchy filteredActual = HierarchyFilter.filter(unfiltered,
				nodeId -> nodeId != 1);
		Hierarchy filteredExpected = new ArrayBasedHierarchy(new int[0],
				new int[0]);
		assertEquals(filteredExpected.formatString(),
				filteredActual.formatString());
		assertEquals(0, filteredActual.size());
	}

	@Test
	void testForestPruning() {
		Hierarchy unfiltered = new ArrayBasedHierarchy(new int[]{1, 2, 6, 7},
				new int[]{0, 1, 0, 1});
		Hierarchy filteredActual = HierarchyFilter.filter(unfiltered,
				nodeId -> nodeId >= 6);
		Hierarchy filteredExpected = new ArrayBasedHierarchy(new int[]{6, 7},
				new int[]{0, 1});
		assertEquals(filteredExpected.formatString(),
				filteredActual.formatString());
		assertEquals(2, filteredActual.size());
		assertEquals(6, filteredActual.nodeId(0));
	}

	@Test
	void testEmptyHierarchy() {
		Hierarchy unfiltered = new ArrayBasedHierarchy(new int[0], new int[0]);
		Hierarchy filteredActual = HierarchyFilter.filter(unfiltered,
				nodeId -> true);
		assertEquals(0, filteredActual.size());
	}
}
