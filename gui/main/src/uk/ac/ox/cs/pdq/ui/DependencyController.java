// This file is part of PDQ (https://github.com/ProofDrivenQuerying/pdq) which is released under the MIT license.
// See accompanying LICENSE for copyright notice and full details.

package uk.ac.ox.cs.pdq.ui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import uk.ac.ox.cs.pdq.db.Schema;
import uk.ac.ox.cs.pdq.fol.Dependency;
import uk.ac.ox.cs.pdq.io.ReaderException;
import uk.ac.ox.cs.pdq.ui.model.ObservableQuery;
import uk.ac.ox.cs.pdq.ui.model.ObservableSchema;

// TODO: Auto-generated Javadoc
/**
 * The Class DependencyController.
 */
public class DependencyController {

	/** DependencyController logger. */
	private static Logger log = Logger.getLogger(DependencyController.class);

    /** The dependency text area. */
    @FXML private TextArea dependencyTextArea;
    
    /** The dependency editor button. */
    @FXML private Button dependencyEditorButton;
    
    /** The dependency editor message. */
    @FXML private Label dependencyEditorMessage;
    
    /** The invalid. */
    private SimpleBooleanProperty invalid = new SimpleBooleanProperty(false);

    /**
     * Initialize.
     */
    @FXML void initialize() {
        assert this.dependencyTextArea != null : "fx:id=\"dependencyTextArea\" was not injected: check your FXML file 'dependency-window.fxml'.";

        this.dependencyEditorButton.disableProperty().bind(this.invalid);
		this.dependencyTextArea.textProperty().addListener(
				(ObservableValue<? extends String> obs, String oldString, String newString) -> {
				DependencyController.this.dependencyEditorMessage.setText("");
				if (DependencyController.this.schema != null) {
					try {
					} catch (ReaderException e) {
						DependencyController.this.dependencyEditorMessage.setText(e.getMessage());
						DependencyController.this.invalid.set(true);
						return;
					}
					DependencyController.this.invalid.set(false);
				}
		});
    }

    /** The dependency. */
    private Dependency dependency;
    
    /** The new dependency. */
    private Dependency newDependency;
    
    /** The schema. */
    private ObservableSchema schema;
    
    /** The queries. */
    private ObservableList<ObservableQuery> queries;
    
    /** The data queue. */
    private ConcurrentLinkedQueue dataQueue;

	/**
	 * Gets the dependency.
	 *
	 * @return the dependency
	 */
	public Dependency getDependency() {
		return this.dependency;
	}

	/**
	 * Sets the queue.
	 *
	 * @param q the new queue
	 */
	public void setQueue(ConcurrentLinkedQueue q) {
		this.dataQueue = q;
	}

	/**
	 * Sets the dependency.
	 *
	 * @param dependency the new dependency
	 */
	public void setDependency(Dependency dependency) {
		this.dependency = dependency;
		this.refreshEditor();
	}

	/**
	 * Sets the schema.
	 *
	 * @param s the new schema
	 */
	public void setSchema(ObservableSchema s) {
		this.schema = s;
	}

	/**
	 * Sets the queries.
	 *
	 * @param queries the new queries
	 */
	public void setQueries(ObservableList<ObservableQuery> queries) {
		this.queries = queries;
		this.dependencyTextArea.editableProperty().set(queries.isEmpty());
		this.invalid.set(!queries.isEmpty());
	}

	/**
	 * Refresh editor.
	 */
	private void refreshEditor() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new PrintStream(out).println(this.dependency.toString());
		this.dependencyTextArea.setText(out.toString());
	}

	/**
	 * Save and close.
	 *
	 * @param event the event
	 */
	@FXML
	void saveAndClose(ActionEvent event) {
	   	if (!event.isConsumed()) {
    		event.consume();
    		if (this.newDependency != null) {
    			List<Dependency> dep = new ArrayList<>();
    			dep.addAll(Arrays.asList(this.schema.getSchema().getAllDependencies()));
    			dep.add(newDependency);
        		Schema newSchema = new Schema(this.schema.getSchema().getRelations(),dep.toArray(new Dependency[dep.size()]));
        		this.schema.setSchema(newSchema);
        		this.dataQueue.add(this.schema);
    		}
    		Stage stage = (Stage) this.dependencyEditorButton.getScene().getWindow();
    		stage.close();
	   	}
    }
}