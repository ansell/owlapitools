package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class Bag<Type> {
	ConcurrentMap<Type, AtomicLong> map = new ConcurrentHashMap<Type, AtomicLong>();

	public Bag() {}

	public long add(Type t) {
	    AtomicLong putIfAbsent = map.putIfAbsent(t, new AtomicLong(1));
		if (putIfAbsent != null) {
			return putIfAbsent.incrementAndGet();
		} else {
			return 1;
		}
	}

	public long check(Type t) {
		AtomicLong l = map.get(t);
		if (l != null) {
			return l.get();
		}
		return 0;
	}

	public List<Type> sortedList(final boolean increasing) {
		List<Type> toReturn = new ArrayList<Type>(map.keySet());
		Collections.sort(toReturn, new Comparator<Type>() {
			public int compare(Type o1, Type o2) {
				if (increasing) {
					return Long.signum(check(o1) - check(o2));
				} else {
					return Long.signum(check(o2) - check(o1));
				}
			}
		});
		return toReturn;
	}

	public Collection<Type> list() {
		return Collections.unmodifiableSet(map.keySet());
	}

	public long[] values(){
		List<AtomicLong> values = new ArrayList<AtomicLong>(map.values());
        long[] toReturn=new long[values.size()];
		int i=0;
		for(AtomicLong l:values) {
			toReturn[i++]=l.get();
		}
		Arrays.sort(toReturn);

		return toReturn;
	}

	@Override
	public String toString() {

		return map.toString();
	}

}
