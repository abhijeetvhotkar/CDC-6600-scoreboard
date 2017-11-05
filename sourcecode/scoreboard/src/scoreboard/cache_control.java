package scoreboard;

class cache_control{
	private cache_initializer inst_cache = null;
	private data_cache data_in_cache = null;
	private int Inst_to_fetch_in_q = 0;
	private int data_to_fetch_in_q = 0;
	private boolean inst_cache_flag = false;
	private boolean inst_cache_req = false;
	private boolean data_cache_flag = false;
	private boolean data_cache_req = false;
	public int inst_cache_miss_cnt = 0;
	public int data_cache_miss_count = 0;

	public cache_control(cache_initializer inst_cache, data_cache data_in_cache) {
		this.inst_cache = inst_cache;
		this.data_in_cache = data_in_cache;
	}

	public boolean inst_cache_active() {
		return inst_cache_flag;
	}

	public boolean avail(int instructionId) {
		boolean Hit_available = true;
		Hit_available = inst_cache.hit_available(instructionId);
		if (!Hit_available) {
			Inst_to_fetch_in_q = instructionId;
			inst_cache_req = true;
		} else {
			Inst_to_fetch_in_q = 0;
		}
		return Hit_available;
	}

	public boolean data_present(int dataId) {
		boolean Hit_available = true;
		Hit_available = data_in_cache.hit_available(dataId);
		if (!Hit_available) {
			data_to_fetch_in_q = dataId;
			data_cache_req = true;
		} else {
			data_to_fetch_in_q = 0;
		}
		return Hit_available;
	}

	public void upd_cache() {

		if (!inst_cache_flag && !data_cache_flag) {
			if (inst_cache_req) {
				inst_cache_flag = true;
				inst_cache_miss_cnt += 1;
			} else if (data_cache_req) {
				data_cache_flag = true;
				data_cache_miss_count += 1;
			}
		}

		if (inst_cache_flag) {
			inst_cache.check_inst_blk(Inst_to_fetch_in_q);
			if (inst_cache.hit_available(Inst_to_fetch_in_q)) {
				Inst_to_fetch_in_q = 0;
				inst_cache_req = false;
				inst_cache_flag = false;
			}
		}

		if (data_cache_flag) {
			data_in_cache.Check_data_blk(data_to_fetch_in_q);
			if (data_in_cache.hit_available(data_to_fetch_in_q)) {
				data_to_fetch_in_q = 0;
				data_cache_req = false;
				data_cache_flag = false;
			}
		}
	}
	
}