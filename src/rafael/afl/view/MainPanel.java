package rafael.afl.view;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import rafael.afl.core.Constantes;
import rafael.afl.parser.AnalisadorFormula;
import rafael.afl.parser.ArvoreBinaria;
import rafael.afl.parser.ArvoreBinaria.Node;

/**
 * Panel principal da aplicação usado nos modos Swing standalone e Applet
 */
public class MainPanel extends JPanel implements ActionListener {

	/*
	 * Ações utilizadas através de botões:
	 */
	private enum Action {
		AJUDA("Ajuda", "Exibe infomações sobre o funcionamento da aplicação"), ANALISAR("Analisar/Construir árvore",
				"Analisa se uma fórmula é bem formada e, se for, constrói sua árvore de decomposição");

		public String nome;
		public String toolTip;

		private Action() {
			nome = "";
			toolTip = "";
		}

		private Action(String nome, String toolTip) {
			this.nome = nome;
			this.toolTip = toolTip;
		}
	}

	private static final long serialVersionUID = 1L;
	private JBinaryTree binaryTree;

	private JBinaryTree.BinaryTreeModel<ArvoreBinaria.Node<String>> binaryTreeModelImpl;
	private JButton btAjuda;
	private JButton btAnalisar;
	private JScrollPane scrollPaneBynaryTree;
	private JScrollPane scrollPaneTree;

	private JTextField tfFormula;

	private JToolBar toolBar;
	private JTree tree;

	public MainPanel() {
		binaryTreeModelImpl = new JBinaryTree.BinaryTreeModel<ArvoreBinaria.Node<String>>() {
			@Override
			public int obterAltura() {
				return ArvoreBinaria.calcularAltura(raiz);
			}

			@Override
			public Node<String> obterFilhoDireita(Node<String> modelPai) {
				return modelPai.direita;
			}

			@Override
			public Node<String> obterFilhoEsquerda(Node<String> modelPai) {
				return modelPai.esquerda;
			}

			@Override
			public String obterValor(Node<String> model) {
				return model.valor;
			}
		};

		setLayout(new GridBagLayout());
		int row = 0;

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(3, 3, 3, 3);
		gridBagConstraints.ipadx = 6;
		gridBagConstraints.ipady = 6;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;

		// Cria a ToolBar e seus botões internos
		{
			toolBar = new JToolBar(SwingConstants.HORIZONTAL);
			toolBar.setFloatable(false);
			gridBagConstraints.weightx = 1; // Proporção de ocupação horizontal do componente
			gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
			gridBagConstraints.gridy = row++; // Define a linha
			add(toolBar, gridBagConstraints);

			btAnalisar = createButton(Action.ANALISAR);
			toolBar.add(btAnalisar);
			btAjuda = createButton(Action.AJUDA);
			toolBar.add(btAjuda);
		}
		{
			gridBagConstraints.gridwidth = GridBagConstraints.RELATIVE;
			gridBagConstraints.gridy = row++; // Define a linha
			JLabel lbFormula = new JLabel(Constantes.FORMULA_NOME);
			gridBagConstraints.gridx = 0; // Define a coluna
			gridBagConstraints.weightx = 0; // Proporção de ocupação horizontal do componente
			add(lbFormula, gridBagConstraints);

			tfFormula = new JTextField(Constantes.FORMULA_EXEMPLO);
			tfFormula.setFont(new Font(Font.MONOSPACED, Font.PLAIN, tfFormula.getFont().getSize()));
			gridBagConstraints.gridx = 1; // Define a coluna
			gridBagConstraints.weightx = 1; // Proporção de ocupação horizontal do componente
			add(tfFormula, gridBagConstraints);
		}
		// Cria a JTree para exibição da árvore de decomposição:
		{
			gridBagConstraints.gridx = 0; // Define a coluna

			gridBagConstraints.gridwidth = GridBagConstraints.RELATIVE;
			tree = new JTree();
			tree.setCellRenderer(new FormulaTreeCellRenderer());
			tree.setRootVisible(true);
			tree.setShowsRootHandles(true);
			tree.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
			tree.setRowHeight(20);
			tree.setModel(null);

			// Cria o JScrollPane onde está inserido a JTree:
			scrollPaneTree = new JScrollPane(tree);
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridy = row; // Define a linha
			gridBagConstraints.weighty = 1; // Proporção de ocupação vertical do componente
			add(scrollPaneTree, gridBagConstraints);
		}
		// Cria a JTree para exibição da árvore de decomposição:
		{
			gridBagConstraints.gridx = 1; // Define a coluna

			gridBagConstraints.gridwidth = GridBagConstraints.RELATIVE;
			binaryTree = new JBinaryTree();
			binaryTree.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
			binaryTree.setModel(binaryTreeModelImpl);

			// Cria o JScrollPane onde está inserido a JTree:
			scrollPaneBynaryTree = new JScrollPane(binaryTree);
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridy = row++; // Define a linha
			gridBagConstraints.weighty = 1; // Proporção de ocupação vertical do componente
			add(scrollPaneBynaryTree, gridBagConstraints);
		}
	}

	public void actionPerformed(ActionEvent evt) {
		Action action = Action.valueOf(evt.getActionCommand());
		try {
			switch (action) {
				case ANALISAR:
					criarArvoreDecomposicao();
					break;
				case AJUDA:
					exibirJanelaAjuda();
					break;
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), Constantes.APLICACAO_TITULO_ERRO, JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Cria um botão padrão do JToolBar. O nome, tooptip e ação do botão são extraídos do parametro <code>action</code>
	 * @param action - Ação do botão
	 * @return
	 */
	private JButton createButton(Action action) {
		JButton button = new JButton(action.nome);
		button.setFocusable(false);
		button.setToolTipText(action.toolTip);
		button.setMargin(new Insets(3, 6, 3, 6));
		button.setActionCommand(action.toString());
		button.addActionListener(this);
		return button;
	}

	/**
	 * Requisita a criação da árvore de decomposição
	 * @throws Exception
	 */
	private void criarArvoreDecomposicao() throws Exception {
		tree.setModel(null);
		new Thread(new Runnable() {
			public void run() {
				criarArvoreDecomposicaoSwing();
			}
		}).start();
	}

	private void criarArvoreDecomposicaoSwing() {
		ViewUtil.alterarCursor(this, Cursor.WAIT_CURSOR);
		try {
			final ArvoreBinaria<String> treeDecomposicao = AnalisadorFormula.criarArvoreDecomposicao(tfFormula.getText());
			final DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(treeDecomposicao.raiz.toString());
			criarArvoreDecomposicaoSwing(treeNode, treeDecomposicao.raiz);
			// Atualiza a JTree com a nova árvore de decomposição na thread de eventos do AWT:
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					tree.setModel(new DefaultTreeModel(treeNode));
					binaryTreeModelImpl.setRaiz(treeDecomposicao.raiz);
					// Expande a JTree:
					ViewUtil.expandAll(tree);

					JOptionPane.showMessageDialog(MainPanel.this, Constantes.MSG_FORMULA_BEM_FORMADA, Constantes.APLICACAO_TITULO_INFORMACAO, JOptionPane.INFORMATION_MESSAGE);
				}
			});
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(MainPanel.this, ex.getMessage(), Constantes.APLICACAO_TITULO_ERRO, JOptionPane.INFORMATION_MESSAGE);
		}
		ViewUtil.alterarCursor(this, Cursor.DEFAULT_CURSOR);
	}

	private void criarArvoreDecomposicaoSwing(DefaultMutableTreeNode jTreeSwingNodePai, ArvoreBinaria.Node<String> treeNodePai) {
		if (treeNodePai.esquerda != null) {
			DefaultMutableTreeNode newLeftJTreeNode = new DefaultMutableTreeNode(treeNodePai.esquerda.toString());
			jTreeSwingNodePai.add(newLeftJTreeNode);
			criarArvoreDecomposicaoSwing(newLeftJTreeNode, treeNodePai.esquerda);
		}
		if (treeNodePai.direita != null) {
			DefaultMutableTreeNode newRightJTreeNode = new DefaultMutableTreeNode(treeNodePai.direita.toString());
			jTreeSwingNodePai.add(newRightJTreeNode);
			criarArvoreDecomposicaoSwing(newRightJTreeNode, treeNodePai.direita);
		}
	}

	private void exibirJanelaAjuda() {
		String msgHelp = "";
		msgHelp += Constantes.FORMULA_PROPOSICOES_ACEITAS + "\n";
		msgHelp += Constantes.FORMULA_CONECTIVOS + "\n";
		msgHelp += Constantes.FORMULA_PONTUACAO + "\n";
		msgHelp += Constantes.FORMULA_TEXTO_EXEMPLO + "\n";
		msgHelp += "\n" + Constantes.APLICACAO_INFORMACOES + "\n";
		JOptionPane.showMessageDialog(this, msgHelp, Constantes.APLICACAO_TITULO, JOptionPane.INFORMATION_MESSAGE);
	}
}
