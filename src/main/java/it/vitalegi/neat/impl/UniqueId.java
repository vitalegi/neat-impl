package it.vitalegi.neat.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

public class UniqueId {

	private Map<String, Long> ids;

	public UniqueId() {
		ids = new HashMap<>();
	}

	public void clearConnectionIds() {
		Pattern pattern = Pattern.compile(Connection.class.getName() + "_\\d+_\\d+");
		Iterator<String> it = ids.keySet().iterator();
		while (it.hasNext()) {
			String k = it.next();
			if (pattern.matcher(k).matches()) {
				it.remove();
			}
		}
	}

	private long get(String key) {
		Long v = ids.get(key);
		if (v == null) {
			return 0L;
		}
		return v;
	}

	private synchronized long getAndUpdate(String key) {
		long v = get(key) + 1;
		update(key, v);
		return v;
	}

	public long nextConnectionId(long fromNode, long toNode) {
		String name1 = Connection.class.getName();
		String name2 = Connection.class.getName() + "_" + fromNode + "_" + toNode;

		if (ids.containsKey(name2)) {
			return get(name2);
		}

		long id = getAndUpdate(name1);
		update(name2, id);
		return id;
	}

	public long nextConnectionId(long id, long fromNode, long toNode) {
		String name1 = Connection.class.getName();
		String name2 = Connection.class.getName() + "_" + fromNode + "_" + toNode;

		update(name1, Math.max(id, get(name1)));
		update(name2, id);
		return id;
	}

	public long nextGeneId() {
		return getAndUpdate(Gene.class.getName());
	}

	public long nextGeneId(long id) {
		String name = Gene.class.getName();
		update(name, Math.max(get(name), id));
		return id;
	}

	public long nextNodeId() {
		return getAndUpdate(Node.class.getName());
	}

	public long nextNodeId(long id) {
		String name = Node.class.getName();
		update(name, Math.max(get(name), id));
		return id;
	}

	public long nextSpeciesId() {
		return getAndUpdate(Species.class.getName());
	}

	public long nextSpeciesId(long id) {
		String name = Species.class.getName();
		update(name, Math.max(get(name), id));
		return id;
	}

	private void update(String key, long value) {
		ids.put(key, value);
	}
}
