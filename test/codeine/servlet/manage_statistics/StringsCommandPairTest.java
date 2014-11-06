package codeine.servlet.manage_statistics;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

public class StringsCommandPairTest {

	@Test
	public void testSortOrder() {
		StringsCommandPair before = new StringsCommandPair(null, null, null, 1);
		StringsCommandPair after = new StringsCommandPair(null, null, null, 2);
		List<StringsCommandPair> list = Lists.newArrayList(before, after);
		Collections.sort(list, new StringsCommandPair.CommandComparator());
		assertEquals(Lists.newArrayList(after, before), list);
	}

}
