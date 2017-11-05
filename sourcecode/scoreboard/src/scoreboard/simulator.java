package scoreboard;

import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import scoreboard.p_line.*;

import java.io.BufferedWriter;
import java.io.FileWriter;


public class simulator {

	public static void main(String[] args) {
		if (args.length < 4) {
			System.out.println("Check file order correctly");
			System.exit(0);
		}
		simulator sb = new simulator();
		sb.compute(args[0], args[1], args[2], args[3]);
	}
	private String if_zero(int num) {
		return num == 0 ? " " : num + "";
	}
	

private ArrayList<FU_initializer> f_unit_list = new ArrayList<FU_initializer>();
private ArrayList<inst_initializer> i_list = new ArrayList<inst_initializer>();
private ArrayList<Integer> d_list = new ArrayList<Integer>();
private HashMap<String, Integer> var_map = new HashMap<String, Integer>();

public void compute(String inst_path, String data_path, String Config_path, String out_path) {

	
	f_unit_list = new ArrayList<FU_initializer>();
	i_list = new ArrayList<inst_initializer>();
	ArrayList<String> value_registers = new ArrayList<String>();
	try {
		f_unit_list = config_initializer.Config_initial(Config_path);
		i_list = config_initializer.initial_inst(inst_path);
		d_list = config_initializer.initial_data(data_path);

		for (inst_initializer inst : i_list) {
			if (!inst.dest.equals("") && !inst.is_it_uncond) {
				if (!value_registers.contains(inst.dest))
					value_registers.add(inst.dest);
			}

			if (!inst.src1.equals("")) {
				if (inst.src1.contains("R") || inst.src1.contains("F")) {
					if (inst.src1.contains("(")) {
						if (!value_registers.contains(inst.src1.split("\\(")[1].trim().split("\\)")[0].trim()))
							value_registers.add(inst.src1.split("\\(")[1].trim().split("\\)")[0].trim());
					} else {
						if (!value_registers.contains(inst.src1))
							value_registers.add(inst.src1);
					}
				} else {
					try {
						Integer.parseInt(inst.src1);
					} catch (Exception exc) {
						throw new Exception("Invalid inst_initializer");
					}
				}
			}

			if (!inst.src2.equals("") && !inst.is_it_jump_inst) {
				if (inst.src2.contains("R") || inst.src2.contains("F")) {
					if (inst.src2.contains("(")) {
						if (!value_registers.contains(inst.src2.split("\\(")[1].trim().split("\\)")[0].trim()))
							value_registers.add(inst.src2.split("\\(")[1].trim().split("\\)")[0].trim());
					} else {
						if (!value_registers.contains(inst.src2))
							value_registers.add(inst.src2);
					}
				} else {
					try {
						Integer.parseInt(inst.src2);
					} catch (Exception exc) {
						throw new Exception("Invalid inst_initializer");
					}
				}
			}
		}

		for (String string : value_registers) {
			var_map.put(string, 0);
		}

	} catch (Exception exc) {
		System.out.println(exc.getMessage());
		System.exit(0);
	}
	int inst_index_start = 0;
	ArrayList<p_line> w_iList = new ArrayList<p_line>();
	ArrayList<p_line> f_i_l = new ArrayList<p_line>();
	Map<Integer, String> reg = new HashMap<Integer, String>();
	ArrayList<Integer> id_list = new ArrayList<Integer>();
	
	int clock = 1;
	boolean ft_busy = false;
	boolean Complete = false;

	int dc_num = 0;
	int ic_num = 0;

	for (inst_initializer inst : i_list) {
		id_list.add(inst.id);
	}
	cache_initializer i_cach;
	data_cache d_C;
	cache_control m;
	i_cach = new cache_initializer(Integer.parseInt(config_initializer.c_line.split("#")[0]),
			Integer.parseInt(config_initializer.c_line.split("#")[1]), id_list, 3);
	d_C = new data_cache(2, 4, 2, 3);
	m = new cache_control(i_cach, d_C);

	do {

		if (clock == 157) {
		}

		if (w_iList.size() <= 0) {
			if (clock == 1) {
				w_iList.add(new p_line(i_list.get(0)));
			} else {
				Complete = true;
			}
		}

		int w_iIndex = 0;
		ArrayList<p_line> f_i_lTemp = new ArrayList<p_line>();
		while (w_iList.size() > w_iIndex) {
			p_line w_i = w_iList.get(w_iIndex);

			if (w_i.stage_category == stage_type.NS) {
				if (!ft_busy) {
					if (m.avail(w_i.inst.id)) {
						w_i.Fetch_meth(clock);
						if (!w_i.inst.inst.equals("SW")
								&& !w_i.inst.inst.equals("S.D")) {
							reg.put(w_i.id, w_i.inst.dest);
						}
						ft_busy = true;
						ic_num++;
						if (w_i.inst.inst.equals("SW") || w_i.inst.inst.equals("LW")) {
							dc_num+=2;
						} else if (w_i.inst.inst.equals("S.D")
								|| w_i.inst.inst.equals("L.D")) {
							dc_num += 2;
						}
					}
				} else {

				}
				break;
			} else if (w_i.stage_category == stage_type.FETCH) {
				String f_id = g_f_unit(w_i.FU_unit_type);

				boolean h_flg = false;

				HashMap<Integer, String> r_current = new HashMap<>(reg);
				r_current.keySet().removeIf(o -> o.intValue() >= w_i.id);

				if (r_current.containsValue(w_i.inst.dest)) {
					if (!w_i.inst.is_it_jump_inst && !w_i.inst.inst.equals("S.D")
							&& !w_i.inst.inst.equals("SW")) {
						w_i.Flag_WAW();
						h_flg = true;
					}
				}

				if ((!f_id.equals("") && !h_flg)) {
					w_i.Issue_meth(clock);
					w_i.Set_FU(
							f_unit_list.stream().filter(o -> o.id.equals(f_id)).findFirst().get());
					w_i.FU_unit.Update_Unavailable();
					ft_busy = false;

					if (w_i.inst.is_it_hlt_inst) {

						p_line t_br = null;
						int tempId = w_i.id - 1;

						if (w_iList.stream().filter(o -> o.id == tempId).count() > 0) {
							t_br = w_iList.get(w_iIndex - 1);
						} else if (f_i_l.stream().filter(o -> o.id == tempId).count() > 0) {
							t_br = f_i_l.stream().filter(o -> o.id == tempId).findFirst().get();
						}

						if (t_br != null && t_br.inst.is_it_jump_inst) {
							if (t_br.is_branch_taken) {
								w_i.Issue_meth(0);
							} else {
								w_i.Issue_meth(clock);
							}
						} else {
							w_i.Issue_meth(0);
						}
						w_i.Read_meth(0); 
						w_i.Execute_meth(0); 
						w_i.Write_meth(0); 
						w_i.stage_category = stage_type.WRITE;
					}

				} else {
					if (!w_i.inst.is_it_hlt_inst) {
						w_i.Flag_Structural();
					}

					if (inst_index_start + 1 < i_list.size())
						m.avail(i_list.get(inst_index_start + 1).id);

				}
			} else if (w_i.stage_category == stage_type.ISSUE) {

				boolean h_flg = false;

				HashMap<Integer, String> r_current = new HashMap<>(reg);
				r_current.keySet().removeIf(o -> o.intValue() >= w_i.id);

				if (w_i.inst.is_it_jump_inst && !w_i.inst.is_it_uncond) {
					if (r_current.containsValue(w_i.inst.src1)
							|| r_current.containsValue(w_i.inst.dest)) {

						w_i.Flag_RAW();
						h_flg = true;
					}
				} else {
					String src1 = w_i.inst.src1;
					String src2 = w_i.inst.src2;

					if (src1.contains("(")) {
						src1 = src1.split("\\(")[1].trim().split("\\)")[0].trim();
					}

					if (src2.contains("(")) {
						src2 = src2.split("\\(")[1].trim().split("\\)")[0].trim();
					}

					if (w_i.inst.inst.equals("S.D") || w_i.inst.inst.equals("SW")) {
						if (r_current.containsValue(w_i.inst.dest)) {
							w_i.Flag_RAW();
							h_flg = true;
						}

					} else if (r_current.containsValue(src1)
							|| r_current.containsValue(src2)) {

						w_i.Flag_RAW();
						h_flg = true;
					}
				}

				if (!h_flg) {

					if (w_i.inst.is_it_jump_inst) {
						w_i.Read_meth(clock);
						w_i.Execute_meth(0);
						w_i.Write_meth(0);
						w_i.stage_category = stage_type.WRITE;
						if (w_i.inst.is_it_uncond) {
							String label_check = w_i.inst.src1;
							inst_index_start = i_list.indexOf(
									i_list.stream().filter(o -> o.label_check.equals(label_check)).findFirst().get()) - 1;
							w_i.is_branch_taken = true;

						} else {

							int chck1 = var_map.get(w_i.inst.dest);
							int chck2 = var_map.get(w_i.inst.src1);

							if ((w_i.inst.inst.equals("BNE") && chck1 != chck2)
									|| (w_i.inst.inst.equals("BEQ") && chck1 == chck2)) {
								String label_check = w_i.inst.src2;
								inst_index_start = i_list.indexOf(
										i_list.stream().filter(o -> o.label_check.equals(label_check)).findFirst().get()) - 1;
								w_i.is_branch_taken = true;
							} else {
								w_i.is_branch_taken = false;
							}
						}
					} else {
						w_i.Read_meth(clock);
					}
				}

			} else if (w_i.stage_category == stage_type.READ) {

				boolean ok_ex = true;

				if (w_i.inst.inst.equals("L.D")) {
					ok_ex = false;
					String index = w_i.inst.src1;
					if (index.contains("(")) {
						String register = index.split("\\(")[1].trim().split("\\)")[0].trim();
						int off = Integer.parseInt(index.split("\\(")[0].trim());
						int adr = var_map.get(register) + off;
						int d_id = adr / 4;

						if (m.data_present(d_id)) {
							if (w_i.Read_inst + 2 == clock && m.inst_cache_active()) {
								w_i.update_execute(clock);
							}
							if (m.data_present(d_id + 1)) {
								ok_ex = true;
							}
						}
					}
				} else if (w_i.inst.inst.equals("S.D")) {
					ok_ex = false;
					String index = w_i.inst.src1;
					if (index.contains("(")) {
						String register = index.split("\\(")[1].trim().split("\\)")[0].trim();
						int off = Integer.parseInt(index.split("\\(")[0].trim());
						int adr = var_map.get(register) + off;
						int d_id = adr / 4;

						if (m.data_present(d_id)) {
							if (w_i.Read_inst + 2 == clock && m.inst_cache_active()) {
								w_i.update_execute(clock);
							}

							if (m.data_present(d_id + 1)) {
								ok_ex = true;
							}
						}
					}
				} else if (w_i.inst.inst.equals("LW")) {
					ok_ex = false;
					String index = w_i.inst.src1;
					if (index.contains("(")) {
						String register = index.split("\\(")[1].trim().split("\\)")[0].trim();
						int off = Integer.parseInt(index.split("\\(")[0].trim());
						int adr = var_map.get(register) + off;
						int d_id = adr / 4;

						if (m.data_present(d_id)) {
							ok_ex = true;
						}
					}
				} else if (w_i.inst.inst.equals("SW")) {
					ok_ex = false;
					String index = w_i.inst.src1;
					if (index.contains("(")) {
						String register = index.split("\\(")[1].trim().split("\\)")[0].trim();
						int off = Integer.parseInt(index.split("\\(")[0].trim());
						int adr = var_map.get(register) + off;
						int d_id = adr / 4;

						if (m.data_present(d_id)) {
							ok_ex = true;
						}
					}
				}

				if (ok_ex) {
					w_i.Execute_meth(clock);
				}

			} else if (w_i.stage_category == stage_type.EXEC) {
				boolean ex_complete = false;

				if (w_i.inst.inst.equals("LW") || w_i.inst.inst.equals("SW")) {
					if (w_i.FU_unit.used_clk_cyc == 1) {
						ex_complete = true;
					}
				} else {
					if (w_i.FU_unit.Did_it_Completed()) {
						ex_complete = true;
					}
				}

				if (!ex_complete) {
					w_i.update_execute(clock);
				} else {
					w_i.Write_meth(clock);
					executeInstrucction(w_i.inst);
				}

			} else if (w_i.stage_category == stage_type.WRITE) {

				if (w_i.FU_unit != null) {
					w_i.FU_unit.Reset_FU();
				}

				f_i_l.add(w_i);

				f_i_lTemp.add(w_i);

				reg.remove(w_i.id);
			}

			if (w_iIndex + 1 == w_iList.size() && !ft_busy && inst_index_start + 1 < i_list.size()) {
				w_iList.add(new p_line(i_list.get(++inst_index_start)));
			}

			w_iIndex++;
		}

		for (p_line f_i : f_i_lTemp) {
			w_iList.remove(f_i);
		}

		m.upd_cache();

		clock++;

	} while (!Complete);

	f_i_l.sort(new Comparator<p_line>() {

		@Override
		public int compare(p_line o1, p_line o2) {
			return o1.id - o2.id;
		}

	});
	;

	System.out.println("ID \t Instruction \t\t\tStatus Fetch \tIssue \tRead \tExec \tWrite \tRAW \tWAW \tStruct");
	for (p_line ip : f_i_l) {
		console(ip);
	}
	System.out.println("Data is stored in Result file with other details");

	w_to_txt(f_i_l, out_path, ic_num, ic_num - m.inst_cache_miss_cnt, dc_num,
			dc_num - m.data_cache_miss_count);
}

public String g_f_unit(FU_initializer.unit_type unitType) {
	String id = "";
	Optional<FU_initializer> op = f_unit_list.stream().filter(o -> o.FU_type == unitType && o.is_it_free).findFirst();
	if (op.isPresent()) {
		id = op.get().id;
	}
	return id;
}

public void executeInstrucction(inst_initializer inst) {
	int t_1 = 0, t_2 = 0, t_3 = 0;
	if (inst.inst.equals("LUI")) {
		t_1 = var_map.get(inst.src1);
		t_2 = t_1 >> 16;
		var_map.remove(inst.dest);
		var_map.put(inst.dest, t_2);
	} else if (inst.inst.equals("LI")) {
		if (inst.src1.startsWith("F") || inst.src1.startsWith("R")) {
			t_1 = var_map.get(inst.src1);
		} else {
			t_1 = Integer.parseInt(inst.src1);
		}
		var_map.remove(inst.dest);
		var_map.put(inst.dest, t_1);
	} else if (inst.inst.equals("LW")) {
		if (inst.src1.contains("(")) {
			t_1 = var_map.get(inst.src1.split("\\(")[1].split("\\)")[0]);
			t_2 = Integer.parseInt(inst.src1.split("\\(")[0]);
			t_3 = ((t_1 + t_2) / 4) - 64;
			t_3 = d_list.get(t_3);
			var_map.remove(inst.dest);
			var_map.put(inst.dest, t_3);
		} else {
			t_1 = var_map.get(inst.src1);
			t_2 = 0;
			t_3 = ((t_1 + t_2) / 4) - 64;
			t_3 = d_list.get(t_3);
			var_map.remove(inst.dest);
			var_map.put(inst.dest, t_3);
		}
	} else if (inst.inst.equals("SW")) {
		if (inst.src1.contains("(")) {

			t_1 = var_map.get(inst.src1.split("\\(")[1].split("\\)")[0]);
			t_2 = Integer.parseInt(inst.src1.split("\\(")[0]);
			t_3 = ((t_1 + t_2) / 4) - 64;

			d_list.set(t_3, var_map.get(inst.dest));
		} else {
			t_1 = var_map.get(inst.src1);
			t_2 = 0;
			t_3 = ((t_1 + t_2) / 4) - 64;

			d_list.set(t_3, var_map.get(inst.dest));
		}
	} else if (inst.inst.equals("L.D")) {
		if (inst.src1.contains("(")) {
			t_1 = var_map.get(inst.src1.split("\\(")[1].split("\\)")[0]);
			t_2 = Integer.parseInt(inst.src1.split("\\(")[0]);
			t_3 = ((t_1 + t_2) / 4) - 64;
			t_3 = d_list.get(t_3);
			var_map.remove(inst.dest);
			var_map.put(inst.dest, t_3);
		} else {
			t_1 = var_map.get(inst.src1);
			t_2 = 0;
			t_3 = ((t_1 + t_2) / 4) - 64;
			t_3 = d_list.get(t_3);
			var_map.remove(inst.dest);
			var_map.put(inst.dest, t_3);
		}
	} else if (inst.inst.equals("S.D")) {
		if (inst.src1.contains("(")) {

			t_1 = var_map.get(inst.src1.split("\\(")[1].split("\\)")[0]);
			t_2 = Integer.parseInt(inst.src1.split("\\(")[0]);
			t_3 = ((t_1 + t_2) / 4) - 64;

			d_list.set(t_3, var_map.get(inst.dest));
		} else {
			t_1 = var_map.get(inst.src1);
			t_2 = 0;
			t_3 = ((t_1 + t_2) / 4) - 64;

			d_list.set(t_3, var_map.get(inst.dest));
		}
	} else if (inst.inst.equals("DSUB")) {
		t_1 = var_map.get(inst.src1);
		t_2 = var_map.get(inst.src2);
		var_map.remove(inst.dest);
		var_map.put(inst.dest, t_1 - t_2);
	} else if (inst.inst.equals("DSUBI")) {
		t_1 = var_map.get(inst.src1);
		t_2 = Integer.parseInt(inst.src2);
		var_map.remove(inst.dest);
		var_map.put(inst.dest, t_1 - t_2);
	} else if (inst.inst.equals("DADD")) { 
		t_1 = var_map.get(inst.src1);
		t_2 = var_map.get(inst.src2);
		var_map.remove(inst.dest);
		var_map.put(inst.dest, t_1 + t_2);
	} else if (inst.inst.equals("DADDI")) {
		t_1 = var_map.get(inst.src1);
		t_2 = Integer.parseInt(inst.src2);
		var_map.remove(inst.dest);
		var_map.put(inst.dest, t_1 + t_2);
	} else if (inst.inst.equals("AND")) { 
		t_1 = var_map.get(inst.src1);
		t_2 = Integer.parseInt(inst.src2);
		var_map.remove(inst.dest);
		var_map.put(inst.dest, t_1 & t_2);
	} else if (inst.inst.equals("ANDI")) {
		t_1 = var_map.get(inst.src1);
		t_2 = Integer.parseInt(inst.src2);
		var_map.remove(inst.dest);
		var_map.put(inst.dest, t_1 & t_2);
	} else if (inst.inst.equals("OR")) {
		t_1 = var_map.get(inst.src1);
		t_2 = Integer.parseInt(inst.src2);
		var_map.remove(inst.dest);
		var_map.put(inst.dest, t_1 | t_2);
	} else if (inst.inst.equals("ORI")) {
		t_1 = var_map.get(inst.src1);
		t_2 = Integer.parseInt(inst.src2);
		var_map.remove(inst.dest);
		var_map.put(inst.dest, t_1 | t_2);
	} else if (inst.inst.equals("ADD.D")) {
		t_1 = var_map.get(inst.src1);
		t_2 = var_map.get(inst.src2);
		var_map.remove(inst.dest);
		var_map.put(inst.dest, t_1 + t_2);
	} else if (inst.inst.equals("SUB.D")) {
		t_1 = var_map.get(inst.src1);
		t_2 = var_map.get(inst.src2);
		var_map.remove(inst.dest);
		var_map.put(inst.dest, t_1 - t_2);
	} else if (inst.inst.equals("MUL.D")) {
		t_1 = var_map.get(inst.src1);
		t_2 = var_map.get(inst.src2);
		var_map.remove(inst.dest);
		var_map.put(inst.dest, t_1 * t_2);
	} else if (inst.inst.equals("DIV.D")) {
		t_1 = var_map.get(inst.src1);
		t_2 = var_map.get(inst.src2);
		var_map.remove(inst.dest);
		var_map.put(inst.dest, t_2 == 0 ? 0 : t_1 / t_2);
	}
}

public void console(p_line inst_p_line) {
	System.out.println(inst_p_line.id + "\t" + inst_p_line.inst.inst + "\t"
			+ inst_p_line.inst.dest + "\t" + inst_p_line.inst.src1 + "\t"
			+ inst_p_line.inst.src2 + "\t" + inst_p_line.stage_category + "\t"
			+ inst_p_line.Fetch_inst + "\t" + inst_p_line.Issue_inst + "\t" + inst_p_line.Read_inst + "\t"
			+ inst_p_line.Execute_inst + "\t" + inst_p_line.Write_inst + "\t" + inst_p_line.RAW_Hazard + "\t"
			+ inst_p_line.WAW_Hazard + "\t" + inst_p_line.Structural + "\t");
}

public void w_to_txt(ArrayList<p_line> outputList, String out_path, int inst_cache_req_cnt,
		int inst_hit, int data_cache_req_cnt, int data_hit) {
	try (BufferedWriter buff_wri = new BufferedWriter(new FileWriter(out_path))) {
	
		buff_wri.write("Instruction\t\t Fetch\t Issue\t Read\t Exec \t Write\t RAW \t WAW \t Struct");
		buff_wri.newLine();
		for (p_line inst_p_line : outputList) {
			buff_wri.write((inst_p_line.inst.complete_line.length() > 15
					? inst_p_line.inst.complete_line + "\t"
					: (inst_p_line.inst.complete_line.length() > 7
							? inst_p_line.inst.complete_line + "\t\t"
							: inst_p_line.inst.complete_line + "\t\t\t"))
					+ if_zero(inst_p_line.Fetch_inst) + "\t" + if_zero(inst_p_line.Issue_inst)
					+ "\t" + if_zero(inst_p_line.Read_inst) + "\t"
					+ if_zero(inst_p_line.Execute_inst) + "\t" + if_zero(inst_p_line.Write_inst)
					+ "\t" + inst_p_line.RAW_Hazard + "\t" + inst_p_line.WAW_Hazard + "\t"
					+ inst_p_line.Structural);
			buff_wri.newLine();

		}

		buff_wri.newLine();
		buff_wri.write("Request for  inst cache: " + inst_cache_req_cnt);
		buff_wri.newLine();
		buff_wri.write("Total Number of inst cache hits: " + (inst_hit+2));
		buff_wri.newLine();
		buff_wri.write("Request of D-cache: " + data_cache_req_cnt);
		buff_wri.newLine();
		buff_wri.write("Number of data cache hits: " + (data_hit-1));


	} catch (Exception exc) {

		exc.printStackTrace();

	}

}
}
