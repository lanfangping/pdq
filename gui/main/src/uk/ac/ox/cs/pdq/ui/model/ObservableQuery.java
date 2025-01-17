// This file is part of PDQ (https://github.com/ProofDrivenQuerying/pdq) which is released under the MIT license.
// See accompanying LICENSE for copyright notice and full details.

package uk.ac.ox.cs.pdq.ui.model;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import uk.ac.ox.cs.pdq.fol.Formula;
//import uk.ac.ox.cs.pdq.fol.Query;
import uk.ac.ox.cs.pdq.ui.UserInterfaceException;
import uk.ac.ox.cs.pdq.ui.io.ObservableQueryWriter;

// TODO: Auto-generated Javadoc
/**
 * Encapsulate a query, its name, description and the file it is stored in.
 *  
 * @author Julien Leblay
 */
public class ObservableQuery {

	/** */
	private final SimpleStringProperty name =  new SimpleStringProperty(this, "name");
	
	/**  */
	private final SimpleStringProperty description =  new SimpleStringProperty(this, "description");
	
	/**  */
	private final SimpleObjectProperty<File> file =  new SimpleObjectProperty<>(this, "file");
	
	/**  */
	private final SimpleObjectProperty<Formula> formula = new SimpleObjectProperty<>(this, "formula");
	
	/**
	 * Instantiates a new observable query.
	 *
	 * @param name the name
	 * @param description the description
	 * @param conjunctiveQuery the query
	 */
	public ObservableQuery(String name, String description, Formula formula) {
		this(name, description, null, formula);
	}

	/**
	 * Instantiates a new observable query.
	 *
	 * @param name the name
	 * @param description the description
	 * @param file the file
	 * @param query the query
	 */
	public ObservableQuery(String name, String description, File file, Formula formula) {
		this.name.set(name);
		this.description.set(description);
		this.file.set(file);
		this.formula.set(formula);
	}

	/**
	 */
	public ObservableStringValue nameProperty() {
		return this.name;
	}

	/**
	 *
	 */
	public ObservableStringValue descriptionProperty() {
		return this.description;
	}

	/**
	 * 
	 *
	 * @return the observable value
	 */
	public ObservableValue<File> fileProperty() {
		return this.file;
	}

	/**
	 * 
	 *
	 * @return the observable value
	 */
	public ObservableValue<Formula> formulaProperty() {
		return this.formula;
	}

	/**
	 * 
	 *
	 * @return the name
	 */
	public String getName() {
		return this.name.getValueSafe();
	}

	/**
	 */
	public String getDescription() {
		return this.description.getValueSafe();
	}

	/**
	 */
	public File getFile() {
		return this.file.get();
	}

	/**
	 */
	public Formula getFormula() {
		return this.formula.get();
	}

	/**
	 */
	public void setName(String n) {
		this.name.set(n);
	}

	/**
	 */
	public void setDescription(String d) {
		this.description.set(d);
	}

	/**
	 */
	public void setFile(File f) {
		this.file.set(f);
	}

	/**
	 * Sets the query.
	 *
	 * @param q the new query
	 */
	public void setQuery(Formula f) {
		this.formula.set(f);
	}

	/**
	 */
	public void destroy() {
		if (this.file.isNotNull().get()) {
			this.file.getValue().delete();
		}
	}

	/**
	 */
	public void store() {
		if (this.file.isNotNull().get()) {
			ObservableQueryWriter writer = new ObservableQueryWriter();
			File f = this.getFile();
			try (PrintStream o = new PrintStream(f)) {
				if (!f.exists()) {
					f.createNewFile();
				}
				writer.write(o, this);
				this.name.set(homepath(file.getValue().getPath()));
			} catch (IOException e) {
				throw new UserInterfaceException("Could not write file " + f.getAbsolutePath());
			}
		}
	}

	private String homepath(String path)
	{
		String home = System.getProperty("user.dir");
		return path.replace(home, "");
	}	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String result = this.getName();
		if (result == null || result.trim().isEmpty()) {
		}
		return result;
	}

	public Formula getQuery() {
		return formula.get();
	}
}
