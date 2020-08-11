package com.xrbpowered.jdiagram.data;

import com.xrbpowered.jdiagram.data.Data.Row;

public abstract class Join {

	protected abstract void joinData(Data data, Data left, Data right, int[] lkeyMap, int[] rkeyMap, int rstart);
	
	public Data join(Data left, Data right, String[] lkey, String[] rkey, String leftPrefix, String rightPrefix) {
		if(lkey.length!=rkey.length)
			throw new RuntimeException("Key sizes don't match");

		if(leftPrefix==null)
			leftPrefix = "";
		if(rightPrefix==null)
			rightPrefix = "";
		String[] headers = new String[left.headers.length+right.headers.length];
		int j=0;
		for(int i=0; i<left.headers.length; i++) {
			headers[j] = leftPrefix+left.headers[i];
			j++;
		}
		int rstart = j;
		for(int i=0; i<right.headers.length; i++) {
			headers[j] = rightPrefix+right.headers[i];
			j++;
		}

		Data data = new Data(headers);
		joinData(data, left, right, left.indexMap(lkey), right.indexMap(rkey), rstart);
		return data;
	}

	public Data join(Data left, Data right, String[] key, String leftPrefix, String rightPrefix) {
		return join(left, right, key, key, leftPrefix, rightPrefix);
	}

	public Data join(Data left, Data right, String lkey, String rkey, String leftPrefix, String rightPrefix) {
		return join(left, right, new String[] {lkey}, new String[] {rkey}, leftPrefix, rightPrefix);
	}

	public Data join(Data left, Data right, String key, String leftPrefix, String rightPrefix) {
		return join(left, right, new String[] {key}, leftPrefix, rightPrefix);
	}

	protected boolean match(Row lrow, Row rrow, int[] lkeyMap, int[] rkeyMap) {
		for(int i=0; i<lkeyMap.length; i++) {
			if(!Filter.eq(lrow.get(lkeyMap[i]), rrow.get(rkeyMap[i])))
				return false;
		}
		return true;
	}

	protected void copyData(Row row, Row lrow, Row rrow, int rstart) {
		if(lrow!=null)
			lrow.copy(row, 0, lrow.headers().length-1, 0);
		if(rrow!=null)
			rrow.copy(row, 0, rrow.headers().length-1, rstart);
	}

	public static Join inner = new Join() {
		@Override
		protected void joinData(Data data, Data left, Data right, int[] lkeyMap, int[] rkeyMap, int rstart) {
			for(Row lrow : left.rows)
				for(Row rrow : right.rows) {
					boolean match = match(lrow, rrow, lkeyMap, rkeyMap);
					if(match) {
						Row row = data.addRow();
						copyData(row, lrow, rrow, rstart);
					}
				}
		}
	};
	
	public static Join left = new Join() {
		@Override
		protected void joinData(Data data, Data left, Data right, int[] lkeyMap, int[] rkeyMap, int rstart) {
			for(Row lrow : left.rows) {
				boolean hasMatch = false;
				for(Row rrow : right.rows) {
					boolean match = match(lrow, rrow, lkeyMap, rkeyMap);
					hasMatch |= match;
					if(match) {
						copyData(data.addRow(), lrow, rrow, rstart);
					}
				}
				if(!hasMatch) {
					copyData(data.addRow(), lrow, null, rstart);
				}
			}
		}
	};
	
	public static Join right = new Join() {
		@Override
		protected void joinData(Data data, Data left, Data right, int[] lkeyMap, int[] rkeyMap, int rstart) {
			for(Row rrow : right.rows) {
				boolean hasMatch = false;
				for(Row lrow : left.rows) {
					boolean match = match(lrow, rrow, lkeyMap, rkeyMap);
					hasMatch |= match;
					if(match) {
						copyData(data.addRow(), lrow, rrow, rstart);
					}
				}
				if(!hasMatch) {
					copyData(data.addRow(), null, rrow, rstart);
				}
			}
		}
	};
	
	public static Join outer = new Join() {
		@Override
		protected void joinData(Data data, Data left, Data right, int[] lkeyMap, int[] rkeyMap, int rstart) {
			boolean[] rightMatch = new boolean[right.rows.size()];
			for(Row lrow : left.rows) {
				boolean leftMatch = false;
				int rindex = 0;
				for(Row rrow : right.rows) {
					boolean match = match(lrow, rrow, lkeyMap, rkeyMap);
					leftMatch |= match;
					rightMatch[rindex] |= match;
					if(match) {
						copyData(data.addRow(), lrow, rrow, rstart);
					}
					rindex++;
				}
				if(!leftMatch) {
					copyData(data.addRow(), lrow, null, rstart);
				}
			}
			int rindex = 0;
			for(Row rrow : right.rows) {
				if(!rightMatch[rindex]) {
					copyData(data.addRow(), null, rrow, rstart);
				}
				rindex++;
			}
		}
	};
}
