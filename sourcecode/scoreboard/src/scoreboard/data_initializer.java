package scoreboard;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class data_initializer {
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
	
	public  ArrayList<Integer> initialize(String path_file){
		
		ArrayList<Integer> data_list = new ArrayList<Integer>();
		if (!check_if_file_avail(path_file)) {
			
		} else {
			try {
				BufferedReader buff_read = new BufferedReader(new FileReader(path_file));
				String line_check;
				while ((line_check = buff_read.readLine()) != null) {
					data_list.add(Integer.parseInt(line_check, 2));
				}
				buff_read.close();
			} catch (Exception exc) {

			}
		}
		return data_list;
	}
	

}
