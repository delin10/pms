package pms.util.comm.lambda;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import pms.util.comm.lambda.functon.MergeMapFunction;

public class DStream {
	@SuppressWarnings("rawtypes")
	private ArrayList<Stream> streams = new ArrayList<>();

	public static DStream newInstance() {
		return new DStream();
	}

	@SuppressWarnings("rawtypes")
	public DStream merge(Stream... streams) {
		Arrays.stream(streams).forEach(this.streams::add);
		return this;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DStream map(Function... funcs) {
		for (int i = 0; i < streams.size(); ++i) {
			Stream stream = streams.get(i);
			if (funcs[i] != null) {
				streams.set(i, stream.map(funcs[i]));
			}
		}
		return this;
	}

	/**
	 * 鏈畬鎴�
	 * 
	 * @param consumers
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void forSyncEach(Consumer... consumers) {
		LinkedList<Consumer> list = Arrays.stream(consumers)
				.collect(Collectors.toCollection(LinkedList<Consumer>::new));
		
		Optional<Boolean> optional=Optional.of(false);
		ArrayList<Iterator> its = streams.stream().map(Stream::iterator)
				.collect(Collectors.toCollection(ArrayList<Iterator>::new));
		while (!optional.get()) {
			Iterator<Consumer> it_c=list.iterator();
			its.stream().forEach(it->{
				optional.map(v->v||it.hasNext());
				if (optional.get() && it_c.hasNext()) {
					it_c.next().accept(it.next());
				}
			});
		}
	}

	@SuppressWarnings({ "rawtypes" })
	public Stream<Object> mergeMap(MergeMapFunction func) {
		LinkedList<Object> res = new LinkedList<>();
		ArrayList<Iterator> its = streams.stream().map(Stream::iterator)
				.collect(Collectors.toCollection(ArrayList<Iterator>::new));
		while (its.get(0).hasNext()) {
			ArrayList<Object> list = new ArrayList<>();
			its.stream().forEach(it -> {
				list.add(it.next());
			});
			res.add(func.mergeMap(list.toArray(new Object[0])));
		}
		return res.stream();
	}

}
