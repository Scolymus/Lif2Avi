package main;

import java.io.File;

import javax.swing.JFileChooser;

/*######################################################
#          AutoSave plugin for ImageJ. v6.0            #  
#                                                      #  
#        Author: Scolymus                              #
#        Date: 19/5/2021.                              #
#        License: CC BY-NC-SA 4.0                      #
#                                                      #
#------------------------------------------------------#
#  Version history:                                    #
#                                                      #
#    1.0: 24/7/2016. Browse, fps and mode.             #
#    2.0: 28/7/2016. Added timerstamp and scale bar    #
#                        GUI redesigned.               #
#    3.0: 02/8/2016. Minor bugs fixed, including       #
#                    problems with saving images       #
#                    (videos with 1 frame).            #
#                    Introduced Video split.           #
#                    Automatically gets FPS and mag.   #
#                    Conversion is done in the         #
#                    background.                       #
#    4.0: 30/8/2016. Automatically, it joins the       # 
#                    splitted videos with ffmpeg.      #
#                    Now you can write time lapse for  #
#                    each video                        #
#                    Automatically gets Binning        #
#                    It writes the metadata file       #
#    5.0: 12/9/2016. Bulk option added.                # 
#                    Bugs fixed (including problem with#
#                    substack creation, multiple files #
#                    selection and automatic info).    #
#    5.1: 25/10/2016. Bugs fixed (problem related with #
#					 autoread from metafile).          # 
#    5.2: 19/07/2017. Bugs fixed (problem related with #
#					 autoread from metafile) and       #
#					 drag and drop for bulk.	       # 
#					 First version of autocontrast.    #
#    6.0: 19/05/2021. Adapted to oficial Fiji plugins  #
#                                                      #
######################################################*/

import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import net.imagej.ImageJ;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

@Plugin(type = Command.class, menuPath = "Plugins>Lif2Avi")
public class Lif2Avi implements Command {
    @Override
    public void run() {
    	String[] example = new String[0];
    	main(example); 
    }
	
	public static void main(final String[] args) {
		final ImageJ ij = new ImageJ();
		ij.launch(args);

		try {
			ImagePlus imp = WindowManager.getCurrentImage();
			Window_dialog window = new Window_dialog();
			window.frmAuto.setVisible(true);
			
			if (imp==null) {
				JFileChooser j = new JFileChooser();
		    	try{
		            j.setFileSelectionMode(JFileChooser.FILES_ONLY);
		            j.setMultiSelectionEnabled(true);
		            FileNameExtensionFilter filter = new FileNameExtensionFilter("Lif File","lif");
		            j.setFileFilter(filter);
		            j.showOpenDialog(window.frmAuto);
		            	
		            File files[] = j.getSelectedFiles();
		            if (files.length>1){
		            	for (int i = 0;i<files.length;i++){
		            		window.lst_bulk_model.addElement(files[i].getAbsolutePath());       		
		            	}
		            	window.chk_bulk.setSelected(true);
		            	window.txt_route.setEnabled(false);
		            	window.btn_browse.setEnabled(false);
		            }else if (files.length==1){
		            	//IJ.run("Open...");
		            	IJ.open(files[0].getAbsolutePath());
		        		window.txt_route.setText(IJ.getImage().getOriginalFileInfo().directory);
		        		window.lista_auto_main = window.auto_detect(window.lista_auto_main);
		            }
		    	}catch (Exception e){
		    		
		    	}
			}

		}
		catch (final Exception e) {
			e.printStackTrace();
		}
	}

}
