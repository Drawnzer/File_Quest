/**
 * Copyright(c) 2014 DRAWNZER.ORG PROJECTS -> ANURAG
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *      
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *                             
 *                             anurag.dev1512@gmail.com
 *
 */

package org.anurag.file.quest;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Stack;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

/**
 * 
 * @author Anurag
 *
 */
public class SDManager {

	
	public static int SORT_TYPE;
	public static Stack<String> nStack;
	ArrayList<Item> items;
	private static Context ctx;
	private File file;
	public static boolean SHOW_HIDDEN_FOLDER = false;
	String type;
	private static Resources res;
	public SDManager(Context context) {
		// TODO Auto-generated constructor stub
		ctx = context;
		nStack = new Stack<String>();
		if(new File("/storage").exists()){
			nStack.push("/storage");
		}else
			nStack.push(Constants.PATH);		
		nStack.push(Constants.PATH);
		items = new ArrayList<Item>();
		res = ctx.getResources();
	}
	
	/**
	 * Function to return current path  
	 * @return
	 */
	public static String getCurrentDirectory(){
		return nStack.peek();
	}
	
	/**
	 * Function To return Current File Name
	 * @return
	 */
	public static String getCurrentDirectoryName(){
		File file = new File(nStack.peek());
		return file.getName();
	}
	
	/**
	 * THIS CLASS FILTERS OUT THOSE FILES THAT CANNOT BE READ
	 * @author anurag
	 *
	 */
	public static class ReadFileFilter implements FileFilter{
		@Override
		public boolean accept(File f) {
			// TODO Auto-generated method stub
			return f.canRead();
		}		
	}
	
	/**
	 * THIS CLASS FILTERS OUT THOS FILE THAT CANNOT BE READ AND ARE HIDDEN
	 * @author anurag
	 *
	 */
	public static class HiddenFileFilter implements FileFilter{
		@Override
		public boolean accept(File f) {
			// TODO Auto-generated method stub
			return f.canRead() && !f.isHidden();
		}
		
	}
	
	/**
	 * SORTS THE FILE[] ALPHABETICALLY WITH HAVING FOLDERS FIRST
	 */
	public static Comparator<File> alphaFolderFirst = new Comparator<File>() {
		@Override
		public int compare(File a, File b) {
			boolean aIsFolder = a.isDirectory();
			boolean bIsFolder = b.isDirectory();
			if(bIsFolder == aIsFolder )
				return a.getName().compareToIgnoreCase(b.getName());
			else if(bIsFolder)
				return 1;
			return -1;
		}
	}; 
	
	
	/**
	 * SORTS THE FILE[] ALPHABETICALLY WITH HAVING FILES FIRST
	 */
	public static Comparator<File> alphaFileFirst = new Comparator<File>() {
		@Override
		public int compare(File a, File b) {
			boolean aIsFolder = a.isDirectory();
			boolean bIsFolder = b.isDirectory();
			if(bIsFolder == aIsFolder )
				return a.getName().compareToIgnoreCase(b.getName());
			else if(bIsFolder)
				return -1;
			return 1;
		}
	}; 
	
	/**
	 * SORTS THE FILE[] ALPHABETICALLY IRRESPECTIVE OF FILE OR FOLDER
	 */
	
	public static Comparator<File> alpha = new Comparator<File>() {
		@Override
		public int compare(File a, File b) {
				return a.getName().compareToIgnoreCase(b.getName());
		}
	}; 
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<Item> getList(){
		items.clear();
		file = new File(nStack.peek());
		if(SORT_TYPE == 4)
			return getCurrentFileListWithHiddenItemFirst();
		else if(SORT_TYPE == 5)
			return getCurrentFileListWithHiddenItemLast();
		if(file.canRead() && file.exists()){
			File[] files = null;
			if(!SHOW_HIDDEN_FOLDER)
				files = file.listFiles(new HiddenFileFilter());
			else
				files = file.listFiles(new ReadFileFilter());
			if(SORT_TYPE == 1)
				Arrays.sort(files,alpha);
			else if(SORT_TYPE == 2)
				Arrays.sort(files,alphaFolderFirst);
			else if(SORT_TYPE == 3)
				Arrays.sort(files,alphaFileFirst);
			int l = files.length;
			for(int i = 0 ;i<l ; ++i){
				File f = files[i];
				items.add(new Item(f,buildIcon(f),type,getSize(f)));
			}	
		}
		return items;
	}

	/**
	 * Function to Generate Current Directory File List Without Having Hidden Folders In List
	 * Sorted in alphabetical order
	 * @return
	 */
	public ArrayList<Item> getCurrentFileListWithoutHiddenFolders(){
		items.clear();
		file = new File(nStack.peek());
		if(file.canRead() && file.exists()){
			File[] files = file.listFiles(new HiddenFileFilter());
			int l = files.length;
			for(int i = 0 ;i<l ; ++i)
				items.add(new Item(files[i], buildIcon(files[i]), type, getSize(files[i])));
		}
		return  items;
	}

	/**
	 * function sorting files in alphabetical order
	 * keeping hidden items first
	 * @return
	 */
	public ArrayList<Item> getCurrentFileListWithHiddenItemFirst(){
		items.clear();
		file = new File(nStack.peek());
		if(file.canRead() && file.exists()){
			File[] files = file.listFiles();
			Arrays.sort(files,alphaFolderFirst);
			int l = files.length;
			if(SHOW_HIDDEN_FOLDER)
				for(int i = 0 ;i<l ; ++i)
					if(files[i].getName().startsWith(".") && files[i].canRead())
						items.add(new Item(files[i], buildIcon(files[i]), type, getSize(files[i])));
			for(int i = 0 ;i<l ; ++i)
				if(!files[i].getName().startsWith(".") && files[i].canRead())
					items.add(new Item(files[i], buildIcon(files[i]), type, getSize(files[i])));
		}
		
		return items;
	}
	
	
	/**
	 * function sorting files in alphabetical order
	 * keeping hidden items first
	 * @return
	 */
	public ArrayList<Item> getCurrentFileListWithHiddenItemLast(){
		items.clear();
		file = new File(nStack.peek());
		if(file.canRead() && file.exists()){
			File[] files = file.listFiles();
			Arrays.sort(files,alphaFolderFirst);
			for(int i = 0 ;i<files.length ; ++i)
				if(!files[i].getName().startsWith(".") && files[i].canRead())
					items.add(new Item(files[i], buildIcon(files[i]), type, getSize(files[i])));
			if(SHOW_HIDDEN_FOLDER)
				for(int i = 0 ;i<files.length ; ++i)
					if( files[i].getName().startsWith(".") && files[i].canRead())
						items.add(new Item(files[i], buildIcon(files[i]), type, getSize(files[i])));
		}
		return items;
	}
	
	/**
	 * 
	 * @return
	 */
	private Drawable buildIcon(File f){
		String name = f.getName();
		if(name.endsWith(".zip")||name.endsWith(".ZIP")){
			type=ctx.getString(R.string.zip);
			return res.getDrawable(R.drawable.ic_launcher_zip_it);
			
		}else if(name.endsWith(".7z")||name.endsWith(".7Z")){
			type=ctx.getString(R.string.zip7);
			return res.getDrawable(R.drawable.ic_launcher_7zip);
		}else if(name.endsWith(".rar")||name.endsWith(".RAR")){
			type=ctx.getString(R.string.rar);
			return res.getDrawable(R.drawable.ic_launcher_rar);
		}else if(name.endsWith(".tar")||name.endsWith(".TAR")||name.endsWith(".tar.gz")||name.endsWith(".TAR.GZ")
				||name.endsWith(".TAT.BZ2")||name.endsWith(".tar.bz2")){
			type=ctx.getString(R.string.tar);
			return res.getDrawable(R.drawable.ic_launcher_tar);
		}
		else if(name.endsWith(".mp3")||name.endsWith(".ogg")||name.endsWith(".m4a")||name.endsWith(".wav")
				||name.endsWith(".amr")||name.endsWith(".MP3")||name.endsWith(".OGG")||name.endsWith(".M4A")||
				name.endsWith(".WAV")||name.endsWith(".AMR")){
			type=ctx.getString(R.string.music);
			return res.getDrawable(R.drawable.ic_launcher_music);
		}
		else if(name.endsWith(".apk")||name.endsWith(".APK")){
			type=ctx.getString(R.string.application);
			return res.getDrawable(R.drawable.ic_launcher_apk);
		}		
		else if(name.endsWith(".flv")||name.endsWith(".mp4")||name.endsWith(".3gp")||name.endsWith(".avi")
				||name.endsWith(".mkv")||name.endsWith(".FLV")||name.endsWith(".MP4")||name.endsWith(".3GP")||name.endsWith(".AVI")
				||name.endsWith(".MKV")){
			type=ctx.getString(R.string.vids);
			return res.getDrawable(R.drawable.ic_launcher_video);
		}	
		else if(name.endsWith(".bmp")||name.endsWith(".gif")||name.endsWith(".jpeg")||name.endsWith(".jpg")
				||name.endsWith(".png")||name.endsWith(".BMP")||name.endsWith(".GIF")||name.endsWith(".JPEG")||name.endsWith(".JPG")
				||name.endsWith(".PNG")){
			type=ctx.getString(R.string.image);
			return res.getDrawable(R.drawable.ic_launcher_images);
		}
		else if(name.endsWith(".txt")||name.endsWith(".log")||name.endsWith(".ini")||name.endsWith(".doc")
				||name.endsWith(".ppt")||name.endsWith(".docx")||name.endsWith(".TXT")||name.endsWith(".LOG")||name.endsWith(".INI")||name.endsWith(".DOC")
				||name.endsWith(".PPT")||name.endsWith(".DOCX")){
			type=ctx.getString(R.string.docs);
			return res.getDrawable(R.drawable.ic_launcher_ppt);
		}
		else{
			type=ctx.getString(R.string.unknown);
			return res.getDrawable(R.drawable.ic_launcher_unknown);
		}		
	}
	
	/**
	 * THIS FUNCTION RETURN THE SIZE IF THE GIVEN FIZE IN PARAMETER
	 * @param f
	 * @return
	 */
	static String getSize(File f){
		if(f.isDirectory())
			return ctx.getString(R.string.directory);
		long size = f.length();
		if(size>Constants.GB)
			return String.format(ctx.getString(R.string.sizegb), (double)size/(Constants.GB));
		
		else if(size > Constants.MB)
			return String.format(ctx.getString(R.string.sizemb), (double)size/(Constants.MB));
		
		else if(size>1024)
			return String.format(ctx.getString(R.string.sizekb), (double)size/(1024));
		
		else
			return String.format(ctx.getString(R.string.sizebytes), (double)size);
	}
	
}