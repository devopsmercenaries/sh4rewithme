package me.sh4rewith.domain;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class Privacy {

	private final Type type;
	private final Set<String> buddies;

	public static enum Type {
		PRIVATE, PROTECTED, PUBLIC
	}

	public static class Builder {
		private Type type;
		private Set<String> buddies = new TreeSet<String>();

		public Builder(Type type) {
			this.type = type;
		}

		public Builder shareWithBuddy(String buddyId) {
			this.buddies.add(buddyId);
			return this;
		}

		public Privacy build() {
			return new Privacy(type, buddies);
		}
	}

	private Privacy(Type type, Set<String> buddies) {
		this.type = type;
		this.buddies = Collections.unmodifiableSet(buddies);
	}

	public Type getType() {
		return type;
	}

	public Set<String> getBuddies() {
		return buddies;
	}
}