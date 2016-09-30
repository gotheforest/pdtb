package edu.upenn.cis.anntool.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import edu.upenn.cis.anntool.util.FileManager;
import edu.upenn.cis.anntool.util.Relation;
import edu.upenn.cis.anntool.util.Span;
import edu.upenn.cis.anntool.util.SpanList;

public class RelListPanel extends JPanel implements PanelInterface {

	private static final long serialVersionUID = 1L;
	private JTree relList;
	private DefaultTreeModel relListModel;
	private JButton newButton;
	private JButton cancelButton;
	private JButton saveButton;
	private JButton deleteButton;
	private JButton replaceButton;
	private JButton rejectButton;
	private JButton acceptButton;
	private FileManager fileManager;
	
	private boolean acceptStatus;

	RelListPanel(final MainFrame mainFrame, final FileManager fileManager) {
		super(new GridBagLayout());

		this.fileManager = fileManager;

		relListModel = new DefaultTreeModel(new DefaultMutableTreeNode());
		relList = new JTree(relListModel);
		relList.setShowsRootHandles(true);
		ToolTipManager.sharedInstance().registerComponent(relList);
		relList.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		relList.setRootVisible(false);
		relList.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) relList
						.getLastSelectedPathComponent();
				if (node != null) {
					/* Only pass parent if it isn't root */
					if (relList.getSelectionPath().getPathCount() == 3) {
						mainFrame.selectionAction((Relation) node
								.getUserObject(),
								(Relation) ((DefaultMutableTreeNode) node
										.getParent()).getUserObject());
					} else {
						mainFrame.selectionAction(
								(Relation) node.getUserObject(), null);
					}
				}
			}
		});
		relList.setCellRenderer(new DefaultTreeCellRenderer() {
			private static final long serialVersionUID = 1L;

			public Component getTreeCellRendererComponent(JTree tree,
					Object value, boolean sel, boolean expanded, boolean leaf,
					int row, boolean hasFocus) {
				super.getTreeCellRendererComponent(tree, value, sel, expanded,
						leaf, row, hasFocus);
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
				Relation relation = (Relation) node.getUserObject();
				if (relation != null) {
					String toolTip = "";

					if (!relation.isComplete()) {
						setForeground(Color.red);
						toolTip += "Incomplete relation. ";
					} else {
						toolTip += "Complete relation. ";
					}

					String icon = "";
					/* Parent */
					if (node.getLevel() == 1) {
						if (relation.isGhost()) {
							icon = "/res/red_x.png";
							toolTip += "Relation does not exist in adjudication file but similar relation exists in annotation file(s).";
						} else if (!relation.areAdjudicationsEquivalent()) {
							icon = "/res/red_nequal.png";
							toolTip += "Relation in adjudication file does not completely match all similar relations in annotation file(s).";
						} else if (node.isLeaf()) {
							icon = "/res/orange_question.png";
							toolTip += "Relation exists in adjudication file but similar relation does not exist in annotation file(s).";
						} else {
							icon = "/res/green_equal.png";
							toolTip += "Relation in adjudication file completely matches all similar relations in annotation file(s).";
						}
					}
					/* Child */
					else if (node.getLevel() == 2) {
						Relation parent = (Relation) ((DefaultMutableTreeNode) node
								.getParent()).getUserObject();
						if (!relation.isEquivalent(parent)) {
							icon = "/res/red_nequal.png";
							toolTip += "Relation in annotation file does not completely match similar relation in adjudication file.";
						} else {
							icon = "/res/green_equal.png";
							toolTip += "Relation in annotation file completely matches similar relation in adjudication file.";
						}
					}
//					if (!icon.equals("")) {
//						URL resource = getClass().getResource(icon);
//						setIcon(new ImageIcon(resource));
//						setToolTipText(toolTip);
//					}
				}
				return this;
			}
		});

		//relList.setDragEnabled(false);
		// relList.setAutoscrolls(true);
		final DragSource dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer(relList,
				DnDConstants.ACTION_MOVE, new DragGestureListener() {
					public void dragGestureRecognized(DragGestureEvent dge) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) relList
								.getLastSelectedPathComponent();
						/* Only do drag if node is leaf */
						/*if (node != null
								&& relList.getSelectionPath().getPathCount() == 3) {
							Relation relation = (Relation) node.getUserObject();
							dragSource.startDrag(dge,
									DragSource.DefaultMoveDrop, relation,
									new DragSourceListener() {

										// TODO: implement these to change
										// cursor

										public void dropActionChanged(
												DragSourceDragEvent dsde) {
										}

										public void dragOver(
												DragSourceDragEvent dsde) {
										}

										public void dragExit(
												DragSourceEvent dsde) {
										}

										public void dragEnter(
												DragSourceDragEvent dsde) {
										}

										public void dragDropEnd(
												DragSourceDropEvent dsde) {
										}
									});
						}*/
					}
				});
		relList.setDropTarget(new DropTarget(relList, new DropTargetListener() {
			public void dropActionChanged(DropTargetDragEvent dtde) {
			}

			public void dragOver(DropTargetDragEvent dtde) {
			}

			public void dragExit(DropTargetEvent dtde) {
			}

			public void dragEnter(DropTargetDragEvent dtde) {
			}

			public void drop(DropTargetDropEvent dtde) {
				Transferable transferable = dtde.getTransferable();

				// flavor not supported, reject drop
				if (!transferable.isDataFlavorSupported(Relation.flavor)) {
					dtde.rejectDrop();
					return;
				}

				DefaultMutableTreeNode sourceNode = (DefaultMutableTreeNode) relList
						.getLastSelectedPathComponent();
				if (sourceNode != null
						&& relList.getSelectionPath().getPathCount() == 3) {
					Relation sourceRelation = (Relation) sourceNode
							.getUserObject();
					Relation sourceParent = (Relation) ((DefaultMutableTreeNode) sourceNode
							.getParent()).getUserObject();

					TreePath destinationPath = relList.getPathForLocation(
							dtde.getLocation().x, dtde.getLocation().y);
					DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) destinationPath
							.getLastPathComponent();
					if (targetNode != null) {
						Relation targetRelation = destinationPath
								.getPathCount() == 2 ? (Relation) targetNode
								.getUserObject()
								: (Relation) ((DefaultMutableTreeNode) targetNode
										.getParent()).getUserObject();
						if (sourceParent != targetRelation) {
							sourceRelation.setGroupNumber(targetRelation
									.getGroupNumber());
							// targetRelation.addAdjudication(sourceRelation);
							// sourceParent.getAdjudications().remove(sourceRelation);
							// saveAction(sourceRelation);
							load();
							fileManager.save();

							// Set selected relation
							TreePath path = null;
							for (int i = 0; i < relListModel
									.getChildCount(relListModel.getRoot())
									&& path == null; i++) {
								DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) relListModel
										.getChild(relListModel.getRoot(), i);
								Relation childRelation = (Relation) childNode
										.getUserObject();
								if (sourceRelation.isEquivalent(childRelation)
										&& sourceRelation.getRoot().equals(
												childRelation.getRoot())) {
									path = new TreePath(childNode.getPath());
								} else {
									for (int j = 0; j < childNode
											.getChildCount() && path == null; j++) {
										DefaultMutableTreeNode grandchildNode = (DefaultMutableTreeNode) childNode
												.getChildAt(j);
										Relation grandchildRelation = (Relation) grandchildNode
												.getUserObject();
										if (sourceRelation
												.isEquivalent(grandchildRelation)
												&& sourceRelation
														.getRoot()
														.equals(grandchildRelation
																.getRoot())) {
											path = new TreePath(grandchildNode
													.getPath());
										}
									}
								}
							}
							relList.setSelectionPath(path);
						}
					}
				}
			}
		}));

		JScrollPane jsp = new JScrollPane(relList);
		jsp.setPreferredSize(new Dimension(0, jsp.getPreferredSize().height));

		newButton = new JButton("Add New Relation");
		newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainFrame.newAction();
			}
		});

		saveButton = new JButton("Save Relation");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainFrame.saveAction();
			}
		});
		
		acceptButton = new JButton("Accept Relation");
		acceptButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainFrame.acceptAction();
			}			
		});

		cancelButton = new JButton("Cancel Changes");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) relList
						.getLastSelectedPathComponent();
				if (node == null) {
					/* null after new button had been clicked */
					mainFrame.cancelAction();
				} else {
					/* Only pass parent if it isn't root */
					if (relList.getSelectionPath().getPathCount() == 3) {
						mainFrame.selectionAction((Relation) node
								.getUserObject(),
								(Relation) ((DefaultMutableTreeNode) node
										.getParent()).getUserObject());
					} else {
						mainFrame.selectionAction(
								(Relation) node.getUserObject(), null);
					}
				}
			}
		});

		deleteButton = new JButton("Undo");	
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) relList
						.getLastSelectedPathComponent();
				if (node != null) {	
					//mainFrame.undoRejectAction();
					Relation relation = (Relation) node.getUserObject();
					if (relation.getAdjudications().size() > 0) {
						mainFrame.deleteAction((Relation) node.getUserObject());
					} else {
						mainFrame.undoRejectAction();
					}
				}
			}
		});

		replaceButton = new JButton("Select Annotation");
		replaceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainFrame.saveAction();
			}
		});
		
		rejectButton = new JButton("Reject Token");
		rejectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) relList
						.getLastSelectedPathComponent();
				if (node != null) {
					Relation relation = (Relation) node.getUserObject();
					if (relation.getAdjudications().size() > 0) {
						//mainFrame.deleteAction((Relation) node.getUserObject());
						mainFrame.rejectAction();
					
					} else {
						mainFrame.annotatorRejectAction();
					}
				}
			}
		});

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		add(jsp, c);

		createButtons(new JButton[] { newButton, saveButton, acceptButton, cancelButton,
				deleteButton, replaceButton, rejectButton });

		disableAll();
	}

	private void createButtons(JButton[] buttons) {
		for (int i = 0; i < buttons.length; i++) {
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = i + 1;
			c.weightx = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			add(buttons[i], c);
		}
	}

	private void disableAll() {
		newButton.setEnabled(false);
		saveButton.setEnabled(false);
		cancelButton.setEnabled(false);
		deleteButton.setEnabled(false);
		replaceButton.setEnabled(false);
		acceptButton.setEnabled(false);
		rejectButton.setEnabled(false);
	}

	public void loadAction() {
		load();
		cancelAction();
	}

	public void newAction() {
		newButton.setEnabled(false);		
		saveButton.setEnabled(true);		
		cancelButton.setEnabled(true);
		deleteButton.setEnabled(false);
		replaceButton.setEnabled(false);
		acceptButton.setEnabled(false);
		relList.clearSelection();
		relList.setEnabled(false);
		rejectButton.setEnabled(false);
	}

	public void selectionAction(boolean isChild, boolean isNonGhostParent, boolean isRejected) {
		newButton.setEnabled(true);
		saveButton.setEnabled(false);
		cancelButton.setEnabled(false);
		//deleteButton.setEnabled(isNonGhostParent);	
		
		if (isRejected) {
			deleteButton.setEnabled(true);
		} 
		
		/*else {
			deleteButton.setEnabled(false);
		}*/
		
		else {		
			if (relList != null) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) relList
						.getLastSelectedPathComponent();
				if (node != null) {
					Relation relation = (Relation) node.getUserObject();
					if (relation.getAdjudications().size() > 0) { // don't delete if no children
						deleteButton.setEnabled(isNonGhostParent);
					} else {
						deleteButton.setEnabled(false);
					}
				}
			}
		}
		
		replaceButton.setEnabled(isChild);
		acceptButton.setEnabled(isChild && acceptStatus);
		relList.setEnabled(true);			
		rejectButton.setEnabled(!isChild);

		if (isRejected) {
			rejectButton.setEnabled(false);
		}		
	}
	
	public void acceptAction(boolean isChild, boolean valid) {
		acceptButton.setEnabled(isChild && valid);
	}

	public void saveAction(Relation relation) {
		save(relation);
		cancelAction();
		// TODO: maybe we can do a shortcut here to skip the null compare etc
		TreePath path = null;
		for (int i = 0; i < relListModel.getChildCount(relListModel.getRoot())
				&& path == null; i++) {
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) relListModel
					.getChild(relListModel.getRoot(), i);
			Relation childRelation = (Relation) childNode.getUserObject();
			if (relation.equals(childRelation)) {
				path = new TreePath(childNode.getPath());
			}
		}
		relList.setSelectionPath(path);
	}

	public void deleteAction(Relation relation) {
		
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) relList
				.getLastSelectedPathComponent();
		
	//	if (relation.getAdjudications().size() == 0) { // don't delete if no children			
	//		return;
	//	}
		
		if (node != null) {
			int index = node.getParent().getIndex(node);
			delete((Relation) node.getUserObject());
			if (index >= relListModel.getChildCount(relListModel.getRoot())) {
				index--;
			}
			if (index >= 0) {
				relList.setSelectionPath(new TreePath(
						((DefaultMutableTreeNode) relListModel.getChild(
								relListModel.getRoot(), index)).getPath()));
			} else {
				cancelAction();
			}
		}
	}

	public void cancelAction() {
		newButton.setEnabled(true);
		saveButton.setEnabled(false);
		cancelButton.setEnabled(false);
		deleteButton.setEnabled(false);
		replaceButton.setEnabled(false);
		acceptButton.setEnabled(false);
		relList.clearSelection();
		relList.setEnabled(true);
		rejectButton.setEnabled(false);
	}

	public void inputAction(boolean isSame) {
		if (isSame) {
			selectionAction(replaceButton.isEnabled(), deleteButton.isEnabled(), false);
		} else {
			newButton.setEnabled(false);
			saveButton.setEnabled(!replaceButton.isEnabled());							
			cancelButton.setEnabled(true);
			// (deletebutton stays the same)
			// don't want to change deletebutton because this depends on whether
			// we are editing or not
			// (replaceButton stays the same)
			// want this to be disabled if parent relation selected and enabled
			// if child relation selected
			relList.setEnabled(false);
		}		
	}

	public boolean windowClosingAction() {
		return saveButton.isEnabled();
	}

	public void addAllAction(SpanList connSpans) {
		save(connSpans);
		cancelAction();
	}

	public void setFontName(String fontName) {
		Font f = relList.getFont();
		relList.setFont(new Font(fontName, f.getStyle(), f.getSize()));
	}

	/**
	 * Loads/Reloads the treemodel using the relationlist model
	 */
	public void load() {
		Set<String> expandedGroupIDs = new HashSet<String>();
		for (int i = 0; i < relListModel.getChildCount(relListModel.getRoot()); i++) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) relListModel
					.getChild(relListModel.getRoot(), i);
			if (relList.isExpanded(new TreePath(new Object[] {
					relListModel.getRoot(), node }))) {
				expandedGroupIDs.add(((Relation) node.getUserObject())
						.getRelationID());
			}
		}

		fileManager.insertAdjudicationRelations();
		List<Relation> model = fileManager.getRelationList();
		Collections.sort(model);
		relListModel.setRoot(new DefaultMutableTreeNode());
		for (Relation relation : model) {
			DefaultMutableTreeNode relationNode = new DefaultMutableTreeNode(
					relation);
			relListModel.insertNodeInto(relationNode,
					(DefaultMutableTreeNode) relListModel.getRoot(),
					relListModel.getChildCount(relListModel.getRoot()));
			for (Relation adjudication : relation.getAdjudications()) {
				relListModel.insertNodeInto(new DefaultMutableTreeNode(
						adjudication), relationNode, relationNode
						.getChildCount());
			}
		}

		relListModel.nodeStructureChanged((DefaultMutableTreeNode) relListModel
				.getRoot());

		for (int i = 0; i < relListModel.getChildCount(relListModel.getRoot()); i++) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) relListModel
					.getChild(relListModel.getRoot(), i);
			if (expandedGroupIDs.contains(((Relation) node.getUserObject())
					.getRelationID())) {
				relList.expandPath(new TreePath(new Object[] {
						relListModel.getRoot(), node }));
			}
		}
	}

	/**
	 * Called whenever the public save/save methods are called
	 */
	private boolean add(Relation relation) {
		List<Relation> model = fileManager.getRelationList();
		boolean added = !model.contains(relation);
		if (added) {
			model.add(relation);
		}
		return added;
	}

	/**
	 * Called when a the "Add All" button is pressed after a search for a
	 * connective.
	 * 
	 * @param connSpans
	 */
	public void save(SpanList connSpans) {
		for (Iterator<Span> i = connSpans.iterator(); i.hasNext();) {
			Span connSpan = i.next();
			add(new Relation(fileManager, connSpan));
		}
		load();
		fileManager.save();
	}

	/**
	 * Called when the save button is pressed normally
	 * 
	 * @param relation
	 * @return true if the relation was added
	 */
	public boolean save(Relation relation) {
		boolean added = add(relation);
		load();
		fileManager.save();
		return added;
	}

	/**
	 * Called when the delete button is pressed
	 * 
	 * @param relation
	 * @return
	 */
	public void delete(Relation relation) {
		fileManager.getRelationList().remove(relation);
		load();
		fileManager.save();
	}
	
	public void setAcceptStatus(boolean status) {
		acceptStatus = status;
	}
	
	private boolean hasIdentifier() {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) relList.getLastSelectedPathComponent();		
		if (node == null) {
			return false;
		} else {			
			Relation rel = (Relation) node.getUserObject();
			if (!rel.getIdentifier().equals("")) {
				return true;
			}
		}
		return false;
	}

}
