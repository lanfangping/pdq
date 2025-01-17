// This file is part of PDQ (https://github.com/ProofDrivenQuerying/pdq) which is released under the MIT license.
// See accompanying LICENSE for copyright notice and full details.

package uk.ac.ox.cs.pdq.ui.prefuse.popups;

import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;

import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import uk.ac.ox.cs.pdq.ui.prefuse.types.EdgeTypes;
import uk.ac.ox.cs.pdq.ui.prefuse.utils.Utils;



// TODO: Auto-generated Javadoc
/**
 * The Class CollapseExpandTree.
 */
public class CollapseExpandTree extends JCheckBox {

	/** The node. */
	private final NodeItem node;

	/**
	 *  Creates a new instance of DeleteVertexMenuItem.
	 *
	 * @param node the node
	 */
	public CollapseExpandTree(NodeItem node) {
		super("Collapse...");
		this.node = node;
		this.setSelected((boolean)node.get("isCollapsed"));
		this.setAction(new CollapseExpandListener(this.node));
	}


	/**
	 * The listener interface for receiving collapseExpand events.
	 * The class that is interested in processing a collapseExpand
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addCollapseExpandListener<code> method. When
	 * the collapseExpand event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see CollapseExpandEvent
	 */
	private class CollapseExpandListener extends AbstractAction {

		/** The node. */
		private final NodeItem node;

		/**
		 * Instantiates a new collapse expand listener.
		 *
		 * @param node the node
		 */
		public CollapseExpandListener(NodeItem node) {
			super("Collapse...");
			this.node = node;
		}

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			this.updateTree(this.node, ((JCheckBox) e.getSource()).isSelected());
		}

		/**
		 * Update tree.
		 *
		 * @param n the n
		 * @param isVisible the is visible
		 */
		private void updateTree(NodeItem n, Boolean isVisible) {

			Utils.modifyNodeProperty(n.getGraph(), (Integer) n.get("id"), "isCollapsed", isVisible);
			this.updateTreeRecursive(n, !isVisible);

		}

		/**
		 * Update tree recursive.
		 *
		 * @param n the n
		 * @param isVisible the is visible
		 */
		private void updateTreeRecursive(NodeItem n, Boolean isVisible) {
			if(n == null) {
				return;
			}
			
			
			
			if( n.equals(this.node)) {
				
				Iterator out = n.outEdges();
				
				while ( out.hasNext() ) {
					EdgeItem eitem = (EdgeItem)out.next();
					if( ((EdgeTypes)eitem.get("type")).equals(EdgeTypes.HIERARCHY) ) {
						NodeItem nitem = eitem.getAdjacentItem(n);
						eitem.setVisible(isVisible);
						nitem.setVisible(isVisible);
						this.updateTreeRecursive(nitem, isVisible);
					}
				}
				
			}
			else {
				
				Iterator in = n.inEdges();
				while( in.hasNext()) {
					EdgeItem eitem = (EdgeItem)in.next();
					eitem.setVisible(isVisible);
				}
				
				Iterator out = n.outEdges();
				while ( out.hasNext() ) {
					EdgeItem eitem = (EdgeItem)out.next();
					if( ((EdgeTypes)eitem.get("type")).equals(EdgeTypes.POINTER) ) {
						eitem.setVisible(isVisible);
					}
					else {
						NodeItem nitem = eitem.getAdjacentItem(n);
						eitem.setVisible(isVisible);
						nitem.setVisible(isVisible);
						this.updateTreeRecursive(nitem, isVisible);
					}
				}
				
			}
			
			
//			Iterator out = n.outEdges();
//			
//			Iterator in = n.inEdges();
//			
//			while( in.hasNext()) {
//				EdgeItem eitem = (EdgeItem)in.next();
//				eitem.setVisible(isVisible);
//			}
//			
//			while ( out.hasNext() ) {
//				EdgeItem eitem = (EdgeItem)out.next();
//				if( ((EdgeTypes)eitem.get("type")).equals(EdgeTypes.POINTER) ) {
//					eitem.setVisible(isVisible);
//				}
//				else {
//					NodeItem nitem = eitem.getAdjacentItem(n);
//					eitem.setVisible(isVisible);
//					nitem.setVisible(isVisible);
//					this.updateTreeRecursive(nitem, isVisible);
//				}
//			}
			
			
			//if ( activity != null )
			//	n.getVisualization().run(activity);
		}
	}



}
