package com.example.myfile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import com.vxmt.vcammini.activity.Utils;
import com.vxmt.vcammini.storage.StorageUtils.StorageInfo;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class FileActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        /*if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }*/
        
        
    }

    public static List<StorageInfo> getStorageList() {
        List<StorageInfo> list = new ArrayList<StorageInfo>();
//        String def_path = Environment.getExternalStorageDirectory().getPath();
//        boolean def_path_removable = Environment.isExternalStorageRemovable();
//        String def_path_state = Environment.getExternalStorageState();
//        boolean def_path_available = def_path_state.equals(Environment.MEDIA_MOUNTED)
//                                    || def_path_state.equals(Environment.MEDIA_MOUNTED_READ_ONLY);
//        boolean def_path_readonly = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY);

        HashSet<String> paths = new HashSet<String>();
        int cur_removable_number = 1;

        // We don't need to /storage/sdcard
        // if (def_path_available) {
        //     paths.add(def_path);
        //     list.add(0, new StorageInfo(def_path, def_path_readonly, def_path_removable, def_path_removable ? cur_removable_number++ : -1));
        // }

        BufferedReader buf_reader = null;
        try {
            buf_reader = new BufferedReader(new FileReader("/proc/mounts"));
            String line;
            Log.d(TAG, "/proc/mounts");
            while ((line = buf_reader.readLine()) != null) {
                Log.d(TAG, line);
                if (line.contains("vfat") || line.contains("/mnt")) {
                    StringTokenizer tokens = new StringTokenizer(line, " ");
                    String unused = tokens.nextToken(); //device
                    String mount_point = tokens.nextToken(); //mount point
                    if (paths.contains(mount_point)) {
                        continue;
                    }
                    unused = tokens.nextToken(); //file system
                    List<String> flags = Arrays.asList(tokens.nextToken().split(",")); //flags
                    boolean readonly = flags.contains("ro");

                    if (line.contains("/dev/block/vold")) {
                        if (!line.contains("/mnt/secure")
                            && !line.contains("/mnt/asec")
                            && !line.contains("/mnt/obb")
                            && !line.contains("/dev/mapper")
                            && !line.contains("tmpfs")
                            && line.contains("/mnt/")
                            && ignoreList.indexOf(mount_point) == -1) {

                            Utils.d("Mount line: " + line);
                            Utils.d("Mount Point: " + mount_point);
                            paths.add(mount_point);
                            list.add(new StorageInfo(mount_point, readonly, true, cur_removable_number++));
                        }
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (buf_reader != null) {
                try {
                    buf_reader.close();
                } catch (IOException ex) {}
            }
        }
        return list;
    }

    public static File getOutputMediaFile(int type) {
		File camDir = getCameraFolder();

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyMMdd_HH_mmss").format(new Date());
		File mediaFile = null;
		
		switch(type) {
		case MEDIA_TYPE_IMAGE:
			mediaFile = new File(camDir.getPath() + File.separator + timeStamp + ".jpg");
			break;
		case MEDIA_TYPE_VIDEO:
			mediaFile = new File(camDir.getPath() + File.separator + timeStamp + ".avi");
			break;
		case MEDIA_TYPE_AUDIO:
			mediaFile = new File(camDir.getPath() + File.separator + timeStamp + ".aac");
			break;
		case MEDIA_TYPE_TMP:
			mediaFile = new File(camDir.getPath() + File.separator + generateUniqueFileName());
		}

//		try {
//			if(!mediaFile.exists())
//				mediaFile.createNewFile();
//		} catch (IOException e) {
//			Utils.d(e);
//		}
		return mediaFile;
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.file, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    /*public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_file, container, false);
            return rootView;
        }
    }*/

}
