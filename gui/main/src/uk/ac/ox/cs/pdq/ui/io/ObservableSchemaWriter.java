// This file is part of PDQ (https://github.com/ProofDrivenQuerying/pdq) which is released under the MIT license.
// See accompanying LICENSE for copyright notice and full details.

package uk.ac.ox.cs.pdq.ui.io;

import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import uk.ac.ox.cs.pdq.io.jaxb.IOManager;
import uk.ac.ox.cs.pdq.io.xml.AbstractXMLWriter;
import uk.ac.ox.cs.pdq.ui.model.ObservableSchema;

import javax.xml.bind.JAXBException;
import java.io.*;

// TODO: Auto-generated Javadoc
/**
 * Writes an observable schemas to XML.
 * 
 * @author Julien LEBLAY
 * 
 */
public class ObservableSchemaWriter extends AbstractXMLWriter<ObservableSchema> {

	/** Logger. */
	private static Logger log = Logger.getLogger(ObservableSchemaWriter.class);
	
	/**
	 * Default constructor.
	 */
	public ObservableSchemaWriter() {
	}
	
	/**
	 * should not be used for writing to file instead use write(File file, ObservableSchema o)
	 */
	@Override
	public void write(PrintStream out, ObservableSchema o) {
			out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	}
	
	
	public void write(File file, ObservableSchema o) {
		try
		{
			PrintStream out = new PrintStream(file);
			out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			this.writeSchema(file, o);
			out.close();
		}
		catch(FileNotFoundException e)
		{
			log.error("[FileNotFoundException - ObservableSchemaWriter.class]", e);
		}
	}

	/**
	 * Writes the given schema to the given output.
	 *
	 * @param file the file
	 * @param s the s
	 */
	private void writeSchema(File file, ObservableSchema s) {
		try
		{
			IOManager.exportSchemaToXml(s.getSchema(), file);
			String path1 = file.getAbsolutePath();
			String path2 = s.getFile().getAbsolutePath();
			File schemaDir1 = new File(path1 + "d");
			File schemaDir2 = new File(path2 + "d");
			schemaDir1.mkdirs();
			for(File sourceFile : listFiles(schemaDir2, "", ".sr"))
			{
				File destFile = new File(path1 + "d/" + sourceFile.getName());
				copyFile(sourceFile, destFile);
			}
			for(File sourceFile : listFiles(schemaDir2, "", ".srg"))
			{
				File destFile = new File(path1 + "d/" + sourceFile.getName());
				copyFile(sourceFile, destFile);
			}
		}
		catch(JAXBException | IOException e)
		{
		}
	}

private static File[] listFiles(File directory, final String prefix, final String suffix) {
	Preconditions.checkArgument(directory.isDirectory(), "Invalid internal schema directory " + directory.getAbsolutePath());
	return directory.listFiles(new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			return name.startsWith(prefix) && name.endsWith(suffix);
		}
	});
}


private static void copyFile(File source, File dest) throws IOException {
    InputStream is = null;
    OutputStream os = null;
    try {
        is = new FileInputStream(source);
        os = new FileOutputStream(dest);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }
    } finally {
        is.close();
        os.close();
    }
}
}

