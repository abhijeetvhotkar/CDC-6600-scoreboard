package scoreboard;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class inst_initializer {
	private static AtomicInteger INSTRUCTION_ID_GENERATOR = new AtomicInteger(101);
	
	public int id;
	public String complete_line;
	public String inst;
	public String dest;
	public String src1;
	public String src2;
	public String label_check;
	public boolean is_it_jump_inst;
	public boolean is_it_hlt_inst;
	public boolean is_it_uncond;

	inst_initializer(String complete_line, String inst, String dest, String src1, String src2,
			String label_check, boolean is_it_jump_inst, boolean is_it_hlt_inst, boolean is_it_uncond) {
		this.complete_line = complete_line;
		this.inst = inst;
		this.dest = dest;
		this.src1 = src1;
		this.src2 = src2;
		this.label_check = label_check;
		this.is_it_jump_inst = is_it_jump_inst;
		this.is_it_hlt_inst = is_it_hlt_inst;
		this.is_it_uncond = is_it_uncond;
		this.id = INSTRUCTION_ID_GENERATOR.getAndIncrement();
	}

}
