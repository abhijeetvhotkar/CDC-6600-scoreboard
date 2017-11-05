package scoreboard;


import java.util.concurrent.atomic.AtomicInteger;

import scoreboard.FU_initializer.unit_type;

public class p_line {
	public enum stage_type {
		NS, ISSUE, FETCH, READ, EXEC, WRITE
	};

	private static AtomicInteger GEN_ID_INST_P_LINE = new AtomicInteger(1001);

	int id;
	stage_type stage_category;
	inst_initializer inst;
	int Fetch_inst;
	int Issue_inst;
	int Read_inst;
	int Execute_inst;
	int Write_inst;
	char RAW_Hazard;
	char WAW_Hazard;
	char Structural;
	boolean is_branch_taken;
	FU_initializer FU_unit;
	FU_initializer.unit_type FU_unit_type;

	public p_line(inst_initializer inst) {
		this.inst = inst;
		this.FU_unit_type = GetFunctionalUnitType(this.inst.inst);
		this.FU_unit = null;
		this.stage_category = stage_type.NS;
		this.RAW_Hazard = 'N';
		this.WAW_Hazard = 'N';
		this.Structural = 'N';
		this.id = GEN_ID_INST_P_LINE.getAndIncrement();
		this.is_branch_taken = false;
	}

	private FU_initializer.unit_type GetFunctionalUnitType(String inst) {
		FU_initializer.unit_type unitType = unit_type.INTEGER;
		if (inst.equals("ADD.D") || inst.equals("SUB.D")) {
			unitType = unit_type.ADDER;
		} else if (inst.equals("MUL.D")) {
			unitType = unit_type.MULTIPLIER;
		} else if (inst.equals("DIV.D")) {
			unitType = unit_type.DIVIDER;
		} else if (inst.equals("L.D") || inst.equals("S.D") || inst.equals("LW") || inst.equals("SW")) {
			unitType = unit_type.LOADSTORE;
		} else if (inst.equals("BNE") || inst.equals("BEQ") || inst.equals("HLT")) {
			unitType = unit_type.JMPHLT;
		}
		return unitType;
	}

	public String Compute_FU_unit_id() {
		String FU_unit_id = "";
		if (FU_unit != null) {
			FU_unit_id = this.FU_unit.id;
		}
		return FU_unit_id;
	}

	public void Set_FU(FU_initializer FU_unit) {
		this.FU_unit = FU_unit;
	}

	public void Fetch_meth(int clockCycle) {
		this.stage_category = stage_type.FETCH;
		this.Fetch_inst = clockCycle;
	}

	public void Issue_meth(int clockCycle) {
		this.stage_category = stage_type.ISSUE;
		this.Issue_inst = clockCycle;
	}

	public void Read_meth(int clockCycle) {
		this.stage_category = stage_type.READ;
		this.Read_inst = clockCycle;
	}

	public void Execute_meth(int clockCycle) {
		this.stage_category = stage_type.EXEC;
		this.Execute_inst = clockCycle;
		if (this.FU_unit != null) {
			this.update_execute(clockCycle);
		}
	}

	public void update_execute(int clockCycle) {
		this.Execute_inst = clockCycle;
		this.FU_unit.update_clk_cyc();
	}

	public void Write_meth(int clockCycle) {
		this.stage_category = stage_type.WRITE;
		this.Write_inst = clockCycle;
	}

	public stage_type Compute_curr_stage() {
		return this.stage_category;
	}

	public void Flag_RAW() {
		this.RAW_Hazard = 'Y';
	}

	public void Flag_WAW() {
		this.WAW_Hazard = 'Y';
	}

	public void Flag_Structural() {
		this.Structural = 'Y';
	}

}