import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class bplustree {
/*	public static void tests() {
		BPTree tree = new BPTree();
		tree.Initialize(3);
//		for (int i = 100; i > 0; i -= 6) {
//			int insertval = i % 2 == 0 ? i : 100 - i;
//			System.out.println(insertval);
//			tree.Insert(insertval, (double)insertval + 0.5);
//			insertval = i % 2 == 0 ? 100 - i : i;
//			System.out.println(insertval);
//			tree.Insert(insertval, (double)insertval + 0.5);
//		}
		for (int i = 50; i > 0; i -= 5) {
			int insertval = i;
			System.out.println(insertval);
			tree.Insert(insertval, (double)insertval + 0.5);
		}
		List<Double> list = tree.Search(-1, 1000);
		for (int i = 0; i < list.size(); ++i) {
			System.out.println(list.get(i));
		}
		tree.Delete(10);
		tree.Delete(15);
		tree.Delete(25);
		tree.Delete(35);
		tree.Delete(40);
		tree.Delete(45);		
		list = tree.Search(-1, 1000);
		for (int i = 0; i < list.size(); ++i) {
			System.out.println(list.get(i));
		}

	}*/
	
	public static void main(String[] args) {
		
//		tests();

		if (args.length < 1) {
			System.out.println("No arg provided");
			return;
		}

		FileInputStream ifstream = null;
		BufferedReader reader = null;
		try {
			ifstream = new FileInputStream(args[0]);
			reader = new BufferedReader(new InputStreamReader(ifstream));
		} catch (FileNotFoundException ex) {
			System.out.println("File not found.");
			return;
		}

		BPTree tree = new BPTree();
		String line = "";
		String printout = "";
		try {
			while ((line = reader.readLine()) != null) {
				line = removeSpaces(line);
				String[] halves = line.split("\\(");
				String command = halves[0];
				String arguments = halves[1].substring(0, halves[1].length() - 1);
				if (command.toLowerCase().contentEquals("initialize")) {
					int m = Integer.parseInt(arguments);
					tree.Initialize(m);
				}
				if (command.toLowerCase().contentEquals("insert")) {
					String[] splitargs = arguments.split(",");
					int key = Integer.parseInt(splitargs[0]);
					double val = Double.parseDouble(splitargs[1]);
					tree.Insert(key, val);
				}
				if (command.toLowerCase().contentEquals("delete")){
					int key = Integer.parseInt(arguments);
					tree.Delete(key);
				}
				if (command.toLowerCase().contentEquals("search")) {
					String[] splitargs = arguments.split(",");
					if (splitargs.length == 1) {
						int key = Integer.parseInt(splitargs[0]);
						Double val = tree.Search(key);
						printout += val.toString() + "\n";
					} else {
						int key1 = Integer.parseInt(splitargs[0]);
						int key2 = Integer.parseInt(splitargs[1]);
						List<Double> vals = tree.Search(key1, key2);
						for (int i = 0; i < vals.size(); ++i) {
							printout += vals.get(i).toString();
							if (i != vals.size() - 1) {
								printout += ", ";
							}
						}
						printout += "\n";
					}
				}
			}
		} catch (Exception ex) {
			System.out.println("Error while reading in and executing.");
		}

//		System.out.println(printout);
		try {
			FileWriter writer = new FileWriter("output_file.txt");
			writer.write(printout);
			writer.close();
		} catch (IOException e) {
			System.out.println("Error writing out results.");
		}

	}
	
	public static String removeSpaces(String orig) {
		String newstr = "";
		for (int i = 0; i < orig.length(); ++i) {
			if (orig.charAt(i) != ' ') {
				newstr += orig.charAt(i);
			}
		}
		return newstr;
	}
}
