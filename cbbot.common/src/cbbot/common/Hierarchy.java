package cbbot.common;

import cbbot.util.MapUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Hierarchy implements Serializable {
	private static final long serialVersionUID = 895181183825L;

	protected Map<String, Integer> hierarchy;
	protected int defaultLevel;

	public Hierarchy(int defaultLevel) {
		this.defaultLevel = defaultLevel;
		this.hierarchy = new HashMap<>();
	}

	public Hierarchy(Map<String, Integer> hierarchy, int defaultLevel) {
		this.hierarchy = hierarchy;
		this.defaultLevel = defaultLevel;
	}

	public int getLevelOrSetToDefault(String agent) {
		try {
			return hierarchy.putIfAbsent(agent, defaultLevel);
		} catch (NullPointerException e) { return hierarchy.get(agent); }
	}

	public void setAgentLevel(String agent, int level) {
		if(hierarchy.putIfAbsent(agent, level) != null) { hierarchy.replace(agent, level); }
	}

	public Map<String, Integer> getHierarchy() { return hierarchy; }

	public int getDefaultLevel() { return defaultLevel; }
	public void setDefaultLevel(int defaultLevel) { this.defaultLevel = defaultLevel; }

	@Override
	public int hashCode() {
		return Objects.hash(hierarchy, defaultLevel);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof Hierarchy)) {
			return false;
		}
		Hierarchy h = (Hierarchy) obj;
		return defaultLevel == h.getDefaultLevel() &&
				Objects.equals(hierarchy, h.getHierarchy());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Hierarchy:\n");
		sb.append("\tDefault Level: ").append(defaultLevel).append("\n");
		for(Map.Entry<String, Integer> entry : MapUtils.sortByValue(hierarchy).entrySet()) {
			sb.append("\tAGENT: ").append(entry.getKey()).append("\t| LEVEL: ").append(entry.getValue()).append("\n");
		}
		return sb.toString();
	}
}
