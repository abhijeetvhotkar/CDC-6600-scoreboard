package scoreboard;

import java.util.ArrayList;

public class data_cache {
	int Sets_in_cache;
	int Blocks_in_cache;
	int Words_in_cache;
	int Word_hit_clk_cyc;
	int clk_cyc_used;
	ArrayList<Set> List_of_sets;

	private class Set {
		ArrayList<Block> List_of_blocks = new ArrayList<Block>();
	}

	private class Block {
		ArrayList<Integer> List_of_words = new ArrayList<Integer>();
	}

	data_cache(int Blocks_in_cache, int Words_in_cache, int Sets_in_cache, int Word_hit_clk_cyc) {
		this.Blocks_in_cache = Blocks_in_cache;
		this.Words_in_cache = Words_in_cache;
		this.Sets_in_cache = Sets_in_cache;
		this.Word_hit_clk_cyc = Word_hit_clk_cyc;
		this.clk_cyc_used = 0;
		Check_sets();
	}

	private void Check_sets() {
		List_of_sets = new ArrayList<>();
		for (int i = 0; i < Sets_in_cache; i++) {
			List_of_sets.add(new Set());
		}
	}

	public boolean hit_available(int dataId) {
		boolean Hit_available = false;
		Set set = List_of_sets.get(Location_of_set(dataId));
		if (set.List_of_blocks.size() > 0) {
			for (Block block : set.List_of_blocks) {
				if (block.List_of_words.contains(dataId)) {
					Hit_available = true;
					update_LRU(set, block);
					break;
				}
			}
		} else {
		}
		return Hit_available;
	}

	private int Location_of_word(int dataId) {
		return dataId % Words_in_cache;
	}

	private int Location_of_set(int dataId) {
		return (dataId / Words_in_cache) % 2;
	}

	public void update_LRU(Set set, Block block) {
		set.List_of_blocks.remove(block);
		set.List_of_blocks.add(block);
	}

	public void Check_data_blk(int dataId) {
		clk_cyc_used += 1;
		if (clk_cyc_used == Word_hit_clk_cyc * Words_in_cache) {
			clk_cyc_used = 0;
			Set set = List_of_sets.get(Location_of_set(dataId));
			int startDataId = dataId - Location_of_word(dataId);
			Block block = new Block();
			for (int i = startDataId; i < startDataId + Words_in_cache; i++) {
				block.List_of_words.add(i);
			}
			if (set.List_of_blocks.size() < Blocks_in_cache) {
				set.List_of_blocks.add(block);
			} else {

				set.List_of_blocks.remove(0);
				set.List_of_blocks.add(block);
			}
		} else {
		}
	}
}
