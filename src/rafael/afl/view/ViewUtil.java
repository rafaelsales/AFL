package rafael.afl.view;

import java.awt.Container;
import java.awt.Cursor;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import rafael.afl.core.Constantes;
import rafael.afl.core.Main;
import rafael.afl.parser.AnalisadorFormula.Conectivo;

public class ViewUtil {

	/**
	 * Altera o cursor
	 * @param container
	 * @param tipoCursor - Constante informando o tipo de cursor obtida na classe <code>java.awt.Cursor</code>
	 */
	public static void alterarCursor(final Container container, final int tipoCursor) {
		Runnable runnableTrocaCursor = new Runnable() {
			public void run() {
				container.setCursor(Cursor.getPredefinedCursor(tipoCursor));
			}
		};
		if (SwingUtilities.isEventDispatchThread()) {
			runnableTrocaCursor.run();
		} else {
			try {
				SwingUtilities.invokeAndWait(runnableTrocaCursor);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(container, Constantes.MSG_ERRO_INESPERADO, Constantes.APLICACAO_TITULO_ERRO, JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Expande todos os nós de uma JTree
	 * @param tree
	 */
	public static void expandAll(JTree tree) {
		// Percorre a arvore a partir da raiz:
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		ViewUtil.expandAll(tree, new TreePath(root), true);
	}

	/**
	 * Expande/contrai todos os nós de uma JTree
	 * @param tree
	 * @param parent
	 * @param expand - true: Expande todos os nós da JTree; false: contrai todos os nós de uma JTree
	 */
	@SuppressWarnings("unchecked")
	private static void expandAll(JTree tree, TreePath parent, boolean expand) {
		// Filhos percorridos:
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				ViewUtil.expandAll(tree, path, expand);
			}
		}

		// Expansão e contração é feita de baixo para cima:
		if (expand) {
			tree.expandPath(parent);
		} else {
			tree.collapsePath(parent);
		}
	}

	/**
	 * Obtém o ícone que representa um conectivo
	 * @param conectivo
	 * @return
	 */
	public static ImageIcon getIconeConectivo(Conectivo conectivo) {
		String nomeArquivoImagem;
		switch (conectivo) {
			case CONJUNCAO:
				nomeArquivoImagem = "conjuncao.png";
				break;
			case DISJUNCAO:
				nomeArquivoImagem = "disjuncao.png";
				break;
			case CONDICIONAL:
				nomeArquivoImagem = "condicional.png";
				break;
			case BICONDICIONAL:
				nomeArquivoImagem = "bicondicional.png";
				break;
			case NEGACAO:
				nomeArquivoImagem = "negacao.png";
				break;
			default:
				return null;
		}
		return new ImageIcon(ViewUtil.class.getResource(Main.URL_BASE_RESOURCE + nomeArquivoImagem));
	}

	/**
	 * Define o Look And Feel para o padrão do Sistema Operacional
	 */
	public static void setDefaultLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			// Ignora o erro e continua no Look And Feel padrão do SO.
		}
	}
}
