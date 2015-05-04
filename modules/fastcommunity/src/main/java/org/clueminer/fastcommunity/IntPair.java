package org.clueminer.fastcommunity;

import java.util.Objects;

/**
 *
 * @author Hamster
 */
public class IntPair {
	Integer first;
	Integer second;

	public IntPair(Integer first, Integer second) {
		this.first = first;
		this.second = second;
	}

	public Integer getFirst() {
		return first;
	}

	public Integer getSecond() {
		return second;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		final IntPair other = (IntPair) obj;
		if(!this.first.equals(other.first))
			return false;
		if(!this.second.equals(other.second))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 41 * hash + Objects.hashCode(this.first);
		hash = 41 * hash + Objects.hashCode(this.second);
		return hash;
	}
	
}
