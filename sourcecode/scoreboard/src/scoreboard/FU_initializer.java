package scoreboard;


import java.io.*;
import java.util.ArrayList;

public class FU_initializer {
	public enum unit_type {
		INTEGER, ADDER, MULTIPLIER, DIVIDER, LOADSTORE, JMPHLT
	};

	public unit_type FU_type;
	public String id;
	public int FU_latency;
	public int used_clk_cyc;
	public boolean is_it_free;

	FU_initializer(unit_type FU_type, String id, int FU_latency) {
		this.FU_type = FU_type;
		this.id = id;
		this.FU_latency = FU_latency;
		this.used_clk_cyc = 0;
		this.is_it_free = true;
	}

	void Update_Unavailable() {
		this.is_it_free = false;
	}

	void update_clk_cyc() {
		this.used_clk_cyc += 1;
		if (this.is_it_free)
			this.is_it_free = false;
	}

	boolean Did_it_Completed() {
		return ((this.FU_latency - this.used_clk_cyc) == 0);
	}

	void Reset_FU() {
		this.used_clk_cyc = 0;
		this.is_it_free = true;
	}

}