package scoreboard;
import java.util.ArrayList;
public class cache_initializer {
	private int blk_cnt;
	private int blk_size;
	private int Word_hit_clk_cyc;
	private int clk_cyc_used;
	private ArrayList<Integer> inst_id_list;
	private ArrayList<Integer> cache_list;

	public cache_initializer(int blk_cnt, int blk_size, ArrayList<Integer> instructionList,
			int Word_hit_clk_cyc) {
		this.blk_cnt = blk_cnt;
		this.blk_size = blk_size;
		this.Word_hit_clk_cyc = Word_hit_clk_cyc;
		this.clk_cyc_used = 0;
		this.inst_id_list = instructionList;
		this.cache_list = new ArrayList<Integer>();
	}

	public boolean hit_available(int inst_id) {
		boolean Hit_available = true;
		if (cache_list.contains(check_sob_inst(inst_id))) {
			Hit_available = true;
			from_LRU_move_inst(inst_id);
		} else {
			Hit_available = false;
		}
		return Hit_available;
	}

	public void view_cache() {
		for (Integer integer : cache_list) {
			System.out.println("**" + integer);
		}
	}

	public void check_inst_blk(int inst_id) {
		clk_cyc_used += 1;
		if (clk_cyc_used == Word_hit_clk_cyc * blk_cnt) {
			clk_cyc_used = 0;
			int blk_index_start = check_sob_inst(inst_id);
			if (cache_list.size() + 1 > blk_cnt) {

				rem_LRU_inst();
			}
			inst_load_cache(blk_index_start);

		} else {
		}

	}

	private void from_LRU_move_inst(int inst_id) {
		if (cache_list.size() > 1) {
			int blk_index_start = check_sob_inst(inst_id);
			rem_inst_cache(blk_index_start);
			inst_load_cache(blk_index_start);
		} else {
		}
	}

	private void rem_LRU_inst() {
		this.cache_list.remove(0);
	}

	private void rem_inst_cache(int blk_index_start) {
		this.cache_list.remove(this.cache_list.indexOf(blk_index_start));
	}

	private void inst_load_cache(int blk_index_start) {
		this.cache_list.add(blk_index_start);
	}

	private int check_sob_inst(int inst_id) {
		int inst_index = inst_id_list.indexOf(inst_id);
		int blk_index_start = (inst_index / blk_size) * blk_size;
		return blk_index_start;
	}
}
