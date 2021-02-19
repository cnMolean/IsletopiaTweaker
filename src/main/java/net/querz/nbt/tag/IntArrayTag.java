package net.querz.nbt.tag;

import net.querz.nbt.tag.ArrayTag;

import java.util.Arrays;

public class IntArrayTag extends ArrayTag<int[]> implements Comparable<net.querz.nbt.tag.IntArrayTag> {

	public static final byte ID = 11;
	public static final int[] ZERO_VALUE = new int[0];

	public IntArrayTag() {
		super(ZERO_VALUE);
	}

	public IntArrayTag(int[] value) {
		super(value);
	}

	@Override
	public byte getID() {
		return ID;
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && Arrays.equals(getValue(), ((net.querz.nbt.tag.IntArrayTag) other).getValue());
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(getValue());
	}

	@Override
	public int compareTo(net.querz.nbt.tag.IntArrayTag other) {
		return Integer.compare(length(), other.length());
	}

	@Override
	public net.querz.nbt.tag.IntArrayTag clone() {
		return new net.querz.nbt.tag.IntArrayTag(Arrays.copyOf(getValue(), length()));
	}
}