package scoreboard;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class config_initializer {
	private static String ADDER = "FP ADDER";
	private static String MULTIPLIER = "FP MULTIPLIER";
	private static String DIVIDER = "FP DIVIDER";
	private static String CACHE = "I-CACHE";
	public static String c_line = "";

	public static ArrayList<FU_initializer> Config_initial(String path_file) {
		ArrayList<FU_initializer> List_FU = new ArrayList<FU_initializer>();
		if (!check_if_file_avail(path_file)) {
		} else {
			try {
				ArrayList<FU_initializer> Adder_FU_list = new ArrayList<>(), Multiplier_FU_list = new ArrayList<>(),
						Divider_FU_List = new ArrayList<>();

				BufferedReader buff_read = new BufferedReader(new FileReader(path_file));
				String line_check, s_line, FU_kind;
				int FU_count = 0, FU_cycle_count = 0, line_numbers = 0;
				while ((line_check = buff_read.readLine()) != null) {
					if (line_numbers > 4) {
						throw new Exception("Invalid configuration file!!!");
					}
					FU_kind = line_check.trim().split(":")[0].trim().toUpperCase();
					s_line = line_check.trim().split(":")[1].trim();
					FU_count = Integer.parseInt(s_line.split(",")[0].trim());
					FU_cycle_count = Integer.parseInt(s_line.split(",")[1].trim());
					line_numbers++;
					if (FU_kind.equals(ADDER))
						for (int i = 0; i < FU_count; i++)
							Adder_FU_list.add(new FU_initializer(FU_initializer.unit_type.ADDER, "A" + i, FU_cycle_count));

					if (FU_kind.equals(MULTIPLIER))
						for (int i = 0; i < FU_count; i++)
							Multiplier_FU_list
							.add(new FU_initializer(FU_initializer.unit_type.MULTIPLIER, "M" + i, FU_cycle_count));

					if (FU_kind.equals(DIVIDER))
						for (int i = 0; i < FU_count; i++)
							Divider_FU_List.add(new FU_initializer(FU_initializer.unit_type.DIVIDER, "D" + i, FU_cycle_count));

					if (FU_kind.equals(CACHE))
						c_line = FU_count + "#" + FU_cycle_count;

				}
				buff_read.close();

				List_FU.addAll(Adder_FU_list);
				List_FU.addAll(Multiplier_FU_list);
				List_FU.addAll(Divider_FU_List);
				List_FU.add(new FU_initializer(FU_initializer.unit_type.INTEGER, "I0", 1));
				List_FU.add(new FU_initializer(FU_initializer.unit_type.LOADSTORE, "L0", 2));
				List_FU.add(new FU_initializer(FU_initializer.unit_type.JMPHLT, "J0", 1));
			} catch (Exception exc) {

			}
		}

		return List_FU;
	}

	public static ArrayList<inst_initializer> initial_inst(String path_file) {
		ArrayList<inst_initializer> instructionList = new ArrayList<inst_initializer>();
		if (!check_if_file_avail(path_file)) {
		} else {
			try {
				BufferedReader buff_read = new BufferedReader(new FileReader(path_file));
				String line_check, k_lab, instructionLine, complete_line, inst, dest, src1, src2;
				String line_array[];
				boolean is_it_jump_inst, is_it_hlt_inst, is_it_uncond;

				while ((line_check = buff_read.readLine()) != null) {

					instructionLine = "";
					complete_line = "";
					inst = "";
					dest = "";
					src1 = "";
					src2 = "";
					k_lab = "";
					is_it_jump_inst = false;
					is_it_hlt_inst = false;
					is_it_uncond = false;

					line_check = line_check.toUpperCase();

					if (line_check.trim().split(":").length > 1) {
						k_lab = line_check.trim().split(":")[0].trim().toUpperCase();
						instructionLine = line_check.trim().split(":")[1].trim().toUpperCase();
					} else {
						instructionLine = line_check.trim().split(":")[0].trim().toUpperCase();
					}

					complete_line = k_lab.equals("") ? instructionLine : k_lab + ":" + instructionLine;

					if (instructionLine.contains(",")) {
						line_array = instructionLine.split(",");

						inst = line_array[0].split(" ")[0].trim().toUpperCase();
						dest = line_array[0].split(" ")[1].trim().toUpperCase();
						src1 = line_array[1].trim().toUpperCase();
						src2 = "";

						if (line_array.length > 2) {
							src2 = line_array[2].trim().toUpperCase();
						}

						if (inst.equals("BNE") || inst.equals("BEQ")) {
							is_it_jump_inst = true;
						}

					} else {
						inst = instructionLine.split(" ")[0].trim().toUpperCase();
						if (inst.equalsIgnoreCase("J")) {
							dest = instructionLine.split(" ")[0].trim().toUpperCase();
							is_it_jump_inst = true;
							is_it_uncond = true;
						} else {
							if (inst.equalsIgnoreCase("HLT")) {
								is_it_hlt_inst = true;
							}
						}
					}

					instructionList.add(new inst_initializer(complete_line, inst, dest, src1, src2,
							k_lab, is_it_jump_inst, is_it_hlt_inst, is_it_uncond));

				}
				buff_read.close();
			} catch (Exception exc) {

			}
		}

		return instructionList;
	}

	public static ArrayList<Integer> initial_data(String path_file) {
		ArrayList<Integer> dataList = new ArrayList<Integer>();
		if (!check_if_file_avail(path_file)) {
		} else {
			try {
				BufferedReader buff_read = new BufferedReader(new FileReader(path_file));
				String line_check;
				while ((line_check = buff_read.readLine()) != null) {
					dataList.add(Integer.parseInt(line_check, 2));
				}
				buff_read.close();
			} catch (Exception exc) {

			}
		}
		return dataList;
	}

	public static void initializeResult(String path_file) {
		if (!check_if_file_avail(path_file)) {
		} else {
		}
	}

	public static boolean check_if_file_avail(String path_file) {
		boolean available = false;
		try {
			BufferedReader buff_read = new BufferedReader(new FileReader(path_file));
			if (buff_read.readLine() != null) {
				available = true;
			}
			buff_read.close();
		} catch (Exception exc) {

		}
		return available;
	}
}
