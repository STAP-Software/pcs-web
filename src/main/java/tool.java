import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class tool {

	public static void main(String[] args) {
		InputStream fis;
		BufferedReader br = null;
		String line;
		List<String> fAndIList = new ArrayList<String>();
		List<String> analList = new ArrayList<String>();
		List<String> oneList = new ArrayList<String>();

		try {
			fis = new FileInputStream("/opt/apps/workspaces/peas-pcs/pcs-fortran-work/pcs_data/config/spot_flag_sufs_gp00.dat");
			br = new BufferedReader(new InputStreamReader(fis));
			
			while ((line = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line, " ");
				String spotNum = st.nextToken();
				int type = new Integer(st.nextToken());
				if (type == 0) {
					fAndIList.add(spotNum);
				}
				if (type == 1) {
					analList.add(spotNum);
				}
			}
		
			StringBuffer buf = new StringBuffer();
			int pos = 0;
			buf.append("'");
			for (String string : fAndIList) {
				buf.append(string + ",");
				if ((buf.length() - pos) > 150) {
					buf.append("' ||\n'");
					pos = buf.length();
				}
			}
			buf.append("'");
			System.out.println("type 1 = " + buf.toString());
			
			StringBuffer buf2 = new StringBuffer();
			pos = 0;
			buf2.append("'");
			for (String string : analList) {
				buf2.append(string + ",");
				if ((buf2.length() - pos) > 150) {
					buf2.append("' ||\n'");
					pos = buf2.length();
				}
			}
			buf2.append("'");
			System.out.println("type 2 = " + buf2.toString());

		} catch (Exception e) {

		} finally {
			// Done with the file
			try {
				br.close();
				br = null;
				fis = null;
			} catch (Exception e) {
			}
		}
	}
}
